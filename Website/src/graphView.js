import cytoscape from "cytoscape";

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
  THIRD_PARTY_SAAS: "External cloud services (SaaS) integrated with corporate identity."
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
  "Network Segmentation": "peer-checked:bg-red-500"
};

// --- UTILITY FUNCTIONS ---

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
        label: `${rawAction} (${e.weight ?? 1})`,
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
  const res = await fetch("/api/graph");
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

  const res = await fetch(url);
  const data = await res.json();

  if (!res.ok) throw new Error(data.message || `Request failed`);

  return data;
}

async function fetchMitigations() {
  const res = await fetch("/api/mitigations");
  if (!res.ok) throw new Error("Failed to load mitigations");
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
  cy.nodes().removeClass("pathNode");
  cy.edges().removeClass("pathEdge");
  cy.nodes().forEach((n) => n.removeData("orderLabel"));
}

function applyPathHighlight(pathResp) {
  clearPathHighlighting();
  pathResp.nodes.forEach((nodeObj, i) => {
    const node = cy.getElementById(nodeObj.id);
    if (!node.empty()) {
      node.addClass("pathNode");
      node.data("orderLabel", `${i + 1}`);
    }
  });
  for (let i = 0; i < pathResp.nodes.length - 1; i++) {
    const source = pathResp.nodes[i].id;
    const target = pathResp.nodes[i + 1].id;
    cy.edges().forEach((edge) => {
      if (edge.data("source") === source && edge.data("target") === target) {
        edge.addClass("pathEdge");
      }
    });
  }
}

// --- CARD UI EXPORTS ---

export function showDetailCard(data, type) {
  const card = document.getElementById("detailCard");
  const content = document.getElementById("cardContent");
  const title = document.getElementById("cardTitle");
  if (!card) return;

  card.classList.remove("hidden");
  if (cy) { cy.resize(); cy.fit(undefined, 60); }

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
        <div class="pt-10 space-y-3 border-t border-slate-700/30">
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
  if (cy) { cy.resize(); cy.fit(undefined, 60); }
}

// --- LOGIC EXPORTS ---

export async function computeAndShowPath() {
  const status = document.getElementById("status");
  const pathResult = document.getElementById("pathResult");
  const costValue = document.getElementById("totalCostValue");
  try {
    status.textContent = "Computing...";
    const pathResp = await fetchAttackPath("ATTACKER", "CUSTOMER_DB");
    applyPathHighlight(pathResp);
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
  const pathResult = document.getElementById("pathResult");
  const status = document.getElementById("status");
  const costValue = document.getElementById("totalCostValue");
  if (pathResult) pathResult.classList.add("hidden");
  if (costValue) costValue.innerHTML = "0";
  if (status) {
    status.textContent = "System Ready";
    status.className = "font-mono text-emerald-400 uppercase tracking-widest drop-shadow-[0_0_8px_rgba(52,211,153,0.5)]";
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
        name: "breadthfirst",
        directed: true,
        padding: 40,
        spacingFactor: 1.2
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
            "text-max-width": 90,
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
        { selector: 'node[id = "EMPLOYEE_EMAIL"]', style: { "background-color": "#ea580c" } },
        { selector: 'node[id = "EMPLOYEE_WORKSTATION"]', style: { "background-color": "#0891b2" } },
        { selector: 'node[id = "IDENTITY_PROVIDER"]', style: { "background-color": "#ca8a04" } },
        { selector: 'node[id = "ADMIN_ACCOUNT"]', style: { "background-color": "#b35d81" } },
        { selector: 'node[id = "FILE_SERVER"]', style: { "background-color": "#0f766e" } },
        { selector: 'node[id = "THIRD_PARTY_SAAS"]', style: { "background-color": "#4338ca" } },
        { selector: 'node[id = "CUSTOMER_DB"]', style: { "background-color": "#16a34a" } },
        {
          selector: "edge",
          style: {
            label: "data(label)",
            "font-size": 9,
            color: "#cbd5e1",
            "text-rotation": "autorotate",
            width: 2,
            "curve-style": "bezier",
            "line-color": "#64748b",
            "target-arrow-shape": "triangle",
            "overlay-opacity": 0,
            "transition-property": "width, line-color, target-arrow-color",
            "transition-duration": "0.2s"
          }
        },
        { selector: "edge.pathEdge", style: { width: 8, "line-color": "#facc15", "target-arrow-color": "#facc15", "z-index": 9999 } },
        { selector: "node.pathNode", style: { label: "data(orderLabel)", "border-width": 6, "border-color": "#facc15", "background-color": "#fef08a", color: "#000", "z-index": 9999 } },
        { selector: "node.hovered", style: { width: 150, height: 150, "font-size": 25, "border-width": 4, "border-color": "#ffffff", "z-index": 9999 } },
        {
          selector: "node:selected",
          style: {
            width: 125,
            height: 125,
            "border-width": 6,
            "border-color": "#ffffff",
            "z-index": 9999
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
    cy.on("mouseover", "edge", (evt) => evt.target.addClass("edgeHovered"));
    cy.on("mouseout", "edge", (evt) => evt.target.removeClass("edgeHovered"));
  } else {
    cy.elements().remove();
    cy.add(elements);
  }

  cy.layout({
    name: "breadthfirst",
    directed: true,
    padding: 40,
    spacingFactor: 1.2
  }).run();

  await renderMitigations();

  setTimeout(() => {
    if (cy) {
      cy.resize();
      cy.fit(undefined, 60);
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
    if (cy) { cy.resize(); cy.fit(undefined, 60); }
  }).observe(container);
}