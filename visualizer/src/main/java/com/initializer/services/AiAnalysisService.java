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
        prompt.append("Never include any mitigation from the Enabled Mitigations list in recommendedMitigations, mitigationDetails, or as the mitigation named in topRecommendation.\n");
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
        prompt.append("Prioritize only ADDITIONAL mitigations that are not already enabled.\n");
        prompt.append("Do not recommend any mitigation that appears in the Enabled Mitigations list.\n");
        prompt.append("Do not tell the user to implement, enable, or add a mitigation that is already enabled.\n");
        prompt.append("Use the following priority meanings:\n");
        prompt.append("- PRIMARY: most important additional mitigation to implement first\n");
        prompt.append("- SECONDARY: helpful additional mitigation, but not the first control to implement\n");
        prompt.append("- DEFENSE-IN-DEPTH: optional added protection beyond the primary recommendations\n");
        prompt.append("If one or more recommended mitigations remain after excluding enabled mitigations, topRecommendation must name the single best additional mitigation and explain why.\n");
        prompt.append("If no recommended mitigations remain after excluding enabled mitigations, topRecommendation must clearly state that the key recommended mitigations are already enabled and briefly describe any remaining residual risk.\n");
        prompt.append("If no recommended mitigations remain after excluding enabled mitigations, recommendedMitigations must be an empty list.\n");
        prompt.append("If no recommended mitigations remain after excluding enabled mitigations, mitigationDetails must be an empty list.\n");
        prompt.append("For each mitigation in mitigationDetails, explain how it makes it harder for the attacker to perform a real-world action (such as gaining access, moving laterally, or extracting data), and clearly describe the security impact in plain language.\n");
        prompt.append("Do NOT describe the mitigation using phrases like 'reduces the X to Y step' or restate graph transitions. Focus on attacker behavior, not system steps.\n");
        prompt.append("Use phrasing like: 'This mitigation makes it harder for the attacker to...' and explain the mechanism and impact.\n");
        prompt.append("When one or more relevant mitigations are already enabled, the summary, weakestPoint, and businessImpact must describe the REMAINING or RESIDUAL risk, not the unmitigated risk.\n");
        prompt.append("Do not describe an already mitigated step as completely unprotected.\n");
        prompt.append("If the overall riskLevel is LOW, use calmer wording that reflects reduced but remaining risk.\n");
        prompt.append("If no additional recommended mitigations remain, weakestPoint should identify the most exposed remaining step or entry point, while acknowledging that relevant protections are already enabled.\n");
        prompt.append("If no additional recommended mitigations remain, summary should explain that the path still exists but that enabled protections have already reduced risk.\n");
        prompt.append("If no additional recommended mitigations remain, businessImpact should describe potential impact only if the remaining controls are bypassed or fail.\n");

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