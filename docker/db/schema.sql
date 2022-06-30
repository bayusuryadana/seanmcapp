CREATE TABLE photos (
    id bigint NOT NULL,
    thumbnail_src character varying(2048) NOT NULL,
    date integer NOT NULL,
    caption character varying(100) NOT NULL,
    account character varying(50) NOT NULL
);

CREATE TABLE customers (
    id bigint NOT NULL,
    name character varying(200) NOT NULL,
    count integer DEFAULT 0 NOT NULL
);

CREATE TABLE accounts (
    id bigint NOT NULL,
    alias character varying(100) NOT NULL,
    group_type int NOT NULL
);

CREATE TABLE heroes (
    id smallint NOT NULL,
    localized_name character varying(32) NOT NULL,
    primary_attr character varying(3) NOT NULL,
    attack_type character varying(10) NOT NULL,
    roles character varying(50) NOT NULL,
    image character varying(128) NOT NULL,
    icon character varying(128) NOT NULL,
    lore text
);

CREATE TABLE hero_attributes (
    id smallint NOT NULL,
    base_health int NOT NULL,
    base_health_regen float NOT NULL,
    base_mana int NOT NULL,
    base_mana_regen float NOT NULL,
    base_armor int NOT NULL,
    base_mr int NOT NULL,
    base_attack_min int NOT NULL,
    base_attack_max int NOT NULL,
    base_str int NOT NULL,
    base_agi int NOT NULL,
    base_int int NOT NULL,
    str_gain float NOT NULL,
    agi_gain float NOT NULL,
    int_gain float NOT NULL,
    attack_range int NOT NULL,
    projectile_speed int NOT NULL,
    attack_rate float NOT NULL,
    move_speed int NOT NULL,
    turn_rate float NOT NULL,
    cm_enabled boolean NOT NULL
);

CREATE TABLE players (
    id integer NOT NULL,
    realname character varying(32) NOT NULL,
    avatarfull character varying(2083),
    personaname character varying(64) NOT NULL,
    rank_tier integer
);

CREATE TABLE people (
    id integer NOT NULL,
    name character varying(100) NOT NULL,
    day integer NOT NULL,
    month integer NOT NULL,
    CONSTRAINT people_day_check CHECK (((day > 0) AND (day <= 31))),
    CONSTRAINT people_month_check CHECK (((month > 0) AND (month <= 12)))
);

CREATE TABLE wallets (
    id SERIAL,
    date integer NOT NULL,
    name character varying(64) NOT NULL,
    category character varying(16) NOT NULL,
    currency character varying(4) NOT NULL,
    amount integer NOT NULL,
    done boolean NOT NULL,
    account varchar(20) NOT NULL
);

CREATE TABLE caches (
    key character varying(128) NOT NULL PRIMARY KEY,
    value character varying(40000) NOT NULL,
    expiry integer
);
