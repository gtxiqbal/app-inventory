alter table sewa drop constraint FKpa7ugg7uvpq13damtbh8401us;
alter table sewa drop constraint FKfw41wld7hei9b0k8c5cmi7uai;
drop table if exists karyawan cascade;
drop table if exists laptop cascade;
drop table if exists login cascade;
drop table if exists sewa cascade;

create table karyawan (
    nik int8 not null,
    dept varchar(255) not null,
    divisi varchar(255) not null,
    email varchar(255) not null,
    first_name varchar(255) not null,
    jabatan varchar(255) not null,
    last_name varchar(255),
    no_hp varchar(255) not null,
    status int4 not null,
    tgl_lahir timestamp not null,
    tgl_masuk timestamp not null,
    tmp_lahir varchar(255) not null,
    primary key (nik)
);

create table laptop (
    id varchar(255) not null,
    cpu float8 not null,
    createAt timestamp,
    merk varchar(255) not null,
    name varchar(255) not null,
    ram float8 not null,
    release timestamp not null,
    total int4 not null,
    type varchar(255) not null,
    updateAt timestamp,
    vga float8 not null,
    primary key (id)
);

create table login (
    username varchar(5) not null,
    password varchar(16) not null,
    primary key (username)
);

create table sewa (
    karyawan_nik int8 not null,
    laptop_id varchar(255) not null,
    tgl_pengembalian timestamp not null,
    tgl_sewa timestamp not null,
    total int4 not null,
    primary key (karyawan_nik, laptop_id)
);

alter table sewa add constraint FKpa7ugg7uvpq13damtbh8401us foreign key (karyawan_nik) references karyawan;
alter table sewa add constraint FKfw41wld7hei9b0k8c5cmi7uai foreign key (laptop_id) references laptop;
