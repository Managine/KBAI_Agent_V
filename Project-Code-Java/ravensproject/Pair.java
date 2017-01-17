package ravensproject;

public class Pair {
	public int x,y;
	
	Pair() {
		this.x=-1;
		this.y=-1;
	}
	
	Pair(int x, int y) {
		this.x=x;
		this.y=y;
	}
	
	Pair(Pair r) {
		this.x=r.x;
		this.y=r.y;
	}
	
	public Pair add(Pair r) {
		return new Pair(x+r.x, y+r.y);
	}
	
	public boolean originEquals(Pair r) {
		return Math.abs(this.x-r.x)<=3 && Math.abs(this.y-r.y)<=3;
	}
	
	@Override
	public String toString() {
		return "("+this.x+","+this.y+")";
	}
}
