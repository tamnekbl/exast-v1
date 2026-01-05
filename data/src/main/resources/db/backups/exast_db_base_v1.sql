--
-- PostgreSQL database dump
--

\
restrict i09i28wMmRzKEE7aT4P1pYokXa9YP9jcmNmmVVWIBH9KelkLE8vzlsfqhgsIoAH

-- Dumped from database version 17.6
-- Dumped by pg_dump version 17.6

-- Started on 2026-01-05 17:51:38

SET statement_timeout = 0;
SET
lock_timeout = 0;
SET
idle_in_transaction_session_timeout = 0;
SET
transaction_timeout = 0;
SET
client_encoding = 'UTF8';
SET
standard_conforming_strings = on;
SELECT pg_catalog.set_config('search_path', '', false);
SET
check_function_bodies = false;
SET
xmloption = content;
SET
client_min_messages = warning;
SET
row_security = off;

--
-- TOC entry 863 (class 1247 OID 25024)
-- Name: event_format; Type: TYPE; Schema: public; Owner: postgres
--

CREATE TYPE public.event_format AS ENUM (
    'online',
    'hybrid',
    'offline'
);


ALTER TYPE public.event_format OWNER TO postgres;

--
-- TOC entry 896 (class 1247 OID 25420)
-- Name: event_level; Type: TYPE; Schema: public; Owner: postgres
--

CREATE TYPE public.event_level AS ENUM (
    'undefined',
    'structural',
    'university',
    'municipal',
    'regional',
    'interregional',
    'district',
    'national',
    'international'
);


ALTER TYPE public.event_level OWNER TO postgres;

--
-- TOC entry 884 (class 1247 OID 25222)
-- Name: participation_type; Type: TYPE; Schema: public; Owner: postgres
--

CREATE TYPE public.participation_type AS ENUM (
    'participation',
    'organization',
    'assistance'
);


ALTER TYPE public.participation_type OWNER TO postgres;

SET
default_tablespace = '';

SET
default_table_access_method = heap;

--
-- TOC entry 229 (class 1259 OID 25190)
-- Name: event_event_types; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE public.event_event_types
(
    event_id integer NOT NULL,
    type_id  integer NOT NULL
);


ALTER TABLE public.event_event_types OWNER TO postgres;

--
-- TOC entry 217 (class 1259 OID 25039)
-- Name: event_participants; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE public.event_participants
(
    event_id       integer  NOT NULL,
    participant_id integer  NOT NULL,
    role_id        smallint NOT NULL,
    id             integer  NOT NULL
);


ALTER TABLE public.event_participants OWNER TO postgres;

--
-- TOC entry 233 (class 1259 OID 25477)
-- Name: event_participants_id_seq; Type: SEQUENCE; Schema: public; Owner: postgres
--

CREATE SEQUENCE public.event_participants_id_seq
    AS integer
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE CACHE 1;


ALTER SEQUENCE public.event_participants_id_seq OWNER TO postgres;

--
-- TOC entry 4917 (class 0 OID 0)
-- Dependencies: 233
-- Name: event_participants_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: postgres
--

ALTER SEQUENCE public.event_participants_id_seq OWNED BY public.event_participants.id;


--
-- TOC entry 219 (class 1259 OID 25045)
-- Name: event_types; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE public.event_types
(
    id   smallint               NOT NULL,
    type character varying(100) NOT NULL
);


ALTER TABLE public.event_types OWNER TO postgres;

--
-- TOC entry 218 (class 1259 OID 25044)
-- Name: event_types_id_seq; Type: SEQUENCE; Schema: public; Owner: postgres
--

CREATE SEQUENCE public.event_types_id_seq
    AS smallint
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE CACHE 1;


ALTER SEQUENCE public.event_types_id_seq OWNER TO postgres;

--
-- TOC entry 4918 (class 0 OID 0)
-- Dependencies: 218
-- Name: event_types_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: postgres
--

ALTER SEQUENCE public.event_types_id_seq OWNED BY public.event_types.id;


--
-- TOC entry 221 (class 1259 OID 25052)
-- Name: events; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE public.events
(
    id                   integer                         NOT NULL,
    title                character varying(1024)         NOT NULL,
    description          text,
    created_at           timestamp without time zone DEFAULT now() NOT NULL,
    started_at           timestamp without time zone NOT NULL,
    ended_at             timestamp without time zone,
    location             character varying(1024),
    participants_total   integer               DEFAULT 0 NOT NULL,
    participants_other   integer               DEFAULT 0 NOT NULL,
    participants_spo     integer               DEFAULT 0 NOT NULL,
    participants_vo      integer               DEFAULT 0 NOT NULL,
    participants_foreign integer               DEFAULT 0 NOT NULL,
    level                character varying(20) DEFAULT 'undefined'::character varying NOT NULL,
    format               character varying(20) DEFAULT 'offline'::character varying NOT NULL,
    organization_role    character varying(20) DEFAULT 'organization'::character varying NOT NULL,
    CONSTRAINT events_format_check CHECK (((format)::text = ANY ((ARRAY['offline':: character varying, 'online':: character varying, 'hybrid':: character varying])::text[])
) ),
    CONSTRAINT events_level_check CHECK (((level)::text = ANY ((ARRAY['undefined'::character varying, 'structural'::character varying, 'university'::character varying, 'municipal'::character varying, 'regional'::character varying, 'interregional'::character varying, 'district'::character varying, 'national'::character varying, 'international'::character varying])::text[]))),
    CONSTRAINT events_organization_role_check CHECK (((organization_role)::text = ANY ((ARRAY['organization'::character varying, 'participation'::character varying, 'assistance'::character varying])::text[])))
);


ALTER TABLE public.events OWNER TO postgres;

--
-- TOC entry 4919 (class 0 OID 0)
-- Dependencies: 221
-- Name: COLUMN events.title; Type: COMMENT; Schema: public; Owner: postgres
--

COMMENT
ON COLUMN public.events.title IS 'Название мероприятия';


--
-- TOC entry 4920 (class 0 OID 0)
-- Dependencies: 221
-- Name: COLUMN events.description; Type: COMMENT; Schema: public; Owner: postgres
--

COMMENT
ON COLUMN public.events.description IS 'Описание';


--
-- TOC entry 4921 (class 0 OID 0)
-- Dependencies: 221
-- Name: COLUMN events.created_at; Type: COMMENT; Schema: public; Owner: postgres
--

COMMENT
ON COLUMN public.events.created_at IS 'Просто дата создания';


--
-- TOC entry 4922 (class 0 OID 0)
-- Dependencies: 221
-- Name: COLUMN events.started_at; Type: COMMENT; Schema: public; Owner: postgres
--

COMMENT
ON COLUMN public.events.started_at IS 'Дата начала. Только это поле обязательно, если дата завершения нулл, значит это дата мероприятия просто';


--
-- TOC entry 4923 (class 0 OID 0)
-- Dependencies: 221
-- Name: COLUMN events.ended_at; Type: COMMENT; Schema: public; Owner: postgres
--

COMMENT
ON COLUMN public.events.ended_at IS 'Дата завершения';


--
-- TOC entry 220 (class 1259 OID 25051)
-- Name: events_id_seq; Type: SEQUENCE; Schema: public; Owner: postgres
--

CREATE SEQUENCE public.events_id_seq
    AS integer
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE CACHE 1;


ALTER SEQUENCE public.events_id_seq OWNER TO postgres;

--
-- TOC entry 4924 (class 0 OID 0)
-- Dependencies: 220
-- Name: events_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: postgres
--

ALTER SEQUENCE public.events_id_seq OWNED BY public.events.id;


--
-- TOC entry 231 (class 1259 OID 25231)
-- Name: organization_type; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE public.organization_type
(
    id   integer                NOT NULL,
    type character varying(255) NOT NULL
);


ALTER TABLE public.organization_type OWNER TO postgres;

--
-- TOC entry 230 (class 1259 OID 25230)
-- Name: organization_type_id_seq; Type: SEQUENCE; Schema: public; Owner: postgres
--

CREATE SEQUENCE public.organization_type_id_seq
    AS integer
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE CACHE 1;


ALTER SEQUENCE public.organization_type_id_seq OWNER TO postgres;

--
-- TOC entry 4925 (class 0 OID 0)
-- Dependencies: 230
-- Name: organization_type_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: postgres
--

ALTER SEQUENCE public.organization_type_id_seq OWNED BY public.organization_type.id;


--
-- TOC entry 227 (class 1259 OID 25093)
-- Name: organizations; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE public.organizations
(
    name        character varying(255) NOT NULL,
    description text,
    type_id     integer,
    id          integer                NOT NULL
);


ALTER TABLE public.organizations OWNER TO postgres;

--
-- TOC entry 228 (class 1259 OID 25100)
-- Name: organizations_events; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE public.organizations_events
(
    event_id        integer NOT NULL,
    organization_id integer NOT NULL
);


ALTER TABLE public.organizations_events OWNER TO postgres;

--
-- TOC entry 232 (class 1259 OID 25249)
-- Name: organizations_id_seq; Type: SEQUENCE; Schema: public; Owner: postgres
--

CREATE SEQUENCE public.organizations_id_seq
    AS integer
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE CACHE 1;


ALTER SEQUENCE public.organizations_id_seq OWNER TO postgres;

--
-- TOC entry 4926 (class 0 OID 0)
-- Dependencies: 232
-- Name: organizations_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: postgres
--

ALTER SEQUENCE public.organizations_id_seq OWNED BY public.organizations.id;


--
-- TOC entry 223 (class 1259 OID 25067)
-- Name: participants; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE public.participants
(
    id            integer                NOT NULL,
    last_name     character varying(255) NOT NULL,
    first_name    character varying(255) NOT NULL,
    middle_name   character varying(255),
    course        smallint,
    speciality_id integer,
    structure_id  integer
);


ALTER TABLE public.participants OWNER TO postgres;

--
-- TOC entry 222 (class 1259 OID 25066)
-- Name: participants_id_seq; Type: SEQUENCE; Schema: public; Owner: postgres
--

CREATE SEQUENCE public.participants_id_seq
    AS integer
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE CACHE 1;


ALTER SEQUENCE public.participants_id_seq OWNER TO postgres;

--
-- TOC entry 4927 (class 0 OID 0)
-- Dependencies: 222
-- Name: participants_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: postgres
--

ALTER SEQUENCE public.participants_id_seq OWNED BY public.participants.id;


--
-- TOC entry 225 (class 1259 OID 25076)
-- Name: roles; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE public.roles
(
    id   smallint NOT NULL,
    name text     NOT NULL
);


ALTER TABLE public.roles OWNER TO postgres;

--
-- TOC entry 224 (class 1259 OID 25075)
-- Name: roles_id_seq; Type: SEQUENCE; Schema: public; Owner: postgres
--

CREATE SEQUENCE public.roles_id_seq
    AS smallint
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE CACHE 1;


ALTER SEQUENCE public.roles_id_seq OWNER TO postgres;

--
-- TOC entry 4928 (class 0 OID 0)
-- Dependencies: 224
-- Name: roles_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: postgres
--

ALTER SEQUENCE public.roles_id_seq OWNED BY public.roles.id;


--
-- TOC entry 226 (class 1259 OID 25086)
-- Name: specialities; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE public.specialities
(
    id   integer               NOT NULL,
    code character varying(10) NOT NULL,
    name text                  NOT NULL
);


ALTER TABLE public.specialities OWNER TO postgres;

--
-- TOC entry 234 (class 1259 OID 25489)
-- Name: specialties_id_seq; Type: SEQUENCE; Schema: public; Owner: postgres
--

CREATE SEQUENCE public.specialties_id_seq
    AS integer
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE CACHE 1;


ALTER SEQUENCE public.specialties_id_seq OWNER TO postgres;

--
-- TOC entry 4929 (class 0 OID 0)
-- Dependencies: 234
-- Name: specialties_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: postgres
--

ALTER SEQUENCE public.specialties_id_seq OWNED BY public.specialities.id;


--
-- TOC entry 4693 (class 2604 OID 25478)
-- Name: event_participants id; Type: DEFAULT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.event_participants ALTER COLUMN id SET DEFAULT nextval('public.event_participants_id_seq'::regclass);


--
-- TOC entry 4694 (class 2604 OID 25048)
-- Name: event_types id; Type: DEFAULT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.event_types ALTER COLUMN id SET DEFAULT nextval('public.event_types_id_seq'::regclass);


--
-- TOC entry 4695 (class 2604 OID 25055)
-- Name: events id; Type: DEFAULT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.events ALTER COLUMN id SET DEFAULT nextval('public.events_id_seq'::regclass);


--
-- TOC entry 4709 (class 2604 OID 25234)
-- Name: organization_type id; Type: DEFAULT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.organization_type ALTER COLUMN id SET DEFAULT nextval('public.organization_type_id_seq'::regclass);


--
-- TOC entry 4708 (class 2604 OID 25250)
-- Name: organizations id; Type: DEFAULT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.organizations ALTER COLUMN id SET DEFAULT nextval('public.organizations_id_seq'::regclass);


--
-- TOC entry 4705 (class 2604 OID 25070)
-- Name: participants id; Type: DEFAULT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.participants ALTER COLUMN id SET DEFAULT nextval('public.participants_id_seq'::regclass);


--
-- TOC entry 4706 (class 2604 OID 25079)
-- Name: roles id; Type: DEFAULT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.roles ALTER COLUMN id SET DEFAULT nextval('public.roles_id_seq'::regclass);


--
-- TOC entry 4707 (class 2604 OID 25490)
-- Name: specialities id; Type: DEFAULT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.specialities ALTER COLUMN id SET DEFAULT nextval('public.specialties_id_seq'::regclass);


--
-- TOC entry 4906 (class 0 OID 25190)
-- Dependencies: 229
-- Data for Name: event_event_types; Type: TABLE DATA; Schema: public; Owner: postgres
--


--
-- TOC entry 4894 (class 0 OID 25039)
-- Dependencies: 217
-- Data for Name: event_participants; Type: TABLE DATA; Schema: public; Owner: postgres
--


--
-- TOC entry 4896 (class 0 OID 25045)
-- Dependencies: 219
-- Data for Name: event_types; Type: TABLE DATA; Schema: public; Owner: postgres
--

INSERT INTO public.event_types (id, type)
VALUES (1, 'civic');
INSERT INTO public.event_types (id, type)
VALUES (2, 'patriotic');
INSERT INTO public.event_types (id, type)
VALUES (3, 'physical');
INSERT INTO public.event_types (id, type)
VALUES (4, 'spiritual_moral');
INSERT INTO public.event_types (id, type)
VALUES (5, 'ecological');
INSERT INTO public.event_types (id, type)
VALUES (6, 'professional_labor');
INSERT INTO public.event_types (id, type)
VALUES (7, 'cultural_creative');
INSERT INTO public.event_types (id, type)
VALUES (8, 'scientefic_educational');
INSERT INTO public.event_types (id, type)
VALUES (9, 'volunteering');
INSERT INTO public.event_types (id, type)
VALUES (10, 'student_self_government');


--
-- TOC entry 4898 (class 0 OID 25052)
-- Dependencies: 221
-- Data for Name: events; Type: TABLE DATA; Schema: public; Owner: postgres
--


--
-- TOC entry 4908 (class 0 OID 25231)
-- Dependencies: 231
-- Data for Name: organization_type; Type: TABLE DATA; Schema: public; Owner: postgres
--


--
-- TOC entry 4904 (class 0 OID 25093)
-- Dependencies: 227
-- Data for Name: organizations; Type: TABLE DATA; Schema: public; Owner: postgres
--


--
-- TOC entry 4905 (class 0 OID 25100)
-- Dependencies: 228
-- Data for Name: organizations_events; Type: TABLE DATA; Schema: public; Owner: postgres
--


--
-- TOC entry 4900 (class 0 OID 25067)
-- Dependencies: 223
-- Data for Name: participants; Type: TABLE DATA; Schema: public; Owner: postgres
--


--
-- TOC entry 4902 (class 0 OID 25076)
-- Dependencies: 225
-- Data for Name: roles; Type: TABLE DATA; Schema: public; Owner: postgres
--


--
-- TOC entry 4903 (class 0 OID 25086)
-- Dependencies: 226
-- Data for Name: specialities; Type: TABLE DATA; Schema: public; Owner: postgres
--


--
-- TOC entry 4930 (class 0 OID 0)
-- Dependencies: 233
-- Name: event_participants_id_seq; Type: SEQUENCE SET; Schema: public; Owner: postgres
--

SELECT pg_catalog.setval('public.event_participants_id_seq', 1, false);


--
-- TOC entry 4931 (class 0 OID 0)
-- Dependencies: 218
-- Name: event_types_id_seq; Type: SEQUENCE SET; Schema: public; Owner: postgres
--

SELECT pg_catalog.setval('public.event_types_id_seq', 10, true);


--
-- TOC entry 4932 (class 0 OID 0)
-- Dependencies: 220
-- Name: events_id_seq; Type: SEQUENCE SET; Schema: public; Owner: postgres
--

SELECT pg_catalog.setval('public.events_id_seq', 1, false);


--
-- TOC entry 4933 (class 0 OID 0)
-- Dependencies: 230
-- Name: organization_type_id_seq; Type: SEQUENCE SET; Schema: public; Owner: postgres
--

SELECT pg_catalog.setval('public.organization_type_id_seq', 5, true);


--
-- TOC entry 4934 (class 0 OID 0)
-- Dependencies: 232
-- Name: organizations_id_seq; Type: SEQUENCE SET; Schema: public; Owner: postgres
--

SELECT pg_catalog.setval('public.organizations_id_seq', 1, false);


--
-- TOC entry 4935 (class 0 OID 0)
-- Dependencies: 222
-- Name: participants_id_seq; Type: SEQUENCE SET; Schema: public; Owner: postgres
--

SELECT pg_catalog.setval('public.participants_id_seq', 1, false);


--
-- TOC entry 4936 (class 0 OID 0)
-- Dependencies: 224
-- Name: roles_id_seq; Type: SEQUENCE SET; Schema: public; Owner: postgres
--

SELECT pg_catalog.setval('public.roles_id_seq', 1, false);


--
-- TOC entry 4937 (class 0 OID 0)
-- Dependencies: 234
-- Name: specialties_id_seq; Type: SEQUENCE SET; Schema: public; Owner: postgres
--

SELECT pg_catalog.setval('public.specialties_id_seq', 1, false);


--
-- TOC entry 4734 (class 2606 OID 25194)
-- Name: event_event_types event_event_types_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.event_event_types
    ADD CONSTRAINT event_event_types_pkey PRIMARY KEY (event_id, type_id);


--
-- TOC entry 4714 (class 2606 OID 25484)
-- Name: event_participants event_participants_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.event_participants
    ADD CONSTRAINT event_participants_pkey PRIMARY KEY (id);


--
-- TOC entry 4716 (class 2606 OID 25050)
-- Name: event_types event_types_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.event_types
    ADD CONSTRAINT event_types_pkey PRIMARY KEY (id);


--
-- TOC entry 4718 (class 2606 OID 25488)
-- Name: event_types event_types_type_key; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.event_types
    ADD CONSTRAINT event_types_type_key UNIQUE (type);


--
-- TOC entry 4720 (class 2606 OID 25065)
-- Name: events events_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.events
    ADD CONSTRAINT events_pkey PRIMARY KEY (id);


--
-- TOC entry 4736 (class 2606 OID 25486)
-- Name: organization_type organization_type_name_key; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.organization_type
    ADD CONSTRAINT organization_type_name_key UNIQUE (type);


--
-- TOC entry 4738 (class 2606 OID 25236)
-- Name: organization_type organization_type_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.organization_type
    ADD CONSTRAINT organization_type_pkey PRIMARY KEY (id);


--
-- TOC entry 4732 (class 2606 OID 25269)
-- Name: organizations_events organizations_events_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.organizations_events
    ADD CONSTRAINT organizations_events_pkey PRIMARY KEY (event_id, organization_id);


--
-- TOC entry 4730 (class 2606 OID 25257)
-- Name: organizations organizations_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.organizations
    ADD CONSTRAINT organizations_pkey PRIMARY KEY (id);


--
-- TOC entry 4722 (class 2606 OID 25074)
-- Name: participants participants_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.participants
    ADD CONSTRAINT participants_pkey PRIMARY KEY (id);


--
-- TOC entry 4724 (class 2606 OID 25085)
-- Name: roles roles_name_key; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.roles
    ADD CONSTRAINT roles_name_key UNIQUE (name);


--
-- TOC entry 4726 (class 2606 OID 25083)
-- Name: roles roles_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.roles
    ADD CONSTRAINT roles_pkey PRIMARY KEY (id);


--
-- TOC entry 4728 (class 2606 OID 25497)
-- Name: specialities specialties_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.specialities
    ADD CONSTRAINT specialties_pkey PRIMARY KEY (id);


--
-- TOC entry 4747 (class 2606 OID 25195)
-- Name: event_event_types event_event_types_event_id_fkey; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.event_event_types
    ADD CONSTRAINT event_event_types_event_id_fkey FOREIGN KEY (event_id) REFERENCES public.events(id) ON
DELETE
CASCADE;


--
-- TOC entry 4748 (class 2606 OID 25200)
-- Name: event_event_types event_event_types_type_id_fkey; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.event_event_types
    ADD CONSTRAINT event_event_types_type_id_fkey FOREIGN KEY (type_id) REFERENCES public.event_types(id) ON
DELETE
CASCADE;


--
-- TOC entry 4739 (class 2606 OID 25128)
-- Name: event_participants event_participants_event_id_fkey; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.event_participants
    ADD CONSTRAINT event_participants_event_id_fkey FOREIGN KEY (event_id) REFERENCES public.events(id) ON
DELETE
CASCADE;


--
-- TOC entry 4740 (class 2606 OID 25133)
-- Name: event_participants event_participants_participant_id_fkey; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.event_participants
    ADD CONSTRAINT event_participants_participant_id_fkey FOREIGN KEY (participant_id) REFERENCES public.participants(id) ON
DELETE
CASCADE;


--
-- TOC entry 4741 (class 2606 OID 25138)
-- Name: event_participants event_participants_role_id_fkey; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.event_participants
    ADD CONSTRAINT event_participants_role_id_fkey FOREIGN KEY (role_id) REFERENCES public.roles(id);


--
-- TOC entry 4745 (class 2606 OID 25168)
-- Name: organizations_events events_fkey; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.organizations_events
    ADD CONSTRAINT events_fkey FOREIGN KEY (event_id) REFERENCES public.events(id) ON
DELETE
CASCADE NOT VALID;


--
-- TOC entry 4746 (class 2606 OID 25270)
-- Name: organizations_events organizations_events_organization_id_fkey; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.organizations_events
    ADD CONSTRAINT organizations_events_organization_id_fkey FOREIGN KEY (organization_id) REFERENCES public.organizations(id) NOT VALID;


--
-- TOC entry 4744 (class 2606 OID 25237)
-- Name: organizations organizations_type_fkey; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.organizations
    ADD CONSTRAINT organizations_type_fkey FOREIGN KEY (type_id) REFERENCES public.organization_type(id) NOT VALID;


--
-- TOC entry 4742 (class 2606 OID 25498)
-- Name: participants participants_speciality_id_fkey; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.participants
    ADD CONSTRAINT participants_speciality_id_fkey FOREIGN KEY (speciality_id) REFERENCES public.specialities(id) NOT VALID;


--
-- TOC entry 4743 (class 2606 OID 25258)
-- Name: participants participants_structure_id_fkey; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.participants
    ADD CONSTRAINT participants_structure_id_fkey FOREIGN KEY (structure_id) REFERENCES public.organizations(id) NOT VALID;


-- Completed on 2026-01-05 17:51:38

--
-- PostgreSQL database dump complete
--

\unrestrict
i09i28wMmRzKEE7aT4P1pYokXa9YP9jcmNmmVVWIBH9KelkLE8vzlsfqhgsIoAH

