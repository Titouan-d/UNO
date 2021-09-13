package uno;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;


import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;

import java.util.ArrayList;

public class Server {


	public boolean running= true;
	
	public Carte talon;
	
	public Paquet_test paquet;
	
	public int N;
	public int sens = 0;
	public int nb_pioche = 0;
	public int current_player;
	
	public ArrayList<Joueur> list_player = new ArrayList<Joueur>();
	
	//Plusieurs Clients
	private ArrayList<BufferedReader> list_txtIn = new ArrayList<BufferedReader>();
	private ArrayList<PrintWriter> list_txtOut = new ArrayList<PrintWriter>();
	private ArrayList<ServerSocket> list_server_socket = new ArrayList<ServerSocket>();
	private ArrayList<Socket> list_socket = new ArrayList<Socket>();
	
	
	/**
	 * Constructeur du serveur
	 * @param port_dep
	 * 	Port de connexion du pour le premier joueur, les ports suivants se trouveront en ajoutant 1.
	 * @param N0
	 * 	Nombre de joueur desirer
	 * @throws IOException
	 */
    public Server(int port_dep,int N0) throws IOException  {
    	N=N0;
    	//IL FAUT CREER AUTANT DE SOCKET QUE DE JOUEUR
    	//On creer un port par client, il n'y a pas d'utilisation de threads.
    	int[] list_port = new int[N];
    	
    	for (int i = 0 ; i < N ; i++ ) {
    		list_port[i] = (port_dep + i);
    		list_player.add(new Joueur("init",0));
    		list_server_socket.add(new ServerSocket(list_port[i]));
    		System.out.println("Server ready at: " + list_port[i]);
    		list_socket.add(list_server_socket.get(i).accept()); // blocks until a client connects to this server
    	    System.out.println("New client connexion: " + list_socket.get(i)); 
    	    list_txtIn.add(new BufferedReader(new InputStreamReader (list_socket.get(i).getInputStream())));
    	    list_txtOut.add(new PrintWriter(list_socket.get(i).getOutputStream()));
    	    current_player = i ;
    	    receiveLine();
    	}
    	//ArrayList<Joueur> list_player = new ArrayList<Joueur>();
    	paquet = new Paquet_test();
    	
    	
    }
    
    ////////Fonction d'initialisation du jeu...///////
    /**
     * Fonction d'initialisation du jeu
     * Creatiion du talon
     * Envois de "debut de manche" aux joueurs
     * Fait appel a la fonction Init player qui genere les joueurs au depart
     */
    public void initGame() {
    	current_player = 0;
    	Init_talon();
    	for (int i = 0; i< list_player.size();i++) {
	    	
			sendtext("debut-de-manche");
			Init_Joueur(list_player.get(current_player), current_player);
			current_player = get_player(current_player);
    	}
    	
    	sendtalon();

    	sendJoue();
    }
    
    /**
     * Actualise le score d'un joueur 
     * @param card
     * 	carte qui vient d'etre piochee ou posee
     * @param add
     * 	add : le joueur prend une carte
     * 	remove : le joueur pose sa carte
     */
    public void get_score(Carte card, String add) {
    	switch(add) {
    	case "add" : list_player.get(current_player).score = list_player.get(current_player).score + card.value;
    		break;
    	case "remove" : list_player.get(current_player).score = list_player.get(current_player).score - card.value;
	    	if (list_player.get(current_player).score == 0) {
	    		running = false;
	    	}
	    	break;
    	}
    	
    }
    
    
    /**
     * Fonction qui tire une carte au hassard dans le paquet pour generer le talon
     */
    public void Init_talon() {
    	int r;
    	int n = paquet.le_paquet.size();
    	r =  (int) (Math.round(n * Math.random())) - 1;
    	this.talon = paquet.le_paquet.get(r);
    	paquet.le_paquet.remove(r);
    	
    	
    }

    /**
     * Fonction qui initialise un joueur en lui attribuant 7 nouvelles cartes, puis envois ce joueur au client correspondant
     * @param player0
     * 	Joueur a initialiser
     * @param player_num
     * 	Numero du joueur dans la liste
     */
    public void Init_Joueur(Joueur player0,int player_num) {
    	int r;
    	int n;
    	for (int i = 0 ; i<7;i++) {
    		
    		n = paquet.le_paquet.size();
    		
   	    	r =  (int) (Math.round(n * Math.random())) - 1;
   	    	if (r<0) r=0; //si jamais le random renvois 0
   	    	player0.hand.add(paquet.le_paquet.get(r));
    	  	paquet.le_paquet.remove(r);
    	}
    	this.current_player = player_num;
    	for (int i = 0 ; i < 7 ; i++) {
    		System.out.println("S -> " + player0.name + " prends " + player0.hand.get(i).toString());
    		get_score(player0.hand.get(i),"add");
    		sendtext("prends " + player0.hand.get(i).toString());
    	}
    }
   
    ///////////FONCTION D'ENVOIS AUX CLIENTS//////////////////
    /**
     * Fonction permettant d'envoyer un text au joueur actuel
     * @param txt
     * 	texte a envoyer
     */
    public void sendtext(String txt) {
    	//System.out.println("S -> " + list_player.get(current_player).name + txt);
    
    list_txtOut.get(current_player).println(txt);
    list_txtOut.get(current_player).flush();
    
    }
    
    
    /**
     * Fonction qui envois le nouveau talon a  tout les joueurs de la liste.
     * 
     * 
     */
    public void sendtalon() {
    	//int save = current_player;
    	
    	for (int i = 0; i < list_player.size();i++) {
    		System.out.println("S -> "+ list_player.get(i).name + " nouveau-talon " + talon);
    		sendtext("nouveau-talon " + talon);
    		current_player = get_player(current_player);
        	}
    }
    
    /**
     * Fonction qui envois ce qui vient d'etre pose (cad le talon) a tout les joueurs de la liste
     */
    public void sendPose() {
	    for (int i = 0; i < list_player.size();i++) {
			System.out.println("S -> "+ list_player.get(i).name + "nouveau-talon " + talon);
	    	sendtext("joueur " + list_player.get(current_player).name + "pose " + talon);
	    	current_player = get_player(current_player);
	    	}
    }
    
    /**
     * Fonction qui envois au joueur actuel "joue"
     */
    
    public void sendJoue() {
    	System.out.println("S -> " + list_player.get(current_player).name + " joue");
    	System.out.println("S -> " + list_player.get(current_player).name + " ...");
    	sendtext("joue");
    }
    
    /**
     * Fonction qui envois la carte piochee au joueur actuel
     * Evois l'information aux autres joueurs que le joueur a pioche
     */
    public void sendPioche() {
    	int pass = current_player;
    	for (int i = 0; i < list_player.size();i++) {
			System.out.println("S -> "+ list_player.get(i).name + ":"+ list_player.get(pass).name+ " pioche ");
	    	sendtext("Joueur " + list_player.get(pass).name + " pioche 1" );
	    	current_player = get_player(current_player);
	    	}
    	Carte card = pioche();
    	sendtext("prends " + card);
    	get_score(card,"add");
    }
    
    
    
    ///////Fonction d'interaction avec le jeu /////////
    /**
     * Fonction qui verifie que la carte entree en parametre peut etre posee sur le talon
     * @param card
     * 	Carte que l'on souhaite poser sur la talon
     * @return true si l'on peut poser la carte
     */
    public boolean peut_poser(Carte card) {
 	
    	if (card.color.contentEquals(talon.color) || card.color.contentEquals("black")) {
    		return true;
    	}
    	if (card.value == talon.value) {
    		if(card.effet == talon.effet) {
    			
    			return true;
    		}
    	}
    	
    	return false;
    	}
    
    /**
     * Fonction qui realise la pioche du jeu, elle prend une carte aleatoirement dans le paquet et la retire de celui-ci
     * @return la carte qui a ete piochee
     */
    public Carte pioche() {
    	int r;
    	int n = paquet.le_paquet.size();
    	
    	
    	r =  (int) (Math.round(n * Math.random())-1);
    	
    	if (r < 0) r=0;
    	Carte card = paquet.le_paquet.get(r);
    	paquet.le_paquet.remove(r);
    	return card;
    }
    
    /**
     * Fonction qui realise les effets des cartes speciales, ces effets sont donnes en parametres sous formes de string
     * @param effect
     * 	effet de la carte, "plus","Inversion" ou bien "none"
     * @param value
     * 	valeur de la carte utilisee pour les +2 ou +4
     * @throws IOException
     */
    public void get_effect(String effect, int value) throws IOException {
    	
    	switch (effect) {
    	case "plus" : 
    		current_player = get_player(current_player);
    		for (int i = 0; i < value ; i++) {
    			System.out.println("Joueur "+list_player.get(current_player).name + " pioche " + value);
	    		sendPioche();
    		}
    		if (value == 4) {
    			current_player = get_player(current_player);
    			//On demande la couleur au joueur
    			sendtext("Quelle couleur ?");
    			receiveLine();
    			current_player = get_player(current_player);
    		}
    		//le joueur passe son tour apres un plus2 ou plus4
    		
    		break;
    	case "Inversion":
    		if(sens == 0) sens = 1;
    		else sens = 0;
    		break;
    	case "none":
    		break;
    	}
    }
    ///////Fonction de bilan des scores, actions,....///////
    
    /**
     * Quand la partie est finis fait un bilan des scores et l'envois au joueurs
     * @param fin
     * 	parametre pouvant etre chnager pour faire un bilan de partie (partie non codee) ou de manche.
     */
    public void bilan_score( int fin) {
    	String bilan = " ";
    	if (fin == 0)bilan = "fin-de-manche ";
    	if (fin == 1)bilan = "fin-de-partie ";
    	
    	System.out.println(bilan);
    	for (int i  =0; i < list_player.size() ;i++) {
    		System.out.print(list_player.get(i).name + " " + list_player.get(i).score );
    	}
    	for(int j =0; j< N; j++) {
	    	sendtext(bilan);
	    	for (int i  =0; i < list_player.size() ;i++) {
	    		sendtext(list_player.get(i).name + " " + list_player.get(i).score );
	    	}
	    	current_player = get_player(current_player);
    	}
    	
    }
    
    ///////Fonction d'echange///////
    /**
     * Fonction rends une carte a partir d'un texte  de type valeur-couleur
     * @param str
     * 	texte qui designe notre carte
     * @return la carte desiree
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
     * Fonction qui traite les demandes des clients en recevant leurs messages
     * 
     * @throws IOException
     */
    public void receiveLine() throws IOException {
    	String msg = " ";
    	boolean check = true;
    	//On choisis le canal de reception
    	try {
    		msg = list_txtIn.get(current_player).readLine();
    	}
    	catch (Exception e) {
    	
    		System.out.println("Un client s'est subitement deconnecté");
    		System.out.println("Fermeture du serveur ...");
    		running = false;
    		check = false;	
    		close();
    		
    	}
    	
    	
    	
    	
    	
    	if (msg.contains("je-pose")) {
    		String la_carte = msg.split(" ")[1];
    		Carte card;//(0,"blue");
    		if (msg.contains("+")) {
    			if(msg.contains("2")) {
    			card = new Plus_2ou4(2,get_card(msg.substring(9)).color);
    			
    			}
    			else {
    				card = new Plus_2ou4(4,"black");
    			}
    		}
    		else { 
    			if (msg.contains("Inversion")) {
    
    				card = new sens(msg.substring(18));
    				
    			}
	    		
	    		else {
	    			card = get_card(la_carte);
	    		}
    		}
    		if (peut_poser(card) ){
    			get_score(card,"remove");
    			talon = card;
    			//System.out.println("nouveau-talon " + talon);
    			
    			String name = list_player.get(current_player).name;
    			System.out.println("S -> " + name + " OK");
    			sendtext("OK");
    			
    			
    			
    			
    			System.out.println("S -> " + name + " pose " + talon + " sur le talon");
    			
    			for (int i =0;i < N ;i++) {
    				sendtext(name +" pose " + talon);
    				current_player = get_player(current_player);
    			}
    			
    			sendtalon();
    			///Tout s'est bien passe le joueur change
    			//// Boucle d'envois d'info;
    			
    			get_effect(talon.effet,talon.value);
    			/////
    			current_player = get_player(current_player);
    			sendJoue();
    		}
    		else {
    			sendtext("ERREUR : Carte non valide");
    		
    			//Il doit piocher
    			sendtext("prends " + pioche());
    			
    			//Puis on fait passer le joueur
    			current_player = get_player(current_player);
    			sendJoue();
    			
    		}
    		
    		check = false;
    	}
    	if (msg.contains("je-suis")) {
    		sendtext("bienvenue");
    		list_player.get(current_player).name = msg.substring(8);
    		list_player.get(current_player).score = 0;
    		check = false;
    	}
    	
    	if (msg.contains("je-passe")) {
    		
    		switch(nb_pioche) {
    		case 0 :
    			nb_pioche = 1;
    			sendtext("Vous ne pouvez pas passer");
    			sendtext("prends " + pioche());
    			sendJoue();
    			//le joueur ne change pas
    			break;
    		case 1:
    			nb_pioche = 0;
    			System.out.println(list_player.get(current_player).name + "passe son tour");
    			current_player = get_player(current_player);
    			sendJoue();
    			break;
    		}
    		check = false;
    		
    	}
    	
    	if (msg.contains("je-pioche")) {
    		check = false;
    		switch(nb_pioche) {
    		case 0:
    			nb_pioche = 1;
    			sendPioche();
    			sendJoue();
    		
    		//Le joueur ne change pas
    		break;
    		case 1:
    			nb_pioche = 0;
    			
    			//Fonction pass
    			String name = list_player.get(current_player).name;
    			sendtext("Vous passez");
    			for(int i = 0 ; i < N ; i++) {
    			System.out.println("S -> " + list_player.get(current_player).name + " joueur " + name +" passe");
    			sendtext(name + " passe");
    			current_player = get_player(current_player);
    			}
    			
    			//Faire joueur le joueur suivant
    			current_player = get_player(current_player);
    			sendJoue();
    		break;
    		}
    	}
    	//RECEPTION DES COULEURS ENVOYEES EN MESSAGE UNIQUE
    	
    	if( msg.equals("rouge")) {
    		talon = new Carte(10,"rouge");
    		sendtalon();
    		current_player = get_player(current_player);
    		sendJoue();
    		check = false;
    	}
    	if( msg.equals("jaune")) {
    		talon = new Carte(10,"jaune");
    		sendtalon();
    		current_player = get_player(current_player);
    		sendJoue();
    		check = false;
    	}
    	if( msg.equals("bleu")) {
    		talon = new Carte(10,"bleu"); //on met 10 pour ne pas que les joueuers trichent en posant la meme valeur
    		sendtalon();
    		current_player = get_player(current_player);
    		sendJoue();
    		check = false;
    	}
    	if( msg.equals("vert")) {
    		talon = new Carte(10,"vert");
    		sendtalon();
    		current_player = get_player(current_player);
    		sendJoue();
    		check = false;
    	}
    	
    	
    	
    	
    	if (check) {
    		System.out.println("Votre message " + msg);
    		System.out.println("Erreur : message invalide");
    		sendJoue();
    	}
    	
    	
    	//return true ;
    }

    

    
    /**
     * Fonction qui ferme notre serveur et envois "stop" au client pour qu'ils se ferment aussi
     * @throws IOException
     */
    public void close() throws IOException {
    	for (int i =0; i < N;i++) {
    		sendtext("stop");
    		list_socket.get(i).close();
    	}
    }
    
    /**
     * Fonction qui prends l'indice du joueurs actuel et renvois l'indice du joueur suivant en fonction du sens de la partie
     * @param i
     * 	indice du joueru actuel
     * @return un entier etant l'indice du joueur suivant
     */
    public int get_player(int i) {
    	if (sens ==0) {
	    	if (i == list_player.size() - 1 ) return 0;
	    	else return i+1;
    	}
    	else {
    		if (i == 0 ) return list_player.size()-1;
    		else return i-1;
    	}
    	
    }
}
