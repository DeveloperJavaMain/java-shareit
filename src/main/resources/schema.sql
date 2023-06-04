DROP ALL OBJECTS;

create table IF NOT EXISTS USERS
(
    ID  INTEGER not null auto_increment,
    NAME     VARCHAR(50) not NULL,
    EMAIL    VARCHAR(50) not NULL,

    CONSTRAINT USERS_PK primary key (ID),
    CONSTRAINT UQ_USER_EMAIL UNIQUE (EMAIL)
);

create table IF NOT EXISTS ITEMS
(
    ID  INTEGER not null auto_increment,
    NAME     VARCHAR(50),
    DESCRIPTION    VARCHAR,
    AVAILABLE BOOLEAN,
    OWNER_ID INTEGER NOT NULL,
    REQUEST_ID INTEGER,

    constraint ITEMS_PK primary key (ID)
);

create table IF NOT EXISTS BOOKINGS
(
    ID  INTEGER not null auto_increment,
    START_DATE TIMESTAMP WITHOUT TIME ZONE,
    END_DATE TIMESTAMP WITHOUT TIME ZONE,
    ITEM_ID INTEGER,
    BOOKER_ID INTEGER,
    STATUS VARCHAR(10),

    constraint BOOKINGS_PK primary key (ID)
);

create table IF NOT EXISTS REQUESTS
(
    ID  INTEGER not null auto_increment,
    DESCRIPTION  VARCHAR,
    REQUESTOR_ID INTEGER,

    CONSTRAINT REQUESTS_PK primary key (ID)
);

create table IF NOT EXISTS COMMENTS
(
    ID  INTEGER not null auto_increment,
    TEXT VARCHAR,
    ITEM_ID INTEGER,
    AUTHOR_ID INTEGER,

    CONSTRAINT COMMENTS_PK primary key (ID)
);

