#!/usr/bin/env node
// ---------------------------------------------------------------------------
//  Zero-dependency static server for the CORS demo.
//  Serves this folder on TWO ports at once (from a single process), so you can
//  open the SAME page from an allowed origin and a non-allowed origin and
//  compare CORS behaviour side by side.
//
//    node serve.js               -> http://localhost:3000  and  http://localhost:5500
//    node serve.js 3000 4000     -> custom pair of ports
//
//  Port 3000 is in the API's cors.allowedOrigins → requests succeed.
//  Port 5500 is NOT → the browser blocks the responses.
// ---------------------------------------------------------------------------
const http = require("http");
const fs = require("fs");
const path = require("path");

const args = process.argv.slice(2).map(Number).filter((n) => Number.isInteger(n) && n > 0);
const PORTS = args.length ? args : [3000, 5500];
const ROOT = __dirname;

const MIME = {
    ".html": "text/html; charset=utf-8",
    ".js": "text/javascript; charset=utf-8",
    ".css": "text/css; charset=utf-8",
    ".ico": "image/x-icon",
};

function handler(req, res) {
    let urlPath = decodeURIComponent(req.url.split("?")[0]);
    if (urlPath === "/") urlPath = "/index.html";
    const filePath = path.join(ROOT, path.normalize(urlPath));
    if (!filePath.startsWith(ROOT)) { res.writeHead(403); res.end("Forbidden"); return; }
    fs.readFile(filePath, (err, data) => {
        if (err) { res.writeHead(404); res.end("Not found"); return; }
        res.writeHead(200, { "Content-Type": MIME[path.extname(filePath)] || "application/octet-stream" });
        res.end(data);
    });
}

for (const port of PORTS) {
    const server = http.createServer(handler);
    // A busy port must not take down the other server — warn and keep going.
    server.on("error", (err) => {
        if (err.code === "EADDRINUSE") {
            console.error("⚠  port " + port + " is already in use — skipping it. "
                + "Free it, or choose other ports:  node serve.js <port1> <port2>");
        } else {
            console.error("⚠  port " + port + ": " + err.message);
        }
    });
    server.listen(port, () => {
        console.log("CORS demo served at http://localhost:" + port);
    });
}
