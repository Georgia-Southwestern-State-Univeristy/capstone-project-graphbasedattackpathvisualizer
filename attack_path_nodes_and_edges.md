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
9. Sensitive Data Store
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

### From Email Server
14. Email Server → Employee Workstation  
    - Malicious Email Delivery / Mail Rule Abuse (Email Security Filtering enabled = +3) 

### From Identity Provider (IdP)
15. Identity Provider (IdP) → Admin Account  
   - Over-Privileged Role Assignment (Role-based access contol (RBAC) enforcement enabled = +3)

### From Employee Workstation
16. Employee Workstation → File Server  
    - Access Shared Drive (File Server Access Controls enabled = +2)

17. Employee Workstation → Admin Account  
    - Privilege Escalation / Credential Dumping (Privileged Account Hardening enabled = +5)

18. Employee Workstation → Sensitive Data Store  
    - Direct Network Access (Network Segmentation enabled = +3)

19. Employee Workstation → Domain Controller  
    - Domain Privilege Escalation (Domain Controller Hardening enabled = +5)

20. Employee Workstation → Internal App  
    - Internal Application Access (Internal Application Hardening enabled = +3)

21. Employee Workstation → HR System  
    - HR System Access (HR System Access Controls enabled = +3)

22. Employee Workstation → Finance System  
    - Finance System Access (Finance System Access Controls enabled = +4)

23. Employee Workstation → Backup Server  
    - Backup System Access (Backup Server Protection enabled = +3)

24. Employee Workstation → MDM Server  
    - Device Management Abuse (MDM Security Controls enabled = +4)

25. Employee Workstation → DNS Server  
    - Network Discovery (DNS Security Monitoring enabled = +2)

    ### From DNS Server
26. DNS Server → Domain Controller  
    - Internal Service Pivot (DNS Access Restrictions enabled = +3)

### From Domain Controller
27. Domain Controller → Admin Account  
    - Domain Privilege Escalation (Domain Controller Hardening enabled = +4)

### From MDM Server
28. MDM Server → Admin Account  
    - MDM Privilege Abuse (MDM Security Controls enabled = +4)

### From File Server
29. File Server → Sensitive Data Store  
    - Stored Credentials / Config Leak

### From Third-Party SaaS App
30. Third-Party SaaS App → Sensitive Data Store  
    - API Access / Data Sync

### From Internal App
31. Internal App → Sensitive Data Store  
    - Application Database Access (Internal Application Hardening enabled = +4)

### From HR System
32. HR System → Sensitive Data Store  
    - HR Data Access / Integration Abuse (HR System Access Controls enabled = +3)

### From Finance System
33. Finance System → Sensitive Data Store  
    - Financial Data Access (Finance System Access Controls enabled = +3)

### From Backup Server
34. Backup Server → Sensitive Data Store  
    - Backup Data Exposure (Backup Server Protection enabled = +3)

### From Admin Account
35. Admin Account → Sensitive Data Store  
    - Admin DB Access

---
