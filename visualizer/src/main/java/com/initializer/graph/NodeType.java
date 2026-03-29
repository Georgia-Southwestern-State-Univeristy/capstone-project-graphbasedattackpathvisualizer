package com.initializer.graph;

// enum NodeType defines a fixed set of constant types of node values
// that can exist in the small business attack graph

public enum NodeType {
    ATTACKER,
    WEB_APP,
    VPN,
    EMPLOYEE_EMAIL,
    EMPLOYEE_WORKSTATION,
    IDENTITY_PROVIDER,
    ADMIN_ACCOUNT,
    FILE_SERVER,
    THIRD_PARTY_SAAS,
    CUSTOMER_DB,

    // --- New nodes for expanded graph (Task 7)
    EMAIL_SERVER,
    DOMAIN_CONTROLLER,
    INTERNAL_APP,
    HR_SYSTEM,
    FINANCE_SYSTEM,
    BACKUP_SERVER,
    MDM_SERVER,
    WIRELESS_ACCESS_POINT,
    FIREWALL,
    DNS_SERVER
}
