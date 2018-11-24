package analyzer;

import java.io.File;
import java.util.ArrayList;
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
	
	public static final int THRESHOLD = 50; // TODO: do javadocs
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
        System.out.println("[Option 2] u and v are adjacent if both movies were made in the same year (regardless of rating).");
        System.out.println("[Option 3] u and v are adjacent if both movies contain the same string (Exploring the Graph Extra Credit).");
        
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
	public static Graph<Integer> option_one(HashMap<Integer, Movie> movies, HashMap<Integer, Reviewer> reviewers) {
//		System.out.println("Entering option one"); // TODO: remove
		
		// Create an empty Graph of Movies
		Graph<Integer> moviesGraph = new Graph<Integer>();
		
		// Populate graph with the movies dataset
		for ( Integer tmp : movies.keySet()) {
			// System.out.println("Adding Vertex @ DEBUG [1]: " + tmp); // Debug: Vertices are added.
			moviesGraph.addVertex(tmp);
			// System.out.println("Contains Vertex @ DEBUG[2]: " +  moviesGraph.containsNode(tmp)); // Debug: check if vertices have been aded.
		}
		
		// Create a 2D Array of Integers that tells whether each movie got over a certain number (THRESHOLD) for each type of review. 
		int[][] alpha = new int[moviesGraph.numVertices()][RATINGS_ARRAY_SIZE];
//		System.out.println("numVertices() @ DEBUG [3]: " + moviesGraph.numVertices());
//		System.out.println("RATINGS_ARRAY_SIZE @ DEBUG [4]: " + RATINGS_ARRAY_SIZE); // 2D[x][0] = 0.0, 2D[x][1] = 0.5, ..., etc.
		
		Integer[] reviewerIds = reviewers.keySet().toArray(new Integer[reviewers.keySet().size()]);		
//		System.out.println("intRatings.length @ DEBUG[5]: " + reviewerIds.length);
		
		
		Integer[] moviesIds = moviesGraph.getVertices().toArray(new Integer[moviesGraph.getVertices().size()]);
		
		
		// fill the alpha array with ratings
		Double tmp = 0.0;
		for ( int i=0; i < moviesIds.length; i++) {
			for (int j=0; j < reviewerIds.length; j++) {
				tmp = reviewers.get(reviewerIds[j]).getRatings().get(moviesIds[i]);
				if (tmp != null) {
//					System.out.println(moviesIds[i]);
//					System.out.println("Other: " + tmp);
					alpha[i][(int) (tmp * 2)] += 1;
				}
//				break;
			}
		}
		
		
		for (int i=0; i < moviesIds.length; i++) {
//			System.out.println("[movie id]= "+ moviesIds[i] + " => " + Arrays.toString(alpha[i]));
			for (int j=0; j < alpha[i].length; j++) {
								
				if (alpha[i][j] >= THRESHOLD) {
//					System.out.println(alpha[i][j]);
					for (int k=0; k < alpha.length; k++) {
						if (alpha[k][j] >= THRESHOLD && k != i && !moviesGraph.edgeExists(moviesIds[i], moviesIds[k])) {
//							System.out.println("[ids i and k]: = "+ moviesIds[i] + ", "+ moviesIds[k]);
//							System.out.format("\ti:[%d], j:[%d] = [%d]\n",i, j, alpha[i][j]);
//							System.out.format("\tk:[%d], j:[%d] = [%d]\n",i, j, alpha[k][j]);
//							System.out.println("\n");
							moviesGraph.addEdge(moviesIds[i], moviesIds[k]);
						}
					}
				}
			}
//			break;
		}
//		System.out.println(Arrays.deepToString(alpha));

		System.out.println(moviesGraph.numEdges());
		
		
		return moviesGraph;
	
	}
	public static Graph<Integer> option_two(HashMap<Integer, Movie> movies, HashMap<Integer, Reviewer> reviewers) { 
		
		// Create an empty Graph of Movies
		Graph<Integer> moviesGraph = new Graph<Integer>();
		
		// Populate graph with the movies dataset
		for ( Integer tmp : movies.keySet()) {
			// System.out.println("Adding Vertex @ DEBUG [1]: " + tmp); // Debug: Vertices are added.
			moviesGraph.addVertex(tmp);
			// System.out.println("Contains Vertex @ DEBUG[2]: " +  moviesGraph.containsNode(tmp)); // Debug: check if vertices have been aded.
		}
		
		Integer[] moviesIds = moviesGraph.getVertices().toArray(new Integer[moviesGraph.getVertices().size()]);

		int movieX = 0;
		int movieY = 0;
		for (int i=0; i < moviesIds.length; i++) {
			movieX = movies.get(moviesIds[i]).getYear();
			
			for (int j=0; j < moviesIds.length; j++) {
				movieY = movies.get(moviesIds[j]).getYear();
				if ( movieX == movieY && i != j && !moviesGraph.edgeExists(moviesIds[i], moviesIds[j])) {
//					System.out.println("[ids i and k]: = "+ moviesIds[i] + ", "+ moviesIds[j]);
//					System.out.println("\n");
					moviesGraph.addEdge(moviesIds[i], moviesIds[j]);
				}
			}
		}
		
		System.out.println(moviesGraph.numEdges());

		
		
		return moviesGraph;
	}
	public static  Graph<Integer> option_three(HashMap<Integer, Movie> movies, HashMap<Integer, Reviewer> reviewers) { 
		
		// Create an empty Graph of Movies
		Graph<Integer> moviesGraph = new Graph<Integer>();
		
		// Populate graph with the movies dataset
		for ( Integer tmp : movies.keySet()) {
			// System.out.println("Adding Vertex @ DEBUG [1]: " + tmp); // Debug: Vertices are added.
			// System.out.println("Contains Vertex @ DEBUG[2]: " +  moviesGraph.containsNode(tmp)); // Debug: check if vertices have been aded.
		}
		
		Integer[] moviesIds = movies.keySet().toArray(new Integer[movies.keySet().size()]);
		
		// Get Keyword from user.
        System.out.println("Enter a new keyword to find all movies based on searched term:");
		Scanner sc = new Scanner(System.in);
        String keyword = sc.nextLine();
        

		String titleX = "";
		String titleY = "";
		
		ArrayList<Integer> keywordMatchedMovies = new ArrayList<Integer>();
		
		// create vertices based on keyword input
		for (int i=0; i < moviesIds.length; i++) {
			titleX = movies.get(moviesIds[i]).getTitle().toUpperCase();
			
			if ( titleX.contains(keyword.toUpperCase())) {
				System.out.println( titleX );
				
			
				keywordMatchedMovies.add(moviesIds[i]);
				moviesGraph.addVertex(moviesIds[i]);
			} 
		}
		
		// populate graph with edges 
		for (int i=0; i < keywordMatchedMovies.size(); i++) {
			for (int j=0; j < keywordMatchedMovies.size(); j++) {
				if ( i != j) {
					moviesGraph.addEdge(keywordMatchedMovies.get(i), keywordMatchedMovies.get(j));
				}
			}
		}
		
		System.out.println(moviesGraph.numVertices());
		System.out.println(moviesGraph.numEdges());
		
		return moviesGraph;
	}
	
	public static void printGraphStats(Graph<Integer> tmpGraph) {}
	public static void printNodeInfo(Graph<Integer> tmpGraph) {}
	public static void getShortestPath(Graph<Integer> tmpGraph) {
		// TODO: pick only vertices that are within the graph
		int[] shortestPath = GraphAlgorithms.dijkstrasAlgorithm(tmpGraph, 1000); // breaks at 2 and 50 and probably other values too.
		System.out.println(Arrays.toString(shortestPath));
		
		// TODO: your dijkstras algorithm breaks.........
	}

}
