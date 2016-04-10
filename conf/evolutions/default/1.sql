# --- !Ups

create table item (
  id                            bigserial not null,
  name                          varchar(255),
  storage_key                   varchar(255),
  constraint pk_item primary key (id)
);

create table tag (
  id                            bigserial not null,
  name                          varchar(255),
  constraint pk_tag primary key (id)
);

create table tag_item (
  tag_id                        bigint not null,
  item_id                       bigint not null,
  constraint pk_tag_item primary key (tag_id,item_id)
);

alter table tag_item add constraint fk_tag_item_tag foreign key (tag_id) references tag (id) on delete restrict on update restrict;
create index ix_tag_item_tag on tag_item (tag_id);

alter table tag_item add constraint fk_tag_item_item foreign key (item_id) references item (id) on delete restrict on update restrict;
create index ix_tag_item_item on tag_item (item_id);


# --- !Downs

alter table tag_item drop constraint if exists fk_tag_item_tag;
drop index if exists ix_tag_item_tag;

alter table tag_item drop constraint if exists fk_tag_item_item;
drop index if exists ix_tag_item_item;

drop table if exists item cascade;

drop table if exists tag cascade;

drop table if exists tag_item cascade;

