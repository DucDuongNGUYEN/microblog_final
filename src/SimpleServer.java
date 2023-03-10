import java.io.*;
import java.net.*;

public class SimpleServer {
    public static void main(String[] args) throws IOException {
        // Créer un socket d'écoute sur le port 1234
        ServerSocket serverSocket = new ServerSocket(1234);
        System.out.println("Serveur démarré sur le port " + serverSocket.getLocalPort());

        while (true) {
            // Attendre qu'un client se connecte
            Socket clientSocket = serverSocket.accept();
            System.out.println("Connexion entrante : " + clientSocket);

            try (BufferedReader in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()))) {
                // Lire la requête du client
                String inputLine = in.readLine();

                // Traiter la requête
                if (inputLine.startsWith("PUBLISH ")) {
                    String message = inputLine.substring(8);
                    System.out.println("Nouveau message publié : " + message);
                } else {
                    System.out.println("Requête non reconnue : " + inputLine);
                }
            } catch (IOException e) {
                System.out.println("Erreur de lecture : " + e);
            } finally {
                // Fermer la connexion avec le client
                clientSocket.close();
                System.out.println("Connexion fermée");
            }
        }
    }
}
