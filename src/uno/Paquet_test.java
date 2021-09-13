package uno;

import java.util.ArrayList;

public class Paquet_test {
	
	public ArrayList<Carte> le_paquet = new ArrayList<Carte>();
	public ArrayList<String> color = new ArrayList<String>();
	//Le paquet est ordonne a sa creation
	//Beaucoup Moins de carte ici
	public Paquet_test() {
		color.add("bleu");
		color.add("rouge");
		color.add("jaune");
		color.add("vert");
		
		for (String couleur : color) {
			le_paquet.add(new Carte(0,couleur));
			for (int i = 1; i < 3 ; i++) {
				le_paquet.add(new Carte(i,couleur));
				le_paquet.add(new Carte(i,couleur));
				// 2 carte de chaque numero sauf le zero
			}
			le_paquet.add(new Plus_2ou4(2,couleur));
			le_paquet.add(new Plus_2ou4(2,couleur));
			
			le_paquet.add(new sens(couleur));
			le_paquet.add(new sens(couleur));
		}
		for (int i = 0 ; i < 4; i++) {
			le_paquet.add(new Plus_2ou4(4,"black"));
		}
		
	}
	
	public void display() {
		System.out.println(le_paquet);
	}
	
	

	
}
