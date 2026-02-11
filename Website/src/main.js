import "./style.css";
import { renderGraph } from "./graphView.js";

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
