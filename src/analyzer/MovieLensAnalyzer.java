package analyzer;

import java.io.File;
import java.util.Map;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Scanner;

import data.Movie;
import data.Reviewer;
import graph.Graph;
import graph.GraphAlgorithms;
import util.DataLoader; 


public class MovieLensAnalyzer {
	
	public static final int THRESHOLD = 10; // TODO: do javadocs
	public static final int RATINGS_ARRAY_SIZE = 11; // TODO: do javadocs

	
	public static void main(String[] args){
		
		// Your program should take two command-line arguments: 
		// 1. A ratings file
		// 2. A movies file with information on each movie e.g. the title and genres
		/*
		if(args.length != 2){
			System.err.println("Usage: java MovieLensAnalyzer [ratings_file] [movie_title_file]");
			System.exit(-1);
		}*/
		
		
		// FILL IN THE REST OF YOUR PROGRAM
		String ratingsString = "./src/ml-latest-small/ratings.csv";
		String moviesString = "./src/ml-latest-small/movies.csv";
		
		DataLoader movieLensDataLoader = new DataLoader();
		
		movieLensDataLoader.loadData(moviesString, ratingsString);
		
		
		HashMap<Integer, Movie> movies = (HashMap) movieLensDataLoader.getMovies();
		HashMap<Integer, Reviewer> ratings = (HashMap) movieLensDataLoader.getReviewers();
		
		// TODO: turn into StringBuilder object
		System.out.println("=========Welcome to MovieLens Analyzer=========");
		
		System.out.println("The Files being analyzed are:\n" + ratingsString + "\n" + moviesString + "\n");
		System.out.println("There are 3 choices for defining adjacency");
        
		// TODO: scanner sucks - do stuff with try catch blocks nerd
		Scanner sc = new Scanner(System.in);  
        System.out.println("[Option 1] u and v are adjacent if both movies have 50 reviewers that gave the same rating.");
        System.out.println("[Option 2] u and v are adjacent if the same 11 users watched both movies (regardless of rating).");
        System.out.println("[Option 3] Recommendation System (N/A).");
        
        System.out.println("\nChoose an Option to build the graph (1-3):");

        int adjOption = sc.nextInt();
       
        
        Graph<Integer> tmpGraph = new Graph<Integer>();
        switch (adjOption) {
        	case 1:
        		tmpGraph  = option_one(movies, ratings);
        		break;
        	case 2: 
        		tmpGraph = option_two(movies, ratings);
        		break;
        	case 3:
        		tmpGraph = option_three(movies, ratings);
        		break;
        	default:
        		System.out.println("You've picked a wrong option. Program is exiting.");
        		System.exit(0);
        		break;

        }

        
        System.out.println("[Option 1]");
        System.out.println("[Option 2]");
        System.out.println("[Option 3]");
        System.out.println("[Option 4]");
        
        int exploreOption = sc.nextInt();
        switch (exploreOption) {
        	case 1:
        		printGraphStats(tmpGraph);
        		break;
        	case 2:
        		printNodeInfo(tmpGraph);
        		break;
        	case 3:
        		getShortestPath(tmpGraph);
        		break;
        	case 4: 
    		default:
    			System.exit(0);
			
        }
       
        System.out.println("Program is now exiting!");
        
        
        
        System.exit(0);
	}
	
	@SuppressWarnings("null")
	public static Graph<Integer> option_one(HashMap<Integer, Movie> movies, HashMap<Integer, Reviewer> ratings) {
		System.out.println("Entering option one"); // TODO: remove
		
		// print this later
		//		The number of nodes
		//		The number of edges
		//		The density of the graph defined as D = E / (V*(V-1)) for a directed graph
		//		The maximum degree (i.e. the largest number of outgoing edges of any node)
		//		The diameter of the graph (i.e. the longest shortest path)
		//		The average length of the shortest paths in the graph
		
		// Create a Graph of Movies
		Graph<Integer> moviesGraph = new Graph<Integer>();
		
		// Populating Graph with movies dataset
		for ( Integer tmp : movies.keySet() ) {
			moviesGraph.addVertex(tmp);
		}
		
		// TODO: remove validation print 
//		System.out.println(moviesGraph.getVertices());
		
		// Created a 2D Array of Booleans that tells whether each movie got over a certain number (THRESHOLD) for each type of review.
		int[][] alpha = new int[moviesGraph.numVertices()][RATINGS_ARRAY_SIZE];
	
		// 
		Integer[] intRatings = ratings.keySet().toArray(new Integer[ratings.keySet().size()]);
//		System.out.println(Arrays.toString(intRatings)); // TODO: remove
		
		
		// filling the array
		Double tmp = 0.0;
		for (int i=0; i < moviesGraph.numVertices(); i++) {
			for (int j=0; j < intRatings.length; j++) {
				tmp = ratings.get(intRatings[j]).getRatings().get(i);
				if (tmp != null) {
					alpha[i][(int) (tmp * 2)] += 1;
				}
			}
		}
		
		for (int i=0; i < alpha.length; i++) {
			for (int j=0; j < alpha[i].length; j++) {
//				System.out.format("i:[%d], j:[%d] = [%d]\n",i, j, alpha[i][j]  );
				
				if ( alpha[i][j] >= THRESHOLD) {
					for (int k=1; k < alpha.length; k++) {
						if (alpha[k][j] >= THRESHOLD) {
							moviesGraph.addEdge(i, k);
//							moviesGraph.addEdge(k, i);
						}
					}
				}
			}
		}
		
//		System.out.println(moviesGraph.getVertices());
		System.out.println(moviesGraph.getNeighbors(50));
		return moviesGraph;
	
	}
	public static Graph<Integer> option_two(HashMap<Integer, Movie> movies, HashMap<Integer, Reviewer> ratings) { return null; }
	public static  Graph<Integer> option_three(HashMap<Integer, Movie> movies, HashMap<Integer, Reviewer> ratings) { return null; }
	
	public static void printGraphStats(Graph<Integer> tmpGraph) {}
	public static void printNodeInfo(Graph<Integer> tmpGraph) {}
	public static void getShortestPath(Graph<Integer> tmpGraph) {//, int src, int dest) {
		// TODO: validation
		int[] shortestPath = GraphAlgorithms.dijkstrasAlgorithm(tmpGraph, 50);//, src);
//		int nodeCurrent = shortestPath[dest];
//		int tmp = nodeCurrent;
//		while ( nodeCurrent != -1 ) {
//			System.out.println(nodeCurrent);
//			
//		}
		System.out.println(Arrays.toString(shortestPath));
	}

}
