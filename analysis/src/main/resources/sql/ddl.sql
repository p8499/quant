create table C
(
    REGION   VARCHAR2(2 char) not null
        primary key,
    SNAPSHOT TIMESTAMP(6)     null,
    BEGIN    TIMESTAMP(6)     null,
    END      TIMESTAMP(6)     null
)
/

create table S
(
    REGION VARCHAR2(2 char)  not null,
    ID     VARCHAR2(32 char) not null,
    NAME   VARCHAR2(64 char) not null,
    primary key (REGION, ID)
)
/

create table SID
(
    REGION VARCHAR2(2 char)  not null,
    ID     VARCHAR2(32 char) not null,
    TYPE   VARCHAR2(16 char) not null,
    DTE    DATE              not null,
    VALUE  NUMBER(32, 8),
    primary key (REGION, ID, TYPE, DTE)
)
/

create table SMD
(
    REGION VARCHAR2(2 char)  not null,
    ID     VARCHAR2(32 char) not null,
    TYPE   VARCHAR2(16 char) not null,
    DTE    DATE              not null,
    VALUE  CLOB,
    primary key (REGION, ID, TYPE, DTE)
)
/

create table SIQ
(
    REGION  VARCHAR2(2 char)  not null,
    ID      VARCHAR2(32 char) not null,
    TYPE    VARCHAR2(16 char) not null,
    PUBLISH DATE              not null,
    QUARTER NUMBER(10)        not null,
    VALUE   NUMBER(32, 8),
    primary key (REGION, ID, TYPE, PUBLISH, QUARTER)
)
/

create table SMQ
(
    REGION  VARCHAR2(2 char)  not null,
    ID      VARCHAR2(32 char) not null,
    TYPE    VARCHAR2(16 char) not null,
    PUBLISH DATE              not null,
    QUARTER NUMBER(10)        not null,
    VALUE   CLOB,
    primary key (REGION, ID, TYPE, PUBLISH, QUARTER)
)
/
