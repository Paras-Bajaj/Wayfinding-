# 🧭 Indoor Wayfinding System

Shortest-path navigation inside a large campus — "Google Maps for indoors."

Given a start and destination, returns the shortest walking route with turn-by-turn directions, handling accessibility, time-based closures, and congestion.

---

## Features

- **Shortest path** — Dijkstra with priority queue
- **Turn-by-turn directions** — ordered node list
- **Multi-stop routing** — visit several stops in best order (TSP-flavored)
- **Nearest POI** — washroom, water, exit, coffee
- **Wheelchair accessibility** — avoids stairs, uses lifts/ramps only
- **Time-based closures** — corridors open only during set hours
- **Peak-hour congestion** — penalize busy corridors
- **JWT authentication** with role-based access
- **LRU cache** (Caffeine) for repeated routes
- **Monitoring** via Actuator + Prometheus
- **Interactive floor map** in the frontend

---

## Tech Stack

**Backend:** Java 17, Spring Boot 3.2.5, Spring Security (JWT), Spring Data JPA, Hibernate, PostgreSQL 16, Flyway, Caffeine, Micrometer, Springdoc OpenAPI, Lombok, JUnit 5

**Frontend:** React 18, Vite, TypeScript, Tailwind CSS v4, React Router, Axios, React Query

---

## Architecture
Frontend (React) → REST API (Spring Boot) → PostgreSQL
│
┌─────┴─────┐
│ Algorithm │ Dijkstra + multi-stop
│ Cache │ Caffeine LRU
│ Security │ JWT filter
└───────────┘

text

**Layers:** Controller → Service → Algorithm → Repository → Entity  
**OOP:** Encapsulation (CampusGraph), Abstraction (repository interfaces), Polymorphism (exception handler), SRP (each class one job)

---

## Algorithm & Complexity

**Dijkstra with binary heap** — chosen because edge weights are non-negative and it gives an optimal path.

| Operation | Time | Space |
|---|---|---|
| Graph build | O(V + E) | O(V + E) |
| Dijkstra | O((V + E) log V) | O(V + E) |
| Multi-stop (n ≤ 7, brute force) | O(n! · (V+E) log V) | O(n²) |
| Multi-stop (n > 7, nearest-neighbor) | O(n² · (V+E) log V) | O(n²) |
| Cache lookup | O(1) | — |

A* is a future improvement — marginal gain at ~1000 nodes.

---

## Real-World Constraints

- **Accessibility** — wheelchair mode filters out stairs and inaccessible edges
- **Time closures** — edges have `openFrom`/`openTo`; closed edges skipped at query time
- **Congestion** — edge weight = `distance × congestionFactor` during peak hours

---

## API Endpoints

| Method | Endpoint | Auth | Purpose |
|---|---|---|---|
| POST | `/api/auth/register` | No | Register |
| POST | `/api/auth/login` | No | Login → JWT |
| POST | `/api/route` | Yes | Shortest path |
| POST | `/api/route/multi-stop` | Yes | Multi-stop route |
| GET | `/api/poi/nearest` | Yes | Nearest POI |
| GET | `/api/monitoring/cache-stats` | Yes | Cache stats |
| POST | `/api/admin/reload-graph` | Admin | Reload graph from DB |
| POST | `/api/admin/invalidate-cache` | Admin | Clear cache |
| GET | `/actuator/health` | No | Health check |

Swagger UI: http://localhost:8080/swagger-ui.html

---

## Database (9 tables)

`buildings` → `floors` → `nodes` → `edges`  
`pois`, `users`, `user_roles`, `audit_logs`, `flyway_schema_history`

Managed by Flyway: `V1__init_schema.sql`

---

## How to Run

**Prerequisites:** JDK 17, Node.js 20, Docker

### 1. Start PostgreSQL

```bash
docker run -d --name wayfinding-db \
  -e POSTGRES_DB=wayfinding \
  -e POSTGRES_USER=wayfinding \
  -e POSTGRES_PASSWORD=wayfinding \
  -p 5432:5432 postgres:16
2. Backend
bash
cd backend
./mvnw clean package -DskipTests
java -Duser.timezone=Asia/Kolkata -jar target/indoor-wayfinding-1.0.0.jar --spring.profiles.active=dev
Windows PowerShell:

powershell
java "-Duser.timezone=Asia/Kolkata" -jar target\indoor-wayfinding-1.0.0.jar --spring.profiles.active=dev
3. Frontend
bash
cd frontend
npm install
npm run dev
Open http://localhost:5173
Login: alice / secret123

Trade-offs
Decision	Why
Dijkstra over A*	Optimal, simple; A* gain negligible at this scale
In-memory graph	Fast; reloaded via admin endpoint when layout changes
LRU + TTL cache	Simpler than precomputation; invalidated on reload
Brute-force ≤ 7 stops	7! = 5040 — fast and optimal; heuristic beyond
JWT over sessions	Stateless, scales horizontally
PostgreSQL over NoSQL	Campus layout is inherently relational
Sync clear + rebuild on reload	Simple; atomic swap is a future improvement
Testing
bash
cd backend
./mvnw test
Edge cases covered: start = end, unreachable destination, invalid node ID, wheelchair with no accessible route, empty DB, duplicate registration.

Monitoring
Custom metrics: route duration, cache hits/misses, route successes/failures
Endpoints: /actuator/health, /actuator/metrics, /actuator/prometheus

Project Structure
text
indoor-wayfinding/
├── backend/                 Spring Boot application
│   └── src/main/java/com/lpu/wayfinding/
│       ├── algorithm/       DijkstraRouter, MultiStopRouter, CampusGraph
│       ├── cache/           RouteCache (Caffeine)
│       ├── config/          Security, Cache, OpenAPI, DataSeeder
│       ├── controller/      Auth, Route, Poi, Admin, Monitoring
│       ├── dto/             Request + Response objects
│       ├── entity/          Building, Floor, Node, Edge, Poi, User, AuditLog
│       ├── exception/       Custom exceptions + GlobalExceptionHandler
│       ├── repository/      JPA repositories
│       ├── security/        JWT filter + service
│       ├── service/         Business logic
│       └── util/            EdgeType, NodeType
│
└── frontend/                React + Vite + TS
    └── src/
        ├── api/             Axios client with JWT interceptor
        ├── context/         AuthContext
        ├── components/      Navbar, FloorMap, ProtectedRoute
        └── pages/           Login, Register, Dashboard, Route,
                             MultiStop, POI, Admin, Monitoring
