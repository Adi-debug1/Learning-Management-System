-- init script create procs
-- Inital script to create stored procedures etc for mysql platform
DROP PROCEDURE IF EXISTS usp_ebean_drop_foreign_keys;

delimiter $$
--
-- PROCEDURE: usp_ebean_drop_foreign_keys TABLE, COLUMN
-- deletes all constraints and foreign keys referring to TABLE.COLUMN
--
CREATE PROCEDURE usp_ebean_drop_foreign_keys(IN p_table_name VARCHAR(255), IN p_column_name VARCHAR(255))
BEGIN
DECLARE done INT DEFAULT FALSE;
DECLARE c_fk_name CHAR(255);
DECLARE curs CURSOR FOR SELECT CONSTRAINT_NAME from information_schema.KEY_COLUMN_USAGE
WHERE TABLE_SCHEMA = DATABASE() and TABLE_NAME = p_table_name and COLUMN_NAME = p_column_name
AND REFERENCED_TABLE_NAME IS NOT NULL;
DECLARE CONTINUE HANDLER FOR NOT FOUND SET done = TRUE;

OPEN curs;

read_loop: LOOP
FETCH curs INTO c_fk_name;
IF done THEN
LEAVE read_loop;
END IF;
SET @sql = CONCAT('ALTER TABLE `', p_table_name, '` DROP FOREIGN KEY ', c_fk_name);
PREPARE stmt FROM @sql;
EXECUTE stmt;
END LOOP;

CLOSE curs;
END
$$

DROP PROCEDURE IF EXISTS usp_ebean_drop_column;

delimiter $$
--
-- PROCEDURE: usp_ebean_drop_column TABLE, COLUMN
-- deletes the column and ensures that all indices and constraints are dropped first
--
CREATE PROCEDURE usp_ebean_drop_column(IN p_table_name VARCHAR(255), IN p_column_name VARCHAR(255))
BEGIN
CALL usp_ebean_drop_foreign_keys(p_table_name, p_column_name);
SET @sql = CONCAT('ALTER TABLE `', p_table_name, '` DROP COLUMN `', p_column_name, '`');
PREPARE stmt FROM @sql;
EXECUTE stmt;
END
$$
-- apply changes
create table admin (
  user_id                       bigint auto_increment not null,
  role                          varchar(7) not null,
  full_name                     varchar(255),
  email                         varchar(255) not null,
  password                      varchar(255),
  mobile_number                 varchar(10),
  created_at                    datetime(6),
  updated_at                    datetime(6),
  constraint uq_admin_email unique (email),
  constraint pk_admin primary key (user_id)
);

create table bulk_upload (
  upload_id                     varchar(40) not null,
  uploaded_by                   bigint not null,
  upload_type                   varchar(7) not null,
  total_records                 integer not null,
  success_count                 integer not null,
  failure_count                 integer not null,
  status                        varchar(11) not null,
  completed_at                  datetime(6),
  created_at                    datetime(6) not null,
  constraint pk_bulk_upload primary key (upload_id)
);

create table kyc_document (
  id                            bigint auto_increment not null,
  user_id                       bigint not null,
  role                          varchar(7),
  document_type                 varchar(8),
  file_name                     varchar(255),
  file_url                      varchar(255),
  validation_status             varchar(13),
  validation_message            varchar(255),
  overridden_by                 bigint,
  overridden_at                 datetime(6),
  override_reason               varchar(255),
  created_at                    datetime(6) not null,
  updated_at                    datetime(6) not null,
  constraint uq_kyc_document_userid_documenttype unique (userId,documentType),
  constraint pk_kyc_document primary key (id)
);

create table students (
  user_id                       bigint auto_increment not null,
  role                          varchar(7) not null,
  full_name                     varchar(255),
  email                         varchar(255) not null,
  password                      varchar(255),
  mobile_number                 varchar(10),
  status                        varchar(255),
  created_at                    datetime(6),
  updated_at                    datetime(6),
  constraint uq_students_email unique (email),
  constraint pk_students primary key (user_id)
);

create table teacher (
  user_id                       bigint auto_increment not null,
  role                          varchar(7) not null,
  full_name                     varchar(255),
  email                         varchar(255) not null,
  password                      varchar(255),
  mobile_number                 varchar(10),
  status                        varchar(255),
  created_at                    datetime(6),
  updated_at                    datetime(6),
  constraint uq_teacher_email unique (email),
  constraint pk_teacher primary key (user_id)
);

