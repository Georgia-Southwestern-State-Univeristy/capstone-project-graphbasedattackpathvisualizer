import "./style.css";
import { renderGraph, computeAndShowPath, clearPath, initializeApp } from "./graphView.js";

/*
function setupHowToModal() {
  const modal = document.getElementById("howToModal");
  const closeBtn = document.getElementById("closeHowTo");
  const okBtn = document.getElementById("howToOk");
  const backdrop = document.getElementById("howToBackdrop");
  const dontShow = document.getElementById("dontShowAgain");

  if (!modal) return;

  const STORAGE_KEY = "gbav_hide_howto";

  const open = () => {
    modal.classList.remove("hidden");
    // Prevent background scroll if you ever enable scrolling
    document.body.style.overflow = "hidden";
  };

  const close = () => {
    modal.classList.add("hidden");
    document.body.style.overflow = "";
    if (dontShow?.checked) localStorage.setItem(STORAGE_KEY, "1");
  };

  closeBtn?.addEventListener("click", close);
  okBtn?.addEventListener("click", close);
  backdrop?.addEventListener("click", close);

  // Show on first visit
  const hide = localStorage.getItem(STORAGE_KEY) === "1";
  if (!hide) open();
}
*/
//document.addEventListener("DOMContentLoaded", setupHowToModal);


document.addEventListener("DOMContentLoaded", async () => {

  document.getElementById("reloadBtn")
    ?.addEventListener("click", () => renderGraph());

  document.getElementById("computeBtn")
    ?.addEventListener("click", () => computeAndShowPath());

  document.getElementById("clearPathBtn")
    ?.addEventListener("click", () => clearPath());

  await initializeApp();
});

