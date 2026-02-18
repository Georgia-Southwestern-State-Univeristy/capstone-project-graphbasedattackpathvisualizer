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

---

## Lateral Movement and Execution

### MALWARE_DELIVERY — Weight: 4
Delivering malware typically requires chaining vulnerabilities or persuading users to execute malicious code, requiring moderate technical effort.

### REMOTE_LOGIN (RDP / SSH) — Weight: 2
Once valid credentials are obtained and remote access services are exposed, logging into a system is a low-effort attacker action.

### CREDENTIAL_REUSE — Weight: 2
Reusing compromised credentials across systems is a common and low-effort technique once credentials have been obtained.

---

## Identity and Access Abuse

### PASSWORD_RESET_SSO_ABUSE — Weight: 4
Abusing password reset or single sign-on workflows requires understanding identity system behavior and exploiting weaknesses in account recovery or authentication flows.

### OAUTH_TOKEN_THEFT — Weight: 3
OAuth tokens may be obtained through phishing, malware, or insecure storage, requiring moderate effort and opportunity.

### OVER_PRIVILEGED_ROLE_ASSIGNMENT — Weight: 5
Gaining elevated access through excessive role assignments requires access to identity systems or exploitation of role misconfiguration, making it a high-effort attack.

---

## Workstation-Based Escalation

### FILE_SERVER_ACCESS — Weight: 2
Shared file servers are often accessible to compromised internal users, making access relatively easy once inside the network.

### PRIVILEGE_ESCALATION — Weight: 6
Privilege escalation requires exploiting operating system or software weaknesses and represents one of the most complex attacker actions.

### DIRECT_NETWORK_ACCESS — Weight: 3
Accessing internal network resources requires internal positioning and awareness of network structure, resulting in moderate difficulty.

---

## Data and Credential Exposure

### STORED_CREDENTIAL_LEAK — Weight: 3
Credentials stored in configuration files or scripts are common but require discovery and access to the relevant storage locations.

### API_DATA_SYNC — Weight: 4
Accessing data through application or API integrations requires understanding how systems exchange data and how authentication is handled.

### ADMIN_DATABASE_ACCESS — Weight: 1
Once an administrative account is compromised, direct access to sensitive databases is trivial.

---