	# --- !Ups

create table pages (
  id INTEGER PRIMARY KEY,
  path VARCHAR NOT NULL,
  filename VARCHAR NOT NULL,
  data VARCHAR NOT NULL
);

# --- !Downs

drop table pages ;

