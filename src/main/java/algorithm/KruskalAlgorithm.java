package algorithm;

import model.Edge;
import model.Graph;
import model.MSTResult;

import java.util.*;

public class KruskalAlgorithm {

    public static MSTResult findMST(Graph graph) {
        long start = System.nanoTime();

        List<Edge> edges = new ArrayList<>(graph.getEdges());
        edges.sort(Comparator.comparingInt(Edge::getWeight));

        // Сопоставляем имена вершин с индексами для Union-Find
        Map<String, Integer> indexMap = new HashMap<>();
        List<String> nodes = graph.getNodes();
        for (int i = 0; i < nodes.size(); i++) indexMap.put(nodes.get(i), i);

        UnionFind uf = new UnionFind(nodes.size());
        List<Edge> mst = new ArrayList<>();
        int totalCost = 0, operations = 0;

        for (Edge e : edges) {
            operations++;
            int u = indexMap.get(e.getFrom());
            int v = indexMap.get(e.getTo());
            if (uf.union(u, v)) {
                mst.add(e);
                totalCost += e.getWeight();
            }
        }

        long end = System.nanoTime();
        return new MSTResult(mst, totalCost, operations, (double) (end - start) / 1_000_000);
    }

    // --- Вспомогательный класс Union-Find ---
    private static class UnionFind {
        int[] parent, rank;

        UnionFind(int n) {
            parent = new int[n];
            rank = new int[n];
            for (int i = 0; i < n; i++) parent[i] = i;
        }

        int find(int x) {
            if (x != parent[x]) parent[x] = find(parent[x]);
            return parent[x];
        }

        boolean union(int a, int b) {
            int rootA = find(a), rootB = find(b);
            if (rootA == rootB) return false;
            if (rank[rootA] < rank[rootB]) parent[rootA] = rootB;
            else if (rank[rootA] > rank[rootB]) parent[rootB] = rootA;
            else {
                parent[rootB] = rootA;
                rank[rootA]++;
            }
            return true;
        }
    }
}
