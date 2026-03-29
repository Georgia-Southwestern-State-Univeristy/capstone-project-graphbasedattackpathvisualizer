# Base Attack Difficulty Weights

This document defines the base difficulty weights assigned to each attack edge type in the attack graph. These weights are used by the shortest-path algorithm to compute and compare possible attacker paths through the system.

All base weights assume a **baseline environment with no security mitigations in place**, representing a minimally hardened small-business network. Security controls such as MFA, network segmentation, application hardening, and monitoring are modeled separately in later phases.

## Weight Scale Definition

The following relative scale is used:

- **1 – Very Easy:** Trivial once access exists
- **2 – Easy:** Common attacker technique with low effort
- **3 – Moderate:** Requires some skill or favorable conditions
- **4 – Difficult:** Requires technical expertise or exploitable weaknesses
- **5 – Very Difficult:** Requires elevated privileges or significant attacker effort
- **6 – Extremely Difficult:** Advanced exploitation or privilege escalation

Weights are **relative**, not probabilistic, and are intended for comparative analysis rather than real-world likelihood modeling.

---

## Initial Access Attacks

### PHISHING_CREDENTIAL_THEFT — Weight: 3
Phishing relies on successful social engineering and user interaction. While commonly observed, it still requires crafting convincing lures and exploiting human behavior.

### WEB_APP_EXPLOIT — Weight: 4
Exploiting a web application generally requires technical knowledge, vulnerability discovery, or abuse of weak authentication or input handling.

### VPN_CREDENTIAL_THEFT — Weight: 5
Compromising VPN access requires obtaining valid credentials and understanding how to connect to the remote access service. Even without additional protections, VPN access presents a higher technical and exposure barrier than phishing or basic credential reuse.

### WIRELESS_NETWORK_COMPROMISE — Weight: 4
Compromising a wireless network typically requires exploiting weak encryption, poor configuration, or exposed management interfaces. This requires technical knowledge and proximity or access to the network.

### PERIMETER_DEVICE_EXPLOIT — Weight: 5
Exploiting perimeter devices such as firewalls requires identifying exposed services, misconfigurations, or firmware vulnerabilities. These attacks often require advanced technical skill and understanding of network infrastructure.

---

## Lateral Movement and Execution

### MALWARE_DELIVERY — Weight: 4
Delivering malware typically requires chaining vulnerabilities or persuading users to execute malicious code, requiring moderate technical effort.

### REMOTE_LOGIN (RDP / SSH) — Weight: 2
Once valid credentials are obtained and remote access services are exposed, logging into a system is a low-effort attacker action.

### CREDENTIAL_REUSE — Weight: 2
Reusing compromised credentials across systems is a common and low-effort technique once credentials have been obtained.

### LOCAL_NETWORK_ACCESS — Weight: 2
Once connected to the internal network, accessing nearby systems is relatively easy due to implicit trust and open communication between devices.

### INTERNAL_SERVICE_PIVOT — Weight: 3
Pivoting into internal services requires awareness of available systems and the ability to exploit weak authentication or exposed services within the internal network.

### NETWORK_DISCOVERY — Weight: 2
Enumerating internal network services such as DNS or directory infrastructure is a low-effort activity once internal access is obtained.

### MALICIOUS_EMAIL_DELIVERY — Weight: 3
This attack involves abusing a compromised email server to deliver malicious attachments, links, or manipulate mailbox rules to facilitate further compromise. It requires control over email infrastructure and user interaction, making it moderately difficult.

---

## Identity and Access Abuse

### PASSWORD_RESET_SSO_ABUSE — Weight: 4
Abusing password reset or single sign-on workflows requires understanding identity system behavior and exploiting weaknesses in account recovery or authentication flows.

### OAUTH_TOKEN_THEFT — Weight: 3
OAuth tokens may be obtained through phishing, malware, or insecure storage, requiring moderate effort and opportunity.

### OVER_PRIVILEGED_ROLE_ASSIGNMENT — Weight: 5
Gaining elevated access through excessive role assignments requires access to identity systems or exploitation of role misconfiguration, making it a high-effort attack.

### DOMAIN_PRIVILEGE_ESCALATION — Weight: 5
Gaining elevated privileges through domain infrastructure such as Active Directory requires significant effort, including exploiting misconfigurations or credential exposure.

### MDM_PRIVILEGE_ABUSE — Weight: 5
Abusing device management platforms requires access to centralized control systems and the ability to execute privileged actions across managed devices.

### MAILBOX_SERVER_ABUSE — Weight: 3
Abusing an email server or mailbox infrastructure requires access to a compromised email account or related administrative functionality. This can allow attackers to manipulate mail flow, access stored messages, or expand visibility across the organization with moderate effort.

---

## Workstation-Based Escalation

### FILE_SERVER_ACCESS — Weight: 2
Shared file servers are often accessible to compromised internal users, making access relatively easy once inside the network.

### PRIVILEGE_ESCALATION — Weight: 6
Privilege escalation requires exploiting operating system or software weaknesses and represents one of the most complex attacker actions.

### DIRECT_NETWORK_ACCESS — Weight: 3
Accessing internal network resources requires internal positioning and awareness of network structure, resulting in moderate difficulty.

### INTERNAL_APPLICATION_ACCESS — Weight: 2
Accessing internal business applications is typically easy once an attacker has compromised a workstation, as these systems often rely on existing user sessions or implicit trust.

### HR_SYSTEM_ACCESS — Weight: 2
HR systems are often accessible to internal users with minimal restrictions, making access relatively easy once inside the network.

### FINANCE_SYSTEM_ACCESS — Weight: 3
Finance systems typically enforce stricter controls than general applications, requiring additional effort or permissions to access sensitive financial data.

### BACKUP_SYSTEM_ACCESS — Weight: 3
Backup systems require internal positioning and knowledge of infrastructure, making access moderately difficult.

### DEVICE_MANAGEMENT_ABUSE — Weight: 4
Abusing device management systems requires understanding of administrative tools and may involve exploiting privileged access or misconfigurations.

---

## Data Access and Exposure

### STORED_CREDENTIAL_LEAK — Weight: 3
Credentials stored in configuration files or scripts are common but require discovery and access to the relevant storage locations.

### API_DATA_SYNC — Weight: 4
Accessing data through application or API integrations requires understanding how systems exchange data and how authentication is handled.

### ADMIN_DATABASE_ACCESS — Weight: 1
Once an administrative account is compromised, direct access to sensitive databases is trivial.

### APPLICATION_DATABASE_ACCESS — Weight: 4
Accessing databases through application layers requires understanding application logic, credentials, or service integrations, making it a technically demanding attack.

### HR_DATA_ACCESS — Weight: 3
Accessing sensitive HR data requires navigating application permissions and data structures, typically requiring moderate effort.

### FINANCIAL_DATA_ACCESS — Weight: 3
Financial data is usually protected by stricter controls, but may still be accessible through compromised internal systems or integrations.

### BACKUP_DATA_EXPOSURE — Weight: 3
Backup systems may contain sensitive data but require discovery and access to storage locations or backup infrastructure.

---