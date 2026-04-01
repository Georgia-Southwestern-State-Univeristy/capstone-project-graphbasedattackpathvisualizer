package com.initializer.services;

import java.util.List;

// DTO representing the frontend request to generate
// an AI-based attack path summary.
public class GenerateAttackSummaryRequest {

    private String source;
    private String target;
    private List<Integer> mitigations;

    public GenerateAttackSummaryRequest() {
    }

    public GenerateAttackSummaryRequest(String source,
                                        String target,
                                        List<Integer> mitigations) {
        this.source = source;
        this.target = target;
        this.mitigations = mitigations;
    }

    public String getSource() {
        return source;
    }

    public void setSource(String source) {
        this.source = source;
    }

    public String getTarget() {
        return target;
    }

    public void setTarget(String target) {
        this.target = target;
    }

    public List<Integer> getMitigations() {
        return mitigations;
    }

    public void setMitigations(List<Integer> mitigations) {
        this.mitigations = mitigations;
    }
}
