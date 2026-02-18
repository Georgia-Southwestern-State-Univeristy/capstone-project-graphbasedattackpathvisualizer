import cytoscape from "cytoscape";

let cy = null;

function toCytoscapeElements(apiGraph) {
  // apiGraph = { nodes: [...], edges: [...] }

  const nodes = (apiGraph.nodes ?? []).map((n) => ({
    data: {
      id: n.id,           // required by cytoscape
      type: n.type ?? "", // optional, but useful for styling
      label: n.id,        // what we show on the node
    },
  }));

const edges = (apiGraph.edges ?? []).map((e) => ({
  data: {
    // IMPORTANT: must match what we compute/highlight later
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

async function fetchGraph() {
  const res = await fetch("/api/graph");
  if (!res.ok) throw new Error(`GET /api/graph failed: ${res.status}`);
  return res.json();
}
async function fetchAttackPath(source, target) {
  const res = await fetch(`/api/path?source=${encodeURIComponent(source)}&target=${encodeURIComponent(target)}`);
  if (!res.ok) throw new Error(`GET /api/path failed: ${res.status}`);
  return res.json();
}

function clearPathHighlighting() {
  if (!cy) return;
  cy.nodes().removeClass("pathNode");
  cy.edges().removeClass("pathEdge");
  cy.nodes().forEach((n) => n.removeData("orderLabel"));
}

function applyPathHighlight(pathResp) {
  clearPathHighlighting();

  if (!Array.isArray(pathResp.nodes) || !Array.isArray(pathResp.edges)) {
    throw new Error("Invalid path response format");
  }

  // Highlight nodes in order
  pathResp.nodes.forEach((nodeObj, i) => {
    const node = cy.getElementById(nodeObj.id);
    if (!node.empty()) {
      node.addClass("pathNode");
      node.data("orderLabel", `${i + 1}`);
    }
  });

  // Highlight edges IN ORDER between consecutive nodes
  for (let i = 0; i < pathResp.nodes.length - 1; i++) {
    const source = pathResp.nodes[i].id;
    const target = pathResp.nodes[i + 1].id;

    const matchingEdge = cy.edges().filter(edge =>
      edge.data("source") === source &&
      edge.data("target") === target
    );

    if (matchingEdge.length > 0) {
      matchingEdge.addClass("pathEdge");
    }
  }

  const sel = cy.elements(".pathNode, .pathEdge");
  if (sel.length > 0) cy.fit(sel, 60);
}


// EXPORT: called by your button
export async function computeAndShowPath() {
  const status = document.getElementById("status");

  const start = "attacker";
  const end = "customerDb";
  
  try {
    if (status) status.textContent = "Computing shortest path...";

    const pathResp = await fetchAttackPath(start, end);

    applyPathHighlight(pathResp);

    if (status) {
      status.textContent = `Shortest path computed. Total cost = ${pathResp.totalCost}`;
    }

  } catch (err) {
    if (status) status.textContent = "Error computing path.";
    console.error(err);
  }
}

// optional export
export function clearPath() {
  clearPathHighlighting();
  const status = document.getElementById("status");
  if (status) status.textContent = "Cleared path highlight.";
}

export async function renderGraph() {
  const status = document.getElementById("status");
  status.textContent = "Loading...";

  const apiGraph = await fetchGraph();

  const elements = toCytoscapeElements(apiGraph);


  if (!cy) {
    cy = cytoscape({
      container: document.getElementById("cy"),
      elements,
      wheelSensitivity: 0.3,
      minZoom: 0.5,
      maxZoom: 2,
      style: [
        {
          selector: "node",
          style: {
            label: "data(label)",
            "text-valign": "center",
            "text-halign": "center",
            "font-size": 10,
            "border-width": 1,
            "border-color": "#111",
            width: 46,
            height: 46,
          },
        },
        // Example: style by node type (optional)
        { selector: 'node[type="USER"]', style: { shape: "round-rectangle" } },
        { selector: 'node[type="SERVER"]', style: { shape: "rectangle" } },
        { selector: 'node[type="WORKSTATION"]', style: { shape: "ellipse" } },

        {
          selector: "edge",
          style: {
            label: "data(label)",
            "font-size": 9,
            "text-rotation": "autorotate",
            width: 2,
            "curve-style": "bezier",
            "target-arrow-shape": "triangle",
          },
        },
        {
  selector: "edge.pathEdge",
  style: {
    width: "mapData(weight, 1, 6, 6, 14)",
    "line-color": "#facc15",        // bright yellow
    "target-arrow-color": "#facc15",
    "z-index": 9999
  },
},

{
  selector: "node.pathNode",
  style: {
    label: "data(orderLabel)",
    "border-width": 5,
    "border-color": "#facc15",
    "background-color": "#fef08a",
    "z-index": 9999
  },
},

      ],
      layout: {
        name: "cose",     // good default for graphs
        animate: true,
      },
    });

    // Helpful: show edge/node info on click
    cy.on("tap", "node", (evt) => {
      const d = evt.target.data();
      status.textContent = `Node: ${d.id} | type=${d.type}`;
    });

    cy.on("tap", "edge", (evt) => {
      const d = evt.target.data();
      status.textContent = `Edge: ${d.source} -> ${d.target} | ${d.attackAction} | weight=${d.weight}`;
    });
  } else {
    // If already created, just replace elements + rerun layout
    cy.elements().remove();
    cy.add(elements);
    cy.layout({ name: "cose", animate: true }).run();
  }

  status.textContent = `Loaded ${apiGraph.nodes?.length ?? 0} nodes, ${apiGraph.edges?.length ?? 0} edges`;
  
}
