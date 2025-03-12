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
