package ravensproject;

import java.util.ArrayList;
import java.util.List;

public class DifferenceEdge{
	boolean equals=false;
	int isSymmetric=0;
	int isRotation=0;
	int[] isScale=new int[]{0,0};
	boolean changeFill=false;
	List<Pair> copy=new ArrayList<Pair>();
	int[] isTripleCopy=new int[]{0,0};
	
	DifferenceEdge() {}
	
	DifferenceEdge(DifferenceEdge r) {
		this.equals=r.equals;
		this.isSymmetric=r.isSymmetric;
		this.isRotation=r.isRotation;
		this.isScale=new int[]{r.isScale[0], r.isScale[1]};
		this.changeFill=r.changeFill;
		this.copy=new ArrayList<Pair>(r.copy);
		this.isTripleCopy=new int[]{r.isTripleCopy[0], r.isTripleCopy[1]};
	}
	
	@Override
	public String toString() {
		String s = "Equals:"+this.equals+" Symmetric:"+this.isSymmetric+" Rotation:"+this.isRotation+" Scale:";
		s+="["+this.isScale[0]+","+this.isScale[1]+"]";
		s+=" Copy:"+this.copy.size()+" Change fill:"+this.changeFill+" TripleCopy:"+"["+this.isTripleCopy[0]+","+this.isTripleCopy[1]+"]";
		return s;
	}
}