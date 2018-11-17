package graph;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import util.PriorityQueue;


public class GraphAlgorithms {
	/**
	 * This method returns a list of shortest path distances and of previous nodes for each destination node in g.
	 * @param g
	 * @param s
	 * @param dist
	 * @param prev
	 */
	public static int[] dijkstrasAlgorithm(Graph<Integer> g, int s) {
		// Getting node list
		Integer[] vertArray = g.getVertices().toArray(new Integer[g.getVertices().size()]);
		ArrayList<Integer> vertices = new ArrayList<Integer>(Arrays.asList(vertArray));

		int[] dist= new int[vertices.size()];
		int[] prev= new int[vertices.size()];
		//initializing the distance and prev lists
		for(int i=0;i<vertices.size();i++) {
			if(vertices.get(i)== s) {
				dist[i]= 0;
				prev[i]= -1;
			}
			else {
				dist[i]= Integer.MAX_VALUE;
				prev[i]= -1;
			}
		}
		// Creating and filling the priority queue
		PriorityQueue queue = new PriorityQueue();
		for(int i=0;i<vertices.size();i++) {
			queue.push(dist[i],i);
		}
		// Main popping loop
		while(!queue.isEmpty()) {
			// Handling the initial pop
			int currVertex = vertices.get(queue.topElement());
			int currDistance = queue.topPriority();
			queue.pop();
			// Getting adjacency list of V
			List<Integer> adjacencyList = g.getNeighbors(currVertex);
			for(Integer node: adjacencyList) {
				int alt = currDistance + 1;
				if(alt<dist[node-1]) {
					dist[node-1]= alt;
					prev[node-1]= currVertex;
					queue.changePriority(node-1, alt);
				}
			}
		}
		return prev;
	}
	
	/**
	 * This method returns the set of shortest paths from any node in the given graph to any other node.
	 * @param g
	 * @return
	 */
	public static int[][] floydWarshall(Graph<Integer> g){
		// Getting node list
		Integer[] vertArray = g.getVertices().toArray(new Integer[g.getVertices().size()]);
		ArrayList<Integer> vertices = new ArrayList<Integer>(Arrays.asList(vertArray));
		// Establishing path lengths with no intermediate vertices
		int[][] wlast= new int[vertices.size()][vertices.size()];
		for(int i=0;i<vertices.size();i++) {
			for(int j=0;j<vertices.size();j++) {
				if(vertices.get(i)==vertices.get(j)) wlast[i][j] = 0;
				else{
					if(g.edgeExists(vertices.get(i), vertices.get(j))) wlast[i][j] = 1;
					else wlast[i][j]= Integer.MAX_VALUE;
				}
			}
		}
		// Looping to create other matrices
		for(int k = 0;k<vertices.size();k++) {
			int[][] wk = new int[vertices.size()][vertices.size()];
			for(int i= 0;i<vertices.size();i++) {
				for(int j=0;j<vertices.size();j++) {
					if(wlast[i][k] != Integer.MAX_VALUE && wlast[k][j] != Integer.MAX_VALUE) {
						wk[i][j] = Math.min(wlast[i][j], wlast[i][k]+ wlast[k][j]);
					}
					else wk[i][j] = wlast[i][j];
				}
			}
			wlast = wk;
		}
		return wlast;
	}
	
	public static void main(String[] Args) {
		Graph<Integer> g = new Graph<Integer>();
		g.addVertex(1);
		g.addVertex(2);
		g.addVertex(3);
		g.addVertex(4);
		g.addEdge(1, 2);
		g.addEdge(1, 4);
		g.addEdge(2, 4);
		g.addEdge(2, 3);
		System.out.println("Testing dijkstras");
		int[] digResult = dijkstrasAlgorithm(g,1);
		System.out.println(Arrays.toString(digResult));
		System.out.println("");
		System.out.println("Testing floydWarshall");
		int[][] warshResult = floydWarshall(g);
		for (int[] pathSet: warshResult) {
			System.out.println(Arrays.toString(pathSet));
		}
	}
}
