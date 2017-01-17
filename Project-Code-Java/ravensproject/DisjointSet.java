package ravensproject;

import java.util.ArrayList;
import java.util.List;
import java.util.Stack;

public class DisjointSet {
	int[] parent;
	int size;
	int numOfTree;
	List<Integer> roots=new ArrayList<Integer>();
	
	DisjointSet() {
		parent=new int[10];
		size=0;
		numOfTree=0;
	}
	
	public int add() {
		if (size==parent.length) {
			int[] t=new int[size*2];
			for (int i=0;i<size;i++) {
				t[i]=parent[i];
			}
			parent=t;
		}
		parent[size]=size;
		size++;
		numOfTree++;
		return size;
	}
	
	public int getNumOfTree() {
		int r=0;
		roots.clear();
		for (int i=0;i<size;i++) {
			if (parent[i]==i) {
				r++;
				roots.add(i+1);
			}
		}
		return r;
	}
	
	public int find(int value) {
		int v=value-1;
		Stack<Integer> stack=new Stack<Integer>();
		while (parent[v]!=v) {
			stack.push(v);
			v=parent[v];
		}
		int res=v;
		while (!stack.isEmpty()) {
			v=stack.pop();
			parent[v]=res;
		}
		return res+1;
	}
	
	public void union(int l, int r) {
		int lp=find(l);
		int rp=find(r);
		if (lp==rp)
			return ;
		parent[rp-1]=lp-1;
		numOfTree--;
	}
}
