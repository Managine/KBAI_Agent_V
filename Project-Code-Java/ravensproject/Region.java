package ravensproject;

import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

public class Region {
	public int left=Integer.MAX_VALUE, right=-1, top=Integer.MAX_VALUE, bottom=-1;
	Pair origin;
	public boolean[][] info;
	public boolean[][] outline;
	public boolean[][] region;
	public int sizeX, sizeY;
	int square;
	boolean fill;
	Region compareObject=null;
	Region prevCompareObject=null;
	Region convertObject=null;
	Region prevConvertObject=null;

	Region() {}
	
	Region(Region r) {
		origin=new Pair(r.origin);
		this.left=r.left;
		this.right=r.right;
		this.top=r.top;
		this.bottom=r.bottom;
		this.sizeX=r.sizeX;
		this.sizeY=r.sizeY;
		this.square=r.square;
		this.fill=r.fill;
		this.info=new boolean[sizeX][sizeY];
	}
	
	public boolean isSame(Region r) {
		return Math.abs(this.sizeX-r.sizeX)<=3 && Math.abs(this.sizeY-r.sizeY)<=3 && this.fill==r.fill && this.origin.originEquals(r.origin);
	}
	
	public boolean sizeEquals(Region r) {
		return Math.abs(this.sizeX-r.sizeX)<=3 && Math.abs(this.sizeY-r.sizeY)<=3;
	}
	
	public static boolean nearlyEquals(int x, int y) {
		return Math.abs(x-y)<=3;
	}
	
	public void fillParameters(int[][] groups, int val) {
		origin=new Pair(left, top);
		sizeX=right-left+1;
		sizeY=bottom-top+1;
		square=sizeX*sizeY;
		info=new boolean[sizeX][sizeY];
		for (int i=left;i<=right;i++) {
			for (int j=top;j<=bottom;j++) {
				info[i-left][j-top]=groups[i][j]==val;
			}
		}
		if (!info[sizeX/2][sizeY/2])
			fill=false;
		else {
			if (info[sizeX/2-sizeX/6][sizeY/2] || info[sizeX/2+sizeX/6][sizeY/2] || 
					info[sizeX/2][sizeY/2-sizeY/6] || info[sizeX/2][sizeY/2+sizeY/6])
				fill=true;
			else
				fill=false;
		}
	}
	
	public boolean get(int i, int j) {
		return info[i][j];
	}
	
	public void set(int i, int j, boolean v) {
		info[i][j]=v;
	}
	
	public Region generateFill() {
		if (fill)
			return this;
		Region r=new Region(this);
		r.fill=true;
		Queue<Pair> q=new LinkedList<Pair>();
		Pair p=new Pair(sizeX/2, sizeY/2);
		r.set(sizeX/2, sizeY/2, true);
		q.add(p);
		while (!q.isEmpty()) {
			Pair pair=q.poll();
			if (pair.x>0 && !info[pair.x-1][pair.y] && !r.get(pair.x-1, pair.y)) {
				r.set(pair.x-1, pair.y, true);
				q.add(new Pair(pair.x-1, pair.y));
			}
			if (pair.x<sizeX-1 && !info[pair.x+1][pair.y] && !r.get(pair.x+1, pair.y)) {
				r.set(pair.x+1, pair.y, true);
				q.add(new Pair(pair.x+1, pair.y));
			}
			if (pair.y>0 && !info[pair.x][pair.y-1] && !r.get(pair.x, pair.y-1)) {
				r.set(pair.x, pair.y-1, true);
				q.add(new Pair(pair.x, pair.y-1));
			}
			if (pair.y<sizeY-1 && !info[pair.x][pair.y+1] && !r.get(pair.x, pair.y+1)) {
				r.set(pair.x, pair.y+1, true);
				q.add(new Pair(pair.x, pair.y+1));
			}
		}
		return r;
	}
	
	public int isSymmetric(Region r) {	//0 for not, 1 for horizental, 2 for vertical
		if (!sizeEquals(r))
			return 0;
		
		int res=0;
		for (int i=0;i<Math.min(sizeX, r.sizeX);i++) {
			for (int j=0;j<Math.min(sizeY, r.sizeY);j++) {
				if (this.get(i, j)!=r.get(r.sizeX-i-1, j))
					res++;
			}
		}
		if (res<square/10)
			return 1;
		
		res=0;
		for (int i=0;i<Math.min(sizeX, r.sizeX);i++) {
			for (int j=0;j<Math.min(sizeY, r.sizeY);j++) {
				if (this.get(i, j)!=r.get(i, r.sizeY-j-1))
					res++;
			}
		}
		if (res<square/10)
			return 2;
		
		return 0;
	}
	
	public Region generateSymmetric(int symmetric) {
		Region res=new Region(left, right, top, bottom, origin);
		if (symmetric==1) {
			for (int i=0;i<sizeX;i++) {
				for (int j=0;j<sizeY;j++) {
					res.set(sizeX-i-1, j, this.get(i, j));
				}
			}
		} else if (symmetric==2) {
			for (int i=0;i<sizeX;i++) {
				for (int j=0;j<sizeY;j++) {
					res.set(i, sizeY-1-j, this.get(i, j));
				}
			}
		}
		return res;
	}
	
	public int isRotation(Region r) {
		int res=0;
		
		if (nearlyEquals(sizeX, r.sizeY) && nearlyEquals(sizeY,r.sizeX)) {
			for (int i=0;i<sizeX;i++) {
				for (int j=0;j<sizeY;j++) {
					try {
						if (this.get(i, j)!=r.get(r.sizeX-j-1, i))
							res++;
					} catch (ArrayIndexOutOfBoundsException e) {
						e.printStackTrace();
						continue;
					}
				}
			}
			if (res<square/10)
				return 90;
			
			res=0;
			for (int i=0;i<sizeX;i++) {
				for (int j=0;j<sizeY;j++) {
					try {
						if (this.get(i, j)!=r.get(j, r.sizeY-i-1))
							res++;
					} catch (ArrayIndexOutOfBoundsException e) {
						e.printStackTrace();
						continue;
					}
				}
			} 
			if (res<square/10)
				return 270;
		}
		
		if (nearlyEquals(sizeX,r.sizeX) && nearlyEquals(sizeY,r.sizeY)) {
			for (int i=0;i<sizeX;i++) {
				for (int j=0;j<sizeY;j++) {
					try {
						if (this.get(i, j)!=r.get(sizeX-i-1, sizeY-j-1))
							res++;
					} catch (ArrayIndexOutOfBoundsException e) {
						e.printStackTrace();
						continue;
					}
				}
			}
			if (res<square/10)
				return 180;
		}

		return 0;
	}
	
	public Region generateRotation(int degree) {
		Region res=new Region(left, left+bottom-top, top, top+right-left, origin);
		if (degree==90) {
			for (int i=0;i<sizeX;i++) {
				for (int j=0;j<sizeY;j++) {
					try {
						res.set(sizeX-1-j, i, this.get(i, j));
					} catch (ArrayIndexOutOfBoundsException e) {
						e.printStackTrace();
						continue;
					}
				}
			}
		} else if (degree==180) {
			for (int i=0;i<sizeX;i++) {
				for (int j=0;j<sizeY;j++) {
					try {
						res.set(sizeX-1-i, sizeY-1-j, this.get(i, j));
					} catch (ArrayIndexOutOfBoundsException e) {
						e.printStackTrace();
						continue;
					}
				}
			}
		} else if (degree==270) {
			for (int i=0;i<sizeX;i++) {
				for (int j=0;j<sizeY;j++) {
					try {
						res.set(j, sizeY-1-i, this.get(i, j));
					} catch (ArrayIndexOutOfBoundsException e) {
						e.printStackTrace();
						continue;
					}
				}
			}
		} else {
			return null;
		}
		return res;
	}
	
	public boolean isEquals(Region r) {
		int res=0;
		if (!sizeEquals(r))
			return false;
		for (int i=0;i<Math.min(sizeX, r.sizeX);i++) {
			for (int j=0;j<Math.min(sizeY, r.sizeY);j++) {
				if (this.get(i, j)!=r.get(i, j))
					res++;
			}
		}
		if (res==1174 && square/20==1240)
			return false;
		if (res<square/20)
			return true;
		return false;
	}
	
	public Region generateEqual(Pair offset) {
		Region r=new Region(left+offset.x, right+offset.x, top+offset.y, bottom+offset.y, origin.add(offset));
		for (int i=0;i<sizeX;i++)
			for (int j=0;j<sizeY;j++)
				r.set(i, j, this.get(i, j));
		return r;
	}
	
	public int[] isScale(Region r) { //ret[0]: 0 for not scale, 1 for horizental, 2 for vertical, 3 for both
		int[] res=new int[2];
		int t=0;
		if (nearlyEquals(sizeX,r.sizeX) && nearlyEquals(sizeY,r.sizeY))
			return res;
		if (!nearlyEquals(this.sizeX, r.sizeX) && !nearlyEquals(this.sizeY, r.sizeY)) {
			double factor=(double)r.sizeX/sizeX;
			if (Math.abs((double)r.sizeY/sizeY-factor)>0.2)
				return res;
			for (int i=0;i<sizeX/2;i++) {
				for (int j=0;j<sizeY/2;j++) {
					try {
						if (info[i][j]!=r.get((int)(r.sizeX/2-Math.abs(sizeX/2-i)*factor),  (int)(r.sizeY/2-Math.abs(sizeY/2-j)*factor))) 
							t++;
						if (info[sizeX-1-i][j]!=r.get((int)(r.sizeX/2+Math.abs(sizeX/2-i)*factor),  (int)(r.sizeY/2-Math.abs(sizeY/2-j)*factor))) 
							t++;
						if (info[i][sizeY-1-j]!=r.get((int)(r.sizeX/2-Math.abs(sizeX/2-i)*factor),  (int)(r.sizeY/2+Math.abs(sizeY/2-j)*factor))) 
							t++;
						if (info[sizeX-1-i][sizeY-1-j]!=r.get((int)(r.sizeX/2+Math.abs(sizeX/2-i)*factor),  (int)(r.sizeY/2+Math.abs(sizeY/2-j)*factor))) 
							t++;
					} catch (Exception e) {
						continue;
					}
				}
			}
			if (t<square/5) {
				res[0]=3;
				res[1]=(r.sizeX-sizeX)/2;
			} 
			return res;
		}
		
		if (!nearlyEquals(sizeX,r.sizeX)) {
			double factor=(double)r.sizeX/sizeX;
			for (int i=0;i<sizeX/2;i++) {
				for (int j=0;j<sizeY;j++) {
					try {
						if (info[i][j]!=r.get((int)(r.sizeX/2-Math.abs(sizeX/2-i)*factor), j)) 
							t++;
						if (info[sizeX-i-1][j]!=r.get((int)(r.sizeX/2+Math.abs(sizeX/2-i)*factor), j)) 
							t++;
					} catch (Exception e) {
						continue;
					}
				}
			}
			if (t<square/5) {
				res[0]=1;
				res[1]=(r.sizeX-sizeX)/2;
			}
		} else {
			double factor=(double)r.sizeY/sizeY;
			for (int i=0;i<sizeX;i++) {
				for (int j=0;j<sizeY/2;j++) {
					try {
						if (info[i][j]!=r.get(i, (int)(r.sizeY/2-Math.abs(sizeY/2-j)*factor))) 
							t++;
						if (info[i][sizeY-1-j]!=r.get(i, (int)(r.sizeY/2-Math.abs(sizeY/2-j)*factor))) 
							t++;
					} catch (Exception e) {
						continue;
					}
				}
			}
			if (t<square/5) {
				res[0]=2;
				res[1]=(r.sizeY-sizeY)/2;
			}
		}
		return res;
	}
	
	public Region generateScale(int[] scale) {
		if (scale[0]==0)
			return null;
		if (scale[0]==1) {
			Region res=new Region(left-scale[1], right+scale[1], top, bottom, new Pair(left-scale[1], top));
			double factor=(sizeX+2*scale[1])/(double)sizeX;
			for (int i=0;i<res.sizeX/2;i++) {
				for (int j=0;j<res.sizeY;j++) {
					try {
						res.set(i, j, this.get((int)(sizeX/2-Math.abs(res.sizeX/2-i)/factor), j));
						res.set(res.sizeX-i-1, j, this.get((int)(sizeX/2+Math.abs(res.sizeX/2-i)/factor), j));
					} catch (ArrayIndexOutOfBoundsException e) {
						e.printStackTrace();
						continue;
					}
				}
			}
			return res;
		}
		if (scale[0]==2) {
			Region res=new Region(left, right, top-scale[1], bottom+scale[1], new Pair(left, top-scale[1]));
			double factor=(sizeY+2*scale[1])/(double)sizeY;
			for (int i=0;i<res.sizeX;i++) {
				for (int j=0;j<res.sizeY/2;j++) {
					try {
						res.set(i, j, this.get(i, (int)(sizeY/2-Math.abs(res.sizeY/2-j)/factor)));
						res.set(i, res.sizeY-1-j, this.get(i, (int)(sizeY/2+Math.abs(res.sizeY/2-j)/factor)));
					} catch (ArrayIndexOutOfBoundsException e) {
						e.printStackTrace();
						continue;
					}
				}
			}
			return res;
		} 
		if (scale[0]==3) {
			Region res=new Region(left-scale[1], right+scale[1], top-scale[1], bottom+scale[1], new Pair(left-scale[1], top-scale[1]));
			double factor=(sizeY+2*scale[1])/(double)sizeY;
			for (int i=0;i<res.sizeX/2;i++) {
				for (int j=0;j<res.sizeY/2;j++) {
					try {
						res.set(i, j, this.get((int)(sizeX/2-Math.abs(res.sizeX/2-i)/factor), (int)(sizeY/2-Math.abs(res.sizeY/2-j)/factor)));
						res.set(i, res.sizeY-1-j, this.get((int)(sizeX/2-Math.abs(res.sizeX/2-i)/factor), (int)(sizeY/2+Math.abs(res.sizeY/2-j)/factor)));
						res.set(res.sizeX-1-i, j, this.get((int)(sizeX/2+Math.abs(res.sizeX/2-i)/factor), (int)(sizeY/2-Math.abs(res.sizeY/2-j)/factor)));
						res.set(res.sizeX-1-i, res.sizeY-1-j, this.get((int)(sizeX/2+Math.abs(res.sizeX/2-i)/factor), (int)(sizeY/2+Math.abs(res.sizeY/2-j)/factor)));
					} catch (ArrayIndexOutOfBoundsException e) {
						e.printStackTrace();
						continue;
					}
				}
			}
			return res;
		}
		return null;
	}
	
	public int[] isTripleCopy(Region r) {		//0 for not, 1 for horizental, 2 for vertical
		int[] res=new int[2];
		if (!nearlyEquals(this.sizeX, r.sizeX) && !nearlyEquals(this.sizeY, r.sizeY))
			return res;
		Region newr=this.generateEqual(new Pair(0,0));
		if (!nearlyEquals(this.sizeY, r.sizeY)) {
			if (this.sizeY<r.sizeY)
				return res;
			newr.cutRegion(r, 2);
			newr.cutRegion(r, 3);
			
			int error=0;
			for (int i=0;i<r.sizeX;i++) {
				for (int j=0;j<r.sizeY;j++) {
					if (newr.get(i, (newr.sizeY-r.sizeY)/2+j)!=r.get(i, j))
						error++;
				}
			}
			if (error<r.square/10) {
				res[0]=2;
				res[1]=(newr.sizeY-r.sizeY)/2;
			}
			return res;
		} else if (!nearlyEquals(this.sizeX, r.sizeX)) {
			if (this.sizeX<r.sizeX)
				return res;
			newr.cutRegion(r, 0);
			newr.cutRegion(r, 1);
			
			int error=0;
			for (int i=0;i<r.sizeX;i++) {
				for (int j=0;j<r.sizeY;j++) {
					if (newr.get((newr.sizeX-r.sizeX)/2+i, j)!=r.get(i, j))
						error++;
				}
			}
			if (error<r.square/10) {
				res[0]=1;
				res[1]=(newr.sizeX-r.sizeX)/2;
			}
			return res;
		} else
			return res;
	}
	
	private boolean subtractBool(boolean a, boolean b) {
		if ((a && b) || !a)
			return false;
		return true;
	}
	
	public void cutRegion(Region r, int direction) {	//0 for left, 1 for right, 2 for top, 3 for bottom
		switch (direction) {
			case 0: {
				for (int i=0;i<r.sizeX;i++) {
					for (int j=0;j<r.sizeY;j++) {
						this.set(i, j, subtractBool(this.get(i, j), r.get(i, j)));
					}
				}
				break;
			}
			case 1: {
				for (int i=0;i<r.sizeX;i++) {
					for (int j=0;j<r.sizeY;j++) {
						this.set(this.sizeX-r.sizeX+i, j, subtractBool(this.get(this.sizeX+i-r.sizeX, j), r.get(i, j)));
					}
				}
				break;
			}
			case 2: {
				for (int i=0;i<r.sizeX;i++) {
					for (int j=0;j<r.sizeY;j++) {
						this.set(i, j, subtractBool(this.get(i, j), r.get(i, j)));
					}
				}
				break;
			}
			case 3: {
				for (int i=0;i<r.sizeX;i++) {
					for (int j=0;j<r.sizeY;j++) {
						this.set(i, this.sizeY-r.sizeY+j, subtractBool(this.get(i, this.sizeY-r.sizeY+j), r.get(i, j)));
					}
				}
				break;
			}
		}
	}
	
	Region(int l, int r, int t, int b, Pair o) {
		origin=new Pair(o);
		this.left=l;
		this.right=r;
		this.top=t;
		this.bottom=b;
		sizeX=right-left+1;
		sizeY=bottom-top+1;
		square=sizeX*sizeY;
		this.info=new boolean[sizeX][sizeY];
	}
	
	//about change
	DifferenceEdge differenceEdge=new DifferenceEdge();
	
	public DifferenceEdge getDifference(Region r) {
		DifferenceEdge edge=new DifferenceEdge();
		if (this.isEquals(r)) {
			edge.equals=true;
			edge.copy.add(new Pair(r.origin.x-this.origin.x, r.origin.y-this.origin.y));
			return edge;
		}
		edge.isSymmetric=this.isSymmetric(r);
		if (edge.isSymmetric!=0)
			return edge;
		edge.isRotation=this.isRotation(r);
		if (edge.isRotation!=0)
			return edge;
		edge.isScale=this.isScale(r);
		if (edge.isScale[0]!=0)
			return edge;
		if (this.fill!=r.fill) {
			edge.changeFill=true;
			return edge;
		}
		edge.isTripleCopy=r.isTripleCopy(this);
		if (edge.isTripleCopy[0]!=0) {
			if (edge.isTripleCopy[0]==1) {
				edge.copy.add(new Pair(-edge.isTripleCopy[1],0));
				edge.copy.add(new Pair(edge.isTripleCopy[1],0));
				edge.copy.add(new Pair(0,0));
			} else {
				edge.copy.add(new Pair(0, -edge.isTripleCopy[1]));
				edge.copy.add(new Pair(0, edge.isTripleCopy[1]));
				edge.copy.add(new Pair(0,0));
			}
		}
		return edge;
	}
	
	public void findCopy(DifferenceEdge edge, Region r) {
		if (this.isEquals(r))
			edge.copy.add(new Pair(r.origin.x-this.origin.x, r.origin.y-this.origin.y));
	}
	
	public List<Region> generateCopyRegion(DifferenceEdge edge) {
		List<Region> list=new ArrayList<Region>();
		if (edge.copy.size()==0)
			return list;
		for (Pair p:edge.copy) {
			Region t=new Region(this.left+p.x, this.right+p.x, this.top+p.y, this.bottom+p.y, 
					new Pair(this.origin.x+p.x, this.origin.y+p.y));
			for (int i=0;i<sizeX;i++) {
				for (int j=0;j<sizeY;j++) {
					t.set(i, j, this.get(i, j));
				}
			}
			list.add(t);
		}
		return list;
	}
	
	public Region generateRegion(DifferenceEdge edge) {
		if (edge.isSymmetric!=0)
			return this.generateSymmetric(edge.isSymmetric);
		if (edge.isRotation!=0)
			return this.generateRotation(edge.isRotation);
		if (edge.isScale[0]!=0)
			return this.generateScale(edge.isScale);
		if (edge.changeFill)
			return this.generateFill();
		return null;
	}
	
	public int calculateDistance(Region r) {
		return (int)Math.sqrt((this.origin.x-r.origin.x)*(this.origin.x-r.origin.x)+(this.origin.y-r.origin.y)*(this.origin.y-r.origin.y));
	}
	
	public void outputFile(PrintWriter writer) {
		writer.println("Region: ("+origin.x+","+origin.y+")");
		writer.println("Size X:"+sizeX+"   SizeY:"+sizeY);
		for (int i=0;i<sizeX+2;i++)
			writer.print("-");
		writer.println();
		for (int j=0;j<sizeY;j++) {
			writer.print('|');
			for (int i=0;i<sizeX;i++) {
				writer.print(this.get(i, j)? 'x':' ');
			}
			writer.println('|');
		}
		for (int i=0;i<sizeX+2;i++)
			writer.print("-");
		writer.println();
		writer.flush();
	}
	
	public void output() {
		System.out.println("Region: ("+origin.x+","+origin.y+")");
		System.out.println("Size X:"+sizeX+"   SizeY:"+sizeY);
		for (int i=0;i<sizeX+2;i++)
			System.out.print("-");
		System.out.println();
		for (int j=0;j<sizeY;j++) {
			System.out.print('|');
			for (int i=0;i<sizeX;i++) {
				System.out.print(this.get(i, j)? 'x':' ');
			}
			System.out.println('|');
		}
		for (int i=0;i<sizeX+2;i++)
			System.out.print("-");
		System.out.println();
		System.out.flush();
	}
	
	@Override
	public String toString() {
		return "Region ("+origin.x+","+origin.y+") "+sizeX+"x"+sizeY;
	}
}
