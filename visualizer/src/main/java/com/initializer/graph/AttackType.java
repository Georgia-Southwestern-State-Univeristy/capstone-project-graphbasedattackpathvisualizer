package com.initializer.graph;


// This enum defines the set of possible attacker actions that allow movement between nodes in the attack graph
public enum AttackType {
    // Attacker gains initial access by tricking a user into revealing credentials
    PHISHING_CREDENTIAL_THEFT(3),


    // Attacker exploits a vulnerable or poorly secured web application
    WEB_APP_EXPLOIT(4),


    // Attacker gains access using stolen or leaked VPN credentials
    VPN_CREDENTIAL_THEFT(5),

    
    // Attacker compromises a wireless network through weak security or exposed management
    WIRELESS_NETWORK_COMPROMISE(4),


    // Attacker exploits a perimeter device such as a firewall
    PERIMETER_DEVICE_EXPLOIT(5),


    // Attacker delivers malicious software to a system
    MALWARE_DELIVERY(4),


    // Attacker delivers a malicious email with a weaponized attachment or link
    MALICIOUS_EMAIL_DELIVERY(3),


    // Attacker remotely logs into a system using valid credentials (RDP/SSH)
    REMOTE_LOGIN(2),


    // Attacker accesses internal systems after gaining local network presence
    LOCAL_NETWORK_ACCESS(2),


    // Attacker pivots into internal services from another compromised system
    INTERNAL_SERVICE_PIVOT(3),


    // Attacker reuses compromised credentials or session tokens
    CREDENTIAL_REUSE(2),
   

    // Attacker abuses password reset flows or single sign-on mechanisms
    PASSWORD_RESET_SSO_ABUSE(4),


    // Attacker steals OAuth tokens or abuses SaaS single sign-on integrations
    OAUTH_TOKEN_THEFT(3),


    // Attacker abuses a compromised mailbox or email server environment
    MAILBOX_SERVER_ABUSE(3),


    // Attacker gains elevated access due to excessive or misconfigured roles
    OVER_PRIVILEGED_ROLE_ASSIGNMENT(5),


    // Attacker gains elevated privileges through domain infrastructure
    DOMAIN_PRIVILEGE_ESCALATION(5),


    // Attacker abuses a mobile device management platform for privileged control
    MDM_PRIVILEGE_ABUSE(5),


    // Attacker accesses shared files or network drives
    FILE_SERVER_ACCESS(2),


    // Attacker escalates privileges or dumps credentials on a compromised system
    PRIVILEGE_ESCALATION(6),


    // Attacker directly accesses sensitive data over the internal network
    DIRECT_NETWORK_ACCESS(3),


    // Attacker accesses an internal business application
    INTERNAL_APPLICATION_ACCESS(2),


    // Attacker accesses an HR system from a compromised workstation
    HR_SYSTEM_ACCESS(2),


    // Attacker accesses a finance system from a compromised workstation
    FINANCE_SYSTEM_ACCESS(3),


    // Attacker accesses a backup system from a compromised workstation
    BACKUP_SYSTEM_ACCESS(3),


    // Attacker abuses a device management system
    DEVICE_MANAGEMENT_ABUSE(4),


    // Attacker performs internal service or DNS/network discovery
    NETWORK_DISCOVERY(2),


    // Attacker extracts stored credentials or configuration data
    STORED_CREDENTIAL_LEAK(3),


    // Attacker accesses data via third-party API integrations or data sync
    API_DATA_SYNC(4),


    // Attacker accesses a database through an internal application layer
    APPLICATION_DATABASE_ACCESS(4),


    // Attacker accesses sensitive HR-related data
    HR_DATA_ACCESS(3),


    // Attacker accesses sensitive financial data
    FINANCIAL_DATA_ACCESS(3),


    // Attacker exposes or extracts data from backups
    BACKUP_DATA_EXPOSURE(3),


     // Attacker uses administrative privileges to access sensitive databases
    ADMIN_DATABASE_ACCESS(1);


    private final int baseWeight;               // Base difficulty weight for this attack type

    AttackType(int baseWeight) {
        this.baseWeight = baseWeight;
    }


    public int getBaseWeight() {                // Returns the base difficulty weight for this attack type
        return baseWeight;
    }
}

