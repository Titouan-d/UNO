package uno;
import java.io.IOException;

import java.util.Scanner;

public class ServerMain {
	
		    public static void main(String[] args) throws IOException, InterruptedException {
		    	
		    	/////////On initialise le serveur////////////
		    	Scanner scan = new Scanner(System.in);
		    	System.out.println("Quel port de depart souhaitez vous ?");
		    	String str = scan.nextLine();
		    	int port = Integer.parseInt(str);
		    	System.out.println("Nombre de joueur ?");
		    	str = scan.nextLine();
		    	int num = Integer.parseInt(str);
		    	
		    	Server server = new Server(port,num); 
		    	
			  	////On initialise la pioche et le talon///
			 	server.initGame();	
				
				
				while(server.running) {
					server.receiveLine();
				}
				
				server.bilan_score(0);
				
				server.close();
			   
				scan.close();
		    	
		        server.close();
		    }

}
