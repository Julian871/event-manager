create table locations (
    id serial primary key,
    name varchar(255) not null,
    address varchar(255) not null,
    capacity integer not null,
    description varchar(255)
)