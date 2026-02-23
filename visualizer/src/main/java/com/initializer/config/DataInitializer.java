package com.initializer.config;

import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import com.initializer.entity.NodeEntity;
import com.initializer.entity.EdgeEntity;
import com.initializer.graph.NodeType;
import com.initializer.graph.AttackEdgeCatalog;
import com.initializer.graph.AttackEdgeDefinition;
import com.initializer.repository.NodeRepository;
import com.initializer.repository.EdgeRepository;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Component
public class DataInitializer implements CommandLineRunner {

    private final NodeRepository nodeRepository;
    private final EdgeRepository edgeRepository;

    public DataInitializer(NodeRepository nodeRepository,
                           EdgeRepository edgeRepository) {
        this.nodeRepository = nodeRepository;
        this.edgeRepository = edgeRepository;
    }

    @Override
    public void run(String... args) {
        seedNodes();
        seedEdges();
    }

    private void seedNodes() {

        if (nodeRepository.count() > 0) {
            System.out.println("Nodes already seeded. Skipping.");
            return;
        }

        System.out.println("Seeding nodes...");

        nodeRepository.save(new NodeEntity(NodeType.ATTACKER.name(), "Attacker"));
        nodeRepository.save(new NodeEntity(NodeType.EMPLOYEE_EMAIL.name(), "Employee Email"));
        nodeRepository.save(new NodeEntity(NodeType.VPN.name(), "VPN / Remote Access"));
        nodeRepository.save(new NodeEntity(NodeType.WEB_APP.name(), "Company Website / Web App"));
        nodeRepository.save(new NodeEntity(NodeType.EMPLOYEE_WORKSTATION.name(), "Employee Workstation"));
        nodeRepository.save(new NodeEntity(NodeType.IDENTITY_PROVIDER.name(), "Identity Provider"));
        nodeRepository.save(new NodeEntity(NodeType.ADMIN_ACCOUNT.name(), "Admin Account"));
        nodeRepository.save(new NodeEntity(NodeType.FILE_SERVER.name(), "File Server"));
        nodeRepository.save(new NodeEntity(NodeType.CUSTOMER_DB.name(), "Customer Database"));
        nodeRepository.save(new NodeEntity(NodeType.THIRD_PARTY_SAAS.name(), "Third-Party SaaS"));

        System.out.println("Nodes seeded successfully.");
    }

    private void seedEdges() {

        if (edgeRepository.count() > 0) {
            System.out.println("Edges already seeded. Skipping.");
            return;
        }

        System.out.println("Seeding edges...");

        List<NodeEntity> nodes = nodeRepository.findAll();

        Map<String, NodeEntity> nodeMap = nodes.stream()
                .collect(Collectors.toMap(NodeEntity::getNodeType, n -> n));

        for (AttackEdgeDefinition def : AttackEdgeCatalog.ATTACK_EDGES) {

            NodeEntity source = nodeMap.get(def.getFrom().name());
            NodeEntity target = nodeMap.get(def.getTo().name());

            if (source == null || target == null) {
                throw new RuntimeException("Missing node for edge: "
                        + def.getFrom() + " -> " + def.getTo());
            }

            edgeRepository.save(
                    new EdgeEntity(
                            source,
                            target,
                            def.getLabel(),
                            def.getWeight()
                    )
            );
        }

        System.out.println("All predefined edges seeded successfully.");
    }
}
