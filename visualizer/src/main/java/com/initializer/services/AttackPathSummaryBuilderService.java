package com.initializer.services;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import org.springframework.stereotype.Service;

import com.initializer.entity.BusinessProfileEntity;
import com.initializer.entity.UserEntity;
import com.initializer.graph.Edge;
import com.initializer.graph.Node;

@Service
public class AttackPathSummaryBuilderService {

    private final ShortestPathService shortestPathService;
    private final BusinessProfileService businessProfileService;
    private final GraphService graphService;
    private final MitigationHelperService mitigationHelperService;

    public AttackPathSummaryBuilderService(ShortestPathService shortestPathService,
                                           BusinessProfileService businessProfileService,
                                           GraphService graphService,
                                           MitigationHelperService mitigationHelperService) {
        this.shortestPathService = shortestPathService;
        this.businessProfileService = businessProfileService;
        this.graphService = graphService;
        this.mitigationHelperService = mitigationHelperService;
    }

    public AttackPathSummaryRequest buildSummaryRequest(UserEntity user,
                                                        String source,
                                                        String target,
                                                        List<Integer> enabledMitigationIds) {

        AttackPathResult pathResult =
                shortestPathService.computeAttackPath(user, source, target, enabledMitigationIds);

        BusinessProfileEntity profile =
                businessProfileService.getProfileByUser(user);

        BusinessProfileDTO infrastructure =
                businessProfileService.toDTO(profile);

        List<String> pathNodes = new ArrayList<>();
        for (Node node : pathResult.getNodes()) {
            pathNodes.add(node.getDisplayName());
        }

        List<AttackPathSummaryStepDTO> steps = new ArrayList<>();
        Set<String> allRecommendedMitigations = new LinkedHashSet<>();

        List<Node> nodes = pathResult.getNodes();
        List<Edge> edges = pathResult.getEdges();

        for (int i = 0; i < edges.size(); i++) {
            Node fromNode = nodes.get(i);
            Node toNode = nodes.get(i + 1);
            Edge edge = edges.get(i);

            List<String> recommendedMitigations =
                    graphService.getRecommendedMitigationsForStep(
                            fromNode.getId(),
                            toNode.getId(),
                            edge.getAttackAction(),
                            enabledMitigationIds
                    );

            allRecommendedMitigations.addAll(recommendedMitigations);

            steps.add(new AttackPathSummaryStepDTO(
                    fromNode.getDisplayName(),
                    toNode.getDisplayName(),
                    edge.getAttackAction(),
                    edge.getWeight(),
                    recommendedMitigations
            ));
        }

        List<String> enabledMitigationNames =
                mitigationHelperService.getMitigationNamesByIds(enabledMitigationIds);

        String readableSource = nodes.get(0).getDisplayName();
        String readableTarget = nodes.get(nodes.size() - 1).getDisplayName();

        return new AttackPathSummaryRequest(
                readableSource,
                readableTarget,
                pathResult.getTotalCost(),
                pathNodes,
                steps,
                enabledMitigationNames,
                new ArrayList<>(allRecommendedMitigations),
                infrastructure
        );
    }
}