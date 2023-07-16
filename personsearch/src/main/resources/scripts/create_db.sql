create table mail_notice
(
    request_uuid uuid
        constraint mail_notice_pk
            primary key,
    email        varchar(2000) not null
);
create table report
(
    id          bigserial
        constraint report_pk
            primary key,
    update_date timestamp not null,
    image_url   varchar(2000),
    image_code  text not null
);
create table link
(
    id             bigserial
        constraint link_pk
            primary key,
    url            varchar(2000) not null,
    title          varchar(2000) not null,
    description    text,
    update_date    timestamp not null,
    publish_date   timestamp,
    images_url     text not null,
    persons        text
);
create table report_link
(
    id             bigserial
        constraint report_link_pk
            primary key,
    report_id      bigint not null
        constraint report_link_report_id_fk
            references report (id),
    url            varchar(2000) not null,
    title          varchar(2000) not null,
    description    text,
    image_url      varchar(2000),
    source_link_id bigint  not null
        constraint report_link_link_id_fk
            references link (id),
    presence_rate  real not null,
    publish_date   timestamp
);
create table request
(
    request_uuid uuid
        constraint request_pk
            primary key,
    report_id    bigint not null
        constraint request_report_id_fk
            references report (id)
);

create unique index link_url_uindex
    on link (url);

create index link_update_date_index
    on link (update_date);
create index report_link_report_id_index
    on report_link (report_id);








