# --- First database schema

# --- !Ups

set ignorecase true;

create table country (
    id              bigint not null,
    name            varchar(100) not null,
  constraint pk_country primary key (id))
;

create table musician (
    id              bigint not null,
    name            varchar(100) not null,
    country_id      bigint,
    constraint pk_musician primary key (id))
;

create table song (
    id              bigint not null,
    name            varchar(100) not null,
    released        timestamp,
    musician_id     bigint,
    constraint pk_song primary key (id))
;

create sequence country_seq start with 100;
create sequence musician_seq start with 500;
create sequence song_seq start with 2000;

alter table musician add constraint fk_musician_country_1 foreign key (country_id) references country (id) on delete restrict on update restrict;
alter table song add constraint fk_song_musician_1 foreign key (musician_id) references musician (id) on delete restrict on update restrict;

create index ix_musician_country_1 on musician (country_id);
create index ix_song_musician_1 on song (musician_id);

# --- !Downs

SET REFERENTIAL_INTEGRITY FALSE;

drop table if exists song;
drop table if exists musician;
drop table if exists country;

SET REFERENTIAL_INTEGRITY TRUE;

drop sequence if exists song_seq;
drop sequence if exists musician_seq;
drop sequence if exists country_seq;
