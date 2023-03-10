import java.io.*;
import java.net.*;

public class PublisherClient {
    public static void main(String[] args) throws IOException {
        BufferedReader stdIn = new BufferedReader(new InputStreamReader(System.in));

        // Demander un pseudo à l'utilisateur
        System.out.print("Entrez votre pseudo : ");
        String pseudo = stdIn.readLine();

        // Se connecter au serveur
        Socket socket = new Socket("localhost", 1234);
        PrintWriter out = new PrintWriter(socket.getOutputStream(), true);

        while (true) {
            // Attendre un message de l'utilisateur
            System.out.print("Entrez votre message : ");
            String message = stdIn.readLine();

            // Envoyer le message au serveur
            out.println("PUBLISH " + pseudo + " " + message);
        }
    }
}
