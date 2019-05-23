create table photos (
	id bigint primary key,
	thumbnail_src varchar(2048) not null,
	date int not null,
	caption varchar(100) not null,
	account varchar(50) not null
);

create table heroes (
	id SMALLINT primary key,
	localized_name varchar(32) not null,
	primary_attr varchar(3) not null,
	image varchar(128) not null,
	lore text
);

create table players (
	id int primary key,
	realname varchar(32) not null,
	avatarfull varchar(2083),
	personaname varchar(64) not null
);

create table people (
	id int primary key,
	name varchar(100) not null,
	day int not null check (day > 0 and day <= 31),
	month int not null check (month > 0 and month <= 12)
);
