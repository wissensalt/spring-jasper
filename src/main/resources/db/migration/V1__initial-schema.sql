CREATE TABLE employee
(
    id      SERIAL NOT NULL,
    email   VARCHAR(255),
    name    VARCHAR(255),
    address VARCHAR(255),
    CONSTRAINT pk_employee PRIMARY KEY (id)
);

insert into employee (email, name, address)
values ('one@mail.com', 'one', 'address-one');
insert into employee (email, name, address)
values ('two@mail.com', 'two', 'address-two');
insert into employee (email, name, address)
values ('three@mail.com', 'three', 'address-three');
insert into employee (email, name, address)
values ('five@mail.com', 'four', 'address-four');
insert into employee (email, name, address)
values ('five@mail.com', 'five', 'address-five');
