package uno;

public class Carte {
	
	//Init des valeurs et couleurs
	public int value;
	public String color;
	public String effet = "none";
	
	public Carte(int value0, String color0) {
		this.value = value0;
		this.color = color0;
	}
	
	public Carte(int value0, String color0,String effet0) {
		this.value = value0;
		this.color = color0;
		this.effet = effet0;
	}
	
	
	public int getValue() {
		return value;
	}
	public void setValue(int value) {
		this.value = value;
	}
	public String getColor() {
		return color;
	}
	public void setColor(String color) {
		this.color = color;
	}

	@Override
	public String toString() {
		return value + "-" + color;
	}
	
	public void display() {
		System.out.println(this);
	}
	
	
	
}
