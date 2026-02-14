import "./style.css";
import { renderGraph, computeAndShowPath, clearPath } from "./graphView.js";

document.addEventListener("DOMContentLoaded", async () => {
  document.getElementById("reloadBtn")
    ?.addEventListener("click", () => renderGraph());

  document.getElementById("computeBtn")
    ?.addEventListener("click", () => computeAndShowPath());

  document.getElementById("clearPathBtn")
    ?.addEventListener("click", () => clearPath());

  await renderGraph();
});

