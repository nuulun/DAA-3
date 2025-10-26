package utils;

import java.io.*;
import java.nio.file.*;
import java.util.*;
import com.google.gson.*;
import com.google.gson.annotations.SerializedName;

public class JsonToCsvConverter {

    static class Edge {
        String from;
        String to;
        int weight;
    }

    static class AlgorithmResult {
        @SerializedName("total_cost")
        int totalCost;

        @SerializedName("operations_count")
        int operationsCount;

        @SerializedName("execution_time_ms")
        double executionTimeMs;

        @SerializedName("mst_edges")
        List<Edge> mstEdges;
    }

    static class GraphResult {
        @SerializedName("graph_id")
        int graphId;

        AlgorithmResult prim;
        AlgorithmResult kruskal;
    }

    static class Root {
        List<GraphResult> results;
    }

    public static void main(String[] args) {
        String inputFile = "data/output.json";
        String outputFile = "data/output.csv";

        try {
            String json = Files.readString(Paths.get(inputFile));
            Gson gson = new Gson();
            Root root = gson.fromJson(json, Root.class);

            try (PrintWriter writer = new PrintWriter(new FileWriter(outputFile))) {
                writer.println("Graph ID,Vertices,Edges,Prim Cost,Prim Time (ms),Prim Ops,Kruskal Cost,Kruskal Time (ms),Kruskal Ops");

                for (GraphResult graph : root.results) {
                    int vertices = countVertices(graph.prim.mstEdges);
                    int edges = graph.prim.mstEdges.size();

                    writer.printf(Locale.US,
                            "%d,%d,%d,%d,%.4f,%d,%d,%.4f,%d%n",
                            graph.graphId,
                            vertices,
                            edges,
                            graph.prim.totalCost,
                            graph.prim.executionTimeMs,
                            graph.prim.operationsCount,
                            graph.kruskal.totalCost,
                            graph.kruskal.executionTimeMs,
                            graph.kruskal.operationsCount
                    );
                }
            }

            System.out.println("✅ Конвертация завершена: создан файл " + outputFile);

        } catch (IOException e) {
            System.err.println("Ошибка чтения файла: " + e.getMessage());
        } catch (JsonSyntaxException e) {
            System.err.println("Ошибка парсинга JSON: " + e.getMessage());
        }
    }

    private static int countVertices(List<Edge> edges) {
        Set<String> vertices = new HashSet<>();
        for (Edge e : edges) {
            vertices.add(e.from);
            vertices.add(e.to);
        }
        return vertices.size();
    }
}
