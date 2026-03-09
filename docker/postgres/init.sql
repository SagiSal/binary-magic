-- This script runs once on first postgres startup
-- Creates separate databases per service (no cross-service DB access)

CREATE DATABASE auth_db;
CREATE DATABASE analytics_db;

-- auth_db schema
\connect auth_db;

CREATE EXTENSION IF NOT EXISTS "pgcrypto";

CREATE TABLE users (
    id           UUID        PRIMARY KEY DEFAULT gen_random_uuid(),
    username     VARCHAR(50) UNIQUE NOT NULL,
    email        VARCHAR(100) UNIQUE NOT NULL,
    password     VARCHAR(60) NOT NULL,   -- BCrypt hash (cost 12)
    created_at   TIMESTAMPTZ NOT NULL DEFAULT now(),
    updated_at   TIMESTAMPTZ NOT NULL DEFAULT now()
);

CREATE INDEX idx_users_email    ON users(email);
CREATE INDEX idx_users_username ON users(username);

-- analytics_db schema
\connect analytics_db;

CREATE EXTENSION IF NOT EXISTS "pgcrypto";

CREATE TABLE game_sessions (
    id               UUID        PRIMARY KEY DEFAULT gen_random_uuid(),
    user_id          UUID        NOT NULL,           -- denormalized from auth_db (no FK across DBs)
    started_at       TIMESTAMPTZ NOT NULL,
    completed_at     TIMESTAMPTZ,
    chosen_number    SMALLINT,                       -- 1-63, revealed at completion
    result_correct   BOOLEAN,
    duration_ms      INTEGER,
    status           VARCHAR(20) NOT NULL DEFAULT 'IN_PROGRESS'  -- IN_PROGRESS | COMPLETED | ABANDONED
);

CREATE TABLE game_answers (
    id           UUID        PRIMARY KEY DEFAULT gen_random_uuid(),
    session_id   UUID        NOT NULL REFERENCES game_sessions(id) ON DELETE CASCADE,
    card_index   SMALLINT    NOT NULL,               -- 0-5
    answer       BOOLEAN     NOT NULL,               -- true=YES, false=NO
    answered_at  TIMESTAMPTZ NOT NULL DEFAULT now()
);

CREATE INDEX idx_sessions_user_id   ON game_sessions(user_id);
CREATE INDEX idx_sessions_started   ON game_sessions(started_at);
CREATE INDEX idx_sessions_status    ON game_sessions(status);
CREATE INDEX idx_answers_session_id ON game_answers(session_id);
