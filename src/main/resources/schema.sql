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

create table people (
	id int primary key,
	name varchar(100) not null,
	day int not null check (day > 0 and day <= 31),
	month int not null check (month > 0 and month <= 12)
);
