package main;

import algorithm.KruskalAlgorithm;
import algorithm.PrimAlgorithm;
import model.Graph;
import model.MSTResult;
import utils.JsonReader;
import utils.JsonWriter;
import java.util.*;

public class Main {
    public static void main(String[] args) {
        List<Graph> graphs = JsonReader.readGraphsFromFile("data/input.json");
        List<MSTResult> primResults = new ArrayList<>();
        List<MSTResult> kruskalResults = new ArrayList<>();

        for (Graph g : graphs) {
            primResults.add(PrimAlgorithm.findMST(g));
            kruskalResults.add(KruskalAlgorithm.findMST(g));
        }

        JsonWriter.writeResultsToFile("data/output.json", primResults, kruskalResults);
        System.out.println("Results written to output.json");
    }
}
