CREATE TABLE account_deck
(
    id         UUID PRIMARY KEY,
    account_id UUID NOT NULL REFERENCES account (id),
    pack_id    UUID NOT NULL REFERENCES pack (id)
);

CREATE INDEX idx_account_deck_account_id ON account_deck (account_id);

CREATE TABLE account_deck_state
(
    id         UUID PRIMARY KEY,
    deck_id    UUID      NOT NULL REFERENCES account_deck (id),
    card_ids   TEXT[]    NOT NULL,
    created_at TIMESTAMP NOT NULL DEFAULT NOW()
);

CREATE INDEX idx_account_deck_state_deck_id ON account_deck_state (deck_id);
CREATE INDEX idx_account_deck_state_created_at ON account_deck_state (deck_id, created_at DESC);
