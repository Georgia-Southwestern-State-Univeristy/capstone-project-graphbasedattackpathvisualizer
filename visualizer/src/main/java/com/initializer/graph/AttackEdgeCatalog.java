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


        // ------------------------------------------------ From admin account


        new AttackEdgeDefinition(
            NodeType.ADMIN_ACCOUNT,
            NodeType.CUSTOMER_DB,
            AttackType.ADMIN_DATABASE_ACCESS,
            "Admin DB Access"
        )
    );
}


