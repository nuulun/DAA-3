package utils;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.*;
import java.io.File;
import java.io.IOException;
import java.util.List;
import model.Edge;
import model.MSTResult;

public class JsonWriter {

    public static void writeResultsToFile(String filePath, List<MSTResult> primResults, List<MSTResult> kruskalResults) {
        ObjectMapper mapper = new ObjectMapper();
        ArrayNode resultsArray = mapper.createArrayNode();

        for (int i = 0; i < primResults.size(); i++) {
            MSTResult prim = primResults.get(i);
            MSTResult kruskal = kruskalResults.get(i);

            ObjectNode resultNode = mapper.createObjectNode();
            resultNode.put("graph_id", i + 1);

            // === Prim result ===
            ObjectNode primNode = mapper.createObjectNode();
            primNode.put("total_cost", prim.getTotalCost());
            primNode.put("operations_count", prim.getOperationCount());
            primNode.put("execution_time_ms", prim.getExecutionTimeMs());
            primNode.set("mst_edges", toEdgeArray(mapper, prim.getMstEdges()));

            // === Kruskal result ===
            ObjectNode kruskalNode = mapper.createObjectNode();
            kruskalNode.put("total_cost", kruskal.getTotalCost());
            kruskalNode.put("operations_count", kruskal.getOperationCount());
            kruskalNode.put("execution_time_ms", kruskal.getExecutionTimeMs());
            kruskalNode.set("mst_edges", toEdgeArray(mapper, kruskal.getMstEdges()));

            resultNode.set("prim", primNode);
            resultNode.set("kruskal", kruskalNode);

            resultsArray.add(resultNode);
        }

        ObjectNode root = mapper.createObjectNode();
        root.set("results", resultsArray);

        try {
            mapper.writerWithDefaultPrettyPrinter().writeValue(new File(filePath), root);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static ArrayNode toEdgeArray(ObjectMapper mapper, List<Edge> edges) {
        ArrayNode edgeArray = mapper.createArrayNode();
        for (Edge e : edges) {
            ObjectNode edgeNode = mapper.createObjectNode();
            edgeNode.put("from", e.getFrom());
            edgeNode.put("to", e.getTo());
            edgeNode.put("weight", e.getWeight());
            edgeArray.add(edgeNode);
        }
        return edgeArray;
    }
}
