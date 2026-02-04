package com.initializer.graph;


// This enum defines the set of possible attacker actions that allow movement between nodes in the attack graph
public enum AttackType {
    // Attacker gains initial access by tricking a user into revealing credentials
    PHISHING_CREDENTIAL_THEFT,


    // Attacker exploits a vulnerable or poorly secured web application
    WEB_APP_EXPLOIT,


    // Attacker gains access using stolen or leaked VPN credentials
    VPN_CREDENTIAL_THEFT,


    // Attacker delivers malicious software to a system
    MALWARE_DELIVERY,


    // Attacker remotely logs into a system using valid credentials (RDP/SSH)
    REMOTE_LOGIN,


    // Attacker reuses compromised credentials or session tokens
    CREDENTIAL_REUSE,
   

    // Attacker abuses password reset flows or single sign-on mechanisms
    PASSWORD_RESET_SSO_ABUSE,


    // Attacker steals OAuth tokens or abuses SaaS single sign-on integrations
    OAUTH_TOKEN_THEFT,


    // Attacker gains elevated access due to excessive or misconfigured roles
    OVER_PRIVILEGED_ROLE_ASSIGNMENT,


    // Attacker accesses shared files or network drives
    FILE_SERVER_ACCESS,


    // Attacker escalates privileges or dumps credentials on a compromised system
    PRIVILEGE_ESCALATION,


    // Attacker directly accesses sensitive data over the internal network
    DIRECT_NETWORK_ACCESS,


    // Attacker extracts stored credentials or configuration data
    STORED_CREDENTIAL_LEAK,


    // Attacker accesses data via third-party API integrations or data sync
    API_DATA_SYNC,


     // Attacker uses administrative privileges to access sensitive databases
    ADMIN_DATABASE_ACCESS
}

