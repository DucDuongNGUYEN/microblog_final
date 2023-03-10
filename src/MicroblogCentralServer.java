import java.net.*;
import java.io.*;
import java.util.*;

public class MicroblogCentralServer {
    private static final int PORT = 1234;
    private static final String SERVER_CONNECT = "SERVERCONNECT";

    // Structure de données pour stocker les messages publiés
    private Map<String, Map<Integer, String>> messages;

    // Structure de données pour stocker les clients abonnés à des mots-clés ou utilisateurs
    private Map<String, Set<String>> subscriptions;

    // Files d'attente pour les messages des clients abonnés
    private Map<String, Queue<String>> queues;

    public MicroblogCentralServer() {
        messages = new HashMap<>();
        subscriptions = new HashMap<>();
        queues = new HashMap<>();
    }

    public void run() {
        try {
            ServerSocket serverSocket = new ServerSocket(PORT);
            System.out.println("Serveur MicroblogCentral démarré sur le port " + PORT);
            while (true) {
                Socket clientSocket = serverSocket.accept();
                Thread t = new Thread(new ClientHandler(clientSocket));
                t.start();
            }
        } catch (IOException e) {
            System.err.println("Erreur : " + e.getMessage());
        }
    }

    public static void main(String[] args) {
        MicroblogCentralServer server = new MicroblogCentralServer();
        server.run();
    }

    private class ClientHandler implements Runnable {
        private Socket clientSocket;
        private PrintWriter out;
        private BufferedReader in;
        private String username;

        public ClientHandler(Socket socket) {
            this.clientSocket = socket;
            this.username = "";
        }

        @Override
        public void run() {
            try {
                out = new PrintWriter(clientSocket.getOutputStream(), true);
                in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));

                String inputLine;
                while ((inputLine = in.readLine()) != null) {
                    String[] parts = inputLine.split(" ");
                    String command = parts[0];

                    if (command.equals("LOGIN")) {
                        username = parts[1];
                        System.out.println("Nouvelle connexion : " + username);
                        if (!queues.containsKey(username)) {
                            queues.put(username, new LinkedList<>());
                        }
                    } else if (command.equals("PUBLISH")) {
                        int id = messages.getOrDefault(username, new HashMap<>()).size();
                        String message = parts[1];
                        messages.putIfAbsent(username, new HashMap<>());
                        messages.get(username).put(id, message);
                        System.out.println("Nouveau message publié par " + username + " : " + id + " " + message);
                        notifySubscribers(username, id, message);
                    } else if (command.equals("SUBSCRIBE")) {
                        String subscription = parts[1];
                        subscriptions.putIfAbsent(subscription, new HashSet<>());
                        subscriptions.get(subscription).add(username);
                        System.out.println(username + " s'abonne à " + subscription);
                    } else if (command.equals("SERVERCONNECT")) {
                        String remoteHost = parts[1];
                        int remotePort = Integer.parseInt(parts[2]);
                        Socket remoteSocket = new Socket(remoteHost, remotePort);
                        System.out.println("Connexion au serveur " + remoteHost + ":" + remotePort);
                        PrintWriter remoteOut = new PrintWriter(remoteSocket.getOutputStream(), true);
                        BufferedReader remoteIn = new BufferedReader(new InputStreamReader(remoteSocket.getInputStream()));
                        String remoteInputLine;
                        while ((remoteInputLine = remoteIn.readLine()) != null) {
                            // Relay messages to local subscribers
                            String[] messageParts = remoteInputLine.split(" ");
                            String messageUsername = messageParts[0];
                            int messageId = Integer.parseInt(messageParts[1]);
                            String messageContent = String.join(" ", Arrays.copyOfRange(messageParts,2, messageParts.length));
                            if (subscriptions.containsKey(messageUsername)) {
                                for (String subscriber : subscriptions.get(messageUsername)) {
                                    Queue<String> queue = queues.get(subscriber);
                                    if (queue != null) {
                                        queue.add("[" + messageUsername + ":" + messageId + "] " + messageContent);
                                        System.out.println("Message relayé à " + subscriber + " : " + messageContent);
                                    }
                                }
                            }
                        }
                        remoteSocket.close();
                    }
                }
        } catch (IOException e) {
                e.printStackTrace();
            }
        }

        private void notifySubscribers (String publisher,int messageId, String messageContent){
            for (String keyword : subscriptions.keySet()) {
                if (keyword.startsWith("@")) {
                    if (keyword.substring(1).equals(publisher)) {
                        Set<String> subscribers = subscriptions.get(keyword);
                        for (String subscriber : subscribers) {
                            Queue<String> queue = queues.get(subscriber);
                            if (queue != null) {
                                queue.add("[" + publisher + ":" + messageId + "] " + messageContent);
                                System.out.println("Message relayé à " + subscriber + " : " + messageContent);
                            }
                        }
                    }
                } else if (messageContent.contains(keyword)) {
                    Set<String> subscribers = subscriptions.get(keyword);
                    for (String subscriber : subscribers) {
                        Queue<String> queue = queues.get(subscriber);
                        if (queue != null) {
                            queue.add("[" + publisher + ":" + messageId + "] " + messageContent);
                            System.out.println("Message relayé à " + subscriber + " : " + messageContent);
                        }
                    }
                }
            }
        }
    }
}
