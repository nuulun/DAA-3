import algorithm.KruskalAlgorithm;
import algorithm.PrimAlgorithm;
import model.Edge;
import model.Graph;
import model.MSTResult;
import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.*;

import java.util.*;
import java.io.*;
import com.fasterxml.jackson.databind.*;



public class MSTAlgorithmsTest {

    private static List<Graph> testGraphs;

    @BeforeAll
    static void setup() throws IOException {
        ObjectMapper mapper = new ObjectMapper();
        File input = new File("data/input.json");
        JsonNode root = mapper.readTree(input).get("graphs");

        testGraphs = new ArrayList<>();
        for (JsonNode graphNode : root) {
            List<String> nodes = new ArrayList<>();
            for (JsonNode n : graphNode.get("nodes")) nodes.add(n.asText());

            List<Edge> edges = new ArrayList<>();
            for (JsonNode e : graphNode.get("edges")) {
                edges.add(new Edge(
                        e.get("from").asText(),
                        e.get("to").asText(),
                        e.get("weight").asInt()
                ));
            }
            testGraphs.add(new Graph(graphNode.get("id").asInt(), nodes, edges));
        }
    }

    @Test
    @DisplayName(" Test 1: MST total cost identical (Prim vs Kruskal)")
    void testMSTCostsAreEqual() {
        for (Graph g : testGraphs) {
            MSTResult prim = PrimAlgorithm.findMST(g);
            MSTResult kruskal = KruskalAlgorithm.findMST(g);
            assertEquals(prim.getTotalCost(), kruskal.getTotalCost(),
                    "MST total cost must be identical for Prim and Kruskal on Graph " + g.getId());
        }
    }

    @Test
    @DisplayName(" Test 2: MST has exactly V - 1 edges")
    void testEdgeCountEqualsVMinusOne() {
        for (Graph g : testGraphs) {
            MSTResult prim = PrimAlgorithm.findMST(g);
            int expectedEdges = g.getNodes().size() - 1;
            assertEquals(expectedEdges, prim.getMstEdges().size(),
                    "MST should have V - 1 edges for Graph " + g.getId());
        }
    }

    @Test
    @DisplayName(" Test 3: MST contains no cycles (acyclic)")
    void testMSTAcyclic() {
        for (Graph g : testGraphs) {
            MSTResult prim = PrimAlgorithm.findMST(g);
            assertTrue(isAcyclic(g.getNodes(), prim.getMstEdges()),
                    "MST should be acyclic for Graph " + g.getId());
        }
    }

    @Test
    @DisplayName(" Test 4: MST connects all vertices (single connected component)")
    void testMSTConnected() {
        for (Graph g : testGraphs) {
            MSTResult prim = PrimAlgorithm.findMST(g);
            assertTrue(isConnected(g.getNodes(), prim.getMstEdges()),
                    "MST should connect all vertices for Graph " + g.getId());
        }
    }

    @Test
    @DisplayName(" Test 5: Disconnected graphs handled gracefully")
    void testDisconnectedGraphHandling() {
        // создаём искусственно разорванный граф
        List<String> nodes = Arrays.asList("A", "B", "C");
        List<Edge> edges = Collections.singletonList(new Edge("A", "B", 1)); // C не соединена
        Graph disconnected = new Graph(999, nodes, edges);

        MSTResult prim = PrimAlgorithm.findMST(disconnected);
        assertTrue(prim.getMstEdges().size() < nodes.size() - 1,
                "Disconnected graph must not produce a full MST");
    }

    @Test
    @DisplayName(" Test 6: Execution time non-negative")
    void testExecutionTimeNonNegative() {
        for (Graph g : testGraphs) {
            MSTResult prim = PrimAlgorithm.findMST(g);
            MSTResult kruskal = KruskalAlgorithm.findMST(g);
            assertTrue(prim.getExecutionTimeMs() >= 0);
            assertTrue(kruskal.getExecutionTimeMs() >= 0);
        }
    }

    @Test
    @DisplayName(" Test 7: Operation counts non-negative")
    void testOperationCountNonNegative() {
        for (Graph g : testGraphs) {
            MSTResult prim = PrimAlgorithm.findMST(g);
            MSTResult kruskal = KruskalAlgorithm.findMST(g);
            assertTrue(prim.getOperationCount() >= 0);
            assertTrue(kruskal.getOperationCount() >= 0);
        }
    }

    @Test
    @DisplayName(" Test 8: Results reproducible for same dataset")
    void testReproducibility() {
        for (Graph g : testGraphs) {
            MSTResult first = PrimAlgorithm.findMST(g);
            MSTResult second = PrimAlgorithm.findMST(g);
            assertEquals(first.getTotalCost(), second.getTotalCost(),
                    "Results must be reproducible for same graph " + g.getId());
        }
    }

    // === Helper methods ===
    private boolean isAcyclic(List<String> nodes, List<Edge> edges) {
        Map<String, String> parent = new HashMap<>();
        for (String v : nodes) parent.put(v, v);

        for (Edge e : edges) {
            String root1 = find(parent, e.getFrom());
            String root2 = find(parent, e.getTo());
            if (root1.equals(root2)) return false; // цикл
            parent.put(root1, root2);
        }
        return true;
    }

    private boolean isConnected(List<String> nodes, List<Edge> edges) {
        if (edges.isEmpty()) return false;
        Map<String, List<String>> adj = new HashMap<>();
        for (String n : nodes) adj.put(n, new ArrayList<>());
        for (Edge e : edges) {
            adj.get(e.getFrom()).add(e.getTo());
            adj.get(e.getTo()).add(e.getFrom());
        }

        Set<String> visited = new HashSet<>();
        dfs(nodes.get(0), adj, visited);
        return visited.size() == nodes.size();
    }

    private void dfs(String node, Map<String, List<String>> adj, Set<String> visited) {
        visited.add(node);
        for (String nei : adj.get(node))
            if (!visited.contains(nei))
                dfs(nei, adj, visited);
    }

    private String find(Map<String, String> parent, String v) {
        if (!parent.get(v).equals(v))
            parent.put(v, find(parent, parent.get(v)));
        return parent.get(v);
    }
}
