# Graph-Based Attack Path Visualizer
## Authoritative Node and Edge List (Phase 1)

---

## Nodes

1. Attacker
2. Employee Email
3. VPN / Remote Access
4. Company Website / Web App
5. Employee Workstation
6. Identity Provider (IdP)
7. Admin Account
8. File Server
9. Customer DB (Sensitive Data)
10. Third-Party SaaS App

---

## Edges

### Initial Access
1. Attacker → Employee Email  
   - Phishing / Credential Theft (Email MFA enabled = +6)

2. Attacker → Company Website / Web App  
   - Exploit Web App / Weak Login (Web app hardening enabled = +4)

3. Attacker → VPN / Remote Access  
   - Stolen VPN Credentials (VPN / Remote Access MFA enabled = +5)

### From Company Website / Web App
4. Company Website / Web App → Employee Workstation  
   - Malware (Endpoint Detection & Response (EDR) enabled = +3)

### From VPN / Remote Access
5. VPN / Remote Access → Employee Workstation  
   - Remote Login (RDP / SSH) (Remote Access Hardening enabled = +3)

### From Employee Email
6. Employee Email → Employee Workstation  
   - Credential Reuse (Password or Session Token) (Conditional Access enabled = +4)

7. Employee Email → Identity Provider (IdP)  
   - Password Reset / SSO Abuse (Identity Provider Hardening enabled = +4)

8. Employee Email → Third-Party SaaS App  
   - OAuth Token Theft / SSO Abuse (Saas Application Security Controls enabled = +3)

### From Identity Provider (IdP)
9. Identity Provider (IdP) → Admin Account  
   - Over-Privileged Role Assignment (Role-based access contol (RBAC) enforcement enabled = +3)

### From Employee Workstation
10. Employee Workstation → File Server  
    - Access Shared Drive (File Server Access Controls enabled = +2)

11. Employee Workstation → Admin Account  
    - Privilege Escalation / Credential Dumping (Privileged Account Hardening enabled = +5)

12. Employee Workstation → Customer DB  
    - Direct Network Access (Network Segmentation enabled = +3)

### From File Server
13. File Server → Customer DB  
    - Stored Credentials / Config Leak

### From Third-Party SaaS App
14. Third-Party SaaS App → Customer DB  
    - API Access / Data Sync

### From Admin Account
15. Admin Account → Customer DB  
    - Admin DB Access

---
