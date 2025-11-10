CREATE TYPE CARD_TIER AS ENUM('STANDARD', 'LEGENDARY');
CREATE TYPE TARGET_TYPE AS ENUM('ALLIES', 'ENEMIES', 'ANY', 'SELF');
CREATE TYPE STATUS_TYPE AS ENUM('ENHANCED', 'ENFEEBLED', 'ANY');
CREATE TYPE TRIGGER_TYPE AS ENUM('WhenPlayed', 'WhenDestroyed', 'WhenFirstStatusChanged', 'WhenFirstReachesPower', 'WhenLaneWon', 'WhileActive', 'None');

CREATE TABLE pack
(
    id    UUID PRIMARY KEY,
    alias VARCHAR(128) NOT NULL UNIQUE,
    name  VARCHAR(128) NOT NULL
);

CREATE TABLE pack_card
(
    id                    UUID PRIMARY KEY,
    pack_id               UUID REFERENCES pack NOT NULL,
    pack_internal_card_id VARCHAR(16)          NOT NULL,
    name                  VARCHAR(128)         NOT NULL,
    tier                  CARD_TIER            NOT NULL,
    rank                  SMALLINT             NOT NULL,
    power                 SMALLINT             NOT NULL,
    spawn_only            BOOLEAN              NOT NULL,
    increments            JSONB                NOT NULL,
    UNIQUE (pack_id, pack_internal_card_id)
);

CREATE TABLE pack_card_effect
(
    id           UUID PRIMARY KEY REFERENCES pack_card NOT NULL,
    type         VARCHAR(128)                          NOT NULL,
    target       TARGET_TYPE                           NOT NULL,
    affected     JSONB                                 NOT NULL,
    trigger_data JSONB                                 NOT NULL,
    effect_data  JSONB                                 NOT NULL,
    description  VARCHAR(128)                          NOT NULL
);
