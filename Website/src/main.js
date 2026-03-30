import "./style.css";
import { renderGraph, computeAndShowPath, clearPath, initializeApp, resetProfileForm } from "./graphView.js";
/* =========================
   AUTH FUNCTIONS
========================= */

async function fetchCurrentUser() {
  const res = await fetch("/api/auth/me", {
    method: "GET",
    credentials: "include"
  });

  if (!res.ok) return null;
  return res.json();
}

async function loginUser(userEmail, password) {
  const res = await fetch("/api/auth/login", {
    method: "POST",
    headers: {
      "Content-Type": "application/json"
    },
    credentials: "include",
    body: JSON.stringify({ userEmail, password })
  });

  let data = {};
  try {
    data = await res.json();
  } catch {}

  if (!res.ok) {
    throw new Error(data.message || "Invalid email or password.");
  }

  return data;
}

async function registerUser(userEmail, password) {
  const res = await fetch("/api/auth/register", {
    method: "POST",
    headers: {
      "Content-Type": "application/json"
    },
    credentials: "include",
    body: JSON.stringify({ userEmail, password })
  });

  let data = {};
  try {
    data = await res.json();
  } catch {}

  if (!res.ok) {
    throw new Error(data.message || "Registration failed.");
  }

  return data;
}

async function logoutUser() {
  const res = await fetch("/api/auth/logout", {
    method: "POST",
    credentials: "include"
  });

  if (!res.ok) {
    throw new Error("Logout failed.");
  }
}

/* =========================
   UI HELPERS
========================= */

function isValidEmail(email) {
  const emailRegex = /^[A-Za-z][A-Za-z0-9._%+-]*@[A-Za-z0-9.-]+\.[A-Za-z]{2,}$/;
  return emailRegex.test(email);
}

function isValidPassword(password) {
  const hasMinLength = password.length >= 8;
  const hasSpecialCharacter = /[^A-Za-z0-9]/.test(password);
  return hasMinLength && hasSpecialCharacter;
}

function showAuthOverlay() {
  document.getElementById("authOverlay")?.classList.remove("hidden");
  document.getElementById("appShell")?.classList.add("hidden");
}

function showAppShell() {
  document.getElementById("authOverlay")?.classList.add("hidden");
  document.getElementById("appShell")?.classList.remove("hidden");
}

/* =========================
   APP BUTTONS
========================= */
function setupHelpModal() {
  const helpBtn = document.getElementById("helpBtn");
  const helpModal = document.getElementById("helpModal");
  const helpBackdrop = document.getElementById("helpBackdrop");
  const closeHelpModal = document.getElementById("closeHelpModal");
  const howToOk = document.getElementById("howToOk");
  const dontShowAgain = document.getElementById("dontShowAgain");

  function openHelpModal() {
    console.log("Help clicked");
    console.log("helpBtn:", helpBtn);
    console.log("helpModal:", helpModal);
    helpModal?.classList.remove("hidden");
  }

  function closeHelp() {
    if (dontShowAgain?.checked) {
      localStorage.setItem("hideHelpModal", "true");
    }
    helpModal?.classList.add("hidden");
  }

  helpBtn?.addEventListener("click", openHelpModal);
  closeHelpModal?.addEventListener("click", closeHelp);
  howToOk?.addEventListener("click", closeHelp);
  helpBackdrop?.addEventListener("click", closeHelp);
}

function setupAppButtons() {
  document.getElementById("reloadBtn")
    ?.addEventListener("click", () => renderGraph());

  document.getElementById("computeBtn")
    ?.addEventListener("click", () => computeAndShowPath());

  document.getElementById("clearPathBtn")
    ?.addEventListener("click", () => clearPath());

    document.getElementById("logoutFromProfileBtn")
    ?.addEventListener("click", async () => {
      try {
        await logoutUser();

        const userProfileModal = document.getElementById("userProfileModal");
        if (userProfileModal) userProfileModal.classList.add("hidden");

        showAuthOverlay();

        const signInPw = document.getElementById("signInPassword");
        const signUpPw = document.getElementById("signUpPassword");
        const signUpConfirmPw = document.getElementById("signUpConfirmPassword");

        if (signInPw) signInPw.value = "";
        if (signUpPw) signUpPw.value = "";
        if (signUpConfirmPw) signUpConfirmPw.value = "";

        resetProfileForm();

      } catch (err) {
        alert(err.message);
      }
    });
}

/* =========================
   LOGIN FORM
========================= */

function setupAuthForms() {
  const signInTab = document.getElementById("signInTab");
  const signUpTab = document.getElementById("signUpTab");
  const authTabSlider = document.getElementById("authTabSlider");
  const authFormsTrack = document.getElementById("authFormsTrack");

  const signInForm = document.getElementById("signInForm");
  const signUpForm = document.getElementById("signUpForm");

  const signInError = document.getElementById("signInError");
  const signUpError = document.getElementById("signUpError");
  const signUpSuccess = document.getElementById("signUpSuccess");

  const signInSubmitBtn = document.getElementById("signInSubmitBtn");
  const signUpSubmitBtn = document.getElementById("signUpSubmitBtn");

  const signInEmail = document.getElementById("signInEmail");
  const signInPassword = document.getElementById("signInPassword");

  const signUpEmail = document.getElementById("signUpEmail");
  const signUpPassword = document.getElementById("signUpPassword");
  const signUpConfirmPassword = document.getElementById("signUpConfirmPassword");

  const passwordRuleHint = document.getElementById("passwordRuleHint");

  function showSignInTab() {
    authFormsTrack.style.transform = "translateX(0%)";
    authTabSlider.style.transform = "translateX(0%)";

    signInTab.classList.remove("text-slate-300");
    signInTab.classList.add("text-white");

    signUpTab.classList.remove("text-white");
    signUpTab.classList.add("text-slate-300");

    signInError.classList.add("hidden");
    signUpError.classList.add("hidden");
    signUpSuccess.classList.add("hidden");
  }

  function showSignUpTab() {
    authFormsTrack.style.transform = "translateX(-50%)";
    authTabSlider.style.transform = "translateX(100%)";

    signUpTab.classList.remove("text-slate-300");
    signUpTab.classList.add("text-white");

    signInTab.classList.remove("text-white");
    signInTab.classList.add("text-slate-300");

    signInError.classList.add("hidden");
    signUpError.classList.add("hidden");
    signUpSuccess.classList.add("hidden");
  }

  signInTab?.addEventListener("click", showSignInTab);
  signUpTab?.addEventListener("click", showSignUpTab);

  signInForm?.addEventListener("submit", async (event) => {
    event.preventDefault();

    signInError.classList.add("hidden");
    signInError.textContent = "";

    const userEmail = signInEmail.value.trim();
    const password = signInPassword.value;

    if (!userEmail || !password) {
      signInError.textContent = "Email and password are required.";
      signInError.classList.remove("hidden");
      return;
    }

    if (!isValidEmail(userEmail)) {
      signInError.textContent = "Please enter a valid email address.";
      signInError.classList.remove("hidden");
      return;
    }

    signInSubmitBtn.disabled = true;
    signInSubmitBtn.textContent = "Signing In...";

    try {
      await loginUser(userEmail, password);
      showAppShell();
      await initializeApp();
    } catch (err) {
      signInError.textContent = err.message || "Login failed.";
      signInError.classList.remove("hidden");
    } finally {
      signInSubmitBtn.disabled = false;
      signInSubmitBtn.textContent = "Sign In";
    }
  });

  signUpForm?.addEventListener("submit", async (event) => {
    event.preventDefault();

    signUpError.classList.add("hidden");
    signUpSuccess.classList.add("hidden");
    signUpError.textContent = "";
    signUpSuccess.textContent = "";

    const userEmail = signUpEmail.value.trim();
    const password = signUpPassword.value;
    const confirmPassword = signUpConfirmPassword.value;

    if (!userEmail || !password || !confirmPassword) {
      signUpError.textContent = "All fields are required.";
      signUpError.classList.remove("hidden");
      return;
    }

    if (!isValidEmail(userEmail)) {
      signUpError.textContent = "Please enter a valid email address.";
      signUpError.classList.remove("hidden");
      return;
    }

    if (!isValidPassword(password)) {
      signUpError.textContent = "Password must be at least 8 characters and include one special character.";
      signUpError.classList.remove("hidden");
      return;
    }

    if (password !== confirmPassword) {
      signUpError.textContent = "Passwords do not match.";
      signUpError.classList.remove("hidden");
      return;
    }

    signUpSubmitBtn.disabled = true;
    signUpSubmitBtn.textContent = "Signing Up...";

    try {
      await registerUser(userEmail, password);
      await loginUser(userEmail, password);

      signUpSuccess.textContent = "Account created successfully.";
      signUpSuccess.classList.remove("hidden");

      showAppShell();
      await initializeApp();
    } catch (err) {
      signUpError.textContent = err.message || "Registration failed.";
      signUpError.classList.remove("hidden");
    } finally {
      signUpSubmitBtn.disabled = false;
      signUpSubmitBtn.textContent = "Sign Up";
    }
  });

  [signInEmail, signInPassword].forEach((field) => {
    field?.addEventListener("input", () => {
      signInError.classList.add("hidden");
      signInError.textContent = "";
    });
  });

  [signUpEmail, signUpPassword, signUpConfirmPassword].forEach((field) => {
    field?.addEventListener("input", () => {
      signUpError.classList.add("hidden");
      signUpError.textContent = "";
      signUpSuccess.classList.add("hidden");
      signUpSuccess.textContent = "";
    });
  });

  signUpPassword?.addEventListener("input", () => {
    const password = signUpPassword.value;

    if (!password) {
      passwordRuleHint?.classList.remove("text-emerald-400", "text-red-400");
      passwordRuleHint?.classList.add("text-slate-400");
      return;
    }

    if (isValidPassword(password)) {
      passwordRuleHint?.classList.remove("text-slate-400", "text-red-400");
      passwordRuleHint?.classList.add("text-emerald-400");
    } else {
      passwordRuleHint?.classList.remove("text-slate-400", "text-emerald-400");
      passwordRuleHint?.classList.add("text-red-400");
    }
  });

  showSignInTab();
}

/* =========================
   MAIN INIT
========================= */

document.addEventListener("DOMContentLoaded", async () => {

  setupAppButtons();
  setupAuthForms();
  setupHelpModal();

  const user = await fetchCurrentUser();

  if (user) {
    // already logged in
    showAppShell();
    await initializeApp();
  } else {
    // not logged in
    showAuthOverlay();
  }

});
