create table C
(
    REGION VARCHAR2(2 char) not null
        primary key,
    DTE    TIMESTAMP(6)     not null
)
/

create table G
(
    REGION VARCHAR2(2 char)  not null,
    ID     VARCHAR2(32 char) not null,
    NAME   VARCHAR2(64 char) not null,
    primary key (REGION, ID)
)
/

create table GID
(
    REGION VARCHAR2(2 char)  not null,
    ID     VARCHAR2(32 char) not null,
    KPI    VARCHAR2(16 char) not null,
    DTE    DATE              not null,
    VALUE  NUMBER(32, 8),
    primary key (REGION, ID, KPI, DTE)
)
/

create table GMD
(
    REGION  VARCHAR2(2 char)  not null,
    ID      VARCHAR2(32 char) not null,
    DTE     DATE              not null,
    MESSAGE CLOB,
    primary key (REGION, ID, DTE)
)
/

create table GS
(
    REGION   VARCHAR2(2 char)  not null,
    GROUP_ID VARCHAR2(32 char) not null,
    STOCK_ID VARCHAR2(32 char) not null,
    PERCENT  NUMBER(5, 4)      not null,
    primary key (GROUP_ID, REGION, STOCK_ID)
)
/

create table S
(
    ID     VARCHAR2(32 char) not null,
    REGION VARCHAR2(2 char)  not null,
    NAME   VARCHAR2(64 char) not null,
    primary key (REGION, ID)
)
/

create table SID
(
    REGION VARCHAR2(2 char)  not null,
    ID     VARCHAR2(32 char) not null,
    KPI    VARCHAR2(16 char) not null,
    DTE    DATE              not null,
    VALUE  NUMBER(32, 8),
    primary key (REGION, ID, KPI, DTE)
)
/

create table SMD
(
    REGION  VARCHAR2(2 char)  not null,
    ID      VARCHAR2(32 char) not null,
    DTE     DATE              not null,
    MESSAGE CLOB,
    primary key (REGION, ID, DTE)
)
/

