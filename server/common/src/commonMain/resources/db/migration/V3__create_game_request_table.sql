CREATE TYPE game_request_status AS ENUM ('WAITING', 'STARTING', 'STARTED');

CREATE TABLE game_request
(
    id                    UUID PRIMARY KEY,
    creator_account_id    UUID                NOT NULL REFERENCES account (id),
    creator_deck_state_id UUID                NOT NULL REFERENCES account_deck_state (id),
    joiner_account_id     UUID REFERENCES account (id),
    joiner_deck_state_id  UUID REFERENCES account_deck_state (id),
    status                game_request_status NOT NULL,
    game_id               UUID,
    created_at            TIMESTAMP           NOT NULL DEFAULT NOW(),
    updated_at            TIMESTAMP           NOT NULL DEFAULT NOW()
);

CREATE INDEX idx_game_request_status ON game_request (status);
CREATE INDEX idx_game_request_creator ON game_request (creator_account_id);
CREATE INDEX idx_game_request_created_at ON game_request (created_at DESC);
