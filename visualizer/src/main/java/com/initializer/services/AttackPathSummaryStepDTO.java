package com.initializer.services;

import java.util.List;

// DTO representing one step in the computed attack path to be passed to the AI analysis layer
public class AttackPathSummaryStepDTO {

    private String fromNode;
    private String toNode;
    private String attackAction;
    private int weight;
    private List<String> recommendedMitigations;

    public AttackPathSummaryStepDTO(String fromNode,
                                    String toNode,
                                    String attackAction,
                                    int weight,
                                    List<String> recommendedMitigations) {
        this.fromNode = fromNode;
        this.toNode = toNode;
        this.attackAction = attackAction;
        this.weight = weight;
        this.recommendedMitigations = recommendedMitigations;
    }

    public String getFromNode() {
        return fromNode;
    }

    public String getToNode() {
        return toNode;
    }

    public String getAttackAction() {
        return attackAction;
    }

    public int getWeight() {
        return weight;
    }

    public List<String> getRecommendedMitigations() {
        return recommendedMitigations;
    }
}
