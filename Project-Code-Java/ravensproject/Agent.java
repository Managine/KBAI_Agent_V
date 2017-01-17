package ravensproject;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.Map;

// Uncomment these lines to access image processing.
//import java.awt.Image;
//import java.io.File;
//import javax.imageio.ImageIO;

/**
 * Your Agent for solving Raven's Progressive Matrices. You MUST modify this
 * file.
 * 
 * You may also create and submit new files in addition to modifying this file.
 * 
 * Make sure your file retains methods with the signatures:
 * public Agent()
 * public char Solve(RavensProblem problem)
 * 
 * These methods will be necessary for the project's main method to run.
 * 
 */
public class Agent {
    /**
     * The default constructor for your Agent. Make sure to execute any
     * processing necessary before your Agent starts solving problems here.
     * 
     * Do not add any variables to this signature; they will not be used by
     * main().
     * 
     */
    public Agent() {
        
    }
    /**
     * The primary method for solving incoming Raven's Progressive Matrices.
     * For each problem, your Agent's Solve() method will be called. At the
     * conclusion of Solve(), your Agent should return an int representing its
     * answer to the question: 1, 2, 3, 4, 5, or 6. Strings of these ints 
     * are also the Names of the individual RavensFigures, obtained through
     * RavensFigure.getName(). Return a negative number to skip a problem.
     * 
     * Make sure to return your answer *as an integer* at the end of Solve().
     * Returning your answer as a string may cause your program to crash.
     * @param problem the RavensProblem your agent should solve
     * @return your Agent's answer to this problem
     */
    public int Solve(RavensProblem problem) {
//    	if (!problem.getName().endsWith("09"))
//    		return -1;
    	try {
	    	PrintWriter writer=new PrintWriter(new BufferedWriter(new FileWriter(problem.getName()+".txt")));
	    	Map<String, Figure> map=new HashMap<String, Figure>();
	    	for (String key : problem.getFigures().keySet()) {
	    		map.put(key, new Figure(key, problem.getFigures().get(key).getVisual()));
	    	}
	    	
	    	for (String key : map.keySet()) {
	    		if (key.charAt(0)>='A' && key.charAt(0)<='Z')
	    			map.get(key).outputFile(writer);
	    	}
	    	
	    	for (String key : map.keySet()) {
	    		if (key.charAt(0)>='0' && key.charAt(0)<='9')
	    			map.get(key).outputFile(writer);
	    	}
	    	
	    	if (problem.getProblemType().equals("2x2")) {
	    		map.get("A").findDifference(map.get("B"), true);
	    		Figure ans = Figure.generateAnswer(map.get("C"), Figure.generateDifferenceEdge(map.get("A").rightDifference, null, null, null));
	    		ans.outputFile(writer);
	    		int minDif=Integer.MAX_VALUE;
	    		int answer=-1;
	    		for (String key:map.keySet()) {
	    			if (key.charAt(0)>='0' && key.charAt(0)<='9') {
	    				int dif=map.get(key).getSimilarity(ans);
	    				if (dif<50)
	    					return key.charAt(0)-'0';
	    				if (dif<minDif) {
	    					minDif=dif;
	    					answer=key.charAt(0)-'0';
	    				}
	    			}
	    		}
	    		
	    		map.get("A").findDifference(map.get("C"), false);
	    		ans = Figure.generateAnswer(map.get("B"), Figure.generateDifferenceEdge(map.get("A").downDifference, null, null, null));
	    		ans.outputFile(writer);
	    		for (String key:map.keySet()) {
	    			if (key.charAt(0)>='0' && key.charAt(0)<='9') {
	    				int dif=map.get(key).getSimilarity(ans);
	    				if (dif<50)
	    					return key.charAt(0)-'0';
	    				if (dif<minDif) {
	    					minDif=dif;
	    					answer=key.charAt(0)-'0';
	    				}
	    			}
	    		}
	    		
	    		if (minDif<map.get("A").sizeX*map.get("A").sizeY/25-24)
	    			return answer;
	    		
	    		//use regions
	    		for (String key:map.keySet()) {
	    			if (key.charAt(0)>='0' && key.charAt(0)<='9') {
	    				map.get(key).cut();
	    			}
	    		}
	    		int minRegionDif=Integer.MAX_VALUE;
	    		int regionAnswer=-1;
	    		
	    		map.get("A").getRegions(writer);
	    		map.get("B").getRegions(writer);
	    		map.get("C").getRegions(writer);
	    		
	    		map.get("A").getDifferenceEdgeUsingRegions(map.get("B"));
	    		map.get("A").getConvertDifferenceEdgeUsingRegions(map.get("C"));
	    		ans=map.get("C").applyChangeEdgesUsingRegion(writer);
	    		ans.cut();
	    		ans.outputFile(writer);
	    		for (String key:map.keySet()) {
	    			if (key.charAt(0)>='0' && key.charAt(0)<='9') {
	    				int dif=map.get(key).getSimilarity(ans);
	    				int rdif=map.get(key).getRegionSimilarity(ans);
	    				if (rdif<50)
	    					return key.charAt(0)-'0';
	    				if (dif<50)
	    					return key.charAt(0)-'0';
	    				if (dif<minDif) {
	    					minDif=dif;
	    					answer=key.charAt(0)-'0';
	    				}
	    				if (rdif<minRegionDif) {
	    					regionAnswer=key.charAt(0)-'0';
	    					minRegionDif=rdif;
	    				}
	    			}
	    		}
	    		
	    		if (minDif<1009)
	    			return answer;

	    		//down side
	    		map.get("A").objects.clear();
	    		map.get("B").objects.clear();
	    		map.get("C").objects.clear();
	    		map.get("A").getRegions(writer);
	    		map.get("B").getRegions(writer);
	    		map.get("C").getRegions(writer);
	    		
	    		map.get("A").getDifferenceEdgeUsingRegions(map.get("C"));
	    		map.get("A").getConvertDifferenceEdgeUsingRegions(map.get("B"));
	    		ans=map.get("B").applyChangeEdgesUsingRegion(writer);
	    		ans.cut();
	    		ans.outputFile(writer);
	    		for (String key:map.keySet()) {
	    			if (key.charAt(0)>='0' && key.charAt(0)<='9') {
		    			int dif=map.get(key).getSimilarity(ans);
	    				int rdif=map.get(key).getRegionSimilarity(ans);
	    				if (rdif<50)
	    					return key.charAt(0)-'0';
	    				if (dif<50)
	    					return key.charAt(0)-'0';
	    				if (dif<minDif) {
	    					minDif=dif;
	    					answer=key.charAt(0)-'0';
	    				}
	    				if (rdif<minRegionDif) {
	    					regionAnswer=key.charAt(0)-'0';
	    					minRegionDif=rdif;
	    				}
	    			}
	    		}
	    		
	    		if (minDif>map.get("A").sizeX*map.get("A").sizeY/20)
	    			return -1;
	    		if (regionAnswer!=answer)
	    			return regionAnswer;
	    		return answer;
	    		
	    	} else {
	    		//right side
	    		map.get("A").findDifference(map.get("B"), true);
	    		map.get("B").findDifference(map.get("C"), true);
	    		map.get("D").findDifference(map.get("E"), true);
	    		map.get("E").findDifference(map.get("F"), true);
	    		map.get("G").findDifference(map.get("H"), true);
	    		map.get("A").rightDifference.findChange(map.get("D").rightDifference, false);
	    		map.get("D").rightDifference.findChange(map.get("G").rightDifference, false);
	    		map.get("B").rightDifference.findChange(map.get("E").rightDifference, false);
	    		int minDif=Integer.MAX_VALUE;
	    		int answer=-1;
	    		Figure ans=Figure.generateAnswer(map.get("H"), Figure.generateDifferenceEdge(map.get("E").rightDifference, 
	    				map.get("B").rightDifference.downChange, 
	    				map.get("A").rightDifference.downChange,
	    				map.get("D").rightDifference.downChange));
	    		ans.outputFile(writer);
	    		for (String key:map.keySet()) {
	    			if (key.charAt(0)>='0' && key.charAt(0)<='9') {
	    				int dif=map.get(key).getSimilarity(ans);
	    				if (dif<50)
	    					return key.charAt(0)-'0';
	    				if (dif<minDif) {
	    					minDif=dif;
	    					answer=key.charAt(0)-'0';
	    				}
	    			}
	    		}
	    		ans=Figure.generateAnswer(map.get("H"), Figure.generateDifferenceEdge(map.get("E").rightDifference, 
	    				map.get("D").rightDifference.downChange, 
	    				map.get("A").rightDifference.downChange,
	    				map.get("B").rightDifference.downChange));
	    		ans.outputFile(writer);
	    		for (String key:map.keySet()) {
	    			if (key.charAt(0)>='0' && key.charAt(0)<='9') {
	    				int dif=map.get(key).getSimilarity(ans);
	    				if (dif<50)
	    					return key.charAt(0)-'0';
	    				if (dif<minDif) {
	    					minDif=dif;
	    					answer=key.charAt(0)-'0';
	    				}
	    			}
	    		}
	    		
	    		//downside
	    		map.get("A").findDifference(map.get("D"), false);
	    		map.get("B").findDifference(map.get("E"), false);
	    		map.get("C").findDifference(map.get("F"), false);
	    		map.get("D").findDifference(map.get("G"), false);
	    		map.get("E").findDifference(map.get("H"), false);
	    		map.get("A").downDifference.findChange(map.get("B").downDifference, true);
	    		map.get("B").downDifference.findChange(map.get("C").downDifference, true);
	    		map.get("D").downDifference.findChange(map.get("E").downDifference, true);
	    		ans=Figure.generateAnswer(map.get("F"), Figure.generateDifferenceEdge(map.get("E").downDifference, 
	    				map.get("D").downDifference.rightChange, 
	    				map.get("A").downDifference.rightChange,
	    				map.get("B").downDifference.rightChange));
	    		ans.outputFile(writer);
	    		for (String key:map.keySet()) {
	    			if (key.charAt(0)>='0' && key.charAt(0)<='9') {
	    				int dif=map.get(key).getSimilarity(ans);
	    				if (dif<50)
	    					return key.charAt(0)-'0';
	    				if (dif<minDif) {
	    					minDif=dif;
	    					answer=key.charAt(0)-'0';
	    				}
	    			}
	    		}
	    		ans=Figure.generateAnswer(map.get("F"), Figure.generateDifferenceEdge(map.get("E").downDifference, 
	    				map.get("B").downDifference.rightChange, 
	    				map.get("A").downDifference.rightChange,
	    				map.get("D").downDifference.rightChange));
	    		ans.outputFile(writer);
	    		for (String key:map.keySet()) {
	    			if (key.charAt(0)>='0' && key.charAt(0)<='9') {
	    				int dif=map.get(key).getSimilarity(ans);
	    				if (dif<50)
	    					return key.charAt(0)-'0';
	    				if (dif<minDif) {
	    					minDif=dif;
	    					answer=key.charAt(0)-'0';
	    				}
	    			}
	    		}
	    		
	    		//convert to 2x2
	    		//right side
	    		map.get("A").findDifference(map.get("C"), false);
	    		ans = Figure.generateAnswer(map.get("G"), Figure.generateDifferenceEdge(map.get("A").downDifference, null, null, null));
	    		ans.outputFile(writer);
	    		for (String key:map.keySet()) {
	    			if (key.charAt(0)>='0' && key.charAt(0)<='9') {
	    				int dif=map.get(key).getSimilarity(ans);
	    				if (dif<50)
	    					return key.charAt(0)-'0';
	    				if (dif<minDif) {
	    					minDif=dif;
	    					answer=key.charAt(0)-'0';
	    				}
	    			}
	    		}
	    		
	    		//down side
	    		map.get("A").findDifference(map.get("G"), false);
	    		ans = Figure.generateAnswer(map.get("C"), Figure.generateDifferenceEdge(map.get("A").downDifference, null, null, null));
	    		ans.outputFile(writer);
	    		for (String key:map.keySet()) {
	    			if (key.charAt(0)>='0' && key.charAt(0)<='9') {
	    				int dif=map.get(key).getSimilarity(ans);
	    				if (dif<50)
	    					return key.charAt(0)-'0';
	    				if (dif<minDif) {
	    					minDif=dif;
	    					answer=key.charAt(0)-'0';
	    				}
	    			}
	    		}
	    		
	    		if (minDif<map.get("A").sizeX*map.get("A").sizeY/50)
	    			return answer;
	    		
	    		//use regions
	    		//right side
	    		for (String key:map.keySet()) {
	    			if (key.charAt(0)>='0' && key.charAt(0)<='9') {
	    				map.get(key).cut();
	    			}
	    		}
	    		int minRegionDif=Integer.MAX_VALUE;
	    		int regionAnswer=-1;
	    		
	    		map.get("A").getRegions(writer);
	    		map.get("C").getRegions(writer);
	    		map.get("G").getRegions(writer);
	    		
	    		map.get("A").getDifferenceEdgeUsingRegions(map.get("C"));
	    		map.get("A").getConvertDifferenceEdgeUsingRegions(map.get("G"));
	    		ans=map.get("G").applyChangeEdgesUsingRegion(writer);
	    		ans.cut();
	    		ans.outputFile(writer);
	    		for (String key:map.keySet()) {
	    			if (key.charAt(0)>='0' && key.charAt(0)<='9') {
	    				int dif=map.get(key).getSimilarity(ans);
	    				int rdif=map.get(key).getRegionSimilarity(ans);
	    				if (rdif<50)
	    					return key.charAt(0)-'0';
	    				if (dif<50)
	    					return key.charAt(0)-'0';
	    				if (dif<minDif) {
	    					minDif=dif;
	    					answer=key.charAt(0)-'0';
	    				}
	    				if (rdif<minRegionDif) {
	    					regionAnswer=key.charAt(0)-'0';
	    					minRegionDif=rdif;
	    				}
	    			}
	    		}
//	    		if (minDif<map.get("A").sizeX*map.get("A").sizeY/20)
//	    			return answer;
//	    		
	    		//down side
	    		map.get("A").objects.clear();
	    		map.get("C").objects.clear();
	    		map.get("G").objects.clear();
	    		map.get("A").getRegions(writer);
	    		map.get("C").getRegions(writer);
	    		map.get("G").getRegions(writer);
	    		
	    		map.get("A").getDifferenceEdgeUsingRegions(map.get("G"));
	    		map.get("A").getConvertDifferenceEdgeUsingRegions(map.get("C"));
	    		ans=map.get("C").applyChangeEdgesUsingRegion(writer);
	    		ans.cut();
	    		ans.outputFile(writer);
	    		for (String key:map.keySet()) {
	    			if (key.charAt(0)>='0' && key.charAt(0)<='9') {
		    			int dif=map.get(key).getSimilarity(ans);
	    				int rdif=map.get(key).getRegionSimilarity(ans);
	    				if (rdif<50)
	    					return key.charAt(0)-'0';
	    				if (dif<50)
	    					return key.charAt(0)-'0';
	    				if (dif<minDif) {
	    					minDif=dif;
	    					answer=key.charAt(0)-'0';
	    				}
	    				if (rdif<minRegionDif) {
	    					regionAnswer=key.charAt(0)-'0';
	    					minRegionDif=rdif;
	    				}
	    			}
	    		}
	    		
	    		if (minDif>map.get("A").sizeX*map.get("A").sizeY/20)
	    			return -1;
	    		if (regionAnswer!=answer)
	    			return regionAnswer;
	    		return answer;
	    	}
    	} catch (IOException e) {
    		e.printStackTrace();
    		return -1;
    	}
    }
}
