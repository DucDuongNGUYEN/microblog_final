import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.util.HashMap;
import java.util.Map;

public class Server {

    private static final int BUFFER_SIZE = 1024;

    private static final Map<String, String> messages = new HashMap<>();

    public static void main(String[] args) throws IOException {

        DatagramSocket socket = new DatagramSocket(4445);

        byte[] buffer = new byte[BUFFER_SIZE];

        while (true) {
            DatagramPacket packet = new DatagramPacket(buffer, buffer.length);
            socket.receive(packet);
            String inputLine = new String(packet.getData(), 0, packet.getLength());

            if (inputLine.startsWith("PUBLISH ")) {
                String message = inputLine.substring(8);
                String[] parts = message.split(" ");
                String messageId = parts[0];
                String content = message.substring(messageId.length() + 1);
                messages.put(messageId, content);
                System.out.println("Message published with id " + messageId + " and content '" + content + "'");
            } else if (inputLine.startsWith("RCV_IDS ")) {
                String[] usernames = inputLine.substring(8).split(" ");
                for (String username : usernames) {
                    for (Map.Entry<String, String> entry : messages.entrySet()) {
                        if (entry.getValue().contains("@" + username)) {
                            sendResponse(socket, packet.getAddress(), packet.getPort(), "ID " + entry.getKey());
                        }
                    }
                }
            } else if (inputLine.startsWith("RCV_MSG ")) {
                String[] messageIds = inputLine.substring(8).split(" ");
                for (String messageId : messageIds) {
                    String content = messages.get(messageId);
                    if (content != null) {
                        sendResponse(socket, packet.getAddress(), packet.getPort(), "MSG " + messageId + " " + content);
                    }
                }
            } else if (inputLine.startsWith("REPLY ")) {
                String[] parts = inputLine.substring(6).split(" ", 2);
                String messageId = parts[0];
                String replyContent = parts[1];
                String content = messages.get(messageId);
                if (content != null) {
                    messages.put(messageId, content + " (reply: " + replyContent + ")");
                    System.out.println("Message " + messageId + " replied with content '" + replyContent + "'");
                }
            } else if (inputLine.startsWith("REPUBLISH ")) {
                String[] parts = inputLine.substring(10).split(" ", 2);
                String messageId = parts[0];
                String newContent = parts[1];
                String oldContent = messages.get(messageId);
                if (oldContent != null) {
                    messages.put(messageId, newContent);
                    System.out.println("Message " + messageId + " republished with new content '" + newContent + "'");
                }
            }
        }
    }

    private static void sendResponse(DatagramSocket socket, InetAddress address, int port, String response) throws IOException {
        byte[] buffer = response.getBytes();
        DatagramPacket packet = new DatagramPacket(buffer, buffer.length, address, port);
        socket.send(packet);
    }
}
