package uno;

import java.io.IOException;

import java.net.Socket;
import java.util.Scanner;
import java.io.PrintWriter;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.ArrayList;




public class Joueur {
	
	//Init des noms et scores
	public String name;
	public int score;
	
	public Joueur() {}

	public Joueur(String name0,int score0) {
		this.name = name0;
		this.score = score0;
		
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public int getScore() {
		return score;
	}
	public void setScore(int score) {
		this.score = score;
	}
	
	//Gestion du jeu de carte
	
	public ArrayList<Carte> hand = new ArrayList<Carte>();
	
	public Carte card; //Is the card the player want to play.
	
	public void add_card(Carte card0) {
		this.hand.add(card0);
	}
	
	public void display() {
		System.out.println("Voici votre jeu");
		System.out.println(this.hand);
	}
	public void put_card(int e) {
		card = this.hand.get(e);
		this.hand.remove(e);
		
	}

	
}
