package ravensproject;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;

import javax.imageio.ImageIO;

public class Figure {
	
	//used to represent figures
	String name;
	public int sizeX, sizeY;
	private int[][] figure;
	Figure rightDifference=null;
	Figure downDifference=null;
	
	public int[][] getFigure() {
		return figure;
	}
	
	public int get(int x, int y) {
		return figure[x][y];
	}
	
	public void set(int x, int y, int v) {
		figure[x][y]=v;
	}
	
	@Override
	public String toString() {
		return name;
	}
	
	Figure(int x, int y) {
		sizeX=x;
		sizeY=y;
		figure=new int[x][y];
		name="tmp";
	}
	
	Figure(String name, String fileName) {
		this.name=name;
		try {
			BufferedImage image=ImageIO.read(new File(fileName));
			figure=new int[image.getWidth()][image.getHeight()];
			sizeX=image.getWidth();
			sizeY=image.getHeight();
			for (int i=0;i<figure.length;i++) {
				for (int j=0;j<figure[0].length;j++) {
					figure[i][j]= image.getRGB(i, j)==0xFF000000? 1:0;
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	Figure(Figure r) {
		this.sizeX=r.sizeX;
		this.sizeY=r.sizeY;
		this.name="tmp";
		figure=new int[sizeX][sizeY];
		for (int i=0;i<sizeX;i++) {
			for (int j=0;j<sizeY;j++) {
				figure[i][j]=r.get(i, j);
			}
		}
	}
	
	public static Figure getBase(Figure r) {
		Figure res=new Figure(r.sizeX, r.sizeY);
		return res;
	}
	
	@Override
	public boolean equals(Object o) {
		Figure r=(Figure)o;
		if (sizeX!=r.sizeX || sizeY!=r.sizeY)
			return false;
		for (int i=0;i<sizeX;i++) {
			for (int j=0;j<sizeY;j++) {
				if (figure[i][j]!=r.get(i, j))
					return false;
			}
		}
		return true;
	}
	
	public boolean isSymmetric(Figure r, boolean isVertical) {
		int error=0;
		if (sizeX!=r.sizeX || sizeY!=r.sizeY)
			return false;
		for (int i=0;i<sizeX;i++) {
			for (int j=0;j<sizeY;j++) {
				if (!isVertical && figure[i][j]!=r.get(sizeX-1-i, j))
					error++;
				if (isVertical && figure[i][j]!=r.get(i, sizeY-1-j))
					error++;
			}
		}
		
		if (error>sizeX*sizeY/100)
			return false;
		return true;
	}
	
	public int isRotation(Figure r) {
		int[] degrees=new int[3];

		System.out.println();
		for (int j=0;j<sizeY;j++) {
			for (int i=0;i<sizeX;i++) {
				if (this.get(i, j)==r.get(r.sizeX-j-1, i))
					degrees[0]++;
				if (this.get(i, j)==r.get(sizeX-i-1, sizeY-j-1))
					degrees[1]++;
				if (this.get(i, j)==r.get(j, r.sizeY-i-1))
					degrees[2]++;
			}
		}

		int max=-1, index=-1;
		for (int i=0;i<3;i++) {
			if (degrees[i]>max) {
				max=degrees[i];
				index=i;
			}
		}
		if (max>this.sizeX*this.sizeY*99/100)
			return (index+1)*90;
		return 0;
	}
	
	public int getSimilarity(Figure r) {
		int res=0;
		for (int i=0;i<sizeX;i++) {
			for (int j=0;j<sizeY;j++) {
				if (figure[i][j]!=r.get(i, j))
					res++;
			}
		}
		return res;
	}
	
	int left=Integer.MAX_VALUE, right=-1, top=Integer.MAX_VALUE, bottom=-1;
	public void cut() {
		for (int i=0;i<sizeX;i++) {
			for (int j=0;j<sizeY;j++) {
				if (this.get(i, j)!=0) {
					if (i<left)
						left=i;
					if (i>right)
						right=i;
					if (j<top)
						top=j;
					if (j>bottom)
						bottom=j;
				}
			}
		}
	}
	
	public int getRegionSimilarity(Figure r) {
		int res=0;
		int sizex=this.right-this.left+1;
		int sizey=this.bottom-this.top+1;
		int rsizex=r.right-r.left+1;
		int rsizey=r.bottom-r.top+1;
		for (int i=0;i<Math.min(sizex, rsizex);i++) {
			for (int j=0;j<Math.min(sizey, rsizey);j++) {
				if (this.get(i+this.left, j+this.top)!=r.get(i+r.left, j+r.top))
					res++;
			}
		}
		res+=Math.abs(sizex*sizey-rsizex*rsizey);
//		if (res>Math.max(sizex*sizey, rsizex*rsizey)/5)
//			res=Integer.MAX_VALUE;
		return res;
	}
	
	public Figure add(Figure r) {
		if (r.equals) {
			return new Figure(this);
		}
		if (r.isHorizentalSymmetric) {
			Figure res=Figure.getBase(r);
			for (int i=0;i<sizeX;i++) {
				for (int j=0;j<sizeY;j++) {
					res.set(i,j,this.get(sizeX-1-i, j));
				}
			}
			return res;
		}
		if (r.isVerticalSymmetric) {
			Figure res=Figure.getBase(r);
			for (int i=0;i<sizeX;i++) {
				for (int j=0;j<sizeY;j++) {
					res.set(i,j,this.get(i, sizeY-1-j));
				}
			}
			return res;
		}
		if (r.rotate!=0) {
			Figure res=Figure.getBase(r);
			if (r.rotate==90) {
				for (int i=0;i<sizeX;i++) {
					for (int j=0;j<sizeY;j++) {
						res.set(sizeX-1-j, i, this.get(i, j));
					}
				}
			} else if (r.rotate==180) {
				for (int i=0;i<sizeX;i++) {
					for (int j=0;j<sizeY;j++) {
						res.set(sizeX-1-i, sizeY-1-j, this.get(i, j));
					}
				}
			} else if (r.rotate==270) {
				for (int i=0;i<sizeX;i++) {
					for (int j=0;j<sizeY;j++) {
						res.set(j, sizeY-1-i, this.get(i, j));
					}
				}
			}
			return res;
		}
//		int left=Integer.MAX_VALUE, right=-1, top=Integer.MAX_VALUE, bottom=-1;
//		for (int i=0;i<sizeX;i++) {
//			for (int j=0;j<sizeY;j++) {
//				if (figure[i][j]==1) {
//					if (i<left)
//						left=i;
//					else if (i>right)
//						right=i;
//					if (j<top)
//						top=j;
//					else if (j>bottom)
//						bottom=j;
//				}
//			}
//		}
		Figure res=Figure.getBase(this);
		if (sizeX!=r.sizeX || sizeY!=r.sizeY)
			System.out.println("Size dismatch!");
		
		for (int i=0;i<sizeX;i++) {
			for (int j=0;j<sizeY;j++) {
				res.set(i, j, figure[i][j]+r.get(i, j));
				if (res.get(i, j)>1)
					res.set(i,j,1);
				if (res.get(i, j)<-1)
					res.set(i,j,-1);
			}
		}
		return res;
	}
	
	public Figure subtract(Figure r) {
		Figure f=Figure.getBase(this);
		if (r==null)
			return null;
		if (this.equals(r)) {
			f.equals=true;
			return f;
		}
		if (this.isSymmetric(r, false)) {
			f.isHorizentalSymmetric=true;
			return f;
		}
		if (this.isSymmetric(r, true)) {
			f.isVerticalSymmetric=true;
			return f;
		}
		int degree=r.isRotation(this);
		if (degree!=0) {
			f.rotate=degree;
			return f;
		}

		if (sizeX!=r.sizeX || sizeY!=r.sizeY)
			System.out.println("Size dismatch!");
		for (int i=0;i<sizeX;i++) {
			for (int j=0;j<sizeY;j++) {
				f.set(i, j, figure[i][j]-r.get(i, j));
			}
		}
		return f;
	}
	
	public void findDifference(Figure r, boolean right) {
		if (right) {
			this.rightDifference=r.subtract(this);
		} else {
			this.downDifference=r.subtract(this);
		}
	}	
	
	//used as difference edges
	Figure downChange=null;
	Figure rightChange=null;
	boolean equals=false;
	boolean isVerticalSymmetric=false;
	boolean isHorizentalSymmetric=false;
	int rotate=0;
	
	public void findChange(Figure r, boolean right) {
		if (r==null)
			return ;
		if (right) {
			this.rightChange=r.subtract(this);
		}
		else {
			this.downChange=r.subtract(this);
		}
	}
	
	//used as change edges
	public static Figure generateDifferenceEdge(Figure baseEdge, Figure baseChangeEdge, Figure changeEdge1, Figure changeEdge2) {
		if (changeEdge1==null || changeEdge2==null || baseChangeEdge==null)
			return baseEdge;
		return baseEdge.add(baseChangeEdge.add(changeEdge2.subtract(changeEdge1)));
	}
	
	public static Figure generateAnswer(Figure baseFigure, Figure differenceEdge) {
		return baseFigure.add(differenceEdge);
	}
	
	public void output() {
		System.out.println(name);
		for (int i=0;i<sizeY;i++) {
			for (int j=0;j<sizeX;j++) {
				System.out.print(figure[j][i]==0? " ":"x");
			}
			System.out.println();
		}
		System.out.println();
	}
	
	//object related
	List<Region> objects=new ArrayList<Region>();
	int freeObjects=0;	//used to link to find change edge
	int prevFreeObjects=0;
	int convertFreeObject=0;	//used to link to apply change edge
	int prevConvertFreeObject=0;
	public void getRegions(int value, PrintWriter writer) {
		int[][] groups=new int[sizeX][sizeY];
		DisjointSet set=new DisjointSet();
		for (int i=0;i<sizeX;i++) {
			for (int j=0;j<sizeY;j++) {
				if (figure[i][j]!=value)
					continue;
				if ((i==0 || groups[i-1][j]==0) && (j==0 || groups[i][j-1]==0)) {
					groups[i][j]=set.add();
				} else if (i==0 || groups[i-1][j]==0){
					groups[i][j]=groups[i][j-1];
				} else if (j==0 || groups[i][j-1]==0) {
					groups[i][j]=groups[i-1][j];
				} else {
					if (groups[i-1][j]!=groups[i][j-1]) {
						set.union(groups[i-1][j], groups[i][j-1]);
					}
					groups[i][j]=groups[i-1][j];
				}	
			}
		}
		
		int n=set.getNumOfTree();
		for (int k=0;k<n;k++) {
			Region r=new Region();
			for (int i=0;i<sizeX;i++) {
				for (int j=0;j<sizeY;j++) {
					if (groups[i][j]==0)
						continue;
					groups[i][j]=set.find(groups[i][j]);
					if (groups[i][j]!=set.roots.get(k))
						continue;
					if (i<r.left)
						r.left=i;
					if (i>r.right)
						r.right=i;
					if (j<r.top)
						r.top=j;
					if (j>r.bottom)
						r.bottom=j;
				}
			}
			r.fillParameters(groups, set.roots.get(k));
			if (r.square>20) {
				objects.add(r);
				freeObjects++;
				prevFreeObjects++;
			}
		}
//		for (Region r:this.objects)
//			r.outputFile(writer);
	}
	
	public void getRegions(PrintWriter writer) {
		this.getRegions(1, writer);
	}
	
	public void link(Figure f, boolean isConverting) {
		this.freeObjects=this.objects.size();
		this.convertFreeObject=this.objects.size();
		f.prevConvertFreeObject=f.objects.size();
		f.prevFreeObjects=f.objects.size();
		if (this.objects.size()<=1 || f.objects.size()<=1) {
			return ;
		}
		for (Region r:this.objects) {
			for (Region s:f.objects) {
				if (r.isSame(s) && r.isEquals(s)) {
					if (!isConverting) {
						r.compareObject=s;
						r.findCopy(r.differenceEdge, s);
						this.freeObjects--;
						f.prevFreeObjects--;
						break;
					} else {
						r.convertObject=s;
						s.differenceEdge=new DifferenceEdge(r.differenceEdge);
						this.convertFreeObject--;
						f.prevConvertFreeObject--;
						break;
					}
				}
			}
		}
		
		if (!isConverting) {
			if (this.freeObjects==0)
				return ;
			if (this.freeObjects>1 || f.prevFreeObjects>1) {
				for (Region r:this.objects) {
					if (r.compareObject!=null)
						continue;
					int minDistance=Integer.MAX_VALUE;
					Region min=null;
					for (Region s:f.objects) {
						if (s.prevCompareObject!=null)
							continue;
						int d=r.calculateDistance(s);
						if (d<minDistance) {
							minDistance=d;
							min=s;
						}
					}
					r.differenceEdge=r.getDifference(min);
				}
				return ;
			}
			if (this.freeObjects==1 && f.prevFreeObjects>1) {
				for (Region r:this.objects) {
					if (r.compareObject!=null)
						continue;
					for (Region s:f.objects) {
						if (s.prevCompareObject!=null)
							continue;
						r.findCopy(r.differenceEdge, s);	
					}
					return ;
				}
				
			}
			for (Region r:this.objects) {
				if (r.compareObject!=null)
					continue;
				for (Region s:f.objects) {
					if (s.compareObject!=null)
						continue;
					r.compareObject=s;
					r.differenceEdge=r.getDifference(s);
					break;
				}
			}
		} else {
			if (this.convertFreeObject==0)
				return ;
			if (f.prevConvertFreeObject>1 && this.convertFreeObject>1) {
				for (Region r:this.objects) {
					if (r.convertObject!=null)
						continue;
					int minDistance=Integer.MAX_VALUE;
					Region min=null;
					for (Region s:f.objects) {
						if (s.prevConvertObject!=null)
							continue;
						int d=r.calculateDistance(s);
						if (d<minDistance) {
							minDistance=d;
							min=s;
						}
					}
					min.differenceEdge=new DifferenceEdge(r.differenceEdge);
				}
				return ;
			}
			if (this.convertFreeObject==1 && f.prevConvertFreeObject>1) {
				for (Region r:this.objects) {
					if (r.convertObject!=null)
						continue;
					for (Region s:f.objects) {
						if (s.prevConvertObject!=null)
							continue;
						s.differenceEdge=new DifferenceEdge(r.differenceEdge);
					}
					return ;
				}
				
			}
			for (Region r:this.objects) {
				if (r.convertObject!=null)
					continue;
				for (Region s:f.objects) {
					if (s.prevConvertObject!=null)
						continue;
					r.convertObject=s;
					s.differenceEdge=new DifferenceEdge(r.differenceEdge);
					break;
				}
			}
		}
	}
	
	public void getDifferenceEdgeUsingRegions(Figure r) {
		if (this.objects.size()==1 && r.objects.size()==1) {
			Region lr=this.objects.get(0);
			Region rr=r.objects.get(0);
			lr.compareObject=rr;
			rr.prevCompareObject=lr;
			lr.differenceEdge=lr.getDifference(rr);
			return ;
		}
		if (this.objects.size()==1) {
			Region lr=this.objects.get(0);
			for (Region region:r.objects)
				lr.findCopy(lr.differenceEdge, region);
			return ;
		}
		this.link(r, false);
	}
	
	public void getConvertDifferenceEdgeUsingRegions(Figure r) {
		if (this.objects.size()==1 && r.objects.size()==1) {
			Region lr=this.objects.get(0);
			Region rr=r.objects.get(0);
			lr.convertObject=rr;
			rr.prevConvertObject=lr;
			rr.differenceEdge=new DifferenceEdge(lr.differenceEdge);
			return ;
		}
		if (this.objects.size()==1) {
			Region lr=this.objects.get(0);
			for (Region region:r.objects) {
//				if (lr.isEquals(region) || lr.isScale(region)[0]!=0) {
					region.prevConvertObject=lr;
					region.differenceEdge=new DifferenceEdge(lr.differenceEdge);
//				}
			}
			return ;
		}
		this.link(r, true);
	}
	
	public void putRegion(Region r, PrintWriter writer) {
		if (r==null)
			return ;
		r.outputFile(writer);
		for (int i=r.left;i<=r.right;i++) {
			for (int j=r.top;j<=r.bottom;j++) {
				try {
					this.set(i, j, this.get(i, j) + (r.get(i-r.left, j-r.top)? 1:0));
					if (this.get(i,j)>1)
						this.set(i, j, 1);
				} catch (ArrayIndexOutOfBoundsException e) {
					e.printStackTrace();
					continue;
				}
			}
		}
	}
	
	public void putRegions(List<Region> list, PrintWriter writer) {
		for (Region r:list)
			this.putRegion(r, writer);
	}
	
	public Figure applyChangeEdgesUsingRegion(PrintWriter writer) {
		Figure res=Figure.getBase(this);
		for (Region r:this.objects) {
			res.putRegions(r.generateCopyRegion(r.differenceEdge), writer);
			res.putRegion(r.generateRegion(r.differenceEdge), writer);
		}
		return res;
	}
	
	public void outputFile(PrintWriter writer) {
		writer.println(name);
		for (int i=0;i<sizeY;i++) {
			for (int j=0;j<sizeX;j++) {
				writer.print(figure[j][i]==0? " ":"x");
			}
			writer.println();
		}
		writer.println();
		writer.flush();
	}
}
