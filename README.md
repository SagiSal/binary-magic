# 🎩 Binary Magic Trick API

A production-grade microservices backend for a binary number magic trick — built with Java 21, Spring Boot 3, Redis, RabbitMQ, PostgreSQL, and React.

## Architecture

```
Client (Browser)
       │
       ▼ HTTP
  API Gateway :8080        ← JWT validation, routes via Eureka
       │
  ┌────┴────┬──────────────┐
  ▼         ▼              ▼
Auth :8081  Game :8082  Analytics :8083
            │              │
            ▼              ▼
          Redis         PostgreSQL
            │
         RabbitMQ ──────► Analytics
```

---

## Services

| Service           | Port | Responsibility                                      |
|-------------------|------|-----------------------------------------------------|
| eureka-server     | 8761 | Service registry — all services register here       |
| api-gateway       | 8080 | Single entry point, JWT validation, Eureka routing  |
| auth-service      | 8081 | Issues anonymous JWT sessions (login coming later)  |
| game-service      | 8082 | Binary card logic, game state in Redis, REST API    |
| analytics-service | 8083 | Async RabbitMQ consumer, persists stats to Postgres |
| frontend          | 3000 | React UI                                            |

---

## Game Flow

```
1. Get a session token (once)
   POST /api/auth/token
   ← { token, sessionId, expiresIn }

2. Start a game
   POST /api/game/session
   Authorization: Bearer <token>
   ← { sessionId, cardIndex: 0, numbers: [1,3,5,7,9,11...] }

3. Answer each card (repeat 6 times)
   POST /api/game/session/{id}/answer
   Authorization: Bearer <token>
   Body: { "answer": true }
   ← { cardIndex: 1, numbers: [2,3,6,7,10,11...] }   ← next card
   ← { result: 42 }                                   ← after 6th answer
```

---

## Stack

| Layer       | Technology                          |
|-------------|-------------------------------------|
| Language    | Java 21                             |
| Framework   | Spring Boot 3.2                     |
| Gateway     | Spring Cloud Gateway + Eureka       |
| Auth        | JJWT 0.12 (anonymous sessions)      |
| Game        | Spring MVC + Spring Data Redis      |
| Messaging   | RabbitMQ (topic exchange + DLQ)     |
| Cache/State | Redis                               |
| Database    | PostgreSQL 16 (analytics only)      |
| Frontend    | React + TypeScript                  |
| Container   | Docker + Docker Compose             |
| CI          | GitHub Actions                      |

---

## Quick Start

### Prerequisites
- Docker Desktop (or Docker + Docker Compose)
- Git

### 1. Clone & configure
```bash
git clone https://github.com/YOUR_USERNAME/binary-magic.git
cd binary-magic

# Create your local .env from the template
cp .env.example .env
# Open .env and fill in values
# Generate a JWT secret:
openssl rand -hex 64
```

### 3. Start infrastructure

```bash
docker compose up postgres redis rabbitmq
```

### 4. Start services (one terminal each)

```bash
# Terminal 1
cd eureka-server && mvn spring-boot:run

# Terminal 2
cd auth-service && mvn spring-boot:run

# Terminal 3
cd api-gateway && mvn spring-boot:run

# Terminal 4
cd game-service && mvn spring-boot:run

# Terminal 5
cd analytics-service && mvn spring-boot:run
```

### 5. Try it

```bash
# Get a token
curl -X POST http://localhost:8080/api/auth/token

# Start a game (paste your token)
curl -X POST http://localhost:8080/api/game/session \
  -H "Authorization: Bearer <token>"
```

---

## Service URLs

| Service         | Port | Path                    |
|-----------------|------|-------------------------|
| API Gateway     | 8080 | /actuator/health        |
| Auth Service    | 8081 | /actuator/health        |
| Game Service    | 8082 | /actuator/health        |
| Analytics       | 8083 | /actuator/health        |
| Eureka Dashboard| 8761 | /                        |
| RabbitMQ UI     | 15672| /                        |

---

## Project Structure

```
binary-magic/
├── eureka-server/          ← Service registry
├── api-gateway/            ← JWT validation + routing
├── auth-service/           ← Token issuance
├── game-service/           ← Game logic + REST API
├── analytics-service/      ← Event consumer + stats
├── frontend/               ← React app
├── docker/
│   ├── postgres/init.sql   ← DB schema
│   └── rabbitmq/           ← Exchange + queue definitions
├── docker-compose.yml
└── .env.example
```

---

## What's Coming

- [ ] Full login / registration (Auth Service expansion)
- [ ] WebSocket live game mode
- [ ] Deployment guide (Railway + Vercel)
- [ ] Frontend UI

---

## Contributing

See [CONTRIBUTING.md](./CONTRIBUTING.md) for branch strategy and commit conventions.
