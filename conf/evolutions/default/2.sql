# --- !Ups

alter table item add column upload_date timestamp;
alter table item add column file_size integer;

# --- !Downs

alter table item drop column upload_date;
alter table item drop column file_size;
