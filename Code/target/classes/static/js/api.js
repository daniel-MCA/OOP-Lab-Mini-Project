const API_BASE = ""; // same-origin

const Api = {
  async post(path, body) { return Api._call(path, "POST", body); },
  async get(path) { return Api._call(path, "GET"); },
  async put(path, body) { return Api._call(path, "PUT", body); },
  async del(path) { return Api._call(path, "DELETE"); },

  async _call(path, method, body) {
    const res = await fetch(API_BASE + path, {
      method,
      headers: body ? { "Content-Type": "application/json" } : {},
      body: body ? JSON.stringify(body) : undefined
    });
    if (res.status === 204) return null;
    let data = null;
    try { data = await res.json(); } catch (e) { /* no body */ }
    if (!res.ok) {
      const message = (data && data.error) ? data.error : `Request failed (${res.status})`;
      throw new Error(message);
    }
    return data;
  }
};

const Session = {
  save(user) { localStorage.setItem("qbank_user", JSON.stringify(user)); },
  get() {
    const raw = localStorage.getItem("qbank_user");
    return raw ? JSON.parse(raw) : null;
  },
  clear() { localStorage.removeItem("qbank_user"); },
  requireLogin() {
    const u = Session.get();
    if (!u) { window.location.href = "/login.html"; return null; }
    return u;
  },
  requireRole(role) {
    const u = Session.requireLogin();
    if (u && u.role !== role) {
      window.location.href = u.role === "ADMIN" ? "/admin.html" : "/student-home.html";
      return null;
    }
    return u;
  }
};

function toast(message, type = "success") {
  const el = document.createElement("div");
  el.className = `toast ${type}`;
  el.textContent = message;
  document.body.appendChild(el);
  setTimeout(() => el.remove(), 3200);
}

function escapeHtml(str) {
  const div = document.createElement("div");
  div.textContent = str ?? "";
  return div.innerHTML;
}
