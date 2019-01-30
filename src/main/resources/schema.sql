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