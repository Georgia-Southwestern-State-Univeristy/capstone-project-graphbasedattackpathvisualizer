import './style.css'
import cytoscape from "cytoscape";

const cy = cytoscape({
  container: document.getElementById("cy"),

  elements: [
    // Nodes
    { data: { id: "attacker", label: "Attacker" } },
    { data: { id: "email", label: "Email" } },
    { data: { id: "vpn", label: "VPN" } },
    { data: { id: "server", label: "Server" } },

    // Edges
    { data: { id: "e1", source: "attacker", target: "email" } },
    { data: { id: "e2", source: "email", target: "server" } },
    { data: { id: "e3", source: "attacker", target: "vpn" } },
    { data: { id: "e4", source: "vpn", target: "server" } },
  ],

  style: [
    {
      selector: "node",
      style: {
        label: "data(label)",
        "text-valign": "center",
        "text-halign": "center",
        "background-color": "#06b6d4",
        color: "#0f172a",
        width: 46,
        height: 46,
        "font-size": 12,
      },
    },
    {
      selector: "edge",
      style: {
        width: 3,
        "line-color": "#334155",
        "target-arrow-color": "#334155",
        "target-arrow-shape": "triangle",
        "curve-style": "bezier",
      },
    },
  ],

  layout: {
    name: "breadthfirst",
    directed: true,
    padding: 20,
  },

  // 🚫 Disable all user interaction
  userZoomingEnabled: false,
  userPanningEnabled: false,
  boxSelectionEnabled: false,
  autoungrabify: true,
  autounselectify: true,
});

