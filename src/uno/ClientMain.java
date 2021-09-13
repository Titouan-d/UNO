package uno;

import java.io.IOException;
import java.util.Scanner;



public class ClientMain {

	public static void main(String[] args) throws IOException {
	
		/////On initialise le client//////
	Scanner scan = new Scanner(System.in);
	System.out.println("Quel est votre nom ?");
	String str = scan.nextLine();
	String name = str;
	System.out.println("Quel port de connexion ?");
	str = scan.nextLine();
	int port = Integer.parseInt(str);
	
	String local = "127.0.0.1";	
    Client client = new Client(local, port,name);
    
    ///Boucle d'acceuil des donnees du serveur, facultative mais details plus l'acceuil////
    client.send_pseudo();
    client.receiveLine();
    client.Init_joueur();
    
    //Boucle While representant un tour de jeu
    while(client.running) {
    	client.receiveLine(); //Recois la consigne
    
    }
    
    System.out.println(client.player.name + "> Connection reset by server. Exiting");  
	client.close();
	scan.close();
	}

}
