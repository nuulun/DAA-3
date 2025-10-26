package algorithm;

import model.Edge;
import model.Graph;
import model.MSTResult;

import java.util.*;

public class PrimAlgorithm {

    public static MSTResult findMST(Graph graph) {
        long start = System.nanoTime();

        List<String> nodes = graph.getNodes();
        Map<String, List<Edge>> adj = new HashMap<>();
        for (String node : nodes) adj.put(node, new ArrayList<>());
        for (Edge e : graph.getEdges()) {
            adj.get(e.getFrom()).add(e);
            adj.get(e.getTo()).add(e);
        }

        Set<String> visited = new HashSet<>();
        List<Edge> mst = new ArrayList<>();
        int totalCost = 0, operations = 0;

        String startNode = nodes.get(0);
        visited.add(startNode);

        PriorityQueue<Edge> pq = new PriorityQueue<>(Comparator.comparingInt(Edge::getWeight));
        pq.addAll(adj.get(startNode));

        while (!pq.isEmpty() && mst.size() < nodes.size() - 1) {
            Edge edge = pq.poll();
            operations++;

            String next = visited.contains(edge.getFrom()) ? edge.getTo() : edge.getFrom();
            if (visited.contains(next)) continue;

            visited.add(next);
            mst.add(edge);
            totalCost += edge.getWeight();

            for (Edge e : adj.get(next)) {
                String neighbor = e.getFrom().equals(next) ? e.getTo() : e.getFrom();
                if (!visited.contains(neighbor)) pq.add(e);
            }
        }

        long end = System.nanoTime();
        return new MSTResult(mst, totalCost, operations, (double) (end - start) / 1_000_000);
    }
}
