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
11. Email Server
12. Domain Controller
13. Internal App
14. HR System
15. Finance System
16. Backup Server
17. MDM Server
18. Wireless Access Point
19. Firewall
20. DNS Server 

---

## Edges

### Initial Access
1. Attacker → Employee Email  
   - Phishing / Credential Theft (Email MFA enabled = +6)

2. Attacker → Company Website / Web App  
   - Exploit Web App / Weak Login (Web app hardening enabled = +4)

3. Attacker → VPN / Remote Access  
   - Stolen VPN Credentials (VPN / Remote Access MFA enabled = +5)

4. Attacker → Wireless Access Point
   - Wireless Network Compromise (Wireless security hardening enabled = +4)

5. Attacker → Firewall  
   - Perimeter Device Exploit (Perimeter Firewall Hardening enabled = +5)

### From Company Website / Web App
6. Company Website / Web App → Employee Workstation  
   - Malware Delivery (Endpoint Detection & Response (EDR) enabled = +3)

### From VPN / Remote Access
7. VPN / Remote Access → Employee Workstation  
   - Remote Login (RDP / SSH) (Remote Access Hardening enabled = +3)

### From Wireless Access Point
8. Wireless Access Point → Employee Workstation  
   - Local Network Access (Wireless Security Hardening enabled = +3)

### From Firewall
9. Firewall → Internal App  
   - Internal Service Pivot (Internal Application Hardening enabled = +3)

### From Employee Email
10. Employee Email → Employee Workstation  
   - Credential Reuse (Password or Session Token) (Conditional Access enabled = +4)

11. Employee Email → Identity Provider (IdP)  
   - Password Reset / SSO Abuse (Identity Provider Hardening enabled = +4)

12. Employee Email → Third-Party SaaS App  
   - OAuth Token Theft (Saas Application Security Controls enabled = +3)

13. Employee Email → Email Server  
    - Mailbox / Server Abuse (Email Server Hardening enabled = +3)

### From Identity Provider (IdP)
14. Identity Provider (IdP) → Admin Account  
   - Over-Privileged Role Assignment (Role-based access contol (RBAC) enforcement enabled = +3)

### From Employee Workstation
15. Employee Workstation → File Server  
    - Access Shared Drive (File Server Access Controls enabled = +2)

16. Employee Workstation → Admin Account  
    - Privilege Escalation / Credential Dumping (Privileged Account Hardening enabled = +5)

17. Employee Workstation → Customer DB  
    - Direct Network Access (Network Segmentation enabled = +3)

18. Employee Workstation → Domain Controller  
    - Domain Privilege Escalation (Domain Controller Hardening enabled = +5)

19. Employee Workstation → Internal App  
    - Internal Application Access (Internal Application Hardening enabled = +3)

20. Employee Workstation → HR System  
    - HR System Access (HR System Access Controls enabled = +3)

21. Employee Workstation → Finance System  
    - Finance System Access (Finance System Access Controls enabled = +4)

22. Employee Workstation → Backup Server  
    - Backup System Access (Backup Server Protection enabled = +3)

23. Employee Workstation → MDM Server  
    - Device Management Abuse (MDM Security Controls enabled = +4)

24. Employee Workstation → DNS Server  
    - Network Discovery (DNS Security Monitoring enabled = +2)

### From Domain Controller
25. Domain Controller → Admin Account  
    - Domain Privilege Escalation (Domain Controller Hardening enabled = +4)

### From MDM Server
26. MDM Server → Admin Account  
    - MDM Privilege Abuse (MDM Security Controls enabled = +4)

### From File Server
27. File Server → Customer DB  
    - Stored Credentials / Config Leak

### From Third-Party SaaS App
28. Third-Party SaaS App → Customer DB  
    - API Access / Data Sync

### From Internal App
29. Internal App → Customer DB  
    - Application Database Access (Internal Application Hardening enabled = +4)

### From HR System
30. HR System → Customer DB  
    - HR Data Access / Integration Abuse (HR System Access Controls enabled = +3)

### From Finance System
31. Finance System → Customer DB  
    - Financial Data Access (Finance System Access Controls enabled = +3)

### From Backup Server
32. Backup Server → Customer DB  
    - Backup Data Exposure (Backup Server Protection enabled = +3)

### From Admin Account
33. Admin Account → Customer DB  
    - Admin DB Access

---
