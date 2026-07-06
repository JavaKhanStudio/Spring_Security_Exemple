// ---------------------------------------------------------------------------
//  CORS demo — vanilla JS. Calls the Spring API on a DIFFERENT origin, so the
//  browser enforces CORS. Nothing here is Spring-specific; it's plain fetch().
// ---------------------------------------------------------------------------

// Must match the backend's `cors.allowedOrigins` (see application-*.yml).
// Used only to PREDICT on the page whether this origin should be allowed.
const ALLOWED_ORIGINS = ["http://localhost:3000", "http://localhost:8080"];

let jwt = null; // token captured by the login call, reused by the protected call

// --- Show which origin/port this page is served from ------------------------
(function showOrigin() {
    const origin = window.location.origin;
    const port = window.location.port || "(default)";
    const allowed = ALLOWED_ORIGINS.includes(origin);

    document.getElementById("origin").textContent = origin + "  (port " + port + ")";
    document.getElementById("dot").style.background = allowed ? "var(--ok)" : "var(--bad)";

    const verdict = document.getElementById("verdict");
    verdict.textContent = allowed ? "in API allowlist" : "NOT in API allowlist";
    verdict.className = "verdict " + (allowed ? "allowed" : "blocked");

    document.getElementById("originHint").textContent = allowed
        ? "The API allows this origin → requests should succeed."
        : "The API does not allow this origin → the browser should BLOCK the responses (the server still receives them).";
})();

// --- Tiny logger ------------------------------------------------------------
function log(msg, type) {
    const el = document.getElementById("log");
    const time = new Date().toLocaleTimeString();
    el.innerHTML += '<span class="log-' + (type || "info") + '">[' + time + "] " + msg + "</span>\n";
    el.scrollTop = el.scrollHeight;
}
function clearLog() { document.getElementById("log").innerHTML = ""; }
function api() { return document.getElementById("apiBase").value.replace(/\/$/, ""); }

// A rejected fetch across origins is almost always the browser blocking CORS.
function explainError(err) {
    log("✖ " + err.name + ": " + err.message, "bad");
    log("   → The browser blocked this: this origin isn't in the API's allowlist, so it "
        + "refused to expose the response to your JS. Open DevTools → Console/Network to see "
        + "the CORS error.", "bad");
    log("   → CORS is a BROWSER rule. A non-browser client (curl, Postman, another backend) "
        + "ignores it entirely — so CORS is browser-user safety, not authentication.", "info");
}

// --- 1. Simple request (GET, no custom headers) → NO preflight --------------
async function callSimple() {
    log("→ GET " + api() + "/test   (simple request)", "req");
    try {
        const res = await fetch(api() + "/test");
        const body = await res.text();
        log("✔ " + res.status + " " + res.statusText + "  |  body: " + body, "ok");
    } catch (err) { explainError(err); }
}

// --- 2. Login (POST + JSON body) → PREFLIGHTED ------------------------------
async function doLogin() {
    const creds = {
        username: document.getElementById("username").value,
        password: document.getElementById("password").value,
    };
    log("→ POST " + api() + "/api/login   (JSON body → preflighted)", "req");
    try {
        const res = await fetch(api() + "/api/login", {
            method: "POST",
            headers: { "Content-Type": "application/json" },
            body: JSON.stringify(creds),
        });
        if (!res.ok) { log("✖ " + res.status + " " + res.statusText, "bad"); return; }
        const data = await res.json();
        jwt = data.token;
        log("✔ " + res.status + " logged in as " + data.username, "ok");
        log("   token stored (used by button 3): " + jwt.slice(0, 24) + "…", "info");
    } catch (err) { explainError(err); }
}

// --- 3. Protected request (Authorization header) → PREFLIGHTED --------------
async function callProtected() {
    if (!jwt) { log("! No token yet — click “2. Login” first.", "info"); return; }
    log("→ GET " + api() + "/examples/whoami   (Bearer token → preflighted)", "req");
    try {
        const res = await fetch(api() + "/examples/whoami", {
            headers: { "Authorization": "Bearer " + jwt },
        });
        const body = await res.text();
        log("✔ " + res.status + " " + res.statusText + "  |  " + body, "ok");
    } catch (err) { explainError(err); }
}
