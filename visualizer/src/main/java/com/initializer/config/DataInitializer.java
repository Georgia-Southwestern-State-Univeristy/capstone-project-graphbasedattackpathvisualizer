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

        // Original nodes
        nodeRepository.save(new NodeEntity(NodeType.ATTACKER.name(), "Attacker"));
        nodeRepository.save(new NodeEntity(NodeType.EMPLOYEE_EMAIL.name(), "Employee Email"));
        nodeRepository.save(new NodeEntity(NodeType.VPN.name(), "VPN / Remote Access"));
        nodeRepository.save(new NodeEntity(NodeType.WEB_APP.name(), "Company Website / Web App"));
        nodeRepository.save(new NodeEntity(NodeType.EMPLOYEE_WORKSTATION.name(), "Employee Workstation"));
        nodeRepository.save(new NodeEntity(NodeType.IDENTITY_PROVIDER.name(), "Identity Provider"));
        nodeRepository.save(new NodeEntity(NodeType.ADMIN_ACCOUNT.name(), "Admin Account"));
        nodeRepository.save(new NodeEntity(NodeType.FILE_SERVER.name(), "File Server"));
        nodeRepository.save(new NodeEntity(NodeType.CUSTOMER_DB.name(), "Sensitive Data Store"));
        nodeRepository.save(new NodeEntity(NodeType.THIRD_PARTY_SAAS.name(), "Third-Party SaaS"));

        // New nodes for expanded 20-node graph
        nodeRepository.save(new NodeEntity(NodeType.EMAIL_SERVER.name(), "Email Server"));
        nodeRepository.save(new NodeEntity(NodeType.DOMAIN_CONTROLLER.name(), "Domain Controller"));
        nodeRepository.save(new NodeEntity(NodeType.INTERNAL_APP.name(), "Internal App"));
        nodeRepository.save(new NodeEntity(NodeType.HR_SYSTEM.name(), "HR System"));
        nodeRepository.save(new NodeEntity(NodeType.FINANCE_SYSTEM.name(), "Finance System"));
        nodeRepository.save(new NodeEntity(NodeType.BACKUP_SERVER.name(), "Backup Server"));
        nodeRepository.save(new NodeEntity(NodeType.MDM_SERVER.name(), "MDM Server"));
        nodeRepository.save(new NodeEntity(NodeType.WIRELESS_ACCESS_POINT.name(), "Wireless Access Point"));
        nodeRepository.save(new NodeEntity(NodeType.FIREWALL.name(), "Firewall"));
        nodeRepository.save(new NodeEntity(NodeType.DNS_SERVER.name(), "DNS Server"));

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

        mitigationRepository.save(new MitigationEntity("Wireless Security Hardening",
                "Strong encryption and secure configuration of wireless networks."));

        mitigationRepository.save(new MitigationEntity("Perimeter Firewall Hardening",
                "Patch and secure firewall and perimeter devices."));

        mitigationRepository.save(new MitigationEntity("Internal Application Hardening",
                "Secure internal applications with proper authentication and patching."));

        mitigationRepository.save(new MitigationEntity("Email Server Hardening",
                "Secure email infrastructure and restrict mailbox abuse."));

        mitigationRepository.save(new MitigationEntity("Email Security Filtering",
                "Attachment scanning, link protection, and phishing detection for email systems."));

        mitigationRepository.save(new MitigationEntity("Domain Controller Hardening",
                "Secure Active Directory and domain controller configurations."));

        mitigationRepository.save(new MitigationEntity("HR System Access Controls",
                "Restrict and monitor access to HR systems."));

        mitigationRepository.save(new MitigationEntity("Finance System Access Controls",
                "Restrict and monitor access to financial systems."));

        mitigationRepository.save(new MitigationEntity("Backup Server Protection",
                "Protect backups with access controls and encryption."));

        mitigationRepository.save(new MitigationEntity("MDM Security Controls",
                "Secure mobile device management systems and enforce policies."));

        mitigationRepository.save(new MitigationEntity("DNS Security Monitoring",
                "Monitor DNS activity to detect suspicious behavior."));

        mitigationRepository.save(new MitigationEntity("DNS Access Restrictions",
            "Restrict and harden access to internal DNS infrastructure."));

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
        EdgeEntity attackerToWireless = findEdge("ATTACKER", "WIRELESS_ACCESS_POINT", edges);
        EdgeEntity attackerToFirewall = findEdge("ATTACKER", "FIREWALL", edges);
        EdgeEntity firewallToInternal = findEdge("FIREWALL", "INTERNAL_APP", edges);
        EdgeEntity emailToEmailServer = findEdge("EMPLOYEE_EMAIL", "EMAIL_SERVER", edges);
        EdgeEntity emailServerToWorkstation = findEdge("EMAIL_SERVER", "EMPLOYEE_WORKSTATION", edges);
        EdgeEntity workstationToDomain = findEdge("EMPLOYEE_WORKSTATION", "DOMAIN_CONTROLLER", edges);
        EdgeEntity domainToAdmin = findEdge("DOMAIN_CONTROLLER", "ADMIN_ACCOUNT", edges);
        EdgeEntity workstationToInternal = findEdge("EMPLOYEE_WORKSTATION", "INTERNAL_APP", edges);
        EdgeEntity workstationToHR = findEdge("EMPLOYEE_WORKSTATION", "HR_SYSTEM", edges);
        EdgeEntity workstationToFinance = findEdge("EMPLOYEE_WORKSTATION", "FINANCE_SYSTEM", edges);
        EdgeEntity workstationToBackup = findEdge("EMPLOYEE_WORKSTATION", "BACKUP_SERVER", edges);
        EdgeEntity workstationToMDM = findEdge("EMPLOYEE_WORKSTATION", "MDM_SERVER", edges);
        EdgeEntity mdmToAdmin = findEdge("MDM_SERVER", "ADMIN_ACCOUNT", edges);
        EdgeEntity workstationToDNS = findEdge("EMPLOYEE_WORKSTATION", "DNS_SERVER", edges);
        EdgeEntity dnsToDomain = findEdge("DNS_SERVER", "DOMAIN_CONTROLLER", edges);
        EdgeEntity wirelessToWorkstation = findEdge("WIRELESS_ACCESS_POINT", "EMPLOYEE_WORKSTATION", edges);
        EdgeEntity internalToDb = findEdge("INTERNAL_APP", "CUSTOMER_DB", edges);
        EdgeEntity hrToDb = findEdge("HR_SYSTEM", "CUSTOMER_DB", edges);
        EdgeEntity financeToDb = findEdge("FINANCE_SYSTEM", "CUSTOMER_DB", edges);
        EdgeEntity backupToDb = findEdge("BACKUP_SERVER", "CUSTOMER_DB", edges);

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
        mitigationEffectRepository.save(new MitigationEffectEntity(mitMap.get("Wireless Security Hardening"), attackerToWireless, 4));
        mitigationEffectRepository.save(new MitigationEffectEntity(mitMap.get("Perimeter Firewall Hardening"), attackerToFirewall, 5));
        mitigationEffectRepository.save(new MitigationEffectEntity(mitMap.get("Internal Application Hardening"), firewallToInternal, 3));
        mitigationEffectRepository.save(new MitigationEffectEntity(mitMap.get("Internal Application Hardening"), workstationToInternal, 3));
        mitigationEffectRepository.save(new MitigationEffectEntity(mitMap.get("Email Server Hardening"), emailToEmailServer, 3));
        mitigationEffectRepository.save(new MitigationEffectEntity(mitMap.get("Email Security Filtering"), emailServerToWorkstation, 3));
        mitigationEffectRepository.save(new MitigationEffectEntity(mitMap.get("Domain Controller Hardening"), workstationToDomain, 5));
        mitigationEffectRepository.save(new MitigationEffectEntity(mitMap.get("Domain Controller Hardening"), domainToAdmin, 4));
        mitigationEffectRepository.save(new MitigationEffectEntity(mitMap.get("HR System Access Controls"), workstationToHR, 3));
        mitigationEffectRepository.save(new MitigationEffectEntity(mitMap.get("Finance System Access Controls"), workstationToFinance, 4));
        mitigationEffectRepository.save(new MitigationEffectEntity(mitMap.get("Backup Server Protection"), workstationToBackup, 3));
        mitigationEffectRepository.save(new MitigationEffectEntity(mitMap.get("MDM Security Controls"), workstationToMDM, 4));
        mitigationEffectRepository.save(new MitigationEffectEntity(mitMap.get("MDM Security Controls"), mdmToAdmin, 4));
        mitigationEffectRepository.save(new MitigationEffectEntity(mitMap.get("DNS Security Monitoring"), workstationToDNS, 2));
        mitigationEffectRepository.save(new MitigationEffectEntity(mitMap.get("DNS Access Restrictions"), dnsToDomain, 3));
        mitigationEffectRepository.save(new MitigationEffectEntity(mitMap.get("Wireless Security Hardening"), wirelessToWorkstation, 3));
        mitigationEffectRepository.save(new MitigationEffectEntity(mitMap.get("Internal Application Hardening"), internalToDb, 4));
        mitigationEffectRepository.save(new MitigationEffectEntity(mitMap.get("HR System Access Controls"), hrToDb, 3));
        mitigationEffectRepository.save(new MitigationEffectEntity(mitMap.get("Finance System Access Controls"), financeToDb, 3));
        mitigationEffectRepository.save(new MitigationEffectEntity(mitMap.get("Backup Server Protection"), backupToDb, 3));
        

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
