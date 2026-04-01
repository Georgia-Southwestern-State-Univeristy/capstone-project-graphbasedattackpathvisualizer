package com.initializer.services;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.openai.client.OpenAIClient;
import com.openai.models.responses.Response;
import com.openai.models.responses.ResponseCreateParams;

@Service
public class AiAnalysisService {

    private final OpenAIClient openAIClient;
    private final ObjectMapper objectMapper;

    @Value("${openai.model}")
    private String model;

    public AiAnalysisService(OpenAIClient openAIClient, ObjectMapper objectMapper) {
        this.openAIClient = openAIClient;
        this.objectMapper = objectMapper;
    }

    public AttackPathSummaryResponse generateAttackPathSummary(
            AttackPathSummaryRequest summaryRequest) {

        String prompt = buildPrompt(summaryRequest);

        Response response = openAIClient.responses().create(
                ResponseCreateParams.builder()
                        .model(model)
                        .input(prompt)
                        .build()
        );

        String responseText = response.output().stream()
                .flatMap(item -> item.message().stream())
                .flatMap(message -> message.content().stream())
                .flatMap(content -> content.outputText().stream())
                .map(outputText -> outputText.text())
                .reduce("", (a, b) -> a + b);

        try {
            return objectMapper.readValue(responseText, AttackPathSummaryResponse.class);
        } catch (Exception e) {
            throw new RuntimeException("Failed to parse AI response into JSON.", e);
        }
    }

    private String buildPrompt(AttackPathSummaryRequest summaryRequest) {
        StringBuilder prompt = new StringBuilder();

        // =========================
        // STRICT RULES
        // =========================
        prompt.append("Base your response only on the provided data.\n");
        prompt.append("Do not invent systems, mitigations, attack steps, or system names that are not included.\n");
        prompt.append("Use only the provided mitigation names when recommending mitigations.\n");
        prompt.append("The recommendedMitigations field must contain EXACTLY all items from the Final Recommended Mitigations list, with no omissions and no additions.\n");

        // Risk rules
        prompt.append("Determine the riskLevel using ONLY the Total Cost and these exact ranges:\n");
        prompt.append("- If Total Cost is 0 through 10, riskLevel must be HIGH.\n");
        prompt.append("- If Total Cost is 11 through 15, riskLevel must be MEDIUM.\n");
        prompt.append("- If Total Cost is 16 or greater, riskLevel must be LOW.\n");

        // =========================
        // OUTPUT STRUCTURE
        // =========================
        prompt.append("\nReturn ONLY valid JSON with this exact structure:\n");
        prompt.append("{\n");
        prompt.append("  \"summary\": \"string\",\n");
        prompt.append("  \"riskLevel\": \"HIGH or MEDIUM or LOW\",\n");
        prompt.append("  \"topRecommendation\": \"string\",\n");
        prompt.append("  \"weakestPoint\": \"string\",\n");
        prompt.append("  \"businessImpact\": \"string\",\n");
        prompt.append("  \"recommendedMitigations\": [\"string\", \"string\"],\n");
        prompt.append("  \"mitigationDetails\": [\n");
        prompt.append("    { \"name\": \"string\", \"reason\": \"string\", \"priority\": \"PRIMARY or SECONDARY or DEFENSE-IN-DEPTH\" }\n");
        prompt.append("  ]\n");
        prompt.append("}\n\n");

        // =========================
        // INSTRUCTIONS FOR QUALITY
        // =========================
        prompt.append("The summary must clearly explain WHY this attack path is dangerous in real-world terms.\n");
        prompt.append("Identify the weakest point in the attack path (the easiest or most exposed step).\n");
        prompt.append("Explain the business impact if the attack succeeds (data breach, financial loss, etc).\n");
        prompt.append("Prioritize mitigations by impact (most important first).\n");
        prompt.append("Use the following priority meanings:\n");
        prompt.append("- PRIMARY: most important mitigation to implement first (protects entry point or weakest step)\n");
        prompt.append("- SECONDARY: important but not the first control to implement\n");
        prompt.append("- DEFENSE-IN-DEPTH: adds additional protection but is not critical on its own\n");
        prompt.append("Provide a topRecommendation that tells the user the single best mitigation to implement first and why.\n");
        prompt.append("For each mitigation, explain how it directly blocks or reduces a specific step in the attack path.\n");

        // =========================
        // DATA INPUT
        // =========================
        prompt.append("\nSource: ").append(summaryRequest.getSource()).append("\n");
        prompt.append("Target: ").append(summaryRequest.getTarget()).append("\n");
        prompt.append("Total Cost: ").append(summaryRequest.getTotalCost()).append("\n\n");

        prompt.append("Path Nodes:\n");
        for (String node : summaryRequest.getPathNodes()) {
            prompt.append("- ").append(node).append("\n");
        }

        prompt.append("\nAttack Steps:\n");
        for (AttackPathSummaryStepDTO step : summaryRequest.getSteps()) {
            prompt.append("- From: ").append(step.getFromNode())
                .append(" | To: ").append(step.getToNode())
                .append(" | Action: ").append(step.getAttackAction())
                .append(" | Weight: ").append(step.getWeight())
                .append(" | Available Mitigations: ").append(step.getRecommendedMitigations())
                .append("\n");
        }

        prompt.append("\nEnabled Mitigations:\n");
        for (String mitigation : summaryRequest.getEnabledMitigations()) {
            prompt.append("- ").append(mitigation).append("\n");
        }

        prompt.append("\nFinal Recommended Mitigations:\n");
        for (String mitigation : summaryRequest.getRecommendedMitigations()) {
            prompt.append("- ").append(mitigation).append("\n");
        }

        prompt.append("\nInfrastructure Configuration:\n");
        prompt.append("- Uses VPN: ").append(summaryRequest.getInfrastructure().isUsesVPN()).append("\n");
        prompt.append("- Has File Server: ").append(summaryRequest.getInfrastructure().isHasFileServer()).append("\n");
        prompt.append("- Uses SaaS: ").append(summaryRequest.getInfrastructure().isUsesSaaS()).append("\n");
        prompt.append("- Has Public Web App: ").append(summaryRequest.getInfrastructure().isHasPublicWebApp()).append("\n");
        prompt.append("- Uses Identity Provider: ").append(summaryRequest.getInfrastructure().isUsesIdentityProvider()).append("\n");
        prompt.append("- Has Email Server: ").append(summaryRequest.getInfrastructure().isHasEmailServer()).append("\n");
        prompt.append("- Has Domain Controller: ").append(summaryRequest.getInfrastructure().isHasDomainController()).append("\n");
        prompt.append("- Has Internal App: ").append(summaryRequest.getInfrastructure().isHasInternalApp()).append("\n");
        prompt.append("- Has HR System: ").append(summaryRequest.getInfrastructure().isHasHRSystem()).append("\n");
        prompt.append("- Has Finance System: ").append(summaryRequest.getInfrastructure().isHasFinanceSystem()).append("\n");
        prompt.append("- Has Backup Server: ").append(summaryRequest.getInfrastructure().isHasBackupServer()).append("\n");
        prompt.append("- Has MDM Server: ").append(summaryRequest.getInfrastructure().isHasMDMServer()).append("\n");
        prompt.append("- Has Wireless Access Point: ").append(summaryRequest.getInfrastructure().isHasWirelessAccessPoint()).append("\n");
        prompt.append("- Has Firewall: ").append(summaryRequest.getInfrastructure().isHasFirewall()).append("\n");
        prompt.append("- Has DNS Server: ").append(summaryRequest.getInfrastructure().isHasDNSServer()).append("\n");

        return prompt.toString();
    }
}