import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

public class MicroblogClient {

    public static void main(String[] args) throws IOException {
        String serverHostname = "localhost";
        int serverPort = 1234;

        // Connect to MicroblogCentral server
        Socket serverSocket = new Socket(serverHostname, serverPort);
        BufferedReader serverIn = new BufferedReader(new InputStreamReader(serverSocket.getInputStream()));
        PrintWriter serverOut = new PrintWriter(serverSocket.getOutputStream(), true);

        // Request username from user
        System.out.print("Enter username: ");
        BufferedReader userIn = new BufferedReader(new InputStreamReader(System.in));
        String username = userIn.readLine();

        // Send initial message to server to register username
        serverOut.println("REGISTER " + username);

        // Start loop to read user input and send messages to server
        String userInput;
        while ((userInput = userIn.readLine()) != null) {
            String[] parts = userInput.split(" ");
            String command = parts[0];

            // Publish a message
            if (command.equals("PUBLISH")) {
                String message = parts[1];
                serverOut.println("PUBLISH " + message);

                // Retrieve message IDs for one or more usernames
            } else if (command.equals("RCV_IDS")) {
                String[] usernames = parts[1].split(",");
                for (String user : usernames) {
                    serverOut.println("RCV_IDS " + user);
                    String serverResponse = serverIn.readLine();
                    String[] messageIds = serverResponse.split(" ");
                    for (String messageId : messageIds) {
                        System.out.println("Message ID for " + user + ": " + messageId);
                    }
                }

                // Retrieve message content for a given ID
            } else if (command.equals("RCV_MSG")) {
                int messageId = Integer.parseInt(parts[1]);
                serverOut.println("RCV_MSG " + messageId);
                String serverResponse = serverIn.readLine();
                System.out.println("Message content for ID " + messageId + ": " + serverResponse);

                // Invalid command
            } else {
                System.out.println("Invalid command. Commands available: PUBLISH, RCV_IDS, RCV_MSG");
            }
        }
    }
}