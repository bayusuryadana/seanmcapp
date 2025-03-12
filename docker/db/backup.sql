--
-- PostgreSQL database dump
--

-- Dumped from database version 11.4
-- Dumped by pg_dump version 14.6 (Homebrew)

SET statement_timeout = 0;
SET lock_timeout = 0;
SET idle_in_transaction_session_timeout = 0;
SET client_encoding = 'UTF8';
SET standard_conforming_strings = on;
SELECT pg_catalog.set_config('search_path', '', false);
SET check_function_bodies = false;
SET xmloption = content;
SET client_min_messages = warning;
SET row_security = off;

SET default_tablespace = '';

--
-- Name: people; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE public.people (
    id integer NOT NULL,
    name character varying(100) NOT NULL,
    day integer NOT NULL,
    month integer NOT NULL,
    CONSTRAINT people_day_check CHECK (((day > 0) AND (day <= 31))),
    CONSTRAINT people_month_check CHECK (((month > 0) AND (month <= 12)))
);


ALTER TABLE public.people OWNER TO postgres;

--
-- Name: wallets; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE public.wallets (
    id integer NOT NULL,
    date integer NOT NULL,
    name character varying(64) NOT NULL,
    category character varying(16) NOT NULL,
    currency character varying(4) NOT NULL,
    amount integer NOT NULL,
    done boolean NOT NULL,
    account character varying(20) NOT NULL
);


ALTER TABLE public.wallets OWNER TO postgres;

--
-- Name: wallets_id_seq; Type: SEQUENCE; Schema: public; Owner: postgres
--

CREATE SEQUENCE public.wallets_id_seq
    AS integer
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE public.wallets_id_seq OWNER TO postgres;

--
-- Name: wallets_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: postgres
--

ALTER SEQUENCE public.wallets_id_seq OWNED BY public.wallets.id;

--
-- Name: wallets id; Type: DEFAULT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.wallets ALTER COLUMN id SET DEFAULT nextval('public.wallets_id_seq'::regclass);

--
-- Data for Name: people; Type: TABLE DATA; Schema: public; Owner: postgres
--

COPY public.people (id, name, day, month) FROM stdin;
1	Satrio Gumilar	23	11
2	Novia Dwi Tirta Sari	22	11
5	Mutiara Kartini	12	1
6	Rahmat Rasyidi Hakim	24	1
7	Cahaya Ikhwan Putra	16	2
8	Andreas Jonanisco	2	2
9	Riandra Ramadana	12	1
10	Irfan Nur Afif	20	3
11	Abdul Majid Hamid	14	4
12	Satrio Labib Mubarak	22	5
13	Agung Gunawan	23	5
14	Dewa Ayu Lintang Rizkirana Dezza Putri	22	6
15	Ibrahim Nurandita	11	10
16	Rizki Harlistyoputro	19	10
17	Muhammad Redho Ayassa	24	10
18	Muhammad Rayanovtam	20	11
21	Rizaldy Syahputra	12	12
22	Astri Purwadhanti	25	5
23	Agung Putra Pasaribu	17	12
24	Rifqi Fakhrul Rijal	5	1
25	Hafiyyan Sayyid Fadhlillah	6	8
26	Dwi Rachmawati	25	2
27	Restu Yulia Vitasari	30	7
28	Chikyta Ayu Putri Ervita Sari	14	11
29	Nico Augustiawan	22	8
30	Franky	1	6
31	Fikri Pranata	4	11
32	Selena Dennysal	8	10
\.

--
-- Data for Name: wallets; Type: TABLE DATA; Schema: public; Owner: postgres
--

COPY public.wallets (id, date, name, category, currency, amount, done, account) FROM stdin;
\.

--
-- Name: wallets_id_seq; Type: SEQUENCE SET; Schema: public; Owner: postgres
--

SELECT pg_catalog.setval('public.wallets_id_seq', 6, true);

--
-- Name: people people_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.people
    ADD CONSTRAINT people_pkey PRIMARY KEY (id);

--
-- Name: SCHEMA public; Type: ACL; Schema: -; Owner: postgres
--

REVOKE ALL ON SCHEMA public FROM postgres;


--
-- PostgreSQL database dump complete
--

