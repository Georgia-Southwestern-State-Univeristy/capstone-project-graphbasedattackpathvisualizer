package com.initializer.services;

import java.util.List;

// DTO representing the structured attack path summary data
// passed from the algorithm/business logic layer to the AI analysis layer.
public class AttackPathSummaryRequest {

    private String source;
    private String target;
    private double totalCost;
    private List<String> pathNodes;
    private List<AttackPathSummaryStepDTO> steps;
    private List<String> enabledMitigations;
    private List<String> recommendedMitigations;
    private BusinessProfileDTO infrastructure;

    public AttackPathSummaryRequest(String source,
                                    String target,
                                    double totalCost,
                                    List<String> pathNodes,
                                    List<AttackPathSummaryStepDTO> steps,
                                    List<String> enabledMitigations,
                                    List<String> recommendedMitigations,
                                    BusinessProfileDTO infrastructure) {
        this.source = source;
        this.target = target;
        this.totalCost = totalCost;
        this.pathNodes = pathNodes;
        this.steps = steps;
        this.enabledMitigations = enabledMitigations;
        this.recommendedMitigations = recommendedMitigations;
        this.infrastructure = infrastructure;
    }

    public String getSource() {
        return source;
    }

    public String getTarget() {
        return target;
    }

    public double getTotalCost() {
        return totalCost;
    }

    public List<String> getPathNodes() {
        return pathNodes;
    }

    public List<AttackPathSummaryStepDTO> getSteps() {
        return steps;
    }

    public List<String> getEnabledMitigations() {
        return enabledMitigations;
    }

    public List<String> getRecommendedMitigations() {
        return recommendedMitigations;
    }

    public BusinessProfileDTO getInfrastructure() {
        return infrastructure;
    }
}