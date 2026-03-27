import cytoscape from "cytoscape";
import fcose from "cytoscape-fcose";

cytoscape.use(fcose);

let cy = null;

// --- CONFIGURATION & DATA MAPS ---

const NODE_DESCRIPTIONS = {
  ATTACKER: "The starting point of the simulation representing an external threat actor.",
  WEB_APP: "Public-facing company web application; a common entry point for exploits.",
  VPN: "Remote access gateway providing entry into the internal corporate network.",
  EMPLOYEE_EMAIL: "Corporate email accounts; primary targets for phishing and credential theft.",
  EMPLOYEE_WORKSTATION: "Standard user endpoint used for internal lateral movement.",
  IDENTITY_PROVIDER: "The IdP (like Okta or Azure AD) managing authentication and SSO.",
  ADMIN_ACCOUNT: "High-privilege credentials capable of managing databases and servers.",
  CUSTOMER_DB: "The ultimate target; contains sensitive customer PII and records.",
  FILE_SERVER: "Internal storage containing shared documents and configuration files.",
  THIRD_PARTY_SAAS: "External cloud services (SaaS) integrated with corporate identity.",
  EMAIL_SERVER: "Business email infrastructure used for message delivery and mail access.",
  DOMAIN_CONTROLLER: "Centralized authentication and directory management server.",
  INTERNAL_APP: "Internal business application accessible from inside the organization.",
  HR_SYSTEM: "Human resources platform containing employee-related records.",
  FINANCE_SYSTEM: "Financial system containing accounting or payment-related data.",
  BACKUP_SERVER: "System used to store or manage organization backups.",
  MDM_SERVER: "Mobile device management platform used to manage endpoints and devices.",
  WIRELESS_ACCESS_POINT: "Wireless network access device providing internal connectivity.",
  FIREWALL: "Perimeter filtering and traffic control device between networks.",
  DNS_SERVER: "Internal or business DNS infrastructure used for hostname resolution."
};

const ATTACK_DETAILS = {
  "Phishing / Credential Theft": {
    desc: "Social engineering to trick employees into revealing credentials.",
    rationale: "Relies on successful user interaction and convincing lures."
  },
  "Exploit Web App / Weak Login": {
    desc: "Exploiting vulnerabilities in public-facing web applications or brute-forcing weak credentials.",
    rationale: "Web apps are high-exposure targets requiring specific exploit payloads."
  },
  "Stolen VPN Credentials": {
    desc: "Using compromised VPN accounts to gain a foothold in the internal network.",
    rationale: "VPNs provide direct encrypted access; difficulty depends on MFA presence."
  },
  "Malware Delivery": {
    desc: "Chaining vulnerabilities to execute malicious code on a target.",
    rationale: "Requires moderate technical effort and user execution."
  },
  "Remote Login (RDP / SSH)": {
    desc: "Using legitimate administrative protocols to move laterally between systems.",
    rationale: "Commonly allowed in internal networks, making it a low-noise movement method."
  },
  "Credential Reuse": {
    desc: "Applying obtained credentials to other internal systems.",
    rationale: "Low-effort technique once an initial set is compromised."
  },
  "Password Reset / SSO Abuse": {
    desc: "Abusing identity workflows to reset or hijack accounts.",
    rationale: "Requires understanding identity system authentication flows."
  },
  "OAuth Token Theft / SSO Abuse": {
    desc: "Stealing active session tokens to bypass authentication entirely.",
    rationale: "Highly effective as it bypasses standard password-based MFA."
  },
  "Over-Privileged Role Assignment": {
    desc: "Exploiting users or accounts with more permissions than necessary.",
    rationale: "Configuration-based weakness that significantly lowers the barrier for attackers."
  },
  "Access Shared Drive": {
    desc: "Gaining access to internal file servers and shared documentation.",
    rationale: "Often weakly protected once the internal network is breached."
  },
  "Privilege Escalation / Credential Dumping": {
    desc: "Extracting high-level credentials from memory or exploiting OS kernels.",
    rationale: "Technical and noisy; requires local system access first."
  },
  "Direct Network Access": {
    desc: "Bypassing internal controls to reach network resources.",
    rationale: "Requires internal positioning and network awareness."
  },
  "Stored Credentials / Config Leak": {
    desc: "Finding plain-text passwords in configuration files or scripts.",
    rationale: "Purely dependent on discovery; very easy if documentation is poor."
  },
  "API Access / Data Sync": {
    desc: "Using stolen API keys to extract data from cloud databases.",
    rationale: "Targets the data layer directly, often bypassing traditional host security."
  },
  "Admin DB Access": {
    desc: "Using full administrative rights to query or export the customer database.",
    rationale: "The final objective; difficulty is minimal once admin status is achieved."
  },

  "Wireless Network Compromise": {
    desc: "Gaining access to the internal network by exploiting weak wireless security or credentials.",
    rationale: "Wireless networks often expose internal access if not properly segmented or secured."
  },
  "Perimeter Device Exploit": {
    desc: "Targeting firewall or edge devices through misconfigurations or known vulnerabilities.",
    rationale: "Perimeter devices are high-value targets that can expose internal services if compromised."
  },
  "Local Network Access": {
    desc: "Accessing internal systems after gaining presence on the local network.",
    rationale: "Once inside the network, lateral movement becomes significantly easier."
  },
  "Internal Service Pivot": {
    desc: "Using one compromised system to pivot into another internal service.",
    rationale: "Internal trust relationships allow attackers to move deeper into the network."
  },
  "Mailbox / Server Abuse": {
    desc: "Exploiting email server features or mailbox access to maintain persistence or spread attacks.",
    rationale: "Email systems provide both communication control and data access."
  },
  "Domain Privilege Escalation": {
    desc: "Escalating privileges within the domain to gain administrative control.",
    rationale: "Domain escalation enables widespread access across systems and accounts."
  },
  "Internal Application Access": {
    desc: "Accessing internal business applications after gaining initial system access.",
    rationale: "Internal apps often trust authenticated users and lack strict defenses."
  },
  "HR System Access": {
    desc: "Accessing a human resources system from a compromised internal workstation to view or interact with employee-related records.",
    rationale: "Once an attacker has internal user-level access, HR platforms may be reachable through reused credentials, trusted sessions, or weak internal access controls."
  },
  "Finance System Access": {
    desc: "Accessing a financial system from a compromised internal workstation to view or interact with accounting, payroll, or payment-related information.",
    rationale: "Finance applications are often reachable from internal user systems and become attractive targets once an attacker gains a foothold inside the network."
  },
  "Backup System Access": {
    desc: "Accessing backup systems to retrieve sensitive data or disable recovery capabilities.",
    rationale: "Backups contain full data copies and are often less protected."
  },
  "Device Management Abuse": {
    desc: "Abusing device management systems to control endpoints or deploy malicious configurations.",
    rationale: "MDM systems have high privileges across managed devices."
  },
  "Network Discovery": {
    desc: "Scanning and mapping internal systems using network services like DNS.",
    rationale: "Understanding the network layout enables more targeted attacks."
  },
  "Malicious Email Delivery / Mail Rule Abuse": {
    desc: "Using compromised email infrastructure to deliver malicious content or create hidden forwarding rules.",
    rationale: "Email rules can persistently redirect or hide attacker activity."
  },
  "MDM Privilege Abuse": {
    desc: "Using elevated MDM permissions to gain administrative-level control over devices.",
    rationale: "MDM systems can enforce policies and execute actions across endpoints."
  },
  "Application Database Access": {
    desc: "Accessing backend databases through compromised applications.",
    rationale: "Applications often have direct connections to sensitive data stores."
  },
  "HR Data Access / Integration Abuse": {
    desc: "Using the HR system itself, or its connected integrations, to retrieve sensitive employee or organizational data from the central data store.",
    rationale: "Once an attacker reaches the HR platform, trusted integrations and backend connections can make sensitive records easier to reach than attacking the data store directly."
  },
  "Financial Data Access": {
    desc: "Using a compromised finance platform to retrieve, export, or manipulate sensitive financial records stored in the organization’s central data systems.",
    rationale: "Finance systems commonly have privileged access to highly valuable business data, making them effective stepping stones to the final target."
  },
  "Backup Data Exposure": {
    desc: "Extracting sensitive data from backup systems.",
    rationale: "Backups often contain unencrypted or complete datasets."
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

// --- UTILITY FUNCTIONS ---

const tooltip = document.getElementById("edgeTooltip");

function toCytoscapeElements(apiGraph) {
  const nodes = (apiGraph.nodes ?? []).map((n) => ({
    data: { id: n.id, type: n.type ?? "", label: n.displayName ?? n.id },
  }));

  const edges = (apiGraph.edges ?? []).map((e) => {
    const rawAction = e.attackAction ?? "";
    return {
      data: {
        id: `${e.source}__${e.target}__${rawAction}`,
        source: e.source,
        target: e.target,
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
  const res = await fetch("/api/graph", {
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

async function renderMitigations() {
  const container = document.getElementById("mitigationContainer");
  if (!container) return;

  const mitigations = await fetchMitigations();

  container.innerHTML = "";

  mitigations.forEach(mit => {

    const colorClass =
      MITIGATION_COLORS[mit.name] || "peer-checked:bg-emerald-500";

    const wrapper = document.createElement("div");

    wrapper.innerHTML = `
      <label class="flex items-center justify-between cursor-pointer group">
        <span class="font-medium group-hover:text-white transition">
          ${mit.name}
        </span>
        <div class="relative">
          <input type="checkbox"
                 class="sr-only peer mitigation-checkbox"
                 data-id="${mit.id}">
          <div class="w-12 h-6 bg-slate-600 rounded-full ${colorClass} transition-all duration-300 shadow-inner"></div>
          <div class="absolute left-1 top-1 w-4 h-4 bg-white rounded-full shadow-md transition-all duration-300 peer-checked:translate-x-6"></div>
        </div>
      </label>
    `;

    container.appendChild(wrapper);
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

export function showDetailCard(data, type) {
  const card = document.getElementById("detailCard");
  const content = document.getElementById("cardContent");
  const title = document.getElementById("cardTitle");
  if (!card) return;

  card.classList.remove("hidden");
  if (cy) { cy.resize(); cy.fit(undefined, 20); }

  title.className = "text-sm font-bold text-slate-400 uppercase tracking-widest";

  if (type === "node") {
    const desc = NODE_DESCRIPTIONS[data.id] || "Internal system asset.";
    title.textContent = "Node Inspector";
    content.innerHTML = `
      <div class="space-y-4">
        <section>
          <label class="text-[10px] text-slate-500 uppercase font-bold tracking-wider">Identifier</label>
          <div class="text-base text-white font-semibold">${data.label}</div>
        </section>
        <section>
          <label class="text-[10px] text-slate-500 uppercase font-bold tracking-wider">Description</label>
          <p class="text-sm text-slate-300 leading-relaxed">${desc}</p>
        </section>
      </div>
    `;
  } else {
    const details = ATTACK_DETAILS[data.attackAction] || {
      desc: "Information is being updated.",
      rationale: "N/A"
    };
    title.textContent = "Edge Inspector";
    content.innerHTML = `
      <div class="space-y-4">
        <section>
          <label class="text-[10px] text-slate-500 uppercase font-bold tracking-wider">Method</label>
          <div class="text-base text-rose-500 font-semibold leading-tight">${data.attackAction}</div>
        </section>
        <section>
          <label class="text-[10px] text-slate-500 uppercase font-bold tracking-wider">Description</label>
          <p class="text-sm text-slate-300 leading-relaxed">${details.desc}</p>
        </section>
        <div class="pt-5 space-y-3 border-t border-slate-700/30">
          <div class="p-3 bg-slate-900/60 rounded border border-slate-700/50">
            <label class="text-[10px] text-slate-500 uppercase font-bold block mb-1">Base Cost</label>
            <span class="text-white font-mono font-bold text-lg">${data.weight}</span>
          </div>
          <section class="p-3 bg-amber-500/5 border border-amber-500/20 rounded">
            <label class="text-[10px] text-amber-500 font-bold uppercase">Rationale</label>
            <p class="text-[11px] text-slate-400 mt-1 italic leading-snug">${details.rationale}</p>
          </section>
        </div>
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

export async function computeAndShowPath() {
  const status = document.getElementById("status");
  const pathResult = document.getElementById("pathResult");
  const costValue = document.getElementById("totalCostValue");
  try {
    status.textContent = "Computing...";
    const pathResp = await fetchAttackPath("ATTACKER", "CUSTOMER_DB");
    await animateAttackPath(pathResp);
    status.textContent = "Path Found";
    status.className = "font-mono text-rose-500 uppercase tracking-widest animate-pulse font-bold drop-shadow-[0_0_10px_rgba(244,63,94,0.7)]";
    if (pathResult && costValue) {
      pathResult.classList.remove("hidden");
      const targetCost = Number(pathResp.totalCost) || 0;
      animateValue(costValue, 0, targetCost, 600);
    }
  } catch (err) {
    status.textContent = `Error: ${err.message}`;
  }
}

export function clearPath() {

  clearPathHighlighting();

  // turn off all mitigation switches
  const switches = document.querySelectorAll(".mitigation-checkbox");
  switches.forEach(sw => sw.checked = false);

  const pathResult = document.getElementById("pathResult");
  const status = document.getElementById("status");
  const costValue = document.getElementById("totalCostValue");

  if (pathResult) pathResult.classList.add("hidden");
  if (costValue) costValue.innerHTML = "0";

  if (status) {
    status.textContent = "System Ready";
    status.className =
      "font-mono text-emerald-400 uppercase tracking-widest drop-shadow-[0_0_8px_rgba(52,211,153,0.5)]";
  }
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
  try {
    const res = await fetch("/api/profile", {
      credentials: "include"
    });

    if (!res.ok) throw new Error("No profile");

    const modal = document.getElementById("profileModal");
    if (modal) modal.classList.add("hidden");

    await renderGraph();

  } catch {
    resetProfileForm();

    const modal = document.getElementById("profileModal");
    if (modal) modal.classList.remove("hidden");
  }
}

export async function renderGraph() {
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
    tooltip.innerHTML = `${data.attackAction}`;

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

  await renderMitigations();

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
});

