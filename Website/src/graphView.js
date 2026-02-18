import cytoscape from "cytoscape";

let cy = null;

/* =======================
   LABEL MAPPING
======================= */
function shortLabel(id) {
  const map = {
    attacker: "Attacker",
    webApp: "Web App",
    vpn: "VPN",
    employeeEmail: "Employee Email",
    employeeWorkstation: "Employee Workstation",
    identityProvider: "IdP",
    adminAccount: "Admin",
    customerDb: "Customer Database",
    fileServer: "File Server",
    thirdPartySaas: "SaaS"
  };
  return map[id] || id;
}

/* =======================
   ELEMENT CONVERSION
======================= */
function toCytoscapeElements(apiGraph) {
  const nodes = (apiGraph.nodes ?? []).map((n) => ({
    data: {
      id: n.id,
      type: n.type ?? "",
      label: shortLabel(n.id),
    },
  }));

  const edges = (apiGraph.edges ?? []).map((e) => ({
    data: {
      id: `${e.source}__${e.target}__${e.attackAction ?? ""}`,
      source: e.source,
      target: e.target,
      attackAction: e.attackAction ?? "",
      weight: Number(e.weight ?? 1),
      label: `${e.attackAction ?? ""} (${e.weight ?? 1})`,
    },
  }));

  return [...nodes, ...edges];
}

/* =======================
   API
======================= */
async function fetchGraph() {
  const res = await fetch("/api/graph");
  if (!res.ok) throw new Error(`GET /api/graph failed: ${res.status}`);
  return res.json();
}

async function fetchAttackPath(source, target) {
  const res = await fetch(
    `/api/path?source=${encodeURIComponent(source)}&target=${encodeURIComponent(target)}`
  );
  const data = await res.json();
  if (!res.ok) throw new Error(data.message || `Request failed`);
  return data;
}

/* =======================
   PATH HIGHLIGHTING
======================= */
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
      if (
        edge.data("source") === source &&
        edge.data("target") === target
      ) {
        edge.addClass("pathEdge");
      }
    });
  }
}

/* =======================
   FIXED POSITIONS (UNCHANGED)
======================= */
const FIXED_POSITIONS = {

  attacker: { x: 70, y: 301 },

  webApp: { x: 350, y: 120 },
  fileServer: { x: 975, y: 120 },
  customerDb: { x: 1350, y: 300 },

  vpn: { x: 350, y: 300 },
  employeeWorkstation: { x: 650, y: 301 },
  adminAccount: { x: 975, y: 400 },

  employeeEmail: { x: 350, y: 480 },
  identityProvider: { x: 650, y: 481 },
  thirdPartySaas: { x: 820, y: 620 }
};

function applyFixedPositions() {
  Object.entries(FIXED_POSITIONS).forEach(([id, pos]) => {
    const node = cy.getElementById(id);
    if (!node.empty()) {
      node.position(pos);
      node.unlock();
    }
  });
}

/* =======================
   PUBLIC FUNCTIONS
======================= */
export async function computeAndShowPath() {
  const status = document.getElementById("status");

  try {
    status.textContent = "Computing shortest path...";
    const pathResp = await fetchAttackPath("attacker", "customerDb");
    applyPathHighlight(pathResp);
    status.textContent = `Shortest path computed. Total cost = ${pathResp.totalCost}`;
  } catch (err) {
    status.textContent = `Error: ${err.message}`;
    console.error(err);
  }
}

export function clearPath() {
  clearPathHighlighting();
  const status = document.getElementById("status");
  if (status) status.textContent = "Cleared path highlight.";
}

/* =======================
   RENDER GRAPH
======================= */
export async function renderGraph() {
  const status = document.getElementById("status");
  if (status) status.textContent = "Loading...";

  const apiGraph = await fetchGraph();
  const elements = toCytoscapeElements(apiGraph);

  if (!cy) {
    cy = cytoscape({
      container: document.getElementById("cy"),
      elements,
      wheelSensitivity: 0.3,
      minZoom: 0.5,
      maxZoom: 2,
      layout: { name: "preset" },
      style: [

        /* DEFAULT NODE STYLE */
        {
          selector: "node",
          style: {
            label: "data(label)",
            shape: "ellipse",
            "text-valign": "center",
            "text-halign": "center",
            "text-wrap": "wrap",
            "text-max-width": 90,
            "line-height": 1.3,
            "font-size": 16,
            "font-weight": 500,
            color: "#ffffff",
            width: 100,
            height: 100,
            "border-width": 2,
            "border-color": "#1e293b",

            "overlay-opacity": 0,
          }
          
        },

        {
          selector: "node:selected",
          style: {
            "overlay-opacity": 0
          }
        },
        {
          selector: "node:active",
          style: {
           "overlay-opacity": 0
          }
        },

        /* ATTACKER */
        {
          selector: 'node[id = "attacker"]',
          style: { "background-color": "#e11d48" }   // crimson
        },

        /* WEB APP */
        {
          selector: 'node[id = "webApp"]',
          style: { "background-color": "#2563eb" }   // blue
        },

        /* VPN */
        {
          selector: 'node[id = "vpn"]',
          style: { "background-color": "#7c3aed" }   // purple
        },

        /* EMAIL */
        {
          selector: 'node[id = "employeeEmail"]',
          style: { "background-color": "#ea580c" }   // orange
        },

        /* WORKSTATION */
        {
          selector: 'node[id = "employeeWorkstation"]',
          style: { "background-color": "#0891b2" }   // teal
        },

        /* IDENTITY PROVIDER */
        {
          selector: 'node[id = "identityProvider"]',
          style: { "background-color": "#ca8a04" }   // gold
        },

        /* ADMIN */
        {
          selector: 'node[id = "adminAccount"]',
          style: { "background-color": "#be185d" }   // magenta
        },

        /* FILE SERVER */
        {
          selector: 'node[id = "fileServer"]',
          style: { "background-color": "#0f766e" }   // dark aqua
        },

        /* THIRD PARTY SAAS */
        {
          selector: 'node[id = "thirdPartySaas"]',
          style: { "background-color": "#4338ca" }   // indigo
        },

        /* CUSTOMER DB */
        {
          selector: 'node[id = "customerDb"]',
          style: { "background-color": "#16a34a" }   // green
        },



        /* DEFAULT EDGE */
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
            "target-arrow-color": "#64748b",
          }
        },

        /* PATH EDGE */
        {
          selector: "edge.pathEdge",
          style: {
            width: 8,
            "line-color": "#facc15",
            "target-arrow-color": "#facc15",
            "z-index": 9999
          }
        },

        /* PATH NODE */
        {
          selector: "node.pathNode",
          style: {
            label: "data(orderLabel)",
            "border-width": 6,
            "border-color": "#facc15",
            "background-color": "#fef08a",
            color: "#000",
            "z-index": 9999
          }
        },

        /* HOVER */
        {
          selector: "node.hovered",
          style: {
            width: 150,
            height: 150,
            "font-size": 23,

            "text-max-width": 145,   // 🔥 this fixes stacking
            "line-height": 1.3,

            "border-width": 5,
            "border-color": "#fbfbfb",
            "z-index": 9999,

            "transition-property": "width height font-size border-width text-max-width",
            "transition-duration": "200ms"
          }
        }
      ]
    });

    /* EVENTS (NO DUPLICATES) */

    cy.on("mouseover", "node", (evt) => {
      if (!evt.target.hasClass("dragging")) {
        evt.target.addClass("hovered");
      }
    });

    cy.on("mouseout", "node", (evt) => {
      evt.target.removeClass("hovered");
    });

    cy.on("grab", "node", (evt) => {
      evt.target.addClass("dragging");
      evt.target.removeClass("hovered");
    });

    cy.on("free", "node", (evt) => {
      evt.target.removeClass("dragging");
    });

  } else {
    cy.elements().remove();
    cy.add(elements);
  }

  applyFixedPositions();

  if (status) {
    status.textContent = `Loaded ${apiGraph.nodes?.length ?? 0} nodes, ${apiGraph.edges?.length ?? 0} edges`;
  }

  setTimeout(() => {
    if (cy) {
      cy.resize();
      cy.fit(undefined, 40);
      cy.zoom(cy.zoom() * 0.99);
      cy.center();
    }
  }, 200);
}




