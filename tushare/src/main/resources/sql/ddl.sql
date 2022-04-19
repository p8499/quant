create table F01
(
    ID   VARCHAR2(4 char) not null
        primary key,
    NAME VARCHAR2(8 char) not null
)
/

create table F02
(
    DTE         DATE             not null,
    EXCHANGE_ID VARCHAR2(4 char) not null,
    primary key (DTE, EXCHANGE_ID)
)
/

create table F03
(
    ID          VARCHAR2(16 char) not null
        primary key,
    CODE        VARCHAR2(6 char)  not null,
    DELISTED    DATE,
    EXCHANGE_ID VARCHAR2(4 char)  not null,
    LISTED      DATE              not null,
    NAME        VARCHAR2(16 char) not null
)
/

create table F04
(
    ID   VARCHAR2(32 char) not null
        primary key,
    NAME VARCHAR2(64 char) not null,
    TYPE NUMBER(10)        not null
)
/

create table F05
(
    GROUP_ID VARCHAR2(32 char) not null,
    STOCK_ID VARCHAR2(16 char) not null,
    WEIGHT   NUMBER(8, 4)      not null,
    primary key (GROUP_ID, STOCK_ID)
)
/

create table F061
(
    STOCK_ID                   VARCHAR2(16 char) not null,
    PUBLISH                    DATE              not null,
    YEAR                       NUMBER(10)        not null,
    PERIOD                     NUMBER(10)        not null,
    TOTAL_HLDR_EQY_EXC_MIN_INT NUMBER(26, 2),
    primary key (STOCK_ID, PUBLISH, YEAR, PERIOD)
)
/

create table F062
(
    STOCK_ID       VARCHAR2(16 char) not null,
    PUBLISH        DATE              not null,
    YEAR           NUMBER(10)        not null,
    PERIOD         NUMBER(10)        not null,
    N_INCOME_ATTRP NUMBER(26, 2),
    REVENUE        NUMBER(26, 2),
    primary key (STOCK_ID, PUBLISH, YEAR, PERIOD)
)
/

create table F063
(
    STOCK_ID       VARCHAR2(16 char) not null,
    PUBLISH        DATE              not null,
    YEAR           NUMBER(10)        not null,
    PERIOD         NUMBER(10)        not null,
    N_CASHFLOW_ACT NUMBER(26, 2),
    primary key (STOCK_ID, PUBLISH, YEAR, PERIOD)
)
/

create table F06E
(
    STOCK_ID                   VARCHAR2(16 char) not null,
    PUBLISH                    DATE              not null,
    YEAR                       NUMBER(10)        not null,
    PERIOD                     NUMBER(10)        not null,
    REVENUE                    NUMBER(26, 2),
    N_INCOME                   NUMBER(26, 2),
    TOTAL_HLDR_EQY_EXC_MIN_INT NUMBER(26, 2),
    primary key (STOCK_ID, PUBLISH, YEAR, PERIOD)
)
/

create table F07
(
    STOCK_ID VARCHAR2(16 char) not null,
    PUBLISH  DATE              not null,
    YEAR     NUMBER(10)        not null,
    PERIOD   NUMBER(10)        not null,
    CONTENT  CLOB,
    SUBJECT  VARCHAR2(64 char) not null,
    primary key (STOCK_ID, PUBLISH, YEAR, PERIOD)
)
/

create table F1501
(
    STOCK_ID VARCHAR2(16 char) not null,
    DTE      DATE              not null,
    AMOUNT   NUMBER(26, 2)     not null,
    CLOSE    NUMBER(10, 2)     not null,
    HIGH     NUMBER(10, 2)     not null,
    LOW      NUMBER(10, 2)     not null,
    OPEN     NUMBER(10, 2)     not null,
    VOLUME   NUMBER(18, 2)     not null,
    primary key (STOCK_ID, DTE)
)
/

create table F1502
(
    STOCK_ID    VARCHAR2(16 char) not null,
    DTE         DATE              not null,
    FLOW_SHARE  NUMBER(18, 2)     not null,
    TOTAL_SHARE NUMBER(18, 2)     not null,
    primary key (STOCK_ID, DTE)
)
/

create table F1503
(
    STOCK_ID VARCHAR2(16 char) not null,
    DTE      DATE              not null,
    FACTOR   NUMBER(24, 12)    not null,
    primary key (STOCK_ID, DTE)
)
/

create table F151
(
    STOCK_ID VARCHAR2(16 char) not null,
    DTE      DATE              not null,
    BUY1     NUMBER(18, 2),
    BUY2     NUMBER(18, 2),
    BUY3     NUMBER(18, 2),
    BUY4     NUMBER(18, 2),
    SELL1    NUMBER(18, 2),
    SELL2    NUMBER(18, 2),
    SELL3    NUMBER(18, 2),
    SELL4    NUMBER(18, 2),
    primary key (STOCK_ID, DTE)
)
/

create table F91
(
    OBJECT_ID VARCHAR2(32 char) not null
        primary key,
    SNAPSHOT  TIMESTAMP(6)      null,
    BEGIN     TIMESTAMP(6)      null,
    END       TIMESTAMP(6)      null
)
/

