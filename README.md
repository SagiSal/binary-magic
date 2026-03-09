<<<<<<< HEAD
# binary-magic
=======
# 🎩 Binary Magic Trick API

A production-grade microservices backend for a binary number magic trick — built with Java 21, Spring Boot 3, Redis, RabbitMQ, PostgreSQL, and React.

## Architecture

```
Client (Browser / Mobile)
        │
        ▼ HTTPS / WSS
   API Gateway :8080     ← JWT validation, rate limiting
        │
   ┌────┴────┬──────────────┐
   ▼         ▼              ▼
Auth :8081  Game :8082   Analytics :8083
   │         │              │
   └────┬────┘              │
        ▼                   ▼
     Redis              PostgreSQL
     RabbitMQ ──────────────┘
```

## Services

| Service          | Port | Responsibility                              |
|------------------|------|---------------------------------------------|
| api-gateway      | 8080 | Single ingress, JWT check, rate limit        |
| auth-service     | 8081 | Register, login, JWT issue, token refresh    |
| game-service     | 8082 | Binary cards logic, WebSocket sessions       |
| analytics-service| 8083 | Async event consumer, stats persistence      |
| frontend         | 3000 | React UI                                    |

## Stack

- **Backend**: Java 21 + Spring Boot 3
- **Gateway**: Spring Cloud Gateway
- **Auth**: Spring Security + JJWT
- **Game**: Spring WebFlux (reactive WebSocket)
- **Queue**: RabbitMQ (topic exchange + DLQ)
- **Cache**: Redis (rate limit, JWT blacklist, game state)
- **Database**: PostgreSQL 16
- **Frontend**: React + TypeScript
- **Container**: Docker + Docker Compose

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

# Edit .env and set real passwords + generate JWT secret:
# openssl rand -hex 64
```

### 2. Start everything
```bash
docker compose up --build
```

### 3. Access
| Service           | URL                          |
|-------------------|------------------------------|
| Frontend          | http://localhost:3000        |
| API Gateway       | http://localhost:8080        |
| RabbitMQ UI       | http://localhost:15672       |
| Auth Service      | http://localhost:8081/actuator/health |
| Game Service      | http://localhost:8082/actuator/health |
| Analytics Service | http://localhost:8083/actuator/health |

## Development Workflow

See [CONTRIBUTING.md](./CONTRIBUTING.md) for branch strategy and commit conventions.

## Deployment

See [DEPLOYMENT.md](./DEPLOYMENT.md) for free-tier deployment guide (Railway + Vercel).
>>>>>>> e0cc914 (chore(infra): init monorepo with docker-compose and project structure)
