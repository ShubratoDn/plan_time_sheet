CREATE TABLE remove_me.`company` (
  `id` int NOT NULL AUTO_INCREMENT,
  `created_datetime` datetime(6) DEFAULT NULL,
  `modified_datetime` datetime(6) DEFAULT NULL,
  `active` bit(1) DEFAULT NULL,
  `address` varchar(255) DEFAULT NULL,
  `db_name` varchar(255) DEFAULT NULL,
  `details` varchar(255) DEFAULT NULL,
  `file_folder` varchar(255) DEFAULT NULL,
  `image_path` varchar(255) DEFAULT NULL,
  `name` varchar(255) DEFAULT NULL,
  `timesheet_submit_email` varchar(255) DEFAULT NULL,
  `url_slug` varchar(255) DEFAULT NULL,
  `uuid` varchar(255) DEFAULT NULL,
  `varify` bit(1) DEFAULT NULL,
  PRIMARY KEY (`id`)
);

CREATE TABLE remove_me.`internal_user` (
  `id` int NOT NULL AUTO_INCREMENT,
  `default_user` bit(1) DEFAULT NULL,
  `email` varchar(255) DEFAULT NULL,
  `first_name` varchar(255) DEFAULT NULL,
  `role` varchar(255) DEFAULT NULL,
  `last_name` varchar(255) DEFAULT NULL,
  `phone` varchar(255) DEFAULT NULL,
  `rate` float DEFAULT NULL,
  `rate_count_on` enum('G_MARGIN','ON_HOURS','REVENUE') DEFAULT NULL,
  `rate_type` enum('FIX','PERCENTAGE') DEFAULT NULL,
  `recurssive` tinyint(1) NOT NULL DEFAULT '0',
  `work_email` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`id`)
);

CREATE TABLE remove_me.`client` (
  `id` int NOT NULL AUTO_INCREMENT,
  `created_datetime` datetime(6) DEFAULT NULL,
  `modified_datetime` datetime(6) DEFAULT NULL,
  `address` varchar(255) DEFAULT NULL,
  `clientName` varchar(255) DEFAULT NULL,
  `phone` varchar(255) DEFAULT NULL,
  `remark` varchar(255) DEFAULT NULL,
  `type` tinyint DEFAULT NULL,
  `zipCode` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`id`),
  CONSTRAINT `client_chk_1` CHECK ((`type` between 0 and 3))
);

CREATE TABLE remove_me.`user` (
  `user_id` int NOT NULL AUTO_INCREMENT,
  `active` int DEFAULT NULL,
  `client_active` int DEFAULT NULL,
  `email` varchar(255) NOT NULL,
  `file_folder` varchar(255) DEFAULT NULL,
  `first_name` varchar(255) NOT NULL,
  `last_name` varchar(255) NOT NULL,
  `password` varchar(255) DEFAULT NULL,
  `phone` varchar(255) NOT NULL,
  `private_sign` text,
  `role` varchar(255) DEFAULT NULL,
  `uuid` varchar(255) DEFAULT NULL,
  `work_email` varchar(255) DEFAULT NULL,
  `company_id` int DEFAULT NULL,
  PRIMARY KEY (`user_id`),
  KEY `FK2yuxsfrkkrnkn5emoobcnnc3r` (`company_id`),
  CONSTRAINT `FK2yuxsfrkkrnkn5emoobcnnc3r` FOREIGN KEY (`company_id`) REFERENCES `company` (`id`)
);

CREATE TABLE remove_me.`user_company` (
  `id` int NOT NULL AUTO_INCREMENT,
  `active` bit(1) DEFAULT NULL,
  `login_count` int DEFAULT NULL,
  `role` varchar(255) DEFAULT NULL,
  `company_id` int DEFAULT NULL,
  `user_id` int DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `FK31viftsw6kihfrdybimaj0r3d` (`company_id`),
  KEY `FK7ca8sstytm1n5sg8if4qq2ph8` (`user_id`),
  CONSTRAINT `FK31viftsw6kihfrdybimaj0r3d` FOREIGN KEY (`company_id`) REFERENCES `company` (`id`),
  CONSTRAINT `FK7ca8sstytm1n5sg8if4qq2ph8` FOREIGN KEY (`user_id`) REFERENCES `user` (`user_id`)
);


CREATE TABLE remove_me.`user_detail` (
  `user_detail_id` int NOT NULL AUTO_INCREMENT,
  `created_datetime` datetime(6) DEFAULT NULL,
  `modified_datetime` datetime(6) DEFAULT NULL,
  `account_manager_commission` float NOT NULL,
  `account_manager_commission_rate_count_on` enum('G_MARGIN','ON_HOURS','REVENUE') DEFAULT NULL,
  `account_manager_commission_rate_type` tinyint NOT NULL,
  `account_manager_recurssive` tinyint(1) NOT NULL DEFAULT '0',
  `account_manager_recurssive_month` int DEFAULT NULL,
  `account_manager_name` varchar(255) DEFAULT NULL,
  `active` bit(1) DEFAULT NULL,
  `address` varchar(255) DEFAULT NULL,
  `business_development_manager_commission` float NOT NULL,
  `business_development_manager_commission_rate_count_on` enum('G_MARGIN','ON_HOURS','REVENUE') DEFAULT NULL,
  `business_development_manager_commission_rate_type` tinyint NOT NULL,
  `business_development_manager_recurssive` tinyint(1) NOT NULL DEFAULT '0',
  `business_development_manager_recurssive_month` int DEFAULT NULL,
  `business_development_manager_name` varchar(255) DEFAULT NULL,
  `c2c_or_other` float DEFAULT NULL,
  `c2c_or_other_rate_type` tinyint DEFAULT NULL,
  `c2c_or_other_recurssive` tinyint(1) NOT NULL DEFAULT '0',
  `c2c_or_other_recurssive_month` int DEFAULT NULL,
  `client_name` varchar(255) DEFAULT NULL,
  `client_rate` float NOT NULL,
  `consultant_rate` float DEFAULT NULL,
  `employer_email` varchar(255) DEFAULT NULL,
  `employer_name` varchar(255) DEFAULT NULL,
  `employer_phone` varchar(255) DEFAULT NULL,
  `end_date` datetime(6) DEFAULT NULL,
  `file_folder` varchar(255) DEFAULT NULL,
  `invoice_to` enum('CLIENT','VENDOR') DEFAULT NULL,
  `ptax` float DEFAULT NULL,
  `recruiter_commission` float NOT NULL,
  `recruiter_rate_count_on` enum('G_MARGIN','ON_HOURS','REVENUE') DEFAULT NULL,
  `recruiter_rate_type` tinyint NOT NULL,
  `recruiter_recurssive` tinyint(1) NOT NULL DEFAULT '0',
  `recruiter_recurssive_month` int DEFAULT NULL,
  `recruiter_name` varchar(255) DEFAULT NULL,
  `start_date` datetime(6) DEFAULT NULL,
  `time_sheet_period` varchar(255) DEFAULT NULL,
  `vendor_name` varchar(255) DEFAULT NULL,
  `w2` float DEFAULT NULL,
  `w2_or_c2c_type` tinyint DEFAULT NULL,
  `account_manager_id` int DEFAULT NULL,
  `business_development_manager_id` int DEFAULT NULL,
  `client_id` int DEFAULT NULL,
  `employer_id` int DEFAULT NULL,
  `recruiter_id` int DEFAULT NULL,
  `user_id` int DEFAULT NULL,
  `vendor_id` int DEFAULT NULL,
  PRIMARY KEY (`user_detail_id`),
  KEY `FK5re2cte5vmmhdj3itpkvmhll8` (`account_manager_id`),
  KEY `FKmrv3o7d2f4e89wyqk4w7koj5b` (`business_development_manager_id`),
  KEY `FKsu5xrmy950c808fcpgogf614q` (`client_id`),
  KEY `FKntaeqq0347yij84icnh2yk4b5` (`employer_id`),
  KEY `FKb4ggho01s7m2aj45psplk99sp` (`recruiter_id`),
  KEY `FKc2fr118twu8aratnm1qop1mn9` (`user_id`),
  KEY `FK5whb3t4kduh0wy1a4j4fx3o3f` (`vendor_id`),
  CONSTRAINT `FK5re2cte5vmmhdj3itpkvmhll8` FOREIGN KEY (`account_manager_id`) REFERENCES `internal_user` (`id`),
  CONSTRAINT `FK5whb3t4kduh0wy1a4j4fx3o3f` FOREIGN KEY (`vendor_id`) REFERENCES `client` (`id`),
  CONSTRAINT `FKb4ggho01s7m2aj45psplk99sp` FOREIGN KEY (`recruiter_id`) REFERENCES `internal_user` (`id`),
  CONSTRAINT `FKc2fr118twu8aratnm1qop1mn9` FOREIGN KEY (`user_id`) REFERENCES `user` (`user_id`),
  CONSTRAINT `FKmrv3o7d2f4e89wyqk4w7koj5b` FOREIGN KEY (`business_development_manager_id`) REFERENCES `internal_user` (`id`),
  CONSTRAINT `FKntaeqq0347yij84icnh2yk4b5` FOREIGN KEY (`employer_id`) REFERENCES `client` (`id`),
  CONSTRAINT `FKsu5xrmy950c808fcpgogf614q` FOREIGN KEY (`client_id`) REFERENCES `client` (`id`),
  CONSTRAINT `user_detail_chk_1` CHECK ((`account_manager_commission_rate_type` between 0 and 1)),
  CONSTRAINT `user_detail_chk_2` CHECK ((`business_development_manager_commission_rate_type` between 0 and 1)),
  CONSTRAINT `user_detail_chk_3` CHECK ((`c2c_or_other_rate_type` between 0 and 1)),
  CONSTRAINT `user_detail_chk_4` CHECK ((`recruiter_rate_type` between 0 and 1)),
  CONSTRAINT `user_detail_chk_5` CHECK ((`w2_or_c2c_type` between 0 and 1))
);


CREATE TABLE remove_me.`activity` (
  `id` int NOT NULL AUTO_INCREMENT,
  `created_datetime` datetime(6) DEFAULT NULL,
  `modified_datetime` datetime(6) DEFAULT NULL,
  `activity_type` varchar(255) DEFAULT NULL,
  `ip_address` varchar(255) DEFAULT NULL,
  `note` varchar(255) DEFAULT NULL,
  `other_note` text,
  `user_id` int DEFAULT NULL,
  `user_details_id` int DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `FKr8wdbwcm475hs03x515cjlftv` (`user_id`),
  KEY `FKpxfj690appbvdf0jlfhwgx43q` (`user_details_id`),
  CONSTRAINT `FKpxfj690appbvdf0jlfhwgx43q` FOREIGN KEY (`user_details_id`) REFERENCES `user_detail` (`user_detail_id`),
  CONSTRAINT `FKr8wdbwcm475hs03x515cjlftv` FOREIGN KEY (`user_id`) REFERENCES `user` (`user_id`)
);



CREATE TABLE remove_me.`hour_log` (
  `id` int NOT NULL AUTO_INCREMENT,
  `created_datetime` datetime(6) DEFAULT NULL,
  `modified_datetime` datetime(6) DEFAULT NULL,
  `daily_hours` float DEFAULT NULL,
  `extra_hours` float DEFAULT NULL,
  `hourlog_status` tinyint DEFAULT NULL,
  `hours_date` date DEFAULT NULL,
  `notes` text,
  `reject_reason` text,
  `vacation_hours` float DEFAULT NULL,
  `user_detail_id` int DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `FK1mg4k657pdlsvkplyhkh02xj6` (`user_detail_id`),
  CONSTRAINT `FK1mg4k657pdlsvkplyhkh02xj6` FOREIGN KEY (`user_detail_id`) REFERENCES `user_detail` (`user_detail_id`),
  CONSTRAINT `hour_log_chk_1` CHECK ((`hourlog_status` between 0 and 3))
);

CREATE TABLE remove_me.`hour_log_file` (
  `id` int NOT NULL AUTO_INCREMENT,
  `created_datetime` datetime(6) DEFAULT NULL,
  `modified_datetime` datetime(6) DEFAULT NULL,
  `approve` bit(1) DEFAULT NULL,
  `approved_date` datetime(6) DEFAULT NULL,
  `description` text,
  `end_date` date DEFAULT NULL,
  `file_original_name` varchar(255) DEFAULT NULL,
  `file_path` varchar(255) DEFAULT NULL,
  `is_hour_added` bit(1) DEFAULT NULL,
  `reject` bit(1) DEFAULT NULL,
  `reject_reason` text,
  `rejected_date` datetime(6) DEFAULT NULL,
  `remark` varchar(255) DEFAULT NULL,
  `start_date` date DEFAULT NULL,
  `approved_by` int DEFAULT NULL,
  `rejected_by` int DEFAULT NULL,
  `user_detail_id` int DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `FK2lrhpdliwl7j9wd5ar4yq3fq6` (`approved_by`),
  KEY `FK449magr22eke1a14lysjrdv91` (`rejected_by`),
  KEY `FKqdnuajhylhwj13n1y6eapw4j` (`user_detail_id`),
  CONSTRAINT `FK2lrhpdliwl7j9wd5ar4yq3fq6` FOREIGN KEY (`approved_by`) REFERENCES `user` (`user_id`),
  CONSTRAINT `FK449magr22eke1a14lysjrdv91` FOREIGN KEY (`rejected_by`) REFERENCES `user` (`user_id`),
  CONSTRAINT `FKqdnuajhylhwj13n1y6eapw4j` FOREIGN KEY (`user_detail_id`) REFERENCES `user_detail` (`user_detail_id`)
);

CREATE TABLE remove_me.`hour_log_file_path` (
  `id` int NOT NULL AUTO_INCREMENT,
  `created_datetime` datetime(6) DEFAULT NULL,
  `modified_datetime` datetime(6) DEFAULT NULL,
  `admin_added_file` bit(1) DEFAULT NULL,
  `file_original_name` varchar(255) DEFAULT NULL,
  `file_path` varchar(255) DEFAULT NULL,
  `hour_log_file_id` int DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `FKdgnfp3qw24nhoa50kxts9so5e` (`hour_log_file_id`),
  CONSTRAINT `FKdgnfp3qw24nhoa50kxts9so5e` FOREIGN KEY (`hour_log_file_id`) REFERENCES `hour_log_file` (`id`)
);


CREATE TABLE remove_me.`manager` (
  `id` int NOT NULL AUTO_INCREMENT,
  `department` varchar(255) DEFAULT NULL,
  `email` varchar(255) DEFAULT NULL,
  `managerName` varchar(255) DEFAULT NULL,
  `phone` varchar(255) DEFAULT NULL,
  `client_id` int DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `FKh3rgqur2465uoiuciyyhft1yw` (`client_id`),
  CONSTRAINT `FKh3rgqur2465uoiuciyyhft1yw` FOREIGN KEY (`client_id`) REFERENCES `client` (`id`)
);

CREATE TABLE remove_me.`otp_token` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `createdAt` datetime(6) NOT NULL,
  `expiresAt` datetime(6) NOT NULL,
  `otp` varchar(6) NOT NULL,
  `resendAvailableAt` datetime(6) NOT NULL,
  `used` bit(1) NOT NULL,
  `user_id` int NOT NULL,
  PRIMARY KEY (`id`),
  KEY `FK5hucs7rrk6tw4n7v58gtx2v51` (`user_id`),
  CONSTRAINT `FK5hucs7rrk6tw4n7v58gtx2v51` FOREIGN KEY (`user_id`) REFERENCES `user` (`user_id`)
);

CREATE TABLE remove_me.`permission_plan` (
  `id` int NOT NULL AUTO_INCREMENT,
  `commission` bit(1) DEFAULT NULL,
  `qb_integration` bit(1) DEFAULT NULL,
  `schedular_can_set` bit(1) DEFAULT NULL,
  `template_can_set` bit(1) DEFAULT NULL,
  `user_can_login` bit(1) DEFAULT NULL,
  `user_limit` int DEFAULT NULL,
  `company_id` int DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `FKcggtnuk32x2k9291pqj8vuehu` (`company_id`),
  CONSTRAINT `FKcggtnuk32x2k9291pqj8vuehu` FOREIGN KEY (`company_id`) REFERENCES `company` (`id`)
);

CREATE TABLE remove_me.`schedular` (
  `id` int NOT NULL AUTO_INCREMENT,
  `created_datetime` datetime(6) DEFAULT NULL,
  `modified_datetime` datetime(6) DEFAULT NULL,
  `daily_hours` float DEFAULT NULL,
  `day_off` bit(1) DEFAULT NULL,
  `extra_hours` float DEFAULT NULL,
  `hours_date` date DEFAULT NULL,
  `remark` varchar(255) DEFAULT NULL,
  `vacation_hours` float DEFAULT NULL,
  PRIMARY KEY (`id`)
);

CREATE TABLE remove_me.`signature` (
  `id` int NOT NULL AUTO_INCREMENT,
  `html_data` text,
  `title` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`id`)
);

CREATE TABLE remove_me.`template` (
  `id` int NOT NULL AUTO_INCREMENT,
  `created_datetime` datetime(6) DEFAULT NULL,
  `modified_datetime` datetime(6) DEFAULT NULL,
  `html_data` text,
  `mail_template_type` enum('approvalTimesheet','followupTimesheet','general','pendingTimesheet','rejectedTimesheet','schedularTimesheet','submission') DEFAULT NULL,
  `role_admin_permission` tinyint(1) NOT NULL DEFAULT '0',
  `role_supervisor_permission` tinyint(1) NOT NULL DEFAULT '0',
  `role_user_permission` tinyint(1) NOT NULL DEFAULT '0',
  `subject` varchar(255) DEFAULT NULL,
  `template_name` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`id`)
);


CREATE TABLE remove_me.`time_sheet_submission` (
  `id` int NOT NULL AUTO_INCREMENT,
  `created_datetime` datetime(6) DEFAULT NULL,
  `modified_datetime` datetime(6) DEFAULT NULL,
  `approve` bit(1) DEFAULT NULL,
  `approved_date` datetime(6) DEFAULT NULL,
  `key_value` varchar(255) NOT NULL,
  `reject` bit(1) DEFAULT NULL,
  `reject_reason` text,
  `rejected_date` datetime(6) DEFAULT NULL,
  `submit` bit(1) NOT NULL,
  `week_end_date` datetime(6) DEFAULT NULL,
  `week_start_date` datetime(6) DEFAULT NULL,
  `approved_by` int DEFAULT NULL,
  `rejected_by` int DEFAULT NULL,
  `user_detail_id` int DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `FK43pxo8d0vp4efw4l5d3ntoxc` (`approved_by`),
  KEY `FK4j9eg2bbd3dns74qnacmi1tfj` (`rejected_by`),
  KEY `FK8i36mlk5cf9ogkrjrn8oqqci3` (`user_detail_id`),
  CONSTRAINT `FK43pxo8d0vp4efw4l5d3ntoxc` FOREIGN KEY (`approved_by`) REFERENCES `user` (`user_id`),
  CONSTRAINT `FK4j9eg2bbd3dns74qnacmi1tfj` FOREIGN KEY (`rejected_by`) REFERENCES `user` (`user_id`),
  CONSTRAINT `FK8i36mlk5cf9ogkrjrn8oqqci3` FOREIGN KEY (`user_detail_id`) REFERENCES `user_detail` (`user_detail_id`)
);

CREATE TABLE remove_me.`user_file` (
  `id` int NOT NULL AUTO_INCREMENT,
  `created_datetime` datetime(6) DEFAULT NULL,
  `modified_datetime` datetime(6) DEFAULT NULL,
  `exp_date` datetime(6) DEFAULT NULL,
  `file_name` varchar(255) DEFAULT NULL,
  `file_path` varchar(255) DEFAULT NULL,
  `remark` varchar(255) DEFAULT NULL,
  `type` varchar(255) DEFAULT NULL,
  `user_id` int DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `FKk170byb5nt79g5mr4aasp4410` (`user_id`),
  CONSTRAINT `FKk170byb5nt79g5mr4aasp4410` FOREIGN KEY (`user_id`) REFERENCES `user` (`user_id`)
);


CREATE TABLE remove_me.`user_role_access` (
  `id` int NOT NULL AUTO_INCREMENT,
  `created_datetime` datetime(6) DEFAULT NULL,
  `modified_datetime` datetime(6) DEFAULT NULL,
  `functionality` enum('ADD_SCHEDULAR','ADD_TIME_SHEET','CLIENT_ACCESS','CLIENT_ASSIGN_USER','CONSULTANT_DASHBOARD','EMPLOYEE_ACCESS','GENERAL_MAIL','HOURS_DASHBOARD','INTERNAL_USER','PENDING_TIMESHEET_MAIL','REPORT_TIME_SHEET','SUBMITTED_TIMESHEET','SUPERVISOR_ACTIVITY','TEMPLATE','TIMESHEET','TIME_SHEET_SCHEDULAR','USER','USER_ACTIVITY','VENDOR_ACCESS') DEFAULT NULL,
  `is_create` bit(1) DEFAULT NULL,
  `is_delete` bit(1) DEFAULT NULL,
  `is_own` bit(1) DEFAULT NULL,
  `is_read` bit(1) DEFAULT NULL,
  `is_update` bit(1) DEFAULT NULL,
  `role` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`id`)
);



#--##################### DATA INSERT#######################################

INSERT INTO remove_me.`internal_user`
(`email`,`first_name`,`role`,`last_name`,`phone`,`rate`,`rate_count_on`,`rate_type`,`recurssive`,`work_email`,`default_user`)
VALUES
('476b3636-0e1a-4a23-abf5-943f3e88b2a6', 'not', 'BDM', 'available', '0000000000', '0', 'G_MARGIN', 'FIX', true, '', true),
('476b3636-0e1a-4a23-abf5-943f3e88b2a22', 'not', 'Recruiter', 'available', '0000000000', '0', 'G_MARGIN', 'FIX', true, '', true),
('476b3636-0e1a-4a23-abf5-943f3e88b221', 'not', 'AccountManager', 'available', '0000000000', '0', 'G_MARGIN', 'FIX', true, '', true);

INSERT INTO remove_me.`user_role_access` (`created_datetime`, `modified_datetime`, `functionality`, `is_create`, `is_delete`, `is_own`, `is_read`, `is_update`, `role`) VALUES ("2021-04-09 19:08:25", "2021-04-09 19:08:25", "TIMESHEET", true, false, false, true, true, "ROLE_SUPERVISOR");
#--1
INSERT INTO remove_me.`user_role_access` (`created_datetime`, `modified_datetime`, `functionality`, `is_create`, `is_delete`, `is_own`, `is_read`, `is_update`, `role`) VALUES ("2021-04-09 19:08:25", "2021-04-09 19:08:25", "CONSULTANT_DASHBOARD", false, false, false, true, false, "ROLE_SUPERVISOR");
#--2
INSERT INTO remove_me.`user_role_access` (`created_datetime`, `modified_datetime`, `functionality`, `is_create`, `is_delete`, `is_own`, `is_read`, `is_update`, `role`) VALUES ("2021-04-09 19:08:25", "2021-04-09 19:08:25", "HOURS_DASHBOARD", false, false, false, true, false, "ROLE_SUPERVISOR");
#--3
INSERT INTO remove_me.`user_role_access` (`created_datetime`, `modified_datetime`, `functionality`, `is_create`, `is_delete`, `is_own`, `is_read`, `is_update`, `role`) VALUES ("2021-04-09 19:08:25", "2021-04-09 19:08:25", "ADD_SCHEDULAR", true, false, false, false, false, "ROLE_SUPERVISOR");
#--4
INSERT INTO remove_me.`user_role_access` (`created_datetime`, `modified_datetime`, `functionality`, `is_create`, `is_delete`, `is_own`, `is_read`, `is_update`, `role`) VALUES ("2021-04-09 19:08:25", "2021-04-09 19:08:25", "TIME_SHEET_SCHEDULAR", false, false, false, true, false, "ROLE_SUPERVISOR");
#--5
INSERT INTO remove_me.`user_role_access` (`created_datetime`, `modified_datetime`, `functionality`, `is_create`, `is_delete`, `is_own`, `is_read`, `is_update`, `role`) VALUES ('2021-04-09 19:08:25', '2021-04-09 19:08:25', 'GENERAL_MAIL', true, false, false, false, false, 'ROLE_SUPERVISOR');
#--6
INSERT INTO remove_me.`user_role_access` (`created_datetime`, `modified_datetime`, `functionality`, `is_create`, `is_delete`, `is_own`, `is_read`, `is_update`, `role`) VALUES ('2021-04-09 19:08:25', '2021-04-09 19:08:25', 'PENDING_TIMESHEET_MAIL', true, false, false, false, false, 'ROLE_SUPERVISOR');
#--7
INSERT INTO remove_me.`user_role_access` (`created_datetime`, `modified_datetime`, `functionality`, `is_create`, `is_delete`, `is_own`, `is_read`, `is_update`, `role`) VALUES ('2021-04-09 19:08:25', '2021-04-09 19:08:25', 'HOURS_DASHBOARD', false, false, false, true, false, 'ROLE_USER');
#--8
INSERT INTO remove_me.`user_role_access` (`created_datetime`, `modified_datetime`, `functionality`, `is_create`, `is_delete`, `is_own`, `is_read`, `is_update`, `role`) VALUES ('2021-04-09 19:08:25', '2021-04-09 19:08:25', 'SUBMITTED_TIMESHEET', false, false, false, true, false, 'ROLE_USER');
#--9
INSERT INTO remove_me.`user_role_access` (`created_datetime`, `modified_datetime`, `functionality`, `is_create`, `is_delete`, `is_own`, `is_read`, `is_update`, `role`) VALUES ('2021-04-09 19:08:25', '2021-04-09 19:08:25', 'ADD_TIME_SHEET', true, false, false, false, false, 'ROLE_USER');
#--10
INSERT INTO remove_me.`user_role_access` (`created_datetime`, `modified_datetime`, `functionality`, `is_create`, `is_delete`, `is_own`, `is_read`, `is_update`, `role`) VALUES ('2021-04-09 19:08:25', '2021-04-09 19:08:25', 'REPORT_TIME_SHEET', false, false,false, true, false, 'ROLE_USER');
#--11
INSERT INTO remove_me.`user_role_access` (`created_datetime`, `modified_datetime`, `functionality`, `is_create`, `is_delete`, `is_own`, `is_read`, `is_update`, `role`) VALUES ('2021-04-09 19:08:25', '2021-04-09 19:08:25', 'SUPERVISOR_ACTIVITY', false, false, false, true, false, 'ROLE_SUPERVISOR');
#--12
INSERT INTO remove_me.`user_role_access` (`created_datetime`, `modified_datetime`, `functionality`, `is_create`, `is_delete`, `is_own`, `is_read`, `is_update`, `role`) VALUES ('2021-04-09 19:08:25', '2021-04-09 19:08:25', 'USER_ACTIVITY', false, false, false, true, false, 'ROLE_SUPERVISOR');
#--13
INSERT INTO remove_me.`user_role_access` (`created_datetime`, `modified_datetime`, `functionality`, `is_create`, `is_delete`, `is_own`, `is_read`, `is_update`, `role`) VALUES ('2021-04-09 19:08:25', '2021-04-09 19:08:25', 'USER', true, false, false, true, true, 'ROLE_SUPERVISOR');
#--14
INSERT INTO remove_me.`user_role_access` (`created_datetime`, `modified_datetime`, `functionality`, `is_create`, `is_delete`, `is_own`, `is_read`, `is_update`, `role`) VALUES ('2021-04-09 19:08:25', '2021-04-09 19:08:25', 'INTERNAL_USER', true, false, false, true, true, 'ROLE_SUPERVISOR');
#--15
INSERT INTO remove_me.`user_role_access` (`created_datetime`, `modified_datetime`, `functionality`, `is_create`, `is_delete`, `is_own`, `is_read`, `is_update`, `role`) VALUES ('2021-04-09 19:08:25', '2021-04-09 19:08:25', 'TEMPLATE', true, true, false, true, true, 'ROLE_SUPERVISOR');
#--16
INSERT INTO remove_me.`user_role_access` (`created_datetime`, `modified_datetime`, `functionality`, `is_create`, `is_delete`, `is_own`, `is_read`, `is_update`, `role`) VALUES ('2021-04-09 19:08:25', '2021-04-09 19:08:25', 'CLIENT_ASSIGN_USER', true, false, false, true, true, 'ROLE_SUPERVISOR');
#--17
INSERT INTO remove_me.`user_role_access` (`created_datetime`, `modified_datetime`, `functionality`, `is_create`, `is_delete`, `is_own`, `is_read`, `is_update`, `role`) VALUES ('2021-04-09 19:08:25', '2021-04-09 19:08:25', 'CLIENT_ACCESS', true, false, false, true, true, 'ROLE_SUPERVISOR');
#--18
INSERT INTO remove_me.`user_role_access` (`created_datetime`, `modified_datetime`, `functionality`, `is_create`, `is_delete`, `is_own`, `is_read`, `is_update`, `role`) VALUES ('2021-04-09 19:08:25', '2021-04-09 19:08:25', 'VENDOR_ACCESS', true, false, false, true, true, 'ROLE_SUPERVISOR');
#--19
INSERT INTO remove_me.`user_role_access` (`created_datetime`, `modified_datetime`, `functionality`, `is_create`, `is_delete`, `is_own`, `is_read`, `is_update`, `role`) VALUES ('2021-04-09 19:08:25', '2021-04-09 19:08:25', 'EMPLOYEE_ACCESS', true, false, false, true, true, 'ROLE_SUPERVISOR');
