package uno;

public class Plus_2ou4 extends Carte {

	public Plus_2ou4(int value0, String color0) {
		super(value0, color0,"plus");
		
		// TODO Auto-generated constructor stub
	}
	
	public String toString() {
		switch(super.value) {
		case 2:
			return "+"+ value +"-" +  super.color;
			
		case 4:
			return "+"+ value;
		}
		return "+"+ value +"-" +  super.color;
		
	}
	
	
}
