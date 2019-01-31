create table customers (
	id bigint primary key,
	name varchar(200) not null,
	platform varchar(50) not null
);

create table photos (
	id bigint primary key,
	thumbnail_src varchar(2048) not null,
	date int not null,
	caption varchar(100) not null,
	account varchar(50) not null
);

create table tracks (
	customers_id bigint references customers(id),
	photos_id bigint references photos(id),
	date int not null,
	primary key (customers_id, photos_id, date)
);

create table votes (
	customers_id bigint references customers(id),
	photos_id bigint references photos(id),
	rating smallint not null,
	primary key (customers_id, photos_id)
);

create table heroes (
	id SMALLINT primary key,
	localized_name varchar(32) not null,
	primary_attr varchar(3) not null,
	attack_type varchar(6) not null
);

create table players (
	id int primary key,
	realname varchar(32) not null,
	avatarfull varchar(2083),
	personaname varchar(64) not null,
	mmr_estimate int not null
);

create table peers (
	players_id int references players(id),
	peer_players_id int references players(id),
	win int not null,
	games int not null,
	primary key (players_id, peer_players_id)
)

create table matches (
	id int primary key,
	players_id int references players(id),
	player_slot int not null,
	radiant_win bit not null,
	duration integer not null,
	game_mode integer not null,
	lobby_type integer not null,
	hero_id integer not null,
	start_time integer not null,
	kills integer not null,
	deaths integer not null,
	assists integer not null,
	skill integer not null
);
