package model;

import java.util.List;

public class MSTResult {
    private List<Edge> mstEdges;
    private int totalCost;
    private int operationCount;
    private double executionTimeMs;

    public MSTResult(List<Edge> mstEdges, int totalCost, int operationCount, double executionTimeMs) {
        this.mstEdges = mstEdges;
        this.totalCost = totalCost;
        this.operationCount = operationCount;
        this.executionTimeMs = executionTimeMs;
    }

    public List<Edge> getMstEdges() { return mstEdges; }
    public int getTotalCost() { return totalCost; }
    public int getOperationCount() { return operationCount; }
    public double getExecutionTimeMs() { return executionTimeMs; }
}
