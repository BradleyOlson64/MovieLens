# MovieLens
An interface for graphing relationships between films. This body of java code takes in a series of movie-rating pairs for a single user and generates a graph linking the various movies according to criteria picked by the user. It then gives options to find the distance between any two graph nodes, to give statistics like highest degree node and the graph diameter, or to print the title of any movie as well as those of its neighbors. 

The interface makes use of Dijkstra's Algorithm and the Floyd-Warshall algorithm to find shortest paths between films and the overall shortest path in the the generated graph structure.
