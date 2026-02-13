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


document.addEventListener("DOMContentLoaded", async () => {
  const reloadBtn = document.getElementById("reloadBtn");
  reloadBtn?.addEventListener("click", () => renderGraph());

  try {
    await renderGraph();
  } catch (err) {
    const status = document.getElementById("status");
    if (status) status.textContent = err.message;
    console.error(err);
  }
});

