import cytoscape from "cytoscape";
import fcose from "cytoscape-fcose";

cytoscape.use(fcose);

let cy = null;
let lastPathSignature = null;
let lastHighlightedActions = new Set();
let lastAiAnalysisSignature = null;
let lastAiAnalysisData = null;

// --- CONFIGURATION & DATA MAPS ---

const NODE_DESCRIPTIONS = {
  ATTACKER: {
    desc: "Represents the external threat actor attempting to penetrate the business environment. This node is the starting point of every simulated attack path and models the perspective of an unauthorized outsider.",
    impact: "It establishes where the attack begins and provides the baseline from which all possible attack paths are evaluated."
  },
  WEB_APP: {
    desc: "A public-facing business application that can be reached from the internet. Because it is externally exposed, it is a common target for exploits, weak authentication attacks, and application-layer abuse.",
    impact: "If compromised, it can provide a direct foothold into internal systems, connected services, or backend data sources."
  },
  VPN: {
    desc: "The organization’s remote access gateway, used by employees to connect into the internal network. VPN services are high-value targets because they provide trusted entry into otherwise protected systems.",
    impact: "Compromising VPN access can let an attacker appear as a legitimate remote user and move deeper into the environment."
  },
  EMPLOYEE_EMAIL: {
    desc: "Corporate email accounts used for daily communication and account-linked services. Email is frequently targeted because it can be used for phishing, account recovery abuse, and internal impersonation.",
    impact: "A compromised mailbox can expose credentials, sensitive communication, and create opportunities for further social engineering."
  },
  EMPLOYEE_WORKSTATION: {
    desc: "A standard employee endpoint such as a desktop or laptop used for business operations. Workstations often become the first internal system an attacker controls after initial access is achieved.",
    impact: "Once compromised, a workstation can be used for credential theft, malware execution, and lateral movement to other internal assets."
  },
  IDENTITY_PROVIDER: {
    desc: "The central authentication platform that manages login flows, account trust, and single sign-on access. It is a critical control point because many other systems rely on it for identity verification.",
    impact: "If an attacker reaches the identity provider, they may gain broad access across multiple connected services and accounts."
  },
  ADMIN_ACCOUNT: {
    desc: "A privileged account with elevated permissions over business systems, servers, or data stores. These accounts are especially valuable because they can bypass many restrictions that apply to normal users.",
    impact: "Compromising an admin account can dramatically reduce attack difficulty and open direct paths to high-value assets."
  },
  CUSTOMER_DB: {
    desc: "The central data store containing sensitive customer information and important business records. This is typically the highest-value target in the attack graph.",
    impact: "If reached, the attacker may be able to view, export, alter, or destroy critical customer data."
  },
  FILE_SERVER: {
    desc: "An internal server used to store shared business files, documents, and configuration data. It often contains operational information that supports both normal business use and attacker discovery.",
    impact: "A compromised file server can expose sensitive records, internal documentation, and stored credentials that help attackers advance."
  },
  THIRD_PARTY_SAAS: {
    desc: "An external software-as-a-service platform connected to the business through accounts, integrations, or shared identity systems. These tools often extend the organization’s attack surface beyond its own network.",
    impact: "If compromised, SaaS access can expose business data and provide additional trusted paths into other connected systems."
  },
  EMAIL_SERVER: {
    desc: "The infrastructure responsible for processing, storing, or routing business email traffic. It supports communication flow and can hold a large amount of sensitive correspondence.",
    impact: "Compromising the email server can enable message interception, malicious delivery, and long-term visibility into internal communication."
  },
  DOMAIN_CONTROLLER: {
    desc: "A core directory and authentication server that manages domain identities, permissions, and trust relationships. It is one of the most strategically important internal systems in many business environments.",
    impact: "If compromised, it can provide centralized control over users, devices, and access policies across the network."
  },
  INTERNAL_APP: {
    desc: "A business application intended for internal use by employees or trusted systems. Internal applications often have direct access to operational data and may rely on trusted network assumptions.",
    impact: "Once reached, an attacker may gain access to additional data, application functions, or backend systems that were not directly exposed before."
  },
  HR_SYSTEM: {
    desc: "The human resources platform used to manage employee information, personnel records, and related workflows. It often contains highly sensitive personal and organizational data.",
    impact: "Compromising the HR system can expose employee records and give attackers useful information for impersonation, fraud, or follow-on attacks."
  },
  FINANCE_SYSTEM: {
    desc: "A financial platform used for accounting, payroll, reporting, or payment-related operations. Because it handles high-value information, it is a major target for both theft and disruption.",
    impact: "Attackers who reach this system may gain access to sensitive financial records or opportunities for fraud and operational damage."
  },
  BACKUP_SERVER: {
    desc: "A system used to store backups or support data recovery operations for the organization. Backup infrastructure is important not only for the data it contains, but also for resilience after an incident.",
    impact: "If compromised, attackers may access complete historical data copies or weaken the organization’s ability to recover from an attack."
  },
  MDM_SERVER: {
    desc: "A mobile or endpoint device management platform used to configure, secure, and control managed devices. Because it has administrative reach over many endpoints, it is highly sensitive.",
    impact: "Attackers who gain access to the MDM platform can potentially influence or control large numbers of devices from one central point."
  },
  WIRELESS_ACCESS_POINT: {
    desc: "A wireless networking device that provides internal connectivity to approved users and systems. If weakly protected, it can become an entry point into the local network.",
    impact: "Compromise here can allow attackers to move from external proximity to internal network access with much less resistance."
  },
  FIREWALL: {
    desc: "A perimeter security device that filters and controls traffic between networks. It plays an important role in segmenting systems and limiting unauthorized access.",
    impact: "If bypassed or compromised, attackers may gain visibility into internal services or weaken one of the organization’s key boundary defenses."
  },
  DNS_SERVER: {
    desc: "A server responsible for resolving hostnames and supporting internal network communication. It also provides useful visibility into how systems are named and connected.",
    impact: "Attackers can use DNS access for discovery, redirection, or better understanding of the internal environment."
  }
};

const ATTACK_DETAILS = {
  "Phishing / Credential Theft": {
    desc: "Attackers use deceptive emails or messages to trick employees into revealing usernames, passwords, or other login details. Once the victim interacts with the lure, the attacker can capture credentials and reuse them to access internal systems. This is one of the most common initial access techniques in real-world attacks.",
    rationale: "This method targets human behavior instead of technical flaws, which makes it highly effective even in otherwise well-defended environments."
  },
  "Exploit Web App / Weak Login": {
    desc: "An attacker targets a public-facing web application by exploiting software vulnerabilities or attempting to guess weak credentials. If successful, this can provide unauthorized access to internal functionality, sensitive data, or connected backend services. Internet-exposed systems make this a frequent entry point.",
    rationale: "Public web applications are constantly exposed to attackers and often serve as a direct path into the environment if security controls are weak."
  },
  "Stolen VPN Credentials": {
    desc: "The attacker uses compromised VPN credentials to log in as a legitimate remote user. This gives them a foothold inside the internal network without needing to bypass external perimeter defenses directly. Once connected, they can begin exploring internal systems and expanding access.",
    rationale: "VPN access is highly valuable because it provides trusted network entry and can dramatically reduce the effort required for lateral movement."
  },
  "Malware Delivery": {
    desc: "Malware is delivered to the target through phishing, malicious downloads, or exploit chains that cause harmful code to run on the system. Once executed, it can establish persistence, steal information, or provide remote control to the attacker. This step often prepares the environment for additional compromise.",
    rationale: "Malware enables attackers to automate control, maintain access, and perform follow-on actions that would be difficult through manual interaction alone."
  },
  "Remote Login (RDP / SSH)": {
    desc: "The attacker uses remote administration protocols such as RDP or SSH to move between systems after obtaining valid credentials or access. Because these are legitimate management tools, the activity may blend in with normal administrative behavior. This makes it a practical technique for lateral movement.",
    rationale: "Using normal remote access protocols reduces noise and allows attackers to move deeper into the network without relying on noisy exploit activity."
  },
  "Credential Reuse": {
    desc: "Credentials stolen from one account are reused against other internal services and systems. If passwords are shared or reused across the environment, the attacker can quickly expand access with little additional effort. This often allows movement from low-value accounts to more sensitive systems.",
    rationale: "Poor password hygiene and reused credentials make this a very efficient method for broadening access after the first compromise."
  },
  "Password Reset / SSO Abuse": {
    desc: "The attacker abuses password reset mechanisms or single sign-on workflows to take control of additional accounts. Weak recovery steps, poor verification, or misconfigured identity flows can make this possible. This allows access without requiring the original password to be known.",
    rationale: "Identity recovery and SSO processes are powerful account control points, so weaknesses here can let attackers bypass traditional credential theft barriers."
  },
  "OAuth Token Theft / SSO Abuse": {
    desc: "Instead of stealing passwords, the attacker captures active session or OAuth tokens from a compromised system or browser. Those tokens can often be replayed to access services directly without triggering standard login prompts. This can allow attackers to bypass password checks and sometimes MFA.",
    rationale: "Session tokens represent already-authenticated access, which makes them extremely valuable and difficult to defend against once stolen."
  },
  "Over-Privileged Role Assignment": {
    desc: "The attacker benefits from a user or service account having more permissions than it actually needs. By compromising that account, they inherit elevated access to sensitive systems, administrative functions, or restricted data. This reduces the need for additional privilege escalation.",
    rationale: "Excessive permissions increase blast radius and let attackers move faster by turning ordinary account compromise into high-impact access."
  },
  "Access Shared Drive": {
    desc: "After gaining internal access, the attacker reaches shared storage used for documents, scripts, and business files. These locations may contain sensitive records, configuration information, or credentials that support further compromise. Shared storage is often a valuable source of discovery material.",
    rationale: "Shared drives commonly hold useful internal information and are frequently less protected once an attacker is already inside the network."
  },
  "Privilege Escalation / Credential Dumping": {
    desc: "The attacker attempts to gain higher privileges by exploiting the operating system, abusing local trust relationships, or extracting credentials from memory. This can turn a standard user foothold into administrative control. Once elevated, the attacker can access more systems and defenses become harder to enforce.",
    rationale: "Administrative privileges are a major force multiplier because they unlock broader system access and make later attack steps much easier."
  },
  "Direct Network Access": {
    desc: "From a compromised internal position, the attacker directly reaches additional network resources that were previously inaccessible. This may include internal servers, sensitive applications, or isolated data systems. Weak segmentation makes this kind of movement much easier.",
    rationale: "Direct internal reach expands the attacker’s options quickly and reduces the need for more complex compromise methods."
  },
  "Stored Credentials / Config Leak": {
    desc: "The attacker discovers plaintext passwords, secrets, or connection details stored in scripts, configuration files, or documentation. These exposed values can then be used to authenticate to other systems or services. Poor secret management turns ordinary files into valuable attack resources.",
    rationale: "When credentials are stored insecurely, attackers can gain powerful access without needing to crack passwords or defeat authentication controls."
  },
  "API Access / Data Sync": {
    desc: "The attacker uses compromised API keys, service credentials, or data synchronization pathways to interact directly with backend systems. This can allow access to cloud data, business records, or application functions without using a normal user interface. API pathways often provide efficient access to high-value data.",
    rationale: "Direct service-to-service access can bypass many endpoint-focused defenses and lead attackers straight to critical data flows."
  },
  "Admin DB Access": {
    desc: "With administrative privileges, the attacker directly accesses the customer database to query, export, alter, or destroy sensitive records. This typically represents the final objective of the attack path. At this stage, business impact is high because core data assets are fully exposed.",
    rationale: "Once database administrative access is achieved, only a few remaining barriers usually stand between the attacker and the organization’s most valuable data."
  },

  "Wireless Network Compromise": {
    desc: "The attacker gains access to the organization’s wireless network by abusing weak credentials, insecure configurations, or poor wireless protections. Once connected, they may be treated like an internal user and can begin probing additional systems. This can provide a quiet entry point into the environment.",
    rationale: "Wireless access can collapse the distance between an external attacker and the internal network if segmentation and authentication are weak."
  },
  "Perimeter Device Exploit": {
    desc: "The attacker targets an exposed firewall or edge device through known vulnerabilities, weak administration settings, or configuration mistakes. Compromising this kind of device can reveal internal services or weaken the organization’s boundary protections. It can also provide strategic access for further movement.",
    rationale: "Perimeter devices sit at critical trust boundaries, so weaknesses here can expose large portions of the internal environment."
  },
  "Local Network Access": {
    desc: "After gaining presence on the local network, the attacker can directly reach internal systems that are not exposed to the internet. This opens the door to scanning, service abuse, and further compromise of trusted internal resources. Internal presence often makes later steps much easier.",
    rationale: "Being on the local network removes many external barriers and gives the attacker a much stronger position for exploration and lateral movement."
  },
  "Internal Service Pivot": {
    desc: "The attacker uses one compromised internal system as a stepping stone into another trusted service. This may rely on reused credentials, implicit trust, or accessible internal connections between systems. Pivots like this help attackers move deeper without returning to the original entry point.",
    rationale: "Internal trust relationships often create efficient pathways for attackers to advance from one compromised asset to the next."
  },
  "Mailbox / Server Abuse": {
    desc: "The attacker abuses email server capabilities or mailbox access to monitor communication, hide activity, or spread additional malicious content. They may create forwarding rules, read internal messages, or use trusted infrastructure to deliver phishing internally. This can support both persistence and further compromise.",
    rationale: "Email systems provide both access to sensitive business communication and a trusted platform for expanding the attack."
  },
  "Domain Privilege Escalation": {
    desc: "The attacker escalates privileges within the domain environment to gain broader administrative control. This can involve abusing misconfigurations, stolen credentials, or weak delegation paths. Once domain-level control is achieved, many internal systems become accessible.",
    rationale: "Domain privilege is extremely powerful because it can provide centralized control over identities, systems, and security policies."
  },
  "Internal Application Access": {
    desc: "The attacker reaches an internal business application after compromising a connected user or workstation. These applications may expose sensitive records, workflows, or backend integrations that help the attacker advance further. Internal apps are often less hardened because they are assumed to be trusted.",
    rationale: "Applications inside the network frequently trust authenticated internal users, which makes them attractive pivot points after a foothold is established."
  },
  "HR System Access": {
    desc: "The attacker accesses the human resources platform from a compromised internal system using available credentials or trusted access paths. This may expose employee records, organizational details, or connected business workflows. HR systems can also reveal information useful for future phishing or impersonation.",
    rationale: "HR platforms contain valuable identity and personnel information that can support both data theft and follow-on social engineering."
  },
  "Finance System Access": {
    desc: "The attacker reaches the financial platform from an already compromised internal position and begins interacting with accounting, payroll, or payment-related data. This may expose highly sensitive business information or enable fraudulent activity. Finance systems are especially valuable because of both data sensitivity and business impact.",
    rationale: "Financial systems often hold critical records and may provide pathways to fraud, data theft, or operational disruption."
  },
  "Backup System Access": {
    desc: "The attacker accesses backup infrastructure to view stored copies of important systems or interfere with recovery processes. Backup platforms can expose large amounts of historical or complete organizational data. They are also strategic targets because damaging them can make incident recovery far more difficult.",
    rationale: "Backup systems combine high data value with high recovery importance, making them especially attractive to attackers."
  },
  "Device Management Abuse": {
    desc: "The attacker abuses a device management platform to issue commands, deploy settings, or control managed endpoints. This can turn one compromise into wider control across many systems. Because management tools are highly trusted, abuse here can have broad impact.",
    rationale: "Management platforms often have elevated authority over many devices, so compromising them creates a strong multiplier effect."
  },
  "Network Discovery": {
    desc: "The attacker scans and maps internal systems, services, and naming information to better understand the environment. This may involve querying DNS, enumerating hosts, or identifying reachable applications and servers. Discovery helps the attacker choose the most efficient next step.",
    rationale: "Good visibility into the environment allows attackers to move more strategically and avoid wasting effort on less valuable targets."
  },
  "Malicious Email Delivery / Mail Rule Abuse": {
    desc: "The attacker uses compromised email infrastructure to send trusted-looking malicious messages or create hidden mail rules that support persistence. This can help spread phishing internally, conceal activity, or redirect sensitive communications. It extends both access and attacker influence.",
    rationale: "Compromised email systems provide a trusted delivery channel and long-term visibility into communication flows."
  },
  "MDM Privilege Abuse": {
    desc: "The attacker leverages elevated permissions in the mobile device management platform to gain deeper control over endpoints. This can include pushing settings, enforcing policies, or executing actions that help establish broader access. It effectively turns centralized management power into an attack advantage.",
    rationale: "MDM privilege is dangerous because it can translate into large-scale control over many user devices from one central platform."
  },
  "Application Database Access": {
    desc: "The attacker reaches backend databases through a compromised application that already has trusted data connections. Instead of attacking the database directly, they abuse the application’s existing access path. This can expose sensitive records while avoiding some direct database defenses.",
    rationale: "Applications often act as trusted intermediaries to databases, making them a practical stepping stone to sensitive data."
  },
  "HR Data Access / Integration Abuse": {
    desc: "After reaching the HR system, the attacker abuses its integrations or internal permissions to retrieve broader employee or organizational data from connected systems. Trusted backend links can make it easier to reach sensitive records than attacking the central store directly. This expands the value of the HR foothold.",
    rationale: "Connected business platforms often inherit trusted access paths that attackers can exploit to reach data beyond the initial system."
  },
  "Financial Data Access": {
    desc: "The attacker uses the compromised finance platform to retrieve, export, or manipulate sensitive financial records stored in connected business systems. Because finance tools often have privileged access to important data, they can serve as an efficient bridge to high-value assets. This step can lead directly to major business impact.",
    rationale: "Finance platforms frequently combine sensitive data access with strong business importance, which makes them ideal stepping stones to critical outcomes."
  },
  "Backup Data Exposure": {
    desc: "The attacker extracts sensitive information from backup repositories rather than from live production systems. Backups may contain complete datasets, historical records, and copies of critical configurations. This allows significant data theft even if primary systems are more tightly protected.",
    rationale: "Backup repositories often contain broad, high-value data collections and may be less monitored than production environments."
  }
};

const MITIGATION_COLORS = {
  "Email MFA": "peer-checked:bg-emerald-500",
  "Web App Hardening": "peer-checked:bg-blue-500",
  "VPN / Remote Access MFA": "peer-checked:bg-blue-300",
  "Endpoint Detection & Response": "peer-checked:bg-amber-500",
  "Remote Access Hardening": "peer-checked:bg-pink-300",
  "Conditional Access": "peer-checked:bg-cyan-500",
  "Identity Provider Hardening": "peer-checked:bg-green-700",
  "SaaS Application Security Controls": "peer-checked:bg-orange-500",
  "Role-Based Access Control (RBAC) Enforcement": "peer-checked:bg-orange-800",
  "File Server Access Controls": "peer-checked:bg-purple-500",
  "Privileged Account Hardening": "peer-checked:bg-yellow-500",
  "Network Segmentation": "peer-checked:bg-red-500",
  "Wireless Security Hardening": "peer-checked:bg-sky-400",
  "Perimeter Firewall Hardening": "peer-checked:bg-red-700",
  "Internal Application Hardening": "peer-checked:bg-purple-500",
  "Email Server Hardening": "peer-checked:bg-teal-500",
  "Email Security Filtering": "peer-checked:bg-cyan-300",
  "Domain Controller Hardening": "peer-checked:bg-yellow-600",
  "HR System Access Controls": "peer-checked:bg-pink-500",
  "Finance System Access Controls": "peer-checked:bg-amber-600",
  "Backup Server Protection": "peer-checked:bg-indigo-700",
  "MDM Security Controls": "peer-checked:bg-fuchsia-500",
  "DNS Security Monitoring": "peer-checked:bg-green-500",
  "DNS Access Restrictions": "peer-checked:bg-emerald-700"
};

const MITIGATION_DESCRIPTIONS = {
  "Email MFA": "Requires a second authentication factor for employee email accounts. This helps prevent attackers from accessing email even if a password is stolen through phishing or credential theft.",

  "Web App Hardening": "Improves the security of public-facing web applications through measures like patching, secure configuration, and input validation. This makes web-based attacks more difficult.",

  "VPN / Remote Access MFA": "Adds multi-factor authentication to VPN or remote access logins. This reduces the risk of attackers successfully using stolen remote access credentials.",

  "Endpoint Detection & Response": "Uses monitoring and detection tools on employee devices to identify suspicious behavior, malware, or attacker activity. This helps stop or contain compromise on workstations.",

  "Remote Access Hardening": "Secures remote administration services like RDP or SSH by restricting access, requiring stronger controls, or disabling unnecessary exposure. This makes lateral movement harder.",

  "Conditional Access": "Applies rules such as device trust, location checks, or risk-based access policies before allowing account access. This helps block suspicious sign-in attempts.",

  "Identity Provider Hardening": "Strengthens SSO and identity systems by securing password reset flows, enforcing MFA, and tightening identity protections. This reduces the chance of account takeover through identity abuse.",

  "SaaS Application Security Controls": "Improves the security of cloud-based business applications by restricting permissions, requiring MFA, and reducing risky third-party access. This helps protect SaaS data and connected systems.",

  "Role-Based Access Control (RBAC) Enforcement": "Limits access based on job role so users only have the permissions they truly need. This reduces the damage that can happen if an account is compromised.",

  "File Server Access Controls": "Restricts who can access shared drives and files through permissions and access control lists. This helps prevent attackers from easily reaching sensitive shared data.",

  "Privileged Account Hardening": "Protects administrator accounts using stronger controls such as MFA, separate admin accounts, and least privilege. This makes privilege escalation and admin compromise harder.",

  "Network Segmentation": "Separates systems into restricted network zones so attackers cannot move freely after gaining a foothold. This helps contain compromise and protect sensitive assets.",

  "Wireless Security Hardening": "Strengthens Wi-Fi protections using secure authentication, strong passwords, and better segmentation. This reduces the chance of attackers entering through the wireless network.",

  "Perimeter Firewall Hardening": "Improves firewall security through strong configuration, patching, and reduced administrative exposure. This helps defend the network boundary from external attacks.",

  "Internal Application Hardening": "Secures internal applications through patching, stronger authentication, and safer configuration. This reduces the chance that attackers can pivot through trusted internal tools.",

  "Email Server Hardening": "Protects the organization’s email infrastructure through stronger configuration, patching, and restricted administration. This helps prevent abuse of the mail environment.",

  "Email Security Filtering": "Filters incoming and outgoing email for phishing, malicious attachments, and suspicious links. This reduces the chance of successful email-based attacks.",

  "Domain Controller Hardening": "Strengthens domain controller security with tighter privileges, better monitoring, and stronger administrative protections. This helps defend one of the most critical internal systems.",

  "HR System Access Controls": "Restricts access to HR platforms and employee records based on need and role. This helps protect sensitive personnel data from unauthorized access.",

  "Finance System Access Controls": "Applies stronger access restrictions to financial systems so only authorized users can reach sensitive accounting or payroll data. This reduces risk of fraud and data theft.",

  "Backup Server Protection": "Secures backup infrastructure with stronger permissions, isolation, and administrative controls. This helps protect recovery systems and stored backup data from attacker access.",

  "MDM Security Controls": "Strengthens mobile and endpoint management systems by limiting privileges and securing administrative actions. This helps prevent broad device control if the platform is targeted.",

  "DNS Security Monitoring": "Monitors DNS activity for suspicious lookups, unusual behavior, or signs of attacker discovery. This can help detect early-stage reconnaissance.",

  "DNS Access Restrictions": "Limits which systems or users can query or modify DNS resources. This helps reduce attacker visibility and abuse of internal naming services."
};

const MITIGATION_EDGE_MAP = {
  "Email MFA": [
    "Phishing / Credential Theft"
  ],

  "Web App Hardening": [
    "Exploit Web App / Weak Login"
  ],

  "VPN / Remote Access MFA": [
    "Stolen VPN Credentials"
  ],

  "Endpoint Detection & Response": [
    "Malware Delivery"
  ],

  "Remote Access Hardening": [
    "Remote Login (RDP / SSH)"
  ],

  "Conditional Access": [
    "Credential Reuse"
  ],

  "Identity Provider Hardening": [
    "Password Reset / SSO Abuse"
  ],

  "SaaS Application Security Controls": [
    "OAuth Token Theft / SSO Abuse"
  ],

  "Role-Based Access Control (RBAC) Enforcement": [
    "Over-Privileged Role Assignment"
  ],

  "File Server Access Controls": [
    "Access Shared Drive"
  ],

  "Privileged Account Hardening": [
    "Privilege Escalation / Credential Dumping"
  ],

  "Network Segmentation": [
    "Direct Network Access"
  ],

  "Wireless Security Hardening": [
    "Wireless Network Compromise",
    "Local Network Access"
  ],

  "Perimeter Firewall Hardening": [
    "Perimeter Device Exploit"
  ],

  "Internal Application Hardening": [
    "Internal Service Pivot",
    "Internal Application Access",
    "Application Database Access"
  ],

  "Email Server Hardening": [
    "Mailbox / Server Abuse"
  ],

  "Email Security Filtering": [
    "Malicious Email Delivery / Mail Rule Abuse"
  ],

  "Domain Controller Hardening": [
    "Domain Privilege Escalation"
  ],

  "HR System Access Controls": [
    "HR System Access",
    "HR Data Access / Integration Abuse"
  ],

  "Finance System Access Controls": [
    "Finance System Access",
    "Financial Data Access"
  ],

  "Backup Server Protection": [
    "Backup System Access",
    "Backup Data Exposure"
  ],

  "MDM Security Controls": [
    "Device Management Abuse",
    "MDM Privilege Abuse"
  ],

  "DNS Security Monitoring": [
    "Network Discovery"
  ],

  "DNS Access Restrictions": [
    "Internal Service Pivot"
  ]
};
// --- UTILITY FUNCTIONS ---

const tooltip = document.getElementById("edgeTooltip");

function toCytoscapeElements(apiGraph) {
  const nodes = (apiGraph.nodes ?? []).map((n) => ({
    data: { id: n.id, type: n.type ?? "", label: n.displayName ?? n.id },
  }));

  const edges = (apiGraph.edges ?? []).map((e) => {
    const rawAction = e.attackAction ?? "";
    const rawAttackType = e.attackType ?? "";

    return {
      data: {
        id: `${e.source}__${e.target}__${rawAttackType}`,
        source: e.source,
        target: e.target,
        attackType: rawAttackType,
        attackAction: rawAction,
        weight: Number(e.weight ?? 1),
      },
    };
  });

  return [...nodes, ...edges];
}

function animateValue(obj, start, end, duration) {
  let startTimestamp = null;
  const step = (timestamp) => {
    if (!startTimestamp) startTimestamp = timestamp;
    const progress = Math.min((timestamp - startTimestamp) / duration, 1);
    const fastSnap = 1 - Math.pow(1 - progress, 3);
    const current = Math.floor(fastSnap * (end - start) + start);
    obj.innerHTML = current;
    if (progress < 1) {
      window.requestAnimationFrame(step);
    } else {
      obj.innerHTML = end;
    }
  };
  window.requestAnimationFrame(step);
}

// --- API FETCHERS ---

async function fetchGraph() {
  const checkedBoxes = document.querySelectorAll(".mitigation-checkbox:checked");
  const ids = Array.from(checkedBoxes).map(cb => cb.dataset.id);

  let url = "/api/graph";

  if (ids.length > 0) {
    url += `?mitigations=${ids.join(",")}`;
  }

  const res = await fetch(url, {
    credentials: "include"
  });
  if (!res.ok) throw new Error(`GET /api/graph failed: ${res.status}`);
  return res.json();
}

async function fetchAttackPath(source, target) {

  const checkedBoxes = document.querySelectorAll(".mitigation-checkbox:checked");
  const ids = Array.from(checkedBoxes).map(cb => cb.dataset.id);

  let url = `/api/path?source=${encodeURIComponent(source)}&target=${encodeURIComponent(target)}`;

  if (ids.length > 0) {
    url += `&mitigations=${ids.join(",")}`;
  }

  const res = await fetch(url, {
  credentials: "include"
});
  const data = await res.json();

  if (!res.ok) throw new Error(data.message || `Request failed`);

  return data;
}

async function fetchAiSummary(source, target) {

  const checkedBoxes = document.querySelectorAll(".mitigation-checkbox:checked");
  const ids = Array.from(checkedBoxes).map(cb => cb.dataset.id);

  const res = await fetch("/api/ai/attack-summary", {
    method: "POST",
    headers: {
      "Content-Type": "application/json"
    },
    credentials: "include",
    body: JSON.stringify({
      source,
      target,
      mitigations: ids
    })
  });

  const data = await res.json();

  if (!res.ok) {
    throw new Error(data.message || "AI summary failed");
  }

  return data;
}

function renderAiModal(data) {
  const modal = document.getElementById("aiModal");

  const summary = document.getElementById("aiSummary");
  const risk = document.getElementById("aiRiskLevel");
  const mitigations = document.getElementById("aiMitigations");

  const topRecommendation = document.getElementById("aiTopRecommendation");
  const weakest = document.getElementById("aiWeakestPoint");
  const impact = document.getElementById("aiBusinessImpact");
  const mitigationDetails = document.getElementById("aiMitigationDetails");

  if (!modal || !data) return;

  // --- Basic fields ---
  summary.textContent = data.summary || "";
  topRecommendation.textContent = data.topRecommendation || "";
  weakest.textContent = data.weakestPoint || "";
  impact.textContent = data.businessImpact || "";

  // --- Risk color logic ---
  const riskLevel = (data.riskLevel || "").toUpperCase();
  risk.textContent = riskLevel;

  risk.classList.remove("text-rose-400", "text-yellow-400", "text-emerald-400");

  if (riskLevel === "HIGH") {
    risk.classList.add("text-rose-400");
  } else if (riskLevel === "MEDIUM") {
    risk.classList.add("text-yellow-400");
  } else if (riskLevel === "LOW") {
    risk.classList.add("text-emerald-400");
  }

  // --- Simple mitigation list ---
  mitigations.innerHTML = "";
  (data.recommendedMitigations || []).forEach(item => {
    const li = document.createElement("li");
    li.textContent = item;
    mitigations.appendChild(li);
  });

  // --- Mitigation details ---
  mitigationDetails.innerHTML = "";

  (data.mitigationDetails || []).forEach(mit => {
    const div = document.createElement("div");
    div.className = "p-2 rounded bg-slate-800 border border-slate-700";

    let priorityClass = "";

    if (mit.priority === "PRIMARY") {
      priorityClass = "bg-red-500/20 text-red-400";
    } else if (mit.priority === "SECONDARY") {
      priorityClass = "bg-yellow-500/20 text-yellow-400";
    } else {
      priorityClass = "bg-emerald-500/20 text-emerald-400";
    }

    div.innerHTML = `
      <div class="flex justify-between items-center mb-1">
        <span class="font-semibold text-white">${mit.name}</span>
        <span class="text-[10px] px-2 py-0.5 rounded ${priorityClass}">
          ${mit.priority}
        </span>
      </div>
      <p class="text-slate-300 text-[11px]">${mit.reason}</p>
    `;

    mitigationDetails.appendChild(div);
  });

  modal.classList.remove("hidden");
}

async function fetchMitigations() {
  const res = await fetch("/api/mitigations", {
    credentials: "include"
  });
  if (!res.ok) throw new Error("Failed to load mitigations");
  return res.json();
}

async function fetchProfile() {
  const res = await fetch("/api/profile", {
    credentials: "include"
  });

  if (!res.ok) {
    throw new Error("Failed to load profile");
  }

  return res.json();
}

async function fetchCurrentUserInfo() {
  const res = await fetch("/api/auth/me", {
    credentials: "include"
  });

  if (!res.ok) {
    throw new Error("Failed to load current user");
  }

  return res.json();
}

async function refreshEdgeWeightsOnly() {
  if (!cy) return;

  const apiGraph = await fetchGraph();
  const updatedEdges = apiGraph.edges ?? [];

  updatedEdges.forEach((e) => {
    const edgeId = `${e.source}__${e.target}__${e.attackType ?? ""}`;
    const cyEdge = cy.getElementById(edgeId);

    if (!cyEdge.empty()) {
      cyEdge.data("weight", Number(e.weight ?? 1));
    }
  });
}

function updateMitigationHighlights() {
  if (!cy) return;

  const checkedBoxes = document.querySelectorAll(".mitigation-checkbox:checked");
  const selectedMitigationNames = Array.from(checkedBoxes).map(cb => cb.dataset.name);

  const newHighlightedActions = new Set();

  selectedMitigationNames.forEach(name => {
    const actions = MITIGATION_EDGE_MAP[name] || [];
    actions.forEach(action => newHighlightedActions.add(action));
  });

  const removedActions = new Set(
    [...lastHighlightedActions].filter(a => !newHighlightedActions.has(a))
  );

  cy.edges().removeClass("mitigationHighlight mitigationRemoved");
  cy.nodes().removeClass("mitigationNodeHighlight mitigationNodeRemoved");

  cy.edges().forEach(edge => {
    const action = edge.data("attackAction");

    if (newHighlightedActions.has(action)) {
      edge.addClass("mitigationHighlight");
      edge.source().addClass("mitigationNodeHighlight");
      edge.target().addClass("mitigationNodeHighlight");
    }
  });

  cy.edges().forEach(edge => {
    const action = edge.data("attackAction");

    if (removedActions.has(action)) {
      edge.addClass("mitigationRemoved");
      edge.source().addClass("mitigationNodeRemoved");
      edge.target().addClass("mitigationNodeRemoved");
    }
  });

  setTimeout(() => {
    cy.edges().removeClass("mitigationRemoved");
    cy.nodes().removeClass("mitigationNodeRemoved");
  }, 800);

  lastHighlightedActions = newHighlightedActions;
}

async function renderMitigations(selectedIds = []) {
  const container = document.getElementById("mitigationContainer");
  if (!container) return;

  const mitigations = await fetchMitigations();

  container.innerHTML = "";

  mitigations.forEach((mit) => {
    const colorClass =
      MITIGATION_COLORS[mit.name] || "peer-checked:bg-emerald-500";

    const isChecked = selectedIds.includes(String(mit.id));

    const wrapper = document.createElement("div");

    wrapper.innerHTML = `
      <div class="space-y-1">
        <div class="flex items-center justify-between gap-3">
          <span class="font-medium leading-snug text-slate-200">
            ${mit.name}
          </span>

          <label class="flex items-center justify-end cursor-pointer shrink-0">
            <div class="relative">
              <input type="checkbox"
                    class="sr-only peer mitigation-checkbox"
                    data-id="${mit.id}"
                    data-name="${mit.name}"
                    ${isChecked ? "checked" : ""}>
              <div class="w-12 h-6 bg-slate-600 rounded-full ${colorClass} transition-all duration-300 shadow-inner"></div>
              <div class="absolute left-1 top-1 w-4 h-4 bg-white rounded-full shadow-md transition-all duration-300 peer-checked:translate-x-6"></div>
            </div>
          </label>
        </div>

        <button
          type="button"
          class="mitigation-info-btn text-[10.5px] text-emerald-400 hover:text-emerald-300 transition font-medium"
        >
          What is this?
        </button>

        <div class="mitigation-info max-h-0 overflow-hidden transition-all duration-300 ease-in-out">
          <div class="rounded-lg border border-slate-700 bg-slate-900/70 px-3 py-1.5 text-[11px] text-slate-300 leading-snug">
            ${MITIGATION_DESCRIPTIONS[mit.name] || "More information for this mitigation will be added soon."}
          </div>
        </div>
      </div>
    `;

    container.appendChild(wrapper);

    const checkbox = wrapper.querySelector(".mitigation-checkbox");
    checkbox?.addEventListener("change", () => {
      updateMitigationHighlights();
    });

    const infoBtn = wrapper.querySelector(".mitigation-info-btn");
    const infoBox = wrapper.querySelector(".mitigation-info");

    infoBtn?.addEventListener("click", () => {
      if (!infoBox) return;

      const isClosed = infoBox.classList.contains("max-h-0");

      if (isClosed) {
        infoBox.classList.remove("max-h-0");
        infoBox.classList.add("max-h-[300px]");

        setTimeout(() => {
          let scrollArea = wrapper.parentElement;
          while (scrollArea) {
            const canScroll = scrollArea.scrollHeight > scrollArea.clientHeight;
            if (canScroll) break;
            scrollArea = scrollArea.parentElement;
          }

          if (!scrollArea) return;

          const infoRect = infoBox.getBoundingClientRect();
          const scrollRect = scrollArea.getBoundingClientRect();

          const cutOffBottom = infoRect.bottom - scrollRect.bottom;
          const cutOffTop = scrollRect.top - infoRect.top;

          if (cutOffBottom > 0) {
            scrollArea.scrollBy({
              top: cutOffBottom + 12,
              behavior: "smooth"
            });
          } else if (cutOffTop > 0) {
            scrollArea.scrollBy({
              top: -cutOffTop - 12,
              behavior: "smooth"
            });
          }
        }, 150);
      } else {
        infoBox.classList.remove("max-h-[300px]");
        infoBox.classList.add("max-h-0");
      }
    });
  });
}

function setupMitigationListeners() {
  const checkboxes = document.querySelectorAll(".mitigation-checkbox");

  checkboxes.forEach(cb => {
    cb.addEventListener("change", async () => {

      await refreshEdgeWeightsOnly();

      // If an edge is currently selected, refresh the inspector card
      const selectedEdge = cy ? cy.edges(":selected") : null;
      if (selectedEdge && selectedEdge.length > 0) {
        showDetailCard(selectedEdge[0].data(), "edge");
      }

      // Hide AI modal
      const aiModal = document.getElementById("aiModal");
      if (aiModal) {
        aiModal.classList.add("hidden");
        aiModal.style.left = "";
        aiModal.style.top = "";
        aiModal.style.right = "1.5rem";
      }

      // Hide AI button
      const aiAnalysisBtn = document.getElementById("aiAnalysisBtn");
      if (aiAnalysisBtn) aiAnalysisBtn.classList.add("hidden");

      // Clear AI cache
      lastPathSignature = null;
      lastAiAnalysisSignature = null;
      lastAiAnalysisData = null;
    });
  });
}

// --- PATH HIGHLIGHTING ---

function clearPathHighlighting() {

  if (!cy) return;

  cy.nodes().removeClass("pathNode dim");
  cy.edges().removeClass("pathEdge dim");

}

async function animateAttackPath(pathResp) {

  clearPathHighlighting();

  // dim the entire graph first
  cy.nodes().addClass("dim");
  cy.edges().addClass("dim");

  for (let i = 0; i < pathResp.nodes.length; i++) {

    const nodeId = pathResp.nodes[i].id;
    const node = cy.getElementById(nodeId);

    if (!node.empty()) {

      node.removeClass("dim");
      node.addClass("pathNode");

      node.animate({
        style: {
          width: 135,
          height: 135
        },
        duration: 120,
        easing: "ease-out"
      });

      node.animate({
        style: {
          width: 110,
          height: 110
        },
        duration: 220,
        easing: "ease-in"
      });
    }

    if (i > 0) {

      const prevId = pathResp.nodes[i - 1].id;

      const edge = cy.edges().filter(e =>
        e.data("source") === prevId &&
        e.data("target") === nodeId
      );

      edge.removeClass("dim");
      edge.addClass("pathEdge");

      edge.animate({
        style: {
          width: 6
        },
        duration: 400,
        easing: "ease-out"
      });
    }

    await new Promise(resolve => setTimeout(resolve, 420));
  }
}

// --- CARD UI EXPORTS ---

export function closeAiModal() {
  const aiModal = document.getElementById("aiModal");
  if (aiModal) {
    aiModal.classList.add("hidden");
    aiModal.style.left = "";
    aiModal.style.top = "";
    aiModal.style.right = "1.5rem";
  }
}

export function resetUiState() {
  // Clear path and mitigation highlighting from graph
  if (cy) {
    cy.nodes().removeClass(
      "pathNode dim mitigationNodeHighlight mitigationNodeRemoved hovered"
    );
    cy.edges().removeClass(
      "pathEdge dim mitigationHighlight mitigationRemoved edgeHovered"
    );
    cy.elements().unselect();
  }

  // Reset mitigation switches
  const switches = document.querySelectorAll(".mitigation-checkbox");
  switches.forEach(sw => {
    sw.checked = false;
  });

  // Reset path result UI
  const pathResult = document.getElementById("pathResult");
  const costValue = document.getElementById("totalCostValue");
  if (pathResult) pathResult.classList.add("hidden");
  if (costValue) costValue.innerHTML = "0";

  // Reset status
  const status = document.getElementById("status");
  if (status) {
    status.textContent = "System Ready";
    status.className =
      "font-mono text-emerald-400 uppercase tracking-widest drop-shadow-[0_0_8px_rgba(52,211,153,0.5)]";
  }

  // Hide AI button
  const aiAnalysisBtn = document.getElementById("aiAnalysisBtn");
  if (aiAnalysisBtn) {
    aiAnalysisBtn.classList.add("hidden");
    aiAnalysisBtn.disabled = false;
    aiAnalysisBtn.textContent = "What Does This Mean?";
  }

  // Hide and reset AI modal
  closeAiModal();

  // Hide inspector
  hideDetailCard();

  // Hide tooltip
  if (tooltip) {
    tooltip.classList.add("hidden");
    tooltip.innerHTML = "";
  }

  // Reset cached state
  lastPathSignature = null;
  lastAiAnalysisSignature = null;
  lastAiAnalysisData = null;
  lastHighlightedActions = new Set();
}

export function resetEntireAppState() {
  resetUiState();
  resetProfileForm();

  const profileModal = document.getElementById("profileModal");
  if (profileModal) profileModal.classList.add("hidden");

  const userProfileModal = document.getElementById("userProfileModal");
  if (userProfileModal) userProfileModal.classList.add("hidden");

  if (cy) {
    cy.elements().remove();
  }
}

export function showDetailCard(data, type) {
  const card = document.getElementById("detailCard");
  const content = document.getElementById("cardContent");
  const title = document.getElementById("cardTitle");
  if (!card) return;

  card.classList.remove("hidden");
  if (cy) { cy.resize(); cy.fit(undefined, 20); }

  title.className = "text-sm font-bold text-slate-400 uppercase tracking-widest";

  if (type === "node") {
  const details = NODE_DESCRIPTIONS[data.id] || {
    desc: "This system is part of the modeled business environment and may play a role in attack progression.",
    impact: "Its presence can influence how attackers move through the network and reach higher-value assets."
  };

  title.textContent = "Node Inspector";

  content.innerHTML = `
    <div class="space-y-5">

      <section class="space-y-1">
        <div class="text-[10px] text-slate-500 uppercase font-bold tracking-wider">System</div>
        <div class="text-xl font-semibold leading-tight">${data.label}</div>
      </section>

      <section class="space-y-1">
        <div class="text-[10px] text-slate-500 uppercase font-bold tracking-wider">Description</div>
        <p class="text-sm text-slate-300 leading-5">${details.desc}</p>
      </section>

      <section class="rounded-xl border border-purple-500/20 bg-purple-500/5 p-3 space-y-1">
        <div class="text-[10px] text-purple-400 uppercase font-bold tracking-wider">Why It Matters</div>
        <p class="text-[12px] text-slate-300 italic leading-5">${details.impact}</p>
      </section>

    </div>
  `;
  return;
  } else {
    const details = ATTACK_DETAILS[data.attackAction] || {
      desc: "Information is being updated.",
      rationale: "N/A"
    };

    const sourceNode = cy ? cy.getElementById(data.source) : null;
    const targetNode = cy ? cy.getElementById(data.target) : null;

    const sourceLabel = sourceNode && !sourceNode.empty()
      ? sourceNode.data("label")
      : data.source;

    const targetLabel = targetNode && !targetNode.empty()
      ? targetNode.data("label")
      : data.target;

    let difficultyLabel = "Low";
    let difficultyClass = "text-emerald-400 bg-emerald-500/10 border border-emerald-500/20";

    if (data.weight >= 5) {
      difficultyLabel = "High";
      difficultyClass = "text-rose-400 bg-rose-500/10 border border-rose-500/20";
    } else if (data.weight >= 3) {
      difficultyLabel = "Medium";
      difficultyClass = "text-amber-400 bg-amber-500/10 border border-amber-500/20";
    }

    title.textContent = "Edge Inspector";
    content.innerHTML = `
      <div class="space-y-5">

        <section class="space-y-1">
          <div class="text-[10px] text-slate-500 uppercase font-bold tracking-wider">Attack Method</div>
          <div class="text-xl text-rose-400 font-semibold leading-tight">${data.attackAction}</div>
        </section>

        <section class="space-y-2">
          <div class="text-[10px] text-slate-500 uppercase font-bold tracking-wider">Path Step</div>
          <div class="rounded-xl border border-slate-700/60 bg-slate-900/40 p-3 space-y-2">
            <div class="text-sm font-medium text-slate-200 leading-snug text-center">${sourceLabel}</div>
            <div class="text-xs text-slate-500 font-semibold tracking-wider text-center">TO</div>
            <div class="text-sm font-medium text-slate-200 leading-snug text-center">${targetLabel}</div>
          </div>
        </section>

        <section class="space-y-1">
          <div class="text-[10px] text-slate-500 uppercase font-bold tracking-wider">Description</div>
          <p class="text-sm text-slate-300 leading-5">${details.desc}</p>
        </section>

        <section class="grid grid-cols-2 gap-3 pt-1">
          <div class="rounded-xl border border-slate-700/60 bg-slate-900/50 p-3">
            <div class="text-[10px] text-slate-500 uppercase font-bold tracking-wider mb-2">Current Cost</div>
            <div class="text-2xl font-mono font-bold text-white leading-none">${data.weight}</div>
          </div>

          <div class="rounded-xl p-3 ${difficultyClass}">
            <div class="text-[10px] uppercase font-bold tracking-wider mb-2 opacity-80">Difficulty</div>
            <div class="text-lg font-semibold leading-none">${difficultyLabel}</div>
          </div>
        </section>

        <section class="rounded-xl border border-purple-500/20 bg-purple-500/5 p-3 space-y-1">
          <div class="text-[10px] text-purple-400 uppercase font-bold tracking-wider">Why It Matters</div>
          <p class="text-[12px] text-slate-300 italic leading-5">${details.rationale}</p>
        </section>

      </div>
    `;
  }
}

export function hideDetailCard() {
  const card = document.getElementById("detailCard");
  if (card) card.classList.add("hidden");
  if (cy) { cy.resize(); cy.fit(undefined, 20); }
}

// --- LOGIC EXPORTS ---

function buildPathSignature(pathResp) {
  if (!pathResp) return null;

  const nodeIds = (pathResp.nodes || []).map(n => n.id).join("->");

  const checkedBoxes = document.querySelectorAll(".mitigation-checkbox:checked");
  const mitigationIds = Array.from(checkedBoxes)
    .map(cb => cb.dataset.id)
    .sort()
    .join(",");

  return `${nodeIds}|cost:${pathResp.totalCost}|mits:${mitigationIds}`;
}

export async function computeAndShowPath() {
  const status = document.getElementById("status");
  const pathResult = document.getElementById("pathResult");
  const costValue = document.getElementById("totalCostValue");
  const aiAnalysisBtn = document.getElementById("aiAnalysisBtn");

  const aiModal = document.getElementById("aiModal");

  if (aiModal) {
    aiModal.classList.add("hidden");
    aiModal.style.left = "";
    aiModal.style.top = "";
    aiModal.style.right = "1.5rem";
  }

  try {
    status.textContent = "Computing...";
    const pathResp = await fetchAttackPath("ATTACKER", "CUSTOMER_DB");

    const currentSignature = buildPathSignature(pathResp);

    if (lastPathSignature !== currentSignature) {
      lastAiAnalysisSignature = null;
      lastAiAnalysisData = null;
    }

    lastPathSignature = currentSignature;

    await animateAttackPath(pathResp);
    
    status.textContent = "Path Found";
    status.className = "font-mono text-rose-500 uppercase tracking-widest animate-pulse font-bold drop-shadow-[0_0_10px_rgba(244,63,94,0.7)]";
    if (pathResult && costValue) {
      pathResult.classList.remove("hidden");
      const targetCost = Number(pathResp.totalCost) || 0;
      animateValue(costValue, 0, targetCost, 600);
    }

    if (aiAnalysisBtn) {
      aiAnalysisBtn.classList.remove("hidden");
    }

  } catch (err) {
    status.textContent = `Error: ${err.message}`;
  }
}

export async function runAiAnalysis() {
  const status = document.getElementById("status");
  const aiAnalysisBtn = document.getElementById("aiAnalysisBtn");

  if (!lastPathSignature) return;

  if (lastAiAnalysisSignature === lastPathSignature && lastAiAnalysisData) {
    renderAiModal(lastAiAnalysisData);
    return;
  }

  try {
    if (status) {
      status.textContent = "Analyzing...";
      status.className = "font-mono text-slate-300 uppercase tracking-widest";
    }

    if (aiAnalysisBtn) {
      aiAnalysisBtn.disabled = true;
      aiAnalysisBtn.textContent = "Loading...";
    }

    const aiData = await fetchAiSummary("ATTACKER", "CUSTOMER_DB");

    lastAiAnalysisSignature = lastPathSignature;
    lastAiAnalysisData = aiData;

    renderAiModal(aiData);

    if (status) {
      status.textContent = "AI Ready";
      status.className = "font-mono text-emerald-400 uppercase tracking-widest drop-shadow-[0_0_8px_rgba(52,211,153,0.5)]";
    }
  } catch (err) {
    console.error("AI summary failed:", err);
    if (status) {
      status.textContent = "AI Error";
      status.className = "font-mono text-rose-500 uppercase tracking-widest";
    }
  } finally {
    if (aiAnalysisBtn) {
      aiAnalysisBtn.disabled = false;
      aiAnalysisBtn.textContent = "What Does This Mean?";
    }
  }
}

export function clearPath() {
  resetUiState();
}

export function resetProfileForm() {
  const fields = [
  "usesVPN",
  "hasFileServer",
  "usesSaaS",
  "hasPublicWebApp",
  "usesIdentityProvider",
  "hasEmailServer",
  "hasDomainController",
  "hasInternalApp",
  "hasHRSystem",
  "hasFinanceSystem",
  "hasBackupServer",
  "hasMDMServer",
  "hasWirelessAccessPoint",
  "hasFirewall",
  "hasDNSServer"
];

  fields.forEach((id) => {
    const checkbox = document.getElementById(id);
    if (checkbox) checkbox.checked = false;
  });
}

function fillProfileForm(profile) {
  const fields = [
    "usesVPN",
    "hasFileServer",
    "usesSaaS",
    "hasPublicWebApp",
    "usesIdentityProvider",
    "hasEmailServer",
    "hasDomainController",
    "hasInternalApp",
    "hasHRSystem",
    "hasFinanceSystem",
    "hasBackupServer",
    "hasMDMServer",
    "hasWirelessAccessPoint",
    "hasFirewall",
    "hasDNSServer"
  ];

  fields.forEach((field) => {
    const checkbox = document.getElementById(field);
    if (checkbox) checkbox.checked = !!profile[field];
  });
}

function renderProfileSummary(profile) {
  const content = document.getElementById("userProfileContent");
  if (!content) return;

  const yesNo = (value) => value ? "Yes" : "No";

  const rows = [
    ["Uses VPN", profile.usesVPN],
    ["Has File Server", profile.hasFileServer],
    ["Uses SaaS", profile.usesSaaS],
    ["Has Public Web App", profile.hasPublicWebApp],
    ["Uses Identity Provider", profile.usesIdentityProvider],
    ["Has Email Server", profile.hasEmailServer],
    ["Has Domain Controller", profile.hasDomainController],
    ["Has Internal App", profile.hasInternalApp],
    ["Has HR System", profile.hasHRSystem],
    ["Has Finance System", profile.hasFinanceSystem],
    ["Has Backup Server", profile.hasBackupServer],
    ["Has MDM Server", profile.hasMDMServer],
    ["Has Wireless Access Point", profile.hasWirelessAccessPoint],
    ["Has Firewall", profile.hasFirewall],
    ["Has DNS Server", profile.hasDNSServer]
  ];

  content.innerHTML = rows.map(([label, value], index) => `
    <div class="flex justify-between items-center ${index < rows.length - 1 ? "border-b border-slate-700/50 pb-2" : ""}">
      <span class="text-slate-300">${label}</span>
      <span class="font-semibold text-white">${yesNo(value)}</span>
    </div>
  `).join("");
}

export async function openUserProfileModal() {
  const modal = document.getElementById("userProfileModal");
  const content = document.getElementById("userProfileContent");
  const emailEl = document.getElementById("userProfileEmail");

  if (!modal || !content || !emailEl) return;

  modal.classList.remove("hidden");
  content.innerHTML = `<p class="text-slate-400">Loading profile...</p>`;
  emailEl.textContent = "Loading...";

  try {
    const [profile, user] = await Promise.all([
      fetchProfile(),
      fetchCurrentUserInfo()
    ]);

    renderProfileSummary(profile);
    emailEl.textContent = user.userEmail ?? "Unavailable";
  } catch {
    content.innerHTML = `<p class="text-red-400">Unable to load saved profile.</p>`;
    emailEl.textContent = "Unavailable";
  }
}

export function closeUserProfileModal() {
  const modal = document.getElementById("userProfileModal");
  if (modal) modal.classList.add("hidden");
}

export async function openProfileEditor() {
  try {
    const profile = await fetchProfile();
    fillProfileForm(profile);
  } catch {
    resetProfileForm();
  }

  closeUserProfileModal();

  const questionnaireModal = document.getElementById("profileModal");
  if (questionnaireModal) questionnaireModal.classList.remove("hidden");
}

export async function initializeApp() {
  resetEntireAppState();

  try {
    const res = await fetch("/api/profile", {
      credentials: "include"
    });

    if (!res.ok) throw new Error("No profile");

    const modal = document.getElementById("profileModal");
    if (modal) modal.classList.add("hidden");

    await renderGraph();

  } catch {
    resetUiState();

    const modal = document.getElementById("profileModal");
    if (modal) modal.classList.remove("hidden");
  }
}

export async function renderGraph() {
  const selectedMitigationIds = Array.from(
  document.querySelectorAll(".mitigation-checkbox:checked")
).map(cb => cb.dataset.id);

  const status = document.getElementById("status");
  const pathResult = document.getElementById("pathResult");
  if (pathResult) pathResult.classList.add("hidden");
  if (status) {
    status.textContent = "Loading...";
    status.className = "font-mono text-slate-500 uppercase tracking-widest transition-all duration-500";
  }

  const apiGraph = await fetchGraph();
  const elements = toCytoscapeElements(apiGraph);

  if (!cy) {
    cy = cytoscape({
      container: document.getElementById("cy"),
      elements,
      wheelSensitivity: 0.3,
      layout: {
        name: "fcose",
        nodeDimensionsIncludeLabels: true,
        padding: 80,
        idealEdgeLength: 60,
        nodeRepulsion: 8000,
        edgeElasticity: 0.45,
        gravity: 0.1,
        numIter: 1500,
        animate: false
      },
      style: [
        {
          selector: "node",
          style: {
            label: "data(label)",
            shape: "ellipse",
            "text-valign": "center",
            "text-halign": "center",
            "text-wrap": "wrap",
            "text-max-width": 99,
            "line-height": 1.4,
            "font-size": 15,
            color: "#ffffff",
            width: 110,
            height: 110,
            "border-width": 2,
            "border-color": "#1e293b",
            "overlay-opacity": 0,
            "transition-property": "width, height, border-width, border-color, font-size",
            "transition-duration": "0.2s"
          }
        },
        { selector: 'node[id = "ATTACKER"]', style: { "background-color": "#a51433" } },
        { selector: 'node[id = "WEB_APP"]', style: { "background-color": "#2563eb" } },
        { selector: 'node[id = "VPN"]', style: { "background-color": "#7c3aed" } },
        { selector: 'node[id = "EMPLOYEE_EMAIL"]', style: { "background-color": "#b65e2e" } },
        { selector: 'node[id = "EMPLOYEE_WORKSTATION"]', style: { "background-color": "#0891b2" } },
        { selector: 'node[id = "IDENTITY_PROVIDER"]', style: { "background-color": "#ca8a04" } },
        { selector: 'node[id = "ADMIN_ACCOUNT"]', style: { "background-color": "#b35d81" } },
        { selector: 'node[id = "FILE_SERVER"]', style: { "background-color": "#0f766e" } },
        { selector: 'node[id = "THIRD_PARTY_SAAS"]', style: { "background-color": "#4338ca" } },
        { selector: 'node[id = "CUSTOMER_DB"]', style: { "background-color": "#16a34a" } },
        { selector: 'node[id = "FIREWALL"]', style: { "background-color": "#dc2626" } },
        { selector: 'node[id = "WIRELESS_ACCESS_POINT"]', style: { "background-color": "#0ea5e9" } },
        { selector: 'node[id = "EMAIL_SERVER"]', style: { "background-color": "#f97316" } },
        { selector: 'node[id = "MDM_SERVER"]', style: { "background-color": "#14b8a6" } },
        { selector: 'node[id = "DOMAIN_CONTROLLER"]', style: { "background-color": "#eab308" } },
        { selector: 'node[id = "DNS_SERVER"]', style: { "background-color": "#22b2c5" } },
        { selector: 'node[id = "BACKUP_SERVER"]', style: { "background-color": "#6366f1" } },
        { selector: 'node[id = "INTERNAL_APP"]', style: { "background-color": "#a855f7" } },
        { selector: 'node[id = "HR_SYSTEM"]', style: { "background-color": "#ec4899" } },
        { selector: 'node[id = "FINANCE_SYSTEM"]', style: { "background-color": "#f59e0b" } },
        {
          selector: "edge",
          style: {
            label: "",
            "font-size": 9,
            color: "#cbd5e1",
            "text-rotation": "autorotate",
            width: 2,
            "curve-style": "unbundled-bezier",
            "line-color": "#64748b",
            "target-arrow-shape": "triangle",
            "overlay-opacity": 0,
            "transition-property": "width, line-color, target-arrow-color",
            "transition-duration": "0.2s"
          }
        },
        
{
  selector: "edge.mitigationHighlight",
  style: {
    width: 5,
    "line-color": "#22c55e",
    "target-arrow-color": "#22c55e",
    "transition-property": "line-color, width",
    "transition-duration": "0.4s"
  }
},
{
  selector: "node.mitigationNodeHighlight",
  style: {
    "border-width": 4,
    "border-color": "#22c55e",
    "shadow-blur": 10,
    "shadow-color": "#22c55e",
    "shadow-opacity": 0.5
  }
},
{
  selector: "edge.mitigationRemoved",
  style: {
    width: 5,
    "line-color": "#ef4444",
    "target-arrow-color": "#ef4444",
    "transition-property": "line-color, width",
    "transition-duration": "0.3s"
  }
},
{
  selector: "node.mitigationNodeRemoved",
  style: {
    "border-width": 4,
    "border-color": "#ef4444",
    "shadow-blur": 10,
    "shadow-color": "#ef4444",
    "shadow-opacity": 0.5
  }
},
{
  selector: "edge.pathEdge.mitigationHighlight",
  style: {
    width: 7,
    "line-color": "#f59e0b",
    "target-arrow-color": "#f59e0b",
    "shadow-color": "#f59e0b",
    "shadow-opacity": 0.8
  }
},
        {
          selector: "edge.pathEdge",
          style: {
            width: 6,
            "line-color": "#ef4444",
            "target-arrow-color": "#ef4444",
            "arrow-scale": 1.2,
            "shadow-blur": 14,
            "shadow-color": "#ef4444",
            "shadow-opacity": 0.7
          }
        },
        {
          selector: "node.pathNode",
          style: {
            "border-width": 3,
            "border-color": "#ef4444",
            "background-color": "#f87171",
            color: "#111",
            "shadow-blur": 18,
            "shadow-color": "#ef4444",
            "shadow-opacity": 0.8
          }
        },
        { selector: "node.hovered", style: { width: 130, height: 130, "font-size": 20, "border-width": 2, "border-color": "#ffffff", "z-index": 9999,} },
        {
          selector: "node:selected",
          style: {
            width: 125,
            height: 125,
            "border-width": 3,
            "border-color": "#ffffff",
            "z-index": 9999
          }
        },
        {
          selector: "edge:selected",
          style: {
            "line-color": "#ffffff",
            "target-arrow-color": "#ffffff",
            "width": 4
          }
        },
        {
          selector: "edge.edgeHovered",
          style: {
            width: 4,
            "line-color": "#ffffff",
            "target-arrow-color": "#ffffff",
            "arrow-scale": 1.2,
            "z-index": 999
          }
        },
        {
          selector: ".dim",
          style: {
            opacity: 0.25
          }
        }
      ]
    });

    document.getElementById("closeCard").onclick = hideDetailCard;

    cy.on("tap", "node", (evt) => {
      cy.nodes().unselect();
      evt.target.select();
      showDetailCard(evt.target.data(), "node");
    });

    cy.on("tap", "edge", (evt) => {
      cy.edges().unselect();
      evt.target.select();
      showDetailCard(evt.target.data(), "edge");
    });

    cy.on("tap", (evt) => {
      if (evt.target === cy) {
        cy.nodes().unselect();
        hideDetailCard();
      }
    });

    cy.on("mouseover", "node", (evt) => evt.target.addClass("hovered"));
    cy.on("mouseout", "node", (evt) => evt.target.removeClass("hovered"));
    cy.on("mouseover", "edge", (evt) => {
    const edge = evt.target;

    edge.addClass("edgeHovered");

    const data = edge.data();
    tooltip.innerHTML = `
    <div class="text-xs font-semibold">${data.attackAction}</div>
    <div class="text-[11px] text-slate-400">Cost: ${data.weight}</div>
  `;

    tooltip.classList.remove("hidden");
  });

  cy.on("mousemove", "edge", (evt) => {
  tooltip.style.left = evt.originalEvent.clientX + 12 + "px";
  tooltip.style.top = evt.originalEvent.clientY + 12 + "px";
  });

  cy.on("mouseout", "edge", (evt) => {
    evt.target.removeClass("edgeHovered");
    tooltip.classList.add("hidden");
  });

  cy.on("drag", "node", (evt) => {

    const node = evt.target;
    const pos = node.renderedPosition();

    const container = cy.container().getBoundingClientRect();

    const margin = 60;

    const nearEdge =
      pos.x < margin ||
      pos.y < margin ||
      pos.x > container.width - margin ||
      pos.y > container.height - margin;

    if (nearEdge) {

      const currentZoom = cy.zoom();

      cy.zoom({
        level: currentZoom * 0.98,
        renderedPosition: {
          x: container.width / 2,
          y: container.height / 2
        }
      });

    }

  });

  cy.on("dragfree", "node", () => {

    cy.animate({
      fit: { padding: 20},
      duration: 300
    });

  });

  } else {
    cy.elements().remove();
    cy.add(elements);
  }

  cy.layout({
    name: "fcose",
    spacingFactor: 1.25,
    nodeDimensionsIncludeLabels: true,
    padding: 80,
    idealEdgeLength: 60,
    nodeRepulsion: 8000,
    edgeElasticity: 0.45,
    gravity: 0.1,
    animate: false
  }).run();

  await renderMitigations(selectedMitigationIds);
  updateMitigationHighlights();
  setupMitigationListeners();

  setTimeout(() => {
    if (cy) {
      cy.resize();
      cy.fit(undefined, 20);
      if (status) {
        status.textContent = "System Ready";
        status.className = "font-mono text-emerald-400 uppercase tracking-widest drop-shadow-[0_0_8px_rgba(52,211,153,0.5)] transition-all duration-500";
      }
    }
  }, 200);
}

// --- OBSERVERS ---

const container = document.getElementById('cy');
if (container) {
  new ResizeObserver(() => {
    if (cy) { cy.resize(); cy.fit(undefined, 20); }
  }).observe(container);
}

function setupAiModal() {
  const modal = document.getElementById("aiModal");
  const header = document.getElementById("aiModalHeader");
  const closeBtn = document.getElementById("closeAiModal");

  if (!modal || !header) return;

  let isDragging = false;
  let offsetX = 0;
  let offsetY = 0;

  header.addEventListener("mousedown", (e) => {
    isDragging = true;

    const rect = modal.getBoundingClientRect();
    offsetX = e.clientX - rect.left;
    offsetY = e.clientY - rect.top;

    modal.style.left = `${rect.left}px`;
    modal.style.top = `${rect.top}px`;
    modal.style.right = "auto";
  });

  document.addEventListener("mousemove", (e) => {
    if (!isDragging) return;

    const modalRect = modal.getBoundingClientRect();
    const modalWidth = modalRect.width;
    const modalHeight = modalRect.height;

    const viewportWidth = window.innerWidth;
    const viewportHeight = window.innerHeight;

    let newLeft = e.clientX - offsetX;
    let newTop = e.clientY - offsetY;

    newLeft = Math.max(0, Math.min(newLeft, viewportWidth - modalWidth));
    newTop = Math.max(0, Math.min(newTop, viewportHeight - modalHeight));

    modal.style.left = `${newLeft}px`;
    modal.style.top = `${newTop}px`;
  });

  document.addEventListener("mouseup", () => {
    isDragging = false;
  });

  closeBtn?.addEventListener("click", () => {
    modal.classList.add("hidden");
  });
}

document.addEventListener("DOMContentLoaded", () => {
  const saveBtn = document.getElementById("saveProfileBtn");
  const profileBtn = document.getElementById("profileBtn");
  const closeUserProfileModalBtn = document.getElementById("closeUserProfileModal");
  const userProfileBackdrop = document.getElementById("userProfileBackdrop");
  const editProfileConfigBtn = document.getElementById("editProfileConfigBtn");

  profileBtn?.addEventListener("click", async () => {
    await openUserProfileModal();
  });

  closeUserProfileModalBtn?.addEventListener("click", () => {
    closeUserProfileModal();
  });

  userProfileBackdrop?.addEventListener("click", () => {
    closeUserProfileModal();
  });

  editProfileConfigBtn?.addEventListener("click", async () => {
    await openProfileEditor();
  });

  if (!saveBtn) return;

  saveBtn.addEventListener("click", async () => {
    const profile = {
      usesVPN: document.getElementById("usesVPN").checked,
      hasFileServer: document.getElementById("hasFileServer").checked,
      usesSaaS: document.getElementById("usesSaaS").checked,
      hasPublicWebApp: document.getElementById("hasPublicWebApp").checked,
      usesIdentityProvider: document.getElementById("usesIdentityProvider").checked,
      hasEmailServer: document.getElementById("hasEmailServer").checked,
      hasDomainController: document.getElementById("hasDomainController").checked,
      hasInternalApp: document.getElementById("hasInternalApp").checked,
      hasHRSystem: document.getElementById("hasHRSystem").checked,
      hasFinanceSystem: document.getElementById("hasFinanceSystem").checked,
      hasBackupServer: document.getElementById("hasBackupServer").checked,
      hasMDMServer: document.getElementById("hasMDMServer").checked,
      hasWirelessAccessPoint: document.getElementById("hasWirelessAccessPoint").checked,
      hasFirewall: document.getElementById("hasFirewall").checked,
      hasDNSServer: document.getElementById("hasDNSServer").checked
    };

    await fetch("/api/profile", {
      method: "POST",
      headers: { "Content-Type": "application/json" },
      credentials: "include",
      body: JSON.stringify(profile)
    });

    document.getElementById("profileModal")?.classList.add("hidden");

    await renderGraph();
  });
  setupAiModal();
});

