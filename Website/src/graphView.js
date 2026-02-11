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

  const edges = (apiGraph.edges ?? []).map((e, idx) => ({
    data: {
      id: `e${idx}-${e.source}->${e.target}`,
      source: e.source,         // required
      target: e.target,         // required
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

export async function renderGraph() {
  const status = document.getElementById("status");
  status.textContent = "Loading...";

  const apiGraph = await fetchGraph();
  const elements = toCytoscapeElements(apiGraph);

  if (!cy) {
    cy = cytoscape({
      container: document.getElementById("cy"),
      elements,
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
