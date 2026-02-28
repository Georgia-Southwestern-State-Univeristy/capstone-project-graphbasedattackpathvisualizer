package com.initializer.config;

import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import com.initializer.entity.NodeEntity;
import com.initializer.entity.EdgeEntity;
import com.initializer.entity.MitigationEffectEntity;
import com.initializer.entity.MitigationEntity;
import com.initializer.graph.NodeType;
import com.initializer.graph.AttackEdgeCatalog;
import com.initializer.graph.AttackEdgeDefinition;
import com.initializer.repository.NodeRepository;
import com.initializer.repository.EdgeRepository;
import com.initializer.repository.MitigationEffectRepository;
import com.initializer.repository.MitigationRepository;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Component
public class DataInitializer implements CommandLineRunner {

    private final NodeRepository nodeRepository;
    private final EdgeRepository edgeRepository;
    private final MitigationRepository mitigationRepository;
    private final MitigationEffectRepository mitigationEffectRepository;

    

    public DataInitializer(NodeRepository nodeRepository,
                       EdgeRepository edgeRepository,
                       MitigationRepository mitigationRepository,
                       MitigationEffectRepository mitigationEffectRepository) {
    this.nodeRepository = nodeRepository;
    this.edgeRepository = edgeRepository;
    this.mitigationRepository = mitigationRepository;
    this.mitigationEffectRepository = mitigationEffectRepository;
}

    @Override
    public void run(String... args) {
        seedNodes();
        seedEdges();
        seedMitigations();
        seedMitigationEffects();
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

    private void seedMitigations() {

    if (mitigationRepository.count() > 0) {
        System.out.println("Mitigations already seeded. Skipping.");
        return;
    }

    System.out.println("Seeding mitigations...");

    mitigationRepository.save(new MitigationEntity("Email MFA",
            "Multi-factor authentication for employee email accounts."));

    mitigationRepository.save(new MitigationEntity("Web App Hardening",
            "Input validation and patching of the web application."));

    mitigationRepository.save(new MitigationEntity("VPN / Remote Access MFA",
            "Multi-factor authentication for VPN access."));

    mitigationRepository.save(new MitigationEntity("Endpoint Detection & Response",
            "Malware detection and behavioral monitoring."));

    mitigationRepository.save(new MitigationEntity("Remote Access Hardening",
            "Restrict and secure remote login mechanisms such as RDP and SSH."));

    mitigationRepository.save(new MitigationEntity("Conditional Access",
            "Context-aware login restrictions such as device trust or location checks."));

    mitigationRepository.save(new MitigationEntity("Identity Provider Hardening",
            "Secure password reset processes and enforce MFA at IdP level."));

    mitigationRepository.save(new MitigationEntity("SaaS Application Security Controls",
            "MFA enforcement and application permission restrictions."));

    mitigationRepository.save(new MitigationEntity("Role-Based Access Control (RBAC) Enforcement",
            "Least privilege enforcement and role approval workflows."));

    mitigationRepository.save(new MitigationEntity("File Server Access Controls",
            "Access control lists and restricted share permissions."));

    mitigationRepository.save(new MitigationEntity("Privileged Account Hardening",
            "Admin MFA, least privilege, and separate admin accounts."));

    mitigationRepository.save(new MitigationEntity("Network Segmentation",
            "Firewall rules and restricted internal network access."));

    System.out.println("Mitigations seeded successfully.");
}

private void seedMitigationEffects() {

    if (mitigationEffectRepository.count() > 0) {
        System.out.println("Mitigation effects already seeded. Skipping.");
        return;
    }

    System.out.println("Seeding mitigation effects...");

    List<EdgeEntity> edges = edgeRepository.findAll();
    List<MitigationEntity> mitigations = mitigationRepository.findAll();

    Map<String, MitigationEntity> mitMap = mitigations.stream()
            .collect(Collectors.toMap(MitigationEntity::getMitName, m -> m));

    // Helper edge lookups
    EdgeEntity attackerToEmail = findEdge("ATTACKER", "EMPLOYEE_EMAIL", edges);
    EdgeEntity attackerToWeb = findEdge("ATTACKER", "WEB_APP", edges);
    EdgeEntity attackerToVpn = findEdge("ATTACKER", "VPN", edges);
    EdgeEntity webToWorkstation = findEdge("WEB_APP", "EMPLOYEE_WORKSTATION", edges);
    EdgeEntity vpnToWorkstation = findEdge("VPN", "EMPLOYEE_WORKSTATION", edges);
    EdgeEntity emailToWorkstation = findEdge("EMPLOYEE_EMAIL", "EMPLOYEE_WORKSTATION", edges);
    EdgeEntity emailToIdp = findEdge("EMPLOYEE_EMAIL", "IDENTITY_PROVIDER", edges);
    EdgeEntity emailToSaas = findEdge("EMPLOYEE_EMAIL", "THIRD_PARTY_SAAS", edges);
    EdgeEntity idpToAdmin = findEdge("IDENTITY_PROVIDER", "ADMIN_ACCOUNT", edges);
    EdgeEntity workstationToFile = findEdge("EMPLOYEE_WORKSTATION", "FILE_SERVER", edges);
    EdgeEntity workstationToAdmin = findEdge("EMPLOYEE_WORKSTATION", "ADMIN_ACCOUNT", edges);
    EdgeEntity workstationToDb = findEdge("EMPLOYEE_WORKSTATION", "CUSTOMER_DB", edges);

    mitigationEffectRepository.save(new MitigationEffectEntity(mitMap.get("Email MFA"), attackerToEmail, 6));
    mitigationEffectRepository.save(new MitigationEffectEntity(mitMap.get("Web App Hardening"), attackerToWeb, 4));
    mitigationEffectRepository.save(new MitigationEffectEntity(mitMap.get("VPN / Remote Access MFA"), attackerToVpn, 5));
    mitigationEffectRepository.save(new MitigationEffectEntity(mitMap.get("Endpoint Detection & Response"), webToWorkstation, 3));
    mitigationEffectRepository.save(new MitigationEffectEntity(mitMap.get("Remote Access Hardening"), vpnToWorkstation, 3));
    mitigationEffectRepository.save(new MitigationEffectEntity(mitMap.get("Conditional Access"), emailToWorkstation, 4));
    mitigationEffectRepository.save(new MitigationEffectEntity(mitMap.get("Identity Provider Hardening"), emailToIdp, 4));
    mitigationEffectRepository.save(new MitigationEffectEntity(mitMap.get("SaaS Application Security Controls"), emailToSaas, 3));
    mitigationEffectRepository.save(new MitigationEffectEntity(mitMap.get("Role-Based Access Control (RBAC) Enforcement"), idpToAdmin, 3));
    mitigationEffectRepository.save(new MitigationEffectEntity(mitMap.get("File Server Access Controls"), workstationToFile, 2));
    mitigationEffectRepository.save(new MitigationEffectEntity(mitMap.get("Privileged Account Hardening"), workstationToAdmin, 5));
    mitigationEffectRepository.save(new MitigationEffectEntity(mitMap.get("Network Segmentation"), workstationToDb, 3));

    System.out.println("Mitigation effects seeded successfully.");
}

private EdgeEntity findEdge(String sourceType,
                            String targetType,
                            List<EdgeEntity> edges) {

    return edges.stream()
            .filter(e ->
                    e.getSourceNode().getNodeType().equals(sourceType)
                    && e.getTargetNode().getNodeType().equals(targetType))
            .findFirst()
            .orElseThrow(() -> new RuntimeException(
                    "Edge not found: " + sourceType + " -> " + targetType
            ));
}

}
