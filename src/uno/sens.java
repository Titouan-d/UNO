package uno;

public class sens extends Carte{
	
	public sens(String color0) {
		//La valeur vaut onze comme cela elle ne peut etre posee par meme valeur
		super(11,color0,"Inversion");
	}
	
	public String toString() {
		return "Inversion-" + super.color;
	}
	
}
