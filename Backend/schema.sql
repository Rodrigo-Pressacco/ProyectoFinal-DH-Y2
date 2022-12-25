drop database if exists dhmoney;
create database dhmoney;

create table dhmoney.Users (
id int NOT NULL AUTO_INCREMENT,
firstname varchar(255) NOT NULL,
lastname varchar(255) NOT NULL,
dni varchar(255) NOT NULL,
email varchar(255) NOT NULL,
phone varchar(255) NOT NULL,
password varchar(255) NOT NULL,
primary key(id)
);

create table dhmoney.Bank_accounts (id int NOT NULL AUTO_INCREMENT,
cvu varchar(22),
alias varchar(255),
id_user int not null,
primary key(id),
foreign key(id_user) references Users(id)
);
