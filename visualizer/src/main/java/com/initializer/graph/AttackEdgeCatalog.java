package com.initializer.graph;


import java.util.List;


// This class catalogs all possible attack edges in the attack graph, defining the source node, target node, attack type, and label for each edge
public class AttackEdgeCatalog {


    // List of all defined attack edges in the attack graph
    public static final List<AttackEdgeDefinition> ATTACK_EDGES = List.of(


        // ------------------------------------------------ Initial Access


        new AttackEdgeDefinition(
            NodeType.ATTACKER,
            NodeType.EMPLOYEE_EMAIL,
            AttackType.PHISHING_CREDENTIAL_THEFT,
            "Phishing / Credential Theft"
        ),


        new AttackEdgeDefinition(
            NodeType.ATTACKER,
            NodeType.WEB_APP,
            AttackType.WEB_APP_EXPLOIT,
            "Exploit Web App / Weak Login"
        ),


         new AttackEdgeDefinition(
            NodeType.ATTACKER,
            NodeType.VPN,
            AttackType.VPN_CREDENTIAL_THEFT,
            "Stolen VPN Credentials"
        ),


        new AttackEdgeDefinition(
            NodeType.ATTACKER,
            NodeType.WIRELESS_ACCESS_POINT,
            AttackType.WIRELESS_NETWORK_COMPROMISE,
            "Wireless Network Compromise"
        ),


        new AttackEdgeDefinition(
            NodeType.ATTACKER,
            NodeType.FIREWALL,
            AttackType.PERIMETER_DEVICE_EXPLOIT,
            "Perimeter Device Exploit"
        ),


        // ------------------------------------------------ From company website / web app


        new AttackEdgeDefinition(
            NodeType.WEB_APP,
            NodeType.EMPLOYEE_WORKSTATION,
            AttackType.MALWARE_DELIVERY,
            "Malware Delivery"
        ),


        // ------------------------------------------------ From VPN / Remote Access


        new AttackEdgeDefinition(
            NodeType.VPN,
            NodeType.EMPLOYEE_WORKSTATION,
            AttackType.REMOTE_LOGIN,
            "Remote Login (RDP / SSH)"
        ),


        // ------------------------------------------------ From wireless access point


        new AttackEdgeDefinition(
            NodeType.WIRELESS_ACCESS_POINT,
            NodeType.EMPLOYEE_WORKSTATION,
            AttackType.LOCAL_NETWORK_ACCESS,
            "Local Network Access"
        ),


        // ------------------------------------------------ From firewall

        new AttackEdgeDefinition(
            NodeType.FIREWALL,
            NodeType.INTERNAL_APP,
            AttackType.INTERNAL_SERVICE_PIVOT,
            "Internal Service Pivot"
        ),


        // ------------------------------------------------ From employee email


        new AttackEdgeDefinition(
            NodeType.EMPLOYEE_EMAIL,
            NodeType.EMPLOYEE_WORKSTATION,
            AttackType.CREDENTIAL_REUSE,
            "Credential Reuse"
        ),


        new AttackEdgeDefinition(
            NodeType.EMPLOYEE_EMAIL,
            NodeType.IDENTITY_PROVIDER,
            AttackType.PASSWORD_RESET_SSO_ABUSE,
            "Password Reset / SSO Abuse"
        ),


        new AttackEdgeDefinition(
            NodeType.EMPLOYEE_EMAIL,
            NodeType.THIRD_PARTY_SAAS,
            AttackType.OAUTH_TOKEN_THEFT,
            "OAuth Token Theft / SSO Abuse"
        ),


        new AttackEdgeDefinition(
            NodeType.EMPLOYEE_EMAIL,
            NodeType.EMAIL_SERVER,
            AttackType.MAILBOX_SERVER_ABUSE,
            "Mailbox / Server Abuse"
        ),


        // ------------------------------------------------ From identity provider


        new AttackEdgeDefinition(
            NodeType.IDENTITY_PROVIDER,
            NodeType.ADMIN_ACCOUNT,
            AttackType.OVER_PRIVILEGED_ROLE_ASSIGNMENT,
            "Over-Privileged Role Assignment"
        ),


        // ------------------------------------------------ From employee workstation


        new AttackEdgeDefinition(
            NodeType.EMPLOYEE_WORKSTATION,
            NodeType.FILE_SERVER,
            AttackType.FILE_SERVER_ACCESS,
            "Access Shared Drive"
        ),


        new AttackEdgeDefinition(
            NodeType.EMPLOYEE_WORKSTATION,
            NodeType.ADMIN_ACCOUNT,
            AttackType.PRIVILEGE_ESCALATION,
            "Privilege Escalation / Credential Dumping"
        ),


        new AttackEdgeDefinition(
            NodeType.EMPLOYEE_WORKSTATION,
            NodeType.CUSTOMER_DB,
            AttackType.DIRECT_NETWORK_ACCESS,
            "Direct Network Access"
        ),


        new AttackEdgeDefinition(
            NodeType.EMPLOYEE_WORKSTATION,
            NodeType.DOMAIN_CONTROLLER,
            AttackType.DOMAIN_PRIVILEGE_ESCALATION,
            "Domain Privilege Escalation"
        ),


        new AttackEdgeDefinition(
            NodeType.EMPLOYEE_WORKSTATION,
            NodeType.INTERNAL_APP,
            AttackType.INTERNAL_APPLICATION_ACCESS,
            "Internal Application Access"
        ),


        new AttackEdgeDefinition(
            NodeType.EMPLOYEE_WORKSTATION,
            NodeType.HR_SYSTEM,
            AttackType.HR_SYSTEM_ACCESS,
            "HR System Access"
        ),


        new AttackEdgeDefinition(
            NodeType.EMPLOYEE_WORKSTATION,
            NodeType.FINANCE_SYSTEM,
            AttackType.FINANCE_SYSTEM_ACCESS,
            "Finance System Access"
        ),


        new AttackEdgeDefinition(
            NodeType.EMPLOYEE_WORKSTATION,
            NodeType.BACKUP_SERVER,
            AttackType.BACKUP_SYSTEM_ACCESS,
            "Backup System Access"
        ),


        new AttackEdgeDefinition(
            NodeType.EMPLOYEE_WORKSTATION,
            NodeType.MDM_SERVER,
            AttackType.DEVICE_MANAGEMENT_ABUSE,
            "Device Management Abuse"
        ),


        new AttackEdgeDefinition(
            NodeType.EMPLOYEE_WORKSTATION,
            NodeType.DNS_SERVER,
            AttackType.NETWORK_DISCOVERY,
            "Network Discovery"
        ),


        // ------------------------------------------------ From DNS Server

        new AttackEdgeDefinition(
        NodeType.DNS_SERVER,
        NodeType.DOMAIN_CONTROLLER,
        AttackType.INTERNAL_SERVICE_PIVOT,
        "Internal Service Pivot"
    ),

        // ------------------------------------------------ From domain controller

        new AttackEdgeDefinition(
            NodeType.DOMAIN_CONTROLLER,
            NodeType.ADMIN_ACCOUNT,
            AttackType.DOMAIN_PRIVILEGE_ESCALATION,
            "Domain Privilege Escalation"
        ),


        // ------------------------------------------------ From email server

        new AttackEdgeDefinition(
            NodeType.EMAIL_SERVER,
            NodeType.EMPLOYEE_WORKSTATION,
            AttackType.MALICIOUS_EMAIL_DELIVERY,
            "Malicious Email Delivery / Mail Rule Abuse"
        ),


        // ------------------------------------------------ From MDM server

        new AttackEdgeDefinition(
            NodeType.MDM_SERVER,
            NodeType.ADMIN_ACCOUNT,
            AttackType.MDM_PRIVILEGE_ABUSE,
            "MDM Privilege Abuse"
        ),


        // ------------------------------------------------ From file server


        new AttackEdgeDefinition(
            NodeType.FILE_SERVER,
            NodeType.CUSTOMER_DB,
            AttackType.STORED_CREDENTIAL_LEAK,
            "Stored Credentials / Config Leak"
        ),


        // ------------------------------------------------ From third-party SaaS app


        new AttackEdgeDefinition(
            NodeType.THIRD_PARTY_SAAS,
            NodeType.CUSTOMER_DB,
            AttackType.API_DATA_SYNC,
            "API Access / Data Sync"
        ),


        // ------------------------------------------------ From internal app

        new AttackEdgeDefinition(
            NodeType.INTERNAL_APP,
            NodeType.CUSTOMER_DB,
            AttackType.APPLICATION_DATABASE_ACCESS,
            "Application Database Access"
        ),


        // ------------------------------------------------ From HR system

        new AttackEdgeDefinition(
            NodeType.HR_SYSTEM,
            NodeType.CUSTOMER_DB,
            AttackType.HR_DATA_ACCESS,
            "HR Data Access / Integration Abuse"
        ),


        // ------------------------------------------------ From finance system

        new AttackEdgeDefinition(
            NodeType.FINANCE_SYSTEM,
            NodeType.CUSTOMER_DB,
            AttackType.FINANCIAL_DATA_ACCESS,
            "Financial Data Access"
        ),


        // ------------------------------------------------ From backup server

        new AttackEdgeDefinition(
            NodeType.BACKUP_SERVER,
            NodeType.CUSTOMER_DB,
            AttackType.BACKUP_DATA_EXPOSURE,
            "Backup Data Exposure"
        ),


        // ------------------------------------------------ From admin account


        new AttackEdgeDefinition(
            NodeType.ADMIN_ACCOUNT,
            NodeType.CUSTOMER_DB,
            AttackType.ADMIN_DATABASE_ACCESS,
            "Admin DB Access"
        )
    );
}


