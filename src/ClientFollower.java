import java.io.*;
import java.net.*;

public class ClientFollower {
    public static void main(String[] args) throws IOException {
        // Demander l'adresse IP et le port du serveur
        BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
        System.out.print("Adresse IP du serveur : ");
        String serverAddress = br.readLine();
        System.out.print("Port du serveur : ");
        int serverPort = Integer.parseInt(br.readLine());

        // Se connecter au serveur
        Socket socket = new Socket(serverAddress, serverPort);
        BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        PrintWriter out = new PrintWriter(socket.getOutputStream(), true);

        // Demander les noms d'utilisateur pour lesquels on veut récupérer les messages
        System.out.print("Noms d'utilisateur à suivre (séparés par des espaces) : ");
        String[] usernames = br.readLine().split(" ");

        // Envoyer la requête RCV_IDS pour récupérer les identifiants des messages des utilisateurs demandés
        String request = "RCV_IDS ";
        for (String username : usernames) {
            request += username + " ";
        }
        out.println(request);

        // Récupérer la liste des identifiants renvoyée par le serveur
        String response = in.readLine();
        String[] messageIds = response.split(" ");

        // Envoyer la requête RCV_MSG pour chaque identifiant récupéré
        for (String messageId : messageIds) {
            out.println("RCV_MSG " + messageId);
            response = in.readLine();
            System.out.println(response);
        }

        // Fermer la connexion
        socket.close();
    }
}
