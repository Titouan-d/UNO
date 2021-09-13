package uno;

import java.io.IOException;

import java.net.Socket;

import java.io.PrintWriter;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.Scanner;


public class Client {

    private Socket socket;
    
    private PrintWriter txtOut;
    
    private BufferedReader txtIn;
    
    public Joueur player = new Joueur(); 
    
    public Scanner sc;
    
    public String str = "init" ;
    
    public boolean running = true;

    public String talon;
    

    /**
     * 
     * @param host
     * 		L'adresse IP du serveur
     * @param port
     * 		Le port de connexion du serveur
     * @param name0
     * 		Le nom du joeur du client
     * @throws IOException
     */
 
    
    public Client(String host, int port, String name0) throws IOException {
    	
    	//Initialisation des joueurs
      		player.name = name0;
    		player.score = 0;
    	//Creation du scanner
    	//	Scanner sc = new Scanner(System.in);
    	//Connexion au serveur
    	
        socket = new Socket(host, port);
       
        
        
        txtOut = new PrintWriter(socket.getOutputStream());
        txtIn = new BufferedReader(new InputStreamReader (socket.getInputStream()));
   
    }
    
    /////////Envois de Message////////
    /**
     * Fonction qui envois le nom du joueur au serveur
     * @throws IOException
     */
    public void send_pseudo() throws IOException {
    	txtOut.println("je-suis " + player.name);
    	txtOut.flush();
    	receiveLine();
    	System.out.println(player.name + "> connected");
    }
    
    /**
     * Envois le texte qui est en parametre au serveur
     * Retire la carte de la main si une carte est envoyee
     * @param txt
     * 	
     */
    public void send(String txt) {
    	
    	if(txt.contains("je-pose")) {
    		if(txt.contains("+")){
    			Plus_2ou4 carte;
    			if (txt.contains("4")) {
    				carte = new Plus_2ou4(4,"black");
    			}
    			else {
	    			String cut = txt.substring(8);
	    			
	    	    	String col = cut.substring(3);
	    	    	carte = new Plus_2ou4(2,col);
    			}
    	    	for (int i =0; i < player.hand.size();i++) {
    	    		
	    			if (player.hand.get(i).value == carte.value) {
	    				
	    				if (player.hand.get(i).color.contentEquals(carte.color)){
	    				
	    					if(player.hand.get(i).effet.contentEquals(carte.effet)){
	    						
	    						player.hand.remove(i);
	    						break; //Pour ne pas supprimer deux cartes identiques dnas la main
	    					}
	    				}
	    				
	    			}
	    		}
    			} 
    		else { 
    			if (txt.contains("Inversion")) {
    				String col = txt.substring(18);
    				sens carte = new sens(col);
	    			for (int i =0; i < player.hand.size();i++) {
		    			if (player.hand.get(i).value == carte.value) {
		    				if (player.hand.get(i).color.contentEquals(carte.color)){
		    					if(player.hand.get(i).effet.contentEquals(carte.effet)){
		    					
		    						player.hand.remove(i);
		    						break;
		    					}
		    				}
		    				
		    			}
	    			}
    			}
	    		else {
		    		Carte carte = new Carte(0,"init");
		    		carte= get_card(txt.substring(8));
		    			
	    			for (int i =0; i < player.hand.size();i++) {
		    			if (player.hand.get(i).value == carte.value) {
		    				if (player.hand.get(i).color.contentEquals(carte.color)){
		    					if(player.hand.get(i).effet.contentEquals(carte.effet)){
		    					
		    						player.hand.remove(i);
		    						break;
		    					}
		    				}
		    				
		    			}
	    			}}
    		}
    		
    	}
    	txtOut.println(txt);
    	txtOut.flush();
    }
    
    ///////Reception des messages////////
    /**
     * 
     * @param str
     * texte sous la forme valeur-couleur 
     * @return une carte qui est creer a partir du texte en parametre
     */
    public Carte get_card(String str) {
    
    	String num = str.substring(0,1);
    	String col = str.substring(2);
    	
    	Carte card = new Carte(0,"init");
    	card.value = Integer.parseInt(num);
    	card.color = col;
    	
    	return card;
    }
    /**
     * Recois 7 carte du serveur et pour les ajouter a la main du joueur (facultatif mais ajoute de la clarete au Main))
     * @throws IOException
     */
    public void Init_joueur() throws IOException {
    	for (int i = 0; i < 7 ; i ++) {
    		String msg = txtIn.readLine();
    		if(msg.contains("+")){
    			if (msg.contains("4")) {
    				
        	    	
        			this.player.hand.add(new Plus_2ou4(4,"black"));
    			}
    		
    			else{
	    			String cut = msg.substring(8);

	    	    	String col = cut.substring(2);
	    			this.player.hand.add(new Plus_2ou4(2,col));
	    			}
    			
    		} 
    		else {
    			if (msg.contains("Inversion")) {
    				String col = msg.substring(17);
    				
    				this.player.hand.add(new sens(col));
    			}
    			else { 
        			this.player.hand.add(get_card(msg.split(" ")[1]));
        		}
    		}
    		
        	System.out.println(player.name + "> you received card " + this.player.hand.get(player.hand.size()-1) + " your hand is now : ");
        	System.out.println(player.hand);
        	System.out.println("");
    	}
    	
    }
    /**
     * Recois le texte envoye par le serveur, et realise les action en accord avec ce qui est reçu
     * @return le texte reçu
     * @throws IOException
     */
    public String receiveLine() throws IOException {
    	String msg = txtIn.readLine();
    	
    	if (msg == null) {
    		msg = "stop";
    	}
    	
    	System.out.println("receiving ... " + msg);

    	if (msg.contains("prends")){
    		receivePrends(msg);
    	}
    	
    	if(msg.contentEquals("joue")) {
    		receiveJoue();
        	}
    	
    	if(msg.contains("talon")) {
    		talon =msg.split(" ")[1];
    		System.out.println(player.name + "> info : nouveau talon " + talon);
    	}
    	
    	if(msg.contains("Ok")){
    		System.out.println(player.name + "> derniere carte jouee ok");
    	}
    	
    	if (msg.contains("stop")) {
    		 running = false ;
    	}
    	
    	if(msg.contains("couleur")) {
    		Scanner sc = new Scanner(System.in);
        	System.out.println("Donne le nom de la couleur en entier");
    		str = sc.nextLine();
        	send(str);
    	}
    	
    	return msg;
    }
    
    /**
     * Fonction qui ajoute a la main du joueur la carte donnee par le serveur
     * @param msg
     * @throws IOException
     */
    public void receivePrends(String msg) throws IOException {
    	
		if(msg.contains("+")){
			if (msg.contains("4")) {

				this.player.hand.add(new Plus_2ou4(4,"black"));
			}
			else{ 
				String cut = msg.substring(8);
			
	    	String col = cut.substring(2);
	    	
			this.player.hand.add(new Plus_2ou4(2,col));
			} 
		}
		else {
			if (msg.contains("Inversion")) {
		
			String col = msg.substring(17);
			
			this.player.hand.add(new sens(col));
			}
		
			else {
				this.player.hand.add(get_card(msg.split(" ")[1]));
			}
		}
		System.out.println(player.name + "> you received card " + this.player.hand.get(player.hand.size()-1) + " your hand is now : ");
    	System.out.println(player.hand);
    }
    
    /**
     * Fonction qui fait joueur le client en lui demandant ce qu'il veut faire
     * @throws IOException
     */
    public void receiveJoue() throws IOException {
    	Scanner sc = new Scanner(System.in);
    	System.out.println(player.name + "> discard top : " + talon + " play now, your hand : ");
		System.out.println(player.hand);
		str = sc.nextLine();
    	send(str);
    
    	
    	
    }
    ///////////Fonction interaction client/////////////

    /**
     * Fonction qui ferme le client
     * @throws IOException
     */
    public void close() throws IOException {
    	socket.close();
    }
}
