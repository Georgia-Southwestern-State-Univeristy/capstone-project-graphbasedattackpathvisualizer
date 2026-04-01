package com.initializer.services;

import java.util.List;

// DTO representing the AI-generated attack path analysis
// returned from the backend to the frontend.
public class AttackPathSummaryResponse {

    private String summary;
    private String riskLevel;
    private String topRecommendation;
    private String weakestPoint;
    private String businessImpact;
    private List<String> recommendedMitigations;
    private List<MitigationDetailDTO> mitigationDetails;

    public AttackPathSummaryResponse() {
    }

    public AttackPathSummaryResponse(String summary,
                                     String riskLevel,
                                     String topRecommendation,
                                     String weakestPoint,
                                     String businessImpact,
                                     List<String> recommendedMitigations,
                                     List<MitigationDetailDTO> mitigationDetails) {
        this.summary = summary;
        this.riskLevel = riskLevel;
        this.topRecommendation = topRecommendation;
        this.weakestPoint = weakestPoint;
        this.businessImpact = businessImpact;
        this.recommendedMitigations = recommendedMitigations;
        this.mitigationDetails = mitigationDetails;
    }

    public String getSummary() {
        return summary;
    }

    public void setSummary(String summary) {
        this.summary = summary;
    }

    public String getRiskLevel() {
        return riskLevel;
    }

    public void setRiskLevel(String riskLevel) {
        this.riskLevel = riskLevel;
    }

    public String getTopRecommendation() {
    return topRecommendation;
    }

    public void setTopRecommendation(String topRecommendation) {
        this.topRecommendation = topRecommendation;
    }

    public String getWeakestPoint() {
        return weakestPoint;
    }

    public void setWeakestPoint(String weakestPoint) {
        this.weakestPoint = weakestPoint;
    }

    public String getBusinessImpact() {
        return businessImpact;
    }

    public void setBusinessImpact(String businessImpact) {
        this.businessImpact = businessImpact;
    }

    public List<String> getRecommendedMitigations() {
        return recommendedMitigations;
    }

    public void setRecommendedMitigations(List<String> recommendedMitigations) {
        this.recommendedMitigations = recommendedMitigations;
    }

    public List<MitigationDetailDTO> getMitigationDetails() {
        return mitigationDetails;
    }

    public void setMitigationDetails(List<MitigationDetailDTO> mitigationDetails) {
        this.mitigationDetails = mitigationDetails;
    }
}