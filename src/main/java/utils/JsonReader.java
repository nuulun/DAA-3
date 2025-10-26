package utils;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.File;
import java.io.IOException;
import java.util.*;
import model.Edge;
import model.Graph;

public class JsonReader {

    public static List<Graph> readGraphsFromFile(String filePath) {
        List<Graph> graphs = new ArrayList<>();
        ObjectMapper mapper = new ObjectMapper();

        try {
            JsonNode root = mapper.readTree(new File(filePath));
            for (JsonNode graphNode : root.get("graphs")) {
                int id = graphNode.get("id").asInt();

                // Читаем вершины
                List<String> nodes = new ArrayList<>();
                for (JsonNode n : graphNode.get("nodes")) {
                    nodes.add(n.asText());
                }

                // Читаем рёбра
                List<Edge> edges = new ArrayList<>();
                for (JsonNode edge : graphNode.get("edges")) {
                    String from = edge.get("from").asText();
                    String to = edge.get("to").asText();
                    int weight = edge.get("weight").asInt();
                    edges.add(new Edge(from, to, weight));
                }

                graphs.add(new Graph(id, nodes, edges));
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        return graphs;
    }
}
