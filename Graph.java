package cryptoportfolio;

//@name Maleke Hanson
//@date/version 5/7/22 Version 2

import java.util.ArrayDeque;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.TreeSet;

public class Graph {

	/* inner class Edge. Do not modify this class. */
	class Edge {
		public final String s;
		public final String t;
		public final Double weight;

		public Edge(String s, String t, Double w) {
			this.s = s;
			this.t = t;
			weight = w;
		}

		@Override
		public String toString() {
			return s + "->" + t + ":" + weight;
		}
	}

	// Inner Vertex Class
	class Vertex {
		int id;
		String name;

		public Vertex(int id, String name) {
			this.id = id;
			this.name = name;
		}

		@Override
		public String toString() {
			return name;
		}
	}

	// Inner Flow class that implements the Comparable interface
	class Flow implements Comparable {
		private String baseAsset;
		private Double flow;

		public Flow(String baseAsset, double flow) {
			this.baseAsset = baseAsset;
			this.flow = flow;
		}

		@Override
		public String toString() {
			return baseAsset + ": " + flow.intValue();
		}

		@Override
		public int compareTo(Object other) {
			Flow otherFlow = (Flow) (other);
			return (int) (otherFlow.flow - flow);
		}

	}

	/* instance variable for Graph class */
	private final HashMap<String, List<Edge>> adjacencyList;
	private final HashSet<String> setV;

	/**
	 * Graph constructor that initializes instance variables and builds the
	 * adjacencyList
	 *
	 * @param HashMap<String, Double> "Base Asset, Quote Asset" and total trade
	 *                        counts data
	 */
	public Graph(HashMap<String, Double> counts) {
		setV = new HashSet<>();
		adjacencyList = new HashMap<>();
		String[] myArray;
		int idCount = 0;
		for (Map.Entry<String, Double> newEntry : counts.entrySet()) {
			String myKey = newEntry.getKey();
			Double myValue = newEntry.getValue();
			myArray = myKey.split("-> ");
			setV.add(myArray[0]);
			setV.add(myArray[1]);
			Vertex a = new Vertex(idCount++, myArray[0]);
			Vertex b = new Vertex(idCount++, myArray[1]);
			Edge e = new Edge(a.name, b.name, myValue);
			if (adjacencyList.get(myArray[0]) == null) {
				List<Edge> connections = new LinkedList<Edge>();
				connections.add(e);
				adjacencyList.put(myArray[0], connections);
			} else {
				adjacencyList.get(myArray[0]).add(e);
			}
		}
	}

	/**
	 * findFlows takes the source cryptocurrency and finds a shortest path from the
	 * source to all reachable cryptocurrencies, and calculate the net trades count
	 * over the shortest path.
	 *
	 * @param String the source cryptocurrency name.
	 * @returns (other cryptocurrency, the net trades count) pairs.
	 */
	public Map<String, Double> findFlows(String source) {
		Map<String, Double> toReturn = new HashMap<>();
		HashMap<String, Boolean> visited = new HashMap<>();

		TreeSet<Flow> flows = new TreeSet<>();

		for (String v : setV) {
			visited.put(v, false);
		}

		BFS(source, visited, flows);

		for (Flow f : flows) {
			toReturn.put(f.baseAsset, f.flow);
		}
		return toReturn;
	}

	/**
	 * BFS method to find the shortest path from a source to a destination and use
	 * of a TreeSet to keep these paths sorted in ascending order
	 *
	 * @param vertex  Takes in the vertex to search from
	 * @param visited Use of a vistied Hashmap to allow for BFS to work properly
	 *                upon visted vertexes
	 * @param flows   Use of a TreeSet to maintain order of the flows to keep them
	 *                sorted
	 */
	private void BFS(String vertex, HashMap<String, Boolean> visited, TreeSet<Flow> flows) {
		ArrayDeque<Flow> queue = new ArrayDeque<>();
		visited.replace(vertex, true);
		queue.addLast(new Flow(vertex, Double.MAX_VALUE));

		while (!queue.isEmpty()) {
			Flow temp = queue.removeFirst();

			if (temp.flow < Double.MAX_VALUE) {

				flows.add(temp);

			}
			String tempVertex = temp.baseAsset;

			if (adjacencyList.get(tempVertex) != null) {
				for (Edge e : adjacencyList.get(tempVertex)) {
					String tempFlow = e.t;
					if (!visited.get(tempFlow)) {
						queue.addLast(new Flow(tempFlow, Math.min(e.weight, temp.flow)));
					}
					visited.replace(tempFlow, true);
				}
			}
		}
	}

}
