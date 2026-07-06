# CORS demo

A tiny vanilla-JS frontend that calls the Spring API from **another origin**, so the
browser actually enforces CORS. No framework, no build step, no dependencies.

## Why a separate frontend?
CORS only kicks in when the **browser** talks to a **different origin** (scheme + host + port).
A Thymeleaf page served from `localhost:8080` is the *same* origin as the API, so CORS never
triggers. Here the page is served from `localhost:3000` / `:5500` and calls the API on
`localhost:8080` → cross-origin → CORS applies.

## Run it

1. **Start the API** (any profile, h2 is easiest — no DB needed):
   ```bash
   export JAVA_HOME=~/.jdks/jdk-21.0.11+10
   ./mvnw spring-boot:run -Dspring-boot.run.profiles=h2
   ```

2. **Serve this folder on two ports at once** (one Node process, no install):
   ```bash
   node cors-demo/serve.js
   ```
   → `http://localhost:3000` (allowed) **and** `http://localhost:5500` (not allowed).

3. Open **both** in the browser and try the buttons. The page shows which port it is
   served from and whether that origin is in the API allowlist.

## What to observe
| Origin | In `cors.allowedOrigins`? | Result |
|--------|---------------------------|--------|
| `http://localhost:3000` | ✅ yes | requests succeed |
| `http://localhost:5500` | ❌ no  | browser **blocks** the responses |

- **Button 1** (`GET /test`) is a *simple* request → no preflight.
- **Buttons 2 & 3** send a JSON body / `Authorization` header → *non-simple* → the browser
  fires an **`OPTIONS` preflight** first (visible in DevTools → Network).
- The key lesson: CORS is enforced by the **browser**, not by your app logic. A non-browser
  client (curl, Postman, another backend) ignores it completely — so CORS is browser-user
  safety, **not** authentication. (This app also rejects disallowed origins with `403` at the
  Spring CORS filter, before your controller even runs — try the curl commands below.)

  ```bash
  # Allowed origin → 200 + Access-Control-Allow-Origin
  curl -i -H "Origin: http://localhost:3000" http://localhost:8080/test
  # Disallowed origin → 403, no Access-Control-Allow-Origin
  curl -i -H "Origin: http://localhost:5500" http://localhost:8080/test
  ```

The allowlist lives in `src/main/resources/application-*.yml` (`cors.allowedOrigins`) and is
applied in `SecurityConfiguration.corsConfigurationSource()`. Add `http://localhost:5500`
there (and restart) to watch the blocked origin start working.
