package analyzer;

import java.io.File;
import java.util.ArrayList;
import java.util.Map;
import java.util.Arrays;
import java.util.HashMap;
import java.util.InputMismatchException;
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
		
		// TODO: these will be command-line arguments
		String ratingsString = "./src/ml-latest-small/ratings.csv";
		String moviesString = "./src/ml-latest-small/movies.csv";
		
		// utilize ETL(EXTRACT|TRANSFORM|LOAD) process
		DataLoader movieLensDataLoader = new DataLoader();
		movieLensDataLoader.loadData(moviesString, ratingsString);
		HashMap<Integer, Movie> movies = (HashMap) movieLensDataLoader.getMovies();
		HashMap<Integer, Reviewer> ratings = (HashMap) movieLensDataLoader.getReviewers();
		
		
		// welcome the user output
		System.out.println("=========Welcome to MovieLens Analyzer=========");
		
		// TODO: this needs to be command-line arguments
		System.out.println("The Files being analyzed are:\n" + ratingsString + "\n" + moviesString + "\n");
		System.out.println("There are 3 choices for defining adjacency");
        
		// initialize input system for user
		Scanner sc = new Scanner(System.in);
		
		// display options to pick how movies are related to each other
        System.out.println("[Option 1] u and v are adjacent if both movies have at least 50 reviewers that gave the same rating.");
        System.out.println("[Option 2] u and v are adjacent if both movies were made in the same year (regardless of rating).");
        System.out.println("[Option 3] u and v are adjacent if both movies contain the same string (Exploring the Graph Extra Credit).");
        
        // initialize option picker for user to determine how movies are related to each other
        System.out.println("\nChoose an Option to build the graph (1-3):");
        int adjOption = 4;
        
        try {
        		adjOption = sc.nextInt();
        } catch (InputMismatchException e) {
        		System.out.println("You've picked a wrong option. Program is exiting.");
        		System.exit(0);
        }
       
        // create populated graph based on option picked by user
        Graph<Integer> tmpGraph = new Graph<Integer>();
	        switch (adjOption) {
	        	case 1:
	        		tmpGraph  = optionFiftyReviewersRating(movies, ratings);
	        		break;
	        	case 2: 
	        		tmpGraph = optionSameYear(movies, ratings);
	        		break;
	        	case 3:
	        		tmpGraph = optionSearchString(movies);
	        		break;
	        	default:
	        		System.out.println("You've picked a wrong option. Program is exiting.");
	        		System.exit(0);
	        		break;
        }

	    // initialize continuous option picker to find stats, node info, and shortest path(s) in the graph.
        int exploreOption = 4;
        
        while (true) {
	        System.out.println("[Option 1] Print out statistics about the graph");
	        System.out.println("[Option 2] Print node information");
	        System.out.println("[Option 3] Display shortest path between two nodes");
	        System.out.println("[Option 4] Quit");
	        
	        try {
	        		exploreOption = sc.nextInt();
	        } catch (InputMismatchException e) {
	        		exploreOption = 4;
	        }
	        
	        switch (exploreOption) {
	        	case 1:
	        		printGraphStats(tmpGraph);
	        		break;
	        	case 2:
	        		printNodeInfo(tmpGraph, movies);
	        		break;
	        	case 3:
	        		getShortestPath(tmpGraph);
	        		break;
	        	case 4:
	    		default:
	    	        System.out.println("Program is now exiting!");
	    			System.exit(0);
	    			break;
				
	        }
        }    
    	}
	
	/**
	 * Fifty Reviewers Rating Option populates a graph based on at least 50 users who gave the same rating to movies (ex. if movie_1 had 57 ratings for 5.0 and movie_2 had 87 ratings for 5.0).
	 * @param movies dataset of movies
	 * @param reviewers dataset of reviewers
	 * @return populated graph based on at least 50 users who gave the same rating to movies
	 */
	public static Graph<Integer> optionFiftyReviewersRating(HashMap<Integer, Movie> movies, HashMap<Integer, Reviewer> reviewers) {
		
		// Create an empty Graph of Movies
		Graph<Integer> moviesGraph = new Graph<Integer>();
		
		// Populate graph with the movies dataset
		for ( Integer tmp : movies.keySet()) {
			moviesGraph.addVertex(tmp);
		}
		
		// Create a 2D Array of Integers that tells whether each movie got over a certain number (THRESHOLD) for each type of review. 
		int[][] thresholdHolder = new int[moviesGraph.numVertices()][RATINGS_ARRAY_SIZE];
		
		// get movies and get reviewers dataset
		Integer[] reviewerIds = reviewers.keySet().toArray(new Integer[reviewers.keySet().size()]);	
		Integer[] moviesIds = moviesGraph.getVertices().toArray(new Integer[moviesGraph.getVertices().size()]);
		
		
		// fill the thresholdHolder array with ratings increments
		Double tmp = 0.0;
		for ( int i=0; i < moviesIds.length; i++) {
			for (int j=0; j < reviewerIds.length; j++) {
				tmp = reviewers.get(reviewerIds[j]).getRatings().get(moviesIds[i]);
				if (tmp != null) {
					thresholdHolder[i][(int) (tmp * 2)] += 1;
				}
			}
		}
		
		// populate graph based on threshholdHolder's values which is dictated by THRESHOLD value
		for (int i=0; i < moviesIds.length; i++) {
			for (int j=0; j < thresholdHolder[i].length; j++) {
								
				if (thresholdHolder[i][j] >= THRESHOLD) {
					for (int k=0; k < thresholdHolder.length; k++) {
						if (thresholdHolder[k][j] >= THRESHOLD && k != i && !moviesGraph.edgeExists(moviesIds[i], moviesIds[k]) && !	moviesGraph.edgeExists(moviesIds[k], moviesIds[i])) {

							moviesGraph.addEdge(moviesIds[i], moviesIds[k]);
							moviesGraph.addEdge(moviesIds[k], moviesIds[i]);
						}
					}
				}
			}
		}
		return moviesGraph;
	
	}
	
	/**
	 * Same Year Option populates a movie graph based on the year it was created (ex. all movies made in 2006 will be neighbors with each other).
	 * @param movies dataset of movies
	 * @param reviewers dataset of reviewers
	 * @return a populated graph based on the year the movie was created.
	 */
	public static Graph<Integer> optionSameYear(HashMap<Integer, Movie> movies, HashMap<Integer, Reviewer> reviewers) { 
		
		// Create an empty Graph of Movies
		Graph<Integer> moviesGraph = new Graph<Integer>();
		
		// Populate graph with the movies dataset
		for ( Integer tmp : movies.keySet()) {
			moviesGraph.addVertex(tmp);
		}
		
		// Get movies dataset
		Integer[] moviesIds = moviesGraph.getVertices().toArray(new Integer[moviesGraph.getVertices().size()]);

		// populate graph with edges based on year
		int movieX = 0;
		int movieY = 0;
		for (int i=0; i < moviesIds.length; i++) {
			movieX = movies.get(moviesIds[i]).getYear();
			
			for (int j=0; j < moviesIds.length; j++) {
				movieY = movies.get(moviesIds[j]).getYear();
				if ( movieX == movieY && i != j && !moviesGraph.edgeExists(moviesIds[i], moviesIds[j])) {
					moviesGraph.addEdge(moviesIds[i], moviesIds[j]);
				}
			}
		}
		return moviesGraph;
	}
	
	/**
	 * Search String option allows for a string input to populate a movie graph based on that string input (ex. casino gives back 1000 vertices but there exists only two edges).
	 * @param movies dataset of movies
	 * @return a populated graph of the intended string input.
	 */
	public static  Graph<Integer> optionSearchString(HashMap<Integer, Movie> movies) { 
		
		// Create an empty Graph of Movies
		Graph<Integer> moviesGraph = new Graph<Integer>();
		
		// Get movies dataset
		Integer[] moviesIds = movies.keySet().toArray(new Integer[movies.keySet().size()]);
		
		// Get Keyword from user.
        System.out.println("Enter a new keyword to find all movies based on searched term:");
		Scanner sc = new Scanner(System.in);
        String keyword = sc.nextLine();
		String titleX = "";
		
		// Storage for keyword matches
		ArrayList<Integer> keywordMatchedMovies = new ArrayList<Integer>();
		
		// create vertices based on keyword input
		for (int i=0; i < moviesIds.length; i++) {
			titleX = movies.get(moviesIds[i]).getTitle().toUpperCase();
			
			if ( titleX.contains(keyword.toUpperCase())) {
				keywordMatchedMovies.add(moviesIds[i]);
			} 
			moviesGraph.addVertex(moviesIds[i]);

		}
		
		// populate graph with edges based on keyword matches
		for (int i=0; i < keywordMatchedMovies.size(); i++) {
			for (int j=0; j < keywordMatchedMovies.size(); j++) {
				if ( i != j) {
					moviesGraph.addEdge(keywordMatchedMovies.get(i), keywordMatchedMovies.get(j));
				}
			}
		}
		

		
		return moviesGraph;
	}
	
	/**
	 * Builds a string to print graph statistics about given graph data structure.
	 * @param tmpGraph a populated graph
	 */
	public static void printGraphStats(Graph<Integer> tmpGraph) {
		
		StringBuilder sb = new StringBuilder("Graph Statistics: \n");
		sb.append("\t|V| = ").append(tmpGraph.numVertices()).append(" vertices.\n");
		sb.append("\t|E| = ").append(tmpGraph.numEdges()).append(" edges\n");
		sb.append("\tDensity = ").append( (double) (tmpGraph.numEdges())/ ( tmpGraph.numVertices() * (tmpGraph.numVertices()-1) )  ).append("\n");
		
		// determine highest degree node
		int maxDegreeVertex = -1;
		for (int moviesId : tmpGraph.getVertices()) {
			if (maxDegreeVertex == -1) {
				maxDegreeVertex = moviesId;
			}
			if (tmpGraph.degree(moviesId) > tmpGraph.degree(maxDegreeVertex)) {
				maxDegreeVertex = moviesId;
			}
		}
		
		sb.append("\tMax. Degree = ").append(tmpGraph.degree(maxDegreeVertex)).append(" (node ").append(maxDegreeVertex).append(")\n");
		
		// TODO: determine longest shortest path (diameter of graph)
		sb.append("\tDiameter = ").append("\n");
		
		// TODO: determine average length of the shortest paths in the graph.
		sb.append("\tAvg. Path Length = ").append("\n");
		
		
		System.out.println(sb.toString());
	}
	
	/**
	 * Displays information pertaining to any movieID node.
	 * @param tmpGraph a populated graph
	 * @param movies dataset of movies
	 */
	public static void printNodeInfo(Graph<Integer> tmpGraph, HashMap<Integer, Movie> movies) {
		
		StringBuilder sb = new StringBuilder("");
		Scanner sc = new Scanner(System.in);
		
		System.out.print("Enter Movie Id (1-1000): "); 
		
		// Handle movieID input
		int id = -1;
		try {
			id = sc.nextInt();
			if ( !(id > 0 && id <= 1000) ) {
				throw new InputMismatchException("You've entered a wrong movieID: " + id);
			}
		} catch (InputMismatchException e) {
			System.out.println("You've entered a wrong movieID: " + id);
			System.out.println("Program is now exiting!");
			System.exit(0);
		}
		
		// Handle print node information
		sb.append(movies.get(id)).append("\n");
		sb.append("Neighbors:\n");
		ArrayList<Integer> neighborMovies = (ArrayList<Integer>) tmpGraph.getNeighbors(id);
		for ( int neighbor : neighborMovies) {
			sb.append("\t").append(movies.get(neighbor).getTitle()).append("\n");
		}
		
		if (neighborMovies.isEmpty()) {
			sb.append("\tNo Neighbors\n");
		}
	
		System.out.println(sb.toString());
		
	}
	
	/**
	 * Finds the shortest path between two nodes.
	 * @param tmpGraph a populated graph
	 */
	public static void getShortestPath(Graph<Integer> tmpGraph) {
		
		Scanner sc = new Scanner(System.in);
		int start = -1;
		int end = -1;
		
		try {
			System.out.println("Enter starting node (1-1000): ");
			start = sc.nextInt();
			System.out.println("Enter ending node (1-1000): ");
			end = sc.nextInt();
			
			if ( !(start > 0 && start <= 1000) || !(end > 0 && end <= 1000) ) {
				throw new InputMismatchException();
			}
			
		} catch (InputMismatchException e) {
			System.out.println("You've entered a wrong movieID: " + start + " or " + end);
			System.out.println("Program is now exiting!");
			System.exit(0);
		}
		
		
		// TODO: pick only vertices that are within the graph
//		int[] shortestPath = GraphAlgorithms.dijkstrasAlgorithm(tmpGraph, 1000); // breaks at 2 and 50 and probably other values too.
//		System.out.println(Arrays.toString(shortestPath));
//		
		// TODO: your dijkstras algorithm breaks.........
	}

}
