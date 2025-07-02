CREATE TABLE innoweb.`company` (
                                          `id` int(11) NOT NULL AUTO_INCREMENT,
                                          `created_datetime` datetime DEFAULT NULL,
                                          `modified_datetime` datetime DEFAULT NULL,
                                          `active` bit(1) DEFAULT NULL,
                                          `address` varchar(255) DEFAULT NULL,
                                          `timesheet_submit_email` VARCHAR(255) DEFAULT NULL,
                                          `db_name` varchar(255) DEFAULT NULL,
                                          `image_path` VARCHAR(255) DEFAULT NULL,
                                          `file_folder` VARCHAR(255) DEFAULT NULL,
                                          `details` varchar(255) DEFAULT NULL,
                                          `name` varchar(255) DEFAULT NULL,
                                          `url_slug` varchar(255) DEFAULT NULL,
                                          `uuid` varchar(255) DEFAULT NULL,
                                          `varify` bit(1) DEFAULT NULL,
                                          PRIMARY KEY (`id`)
);

insert into innoweb.company

#--CREATE TABLE innoweb.`client`
CREATE TABLE innoweb.`client` (
                                         `id` int(11) NOT NULL AUTO_INCREMENT,
                                         `created_datetime` datetime DEFAULT NULL,
                                         `modified_datetime` datetime DEFAULT NULL,
                                         `address` varchar(255) DEFAULT NULL,
                                         `clientName` varchar(255) DEFAULT NULL,
                                         `phone` varchar(255) DEFAULT NULL,
                                         `remark` varchar(255) DEFAULT NULL,
                                         `type` int(11) DEFAULT NULL,
                                         `zipCode` varchar(255) DEFAULT NULL,
                                         PRIMARY KEY (`id`));

#--CREATE TABLE innoweb.`manager`
CREATE TABLE innoweb.`manager` (
                                          `id` int(11) NOT NULL AUTO_INCREMENT,
                                          `department` varchar(255) DEFAULT NULL,
                                          `email` varchar(255) DEFAULT NULL,
                                          `managerName` varchar(255) DEFAULT NULL,
                                          `phone` varchar(255) DEFAULT NULL,
                                          `client_id` int(11) DEFAULT NULL,
                                          PRIMARY KEY (`id`),
                                          KEY (`client_id`),
                                          FOREIGN KEY (`client_id`) REFERENCES `client` (`id`)
);

#--CREATE TABLE innoweb.`user`
CREATE TABLE innoweb.`user` (
                                       `user_id` int(11) NOT NULL AUTO_INCREMENT,
                                       `active` int(11) DEFAULT NULL,
                                       `client_active` int(11) DEFAULT NULL,
                                       `email` varchar(255) NOT NULL,
                                       `first_name` varchar(255) NOT NULL,
                                       `last_name` varchar(255) NOT NULL,
                                       `password` varchar(255) DEFAULT NULL,
                                       `phone` varchar(255) NOT NULL,
                                       `role` varchar(255) DEFAULT NULL,
                                       `uuid` varchar(255) DEFAULT NULL,
                                       `work_email` varchar(255) DEFAULT NULL,
                                       `private_sign` text DEFAULT NULL,
                                       `company_id` int(11) DEFAULT NULL,
                                       `file_folder` varchar(255) DEFAULT NULL,
                                       PRIMARY KEY (`user_id`),
                                       KEY (`company_id`),
                                       FOREIGN KEY (`company_id`) REFERENCES `company` (`id`)
);

#--CREATE TABLE innoweb.`internal_user` (
CREATE TABLE innoweb.`internal_user` (
                                                `id` int(11) NOT NULL AUTO_INCREMENT,
                                                `email` varchar(255) DEFAULT NULL,
                                                `first_name` varchar(255) DEFAULT NULL,
                                                `role` varchar(255) DEFAULT NULL,
                                                `last_name` varchar(255) DEFAULT NULL,
                                                `phone` varchar(255) DEFAULT NULL,
                                                `rate` float DEFAULT NULL,
                                                `rate_count_on` varchar(255) DEFAULT NULL,
                                                `rate_type` varchar(255) DEFAULT NULL,
                                                `recurssive` tinyint(1) NOT NULL DEFAULT '0',
                                                `work_email` varchar(255) DEFAULT NULL,
                                                `default_user` bit(1) DEFAULT NULL,
                                                PRIMARY KEY (`id`)
);

#--CREATE TABLE innoweb.`user_detail`
CREATE TABLE innoweb.`user_detail` (
                                              `user_detail_id` int(11) NOT NULL AUTO_INCREMENT,
                                              `created_datetime` datetime DEFAULT NULL,
                                              `modified_datetime` datetime DEFAULT NULL,
                                              `account_manager_commission` float NOT NULL,
                                              `account_manager_commission_rate_count_on` varchar(255) DEFAULT NULL,
                                              `account_manager_commission_rate_type` int(11) NOT NULL,
                                              `account_manager_recurssive` tinyint(1) NOT NULL DEFAULT '0',
                                              `account_manager_recurssive_month` int(11) DEFAULT NULL,
                                              `account_manager_name` varchar(255) DEFAULT NULL,
                                              `active` bit(1) DEFAULT NULL,
                                              `address` varchar(255) DEFAULT NULL,
                                              `invoice_to` varchar(255) DEFAULT NULL,
                                              `business_development_manager_commission` float NOT NULL,
                                              `business_development_manager_commission_rate_count_on` varchar(255) DEFAULT NULL,
                                              `business_development_manager_commission_rate_type` int(11) NOT NULL,
                                              `business_development_manager_recurssive` tinyint(1) NOT NULL DEFAULT '0',
                                              `business_development_manager_recurssive_month` int(11) DEFAULT NULL,
                                              `business_development_manager_name` varchar(255) DEFAULT NULL,
                                              `c2c_or_other` float DEFAULT NULL,
                                              `c2c_or_other_rate_type` int(11) DEFAULT NULL,
                                              `c2c_or_other_recurssive` tinyint(1) NOT NULL DEFAULT '0',
                                              `c2c_or_other_recurssive_month` int(11) DEFAULT NULL,
                                              `client_name` varchar(255) DEFAULT NULL,
                                              `client_rate` float NOT NULL,
                                              `consultant_rate` float DEFAULT NULL,
                                              `employer_email` varchar(255) DEFAULT NULL,
                                              `employer_name` varchar(255) DEFAULT NULL,
                                              `employer_phone` varchar(255) DEFAULT NULL,
                                              `end_date` datetime DEFAULT NULL,
                                              `ptax` float DEFAULT NULL,
                                              `recruiter_commission` float NOT NULL,
                                              `recruiter_rate_count_on` varchar(255) DEFAULT NULL,
                                              `recruiter_rate_type` int(11) NOT NULL,
                                              `recruiter_recurssive` tinyint(1) NOT NULL DEFAULT '0',
                                              `recruiter_recurssive_month` int(11) DEFAULT NULL,
                                              `recruiter_name` varchar(255) DEFAULT NULL,
                                              `start_date` datetime DEFAULT NULL,
                                              `time_sheet_period` varchar(255) DEFAULT NULL,
                                              `vendor_name` varchar(255) DEFAULT NULL,
                                              `w2` float DEFAULT NULL,
                                              `w2_or_c2c_type` int(11) DEFAULT NULL,
                                              `account_manager_id` int(11) DEFAULT NULL,
                                              `business_development_manager_id` int(11) DEFAULT NULL,
                                              `client_id` int(11) DEFAULT NULL,
                                              `employer_id` int(11) DEFAULT NULL,
                                              `recruiter_id` int(11) DEFAULT NULL,
                                              `user_id` int(11) DEFAULT NULL,
                                              `vendor_id` int(11) DEFAULT NULL,
                                              `file_folder` varchar(255) DEFAULT NULL,
                                              PRIMARY KEY (`user_detail_id`),
                                              KEY (`account_manager_id`),
                                              KEY (`business_development_manager_id`),
                                              KEY (`client_id`),
                                              KEY (`employer_id`),
                                              KEY (`recruiter_id`),
                                              KEY (`user_id`),
                                              KEY (`vendor_id`),
                                              FOREIGN KEY (`account_manager_id`) REFERENCES `internal_user` (`id`),
                                              FOREIGN KEY (`vendor_id`) REFERENCES `client` (`id`),
                                              FOREIGN KEY (`recruiter_id`) REFERENCES `internal_user` (`id`),
                                              FOREIGN KEY (`user_id`) REFERENCES `user` (`user_id`),
                                              FOREIGN KEY (`business_development_manager_id`) REFERENCES `internal_user` (`id`),
                                              FOREIGN KEY (`employer_id`) REFERENCES `client` (`id`),
                                              FOREIGN KEY (`client_id`) REFERENCES `client` (`id`)
);

#--activity
CREATE TABLE innoweb.`activity` (
                                           `id` int(11) NOT NULL AUTO_INCREMENT,
                                           `created_datetime` datetime DEFAULT NULL,
                                           `modified_datetime` datetime DEFAULT NULL,
                                           `activity_type` varchar(255) DEFAULT NULL,
                                           `ip_address` varchar(255) DEFAULT NULL,
                                           `note` varchar(255) DEFAULT NULL,
                                           `other_note` text,
                                           `user_id` int(11) DEFAULT NULL,
                                           `user_details_id` int(11) DEFAULT NULL,
                                           PRIMARY KEY (`id`),
                                           KEY (`user_id`),
                                           KEY (`user_details_id`),
                                           FOREIGN KEY (`user_details_id`) REFERENCES `user_detail` (`user_detail_id`),
                                           FOREIGN KEY (`user_id`) REFERENCES `user` (`user_id`)
);


#--hour_log
CREATE TABLE innoweb.`hour_log` (
                                           `id` int(11) NOT NULL AUTO_INCREMENT,
                                           `created_datetime` datetime DEFAULT NULL,
                                           `modified_datetime` datetime DEFAULT NULL,
                                           `daily_hours` float DEFAULT NULL,
                                           `extra_hours` float DEFAULT NULL,
                                           `hourlog_status` int(11) DEFAULT NULL,
                                           `hours_date` date DEFAULT NULL,
                                           `reject_reason` text,
                                           `vacation_hours` float DEFAULT NULL,
                                           `user_detail_id` int(11) DEFAULT NULL,
                                           `notes` text,
                                           PRIMARY KEY (`id`),
                                           KEY (`user_detail_id`),
                                           FOREIGN KEY (`user_detail_id`) REFERENCES `user_detail` (`user_detail_id`)
);

#--Hour_log_file
CREATE TABLE innoweb.`Hour_log_file` (
                                                `id` int(11) NOT NULL AUTO_INCREMENT,
                                                `created_datetime` datetime DEFAULT NULL,
                                                `modified_datetime` datetime DEFAULT NULL,
                                                `approve` bit(1) DEFAULT NULL,
                                                `approved_date` datetime DEFAULT NULL,
                                                `description` text,
                                                `end_date` date DEFAULT NULL,
                                                `file_original_name` varchar(255) DEFAULT NULL,
                                                `file_path` varchar(255) DEFAULT NULL,
                                                `is_hour_added` bit(1) DEFAULT NULL,
                                                `reject` bit(1) DEFAULT NULL,
                                                `reject_reason` text,
                                                `rejected_date` datetime DEFAULT NULL,
                                                `start_date` date DEFAULT NULL,
                                                `approved_by` int(11) DEFAULT NULL,
                                                `rejected_by` int(11) DEFAULT NULL,
                                                `user_detail_id` int(11) DEFAULT NULL,
                                                `remark` text,
                                                PRIMARY KEY (`id`),
                                                KEY (`approved_by`),
                                                KEY (`rejected_by`),
                                                KEY (`user_detail_id`),
                                                FOREIGN KEY (`approved_by`) REFERENCES `user` (`user_id`),
                                                FOREIGN KEY (`rejected_by`) REFERENCES `user` (`user_id`),
                                                FOREIGN KEY (`user_detail_id`) REFERENCES `user_detail` (`user_detail_id`)
);

#--Hour_log_file_path
CREATE TABLE innoweb.`Hour_log_file_path` (
                                                     `id` int(11) NOT NULL AUTO_INCREMENT,
                                                     `created_datetime` datetime DEFAULT NULL,
                                                     `modified_datetime` datetime DEFAULT NULL,
                                                     `file_original_name` varchar(255) DEFAULT NULL,
                                                     `file_path` varchar(255) DEFAULT NULL,
                                                     `admin_added_file` bit(1) DEFAULT false,
                                                     `hour_log_file_id` int(11) DEFAULT NULL,
                                                     PRIMARY KEY (`id`),
                                                     KEY (`hour_log_file_id`),
                                                     FOREIGN KEY (`hour_log_file_id`) REFERENCES `Hour_log_file` (`id`)
);


#--CREATE TABLE innoweb.`schedular` (
CREATE TABLE innoweb.`schedular` (
                                            `id` int(11) NOT NULL AUTO_INCREMENT,
                                            `created_datetime` datetime DEFAULT NULL,
                                            `modified_datetime` datetime DEFAULT NULL,
                                            `daily_hours` float DEFAULT NULL,
                                            `extra_hours` float DEFAULT NULL,
                                            `hours_date` date DEFAULT NULL,
                                            `vacation_hours` float DEFAULT NULL,
                                            `day_off` bit(1) DEFAULT false,
                                            `remark` varchar(255) DEFAULT NULL,
                                            PRIMARY KEY (`id`)
);

#--CREATE TABLE innoweb.`time_sheet_submission`
CREATE TABLE innoweb.`time_sheet_submission` (
                                                        `id` int(11) NOT NULL AUTO_INCREMENT,
                                                        `created_datetime` datetime DEFAULT NULL,
                                                        `modified_datetime` datetime DEFAULT NULL,
                                                        `approve` bit(1) DEFAULT NULL,
                                                        `approved_date` datetime DEFAULT NULL,
                                                        `key_value` varchar(255) NOT NULL,
                                                        `reject` bit(1) DEFAULT NULL,
                                                        `reject_reason` text,
                                                        `rejected_date` datetime DEFAULT NULL,
                                                        `submit` bit(1) NOT NULL,
                                                        `week_end_date` datetime DEFAULT NULL,
                                                        `week_start_date` datetime DEFAULT NULL,
                                                        `approved_by` int(11) DEFAULT NULL,
                                                        `rejected_by` int(11) DEFAULT NULL,
                                                        `user_detail_id` int(11) DEFAULT NULL,
                                                        PRIMARY KEY (`id`),
                                                        KEY (`approved_by`),
                                                        KEY (`rejected_by`),
                                                        KEY (`user_detail_id`),
                                                        FOREIGN KEY (`approved_by`) REFERENCES `user` (`user_id`),
                                                        FOREIGN KEY (`rejected_by`) REFERENCES `user` (`user_id`),
                                                        FOREIGN KEY (`user_detail_id`) REFERENCES `user_detail` (`user_detail_id`)
);

#--CREATE TABLE innoweb.`user_file`
CREATE TABLE innoweb.`user_file` (
                                            `id` int(11) NOT NULL AUTO_INCREMENT,
                                            `created_datetime` datetime DEFAULT NULL,
                                            `modified_datetime` datetime DEFAULT NULL,
                                            `exp_date` datetime DEFAULT NULL,
                                            `file_path` varchar(255) DEFAULT NULL,
                                            `remark` varchar(255) DEFAULT NULL,
                                            `type` varchar(255) DEFAULT NULL,
                                            `user_id` int(11) DEFAULT NULL,
                                            `file_name` varchar(255) DEFAULT NULL,
                                            PRIMARY KEY (`id`),
                                            KEY (`user_id`),
                                            FOREIGN KEY (`user_id`) REFERENCES `user` (`user_id`)
);

#--CREATE TABLE innoweb.`signature`
CREATE TABLE innoweb.`signature` (
                                            `id` int(11) NOT NULL AUTO_INCREMENT,
                                            `html_data` text,
                                            `title` varchar(255) DEFAULT NULL,
                                            PRIMARY KEY (`id`)
);

#--CREATE TABLE innoweb.`template`
CREATE TABLE innoweb.`template` (
                                           `id` int(11) NOT NULL AUTO_INCREMENT,
                                           `created_datetime` datetime DEFAULT NULL,
                                           `modified_datetime` datetime DEFAULT NULL,
                                           `html_data` text,
                                           `mail_template_type` varchar(255) DEFAULT NULL,
                                           `role_admin_permission` tinyint(1) NOT NULL DEFAULT '0',
                                           `role_supervisor_permission` tinyint(1) NOT NULL DEFAULT '0',
                                           `role_user_permission` tinyint(1) NOT NULL DEFAULT '0',
                                           `subject` varchar(255) DEFAULT NULL,
                                           `template_name` varchar(255) DEFAULT NULL,
                                           PRIMARY KEY (`id`)
);

#--CREATE TABLE innoweb.`user_role_access`
CREATE TABLE innoweb.`user_role_access` (
                                                   `id` int(11) NOT NULL AUTO_INCREMENT,
                                                   `created_datetime` datetime DEFAULT NULL,
                                                   `modified_datetime` datetime DEFAULT NULL,
                                                   `functionality` varchar(255) DEFAULT NULL,
                                                   `is_create` bit(1) DEFAULT NULL,
                                                   `is_delete` bit(1) DEFAULT NULL,
                                                   `is_own` bit(1) DEFAULT NULL,
                                                   `is_read` bit(1) DEFAULT NULL,
                                                   `is_update` bit(1) DEFAULT NULL,
                                                   `role` varchar(255) DEFAULT NULL,
                                                   PRIMARY KEY (`id`)
);

CREATE TABLE innoweb.`otp_token` (
                                            `id` bigint NOT NULL AUTO_INCREMENT,
                                            `createdAt` datetime(6) NOT NULL,
                                            `expiresAt` datetime(6) NOT NULL,
                                            `otp` varchar(6) NOT NULL,
                                            `resendAvailableAt` datetime(6) NOT NULL,
                                            `used` bit(1) NOT NULL,
                                            `user_id` int NOT NULL,
                                            PRIMARY KEY (`id`),
                                            KEY `fk_user_id` (`user_id`),
                                            CONSTRAINT `fk_constraint_user_id` FOREIGN KEY (`user_id`) REFERENCES `user` (`user_id`)
);

#--##################### DATA INSERT#######################################
INSERT INTO innoweb.`user`
(`user_id`, `active`, `email`, `company_id`,`first_name`,`last_name`,`password`,`phone`,`role`,`uuid`,
 `work_email`,
 `client_active`, `file_folder`)
VALUES
    (0,
     true,
     "shubratodn44985@gmail.com",
     null,
     "Shubrato",
     "Debnath",
     "$2a$10$Vt/LtRJJKHuHGx4Cad6lFexYEoPF/t0CzOiotkz1Z9UDHv61fw45W",
     "1231123",
     "ROLE_ADMIN",
     "052a14c2-e5d6-43d5-a58d-62ff68085353",
     "shubratodn44985@gmail.com",
     null, "shubrato");

INSERT INTO innoweb.`internal_user`
		(`email`,`first_name`,`role`,`last_name`,`phone`,`rate`,`rate_count_on`,`rate_type`,`recurssive`,`work_email`,`default_user`)
		VALUES
		('476b3636-0e1a-4a23-abf5-943f3e88b2a6', 'not', 'BDM', 'available', '0000000000', '0', 'G_MARGIN', 'FIX', true, '', true),
		('476b3636-0e1a-4a23-abf5-943f3e88b2a22', 'not', 'Recruiter', 'available', '0000000000', '0', 'G_MARGIN', 'FIX', true, '', true),
		('476b3636-0e1a-4a23-abf5-943f3e88b221', 'not', 'AccountManager', 'available', '0000000000', '0', 'G_MARGIN', 'FIX', true, '', true);

INSERT INTO innoweb.`user_role_access` (`created_datetime`, `modified_datetime`, `functionality`, `is_create`, `is_delete`, `is_own`, `is_read`, `is_update`, `role`) VALUES ("2021-04-09 19:08:25", "2021-04-09 19:08:25", "TIMESHEET", true, false, false, true, true, "ROLE_SUPERVISOR");
#--1
INSERT INTO innoweb.`user_role_access` (`created_datetime`, `modified_datetime`, `functionality`, `is_create`, `is_delete`, `is_own`, `is_read`, `is_update`, `role`) VALUES ("2021-04-09 19:08:25", "2021-04-09 19:08:25", "CONSULTANT_DASHBOARD", false, false, false, true, false, "ROLE_SUPERVISOR");
		#--2
INSERT INTO innoweb.`user_role_access` (`created_datetime`, `modified_datetime`, `functionality`, `is_create`, `is_delete`, `is_own`, `is_read`, `is_update`, `role`) VALUES ("2021-04-09 19:08:25", "2021-04-09 19:08:25", "HOURS_DASHBOARD", false, false, false, true, false, "ROLE_SUPERVISOR");
		#--3
INSERT INTO innoweb.`user_role_access` (`created_datetime`, `modified_datetime`, `functionality`, `is_create`, `is_delete`, `is_own`, `is_read`, `is_update`, `role`) VALUES ("2021-04-09 19:08:25", "2021-04-09 19:08:25", "ADD_SCHEDULAR", true, false, false, false, false, "ROLE_SUPERVISOR");
		#--4
INSERT INTO innoweb.`user_role_access` (`created_datetime`, `modified_datetime`, `functionality`, `is_create`, `is_delete`, `is_own`, `is_read`, `is_update`, `role`) VALUES ("2021-04-09 19:08:25", "2021-04-09 19:08:25", "TIME_SHEET_SCHEDULAR", false, false, false, true, false, "ROLE_SUPERVISOR");
		#--5
INSERT INTO innoweb.`user_role_access` (`created_datetime`, `modified_datetime`, `functionality`, `is_create`, `is_delete`, `is_own`, `is_read`, `is_update`, `role`) VALUES ('2021-04-09 19:08:25', '2021-04-09 19:08:25', 'GENERAL_MAIL', true, false, false, false, false, 'ROLE_SUPERVISOR');
		#--6
INSERT INTO innoweb.`user_role_access` (`created_datetime`, `modified_datetime`, `functionality`, `is_create`, `is_delete`, `is_own`, `is_read`, `is_update`, `role`) VALUES ('2021-04-09 19:08:25', '2021-04-09 19:08:25', 'PENDING_TIMESHEET_MAIL', true, false, false, false, false, 'ROLE_SUPERVISOR');
		#--7
INSERT INTO innoweb.`user_role_access` (`created_datetime`, `modified_datetime`, `functionality`, `is_create`, `is_delete`, `is_own`, `is_read`, `is_update`, `role`) VALUES ('2021-04-09 19:08:25', '2021-04-09 19:08:25', 'HOURS_DASHBOARD', false, false, false, true, false, 'ROLE_USER');
		#--8
INSERT INTO innoweb.`user_role_access` (`created_datetime`, `modified_datetime`, `functionality`, `is_create`, `is_delete`, `is_own`, `is_read`, `is_update`, `role`) VALUES ('2021-04-09 19:08:25', '2021-04-09 19:08:25', 'SUBMITTED_TIMESHEET', false, false, false, true, false, 'ROLE_USER');
		#--9
INSERT INTO innoweb.`user_role_access` (`created_datetime`, `modified_datetime`, `functionality`, `is_create`, `is_delete`, `is_own`, `is_read`, `is_update`, `role`) VALUES ('2021-04-09 19:08:25', '2021-04-09 19:08:25', 'ADD_TIME_SHEET', true, false, false, false, false, 'ROLE_USER');
		#--10
INSERT INTO innoweb.`user_role_access` (`created_datetime`, `modified_datetime`, `functionality`, `is_create`, `is_delete`, `is_own`, `is_read`, `is_update`, `role`) VALUES ('2021-04-09 19:08:25', '2021-04-09 19:08:25', 'REPORT_TIME_SHEET', false, false,false, true, false, 'ROLE_USER');
		#--11
INSERT INTO innoweb.`user_role_access` (`created_datetime`, `modified_datetime`, `functionality`, `is_create`, `is_delete`, `is_own`, `is_read`, `is_update`, `role`) VALUES ('2021-04-09 19:08:25', '2021-04-09 19:08:25', 'SUPERVISOR_ACTIVITY', false, false, false, true, false, 'ROLE_SUPERVISOR');
		#--12
INSERT INTO innoweb.`user_role_access` (`created_datetime`, `modified_datetime`, `functionality`, `is_create`, `is_delete`, `is_own`, `is_read`, `is_update`, `role`) VALUES ('2021-04-09 19:08:25', '2021-04-09 19:08:25', 'USER_ACTIVITY', false, false, false, true, false, 'ROLE_SUPERVISOR');
		#--13
INSERT INTO innoweb.`user_role_access` (`created_datetime`, `modified_datetime`, `functionality`, `is_create`, `is_delete`, `is_own`, `is_read`, `is_update`, `role`) VALUES ('2021-04-09 19:08:25', '2021-04-09 19:08:25', 'USER', true, false, false, true, true, 'ROLE_SUPERVISOR');
		#--14
INSERT INTO innoweb.`user_role_access` (`created_datetime`, `modified_datetime`, `functionality`, `is_create`, `is_delete`, `is_own`, `is_read`, `is_update`, `role`) VALUES ('2021-04-09 19:08:25', '2021-04-09 19:08:25', 'INTERNAL_USER', true, false, false, true, true, 'ROLE_SUPERVISOR');
		#--15
INSERT INTO innoweb.`user_role_access` (`created_datetime`, `modified_datetime`, `functionality`, `is_create`, `is_delete`, `is_own`, `is_read`, `is_update`, `role`) VALUES ('2021-04-09 19:08:25', '2021-04-09 19:08:25', 'TEMPLATE', true, true, false, true, true, 'ROLE_SUPERVISOR');
		#--16
INSERT INTO innoweb.`user_role_access` (`created_datetime`, `modified_datetime`, `functionality`, `is_create`, `is_delete`, `is_own`, `is_read`, `is_update`, `role`) VALUES ('2021-04-09 19:08:25', '2021-04-09 19:08:25', 'CLIENT_ASSIGN_USER', true, false, false, true, true, 'ROLE_SUPERVISOR');
		#--17
INSERT INTO innoweb.`user_role_access` (`created_datetime`, `modified_datetime`, `functionality`, `is_create`, `is_delete`, `is_own`, `is_read`, `is_update`, `role`) VALUES ('2021-04-09 19:08:25', '2021-04-09 19:08:25', 'CLIENT_ACCESS', true, false, false, true, true, 'ROLE_SUPERVISOR');
		#--18
INSERT INTO innoweb.`user_role_access` (`created_datetime`, `modified_datetime`, `functionality`, `is_create`, `is_delete`, `is_own`, `is_read`, `is_update`, `role`) VALUES ('2021-04-09 19:08:25', '2021-04-09 19:08:25', 'VENDOR_ACCESS', true, false, false, true, true, 'ROLE_SUPERVISOR');
		#--19
INSERT INTO innoweb.`user_role_access` (`created_datetime`, `modified_datetime`, `functionality`, `is_create`, `is_delete`, `is_own`, `is_read`, `is_update`, `role`) VALUES ('2021-04-09 19:08:25', '2021-04-09 19:08:25', 'EMPLOYEE_ACCESS', true, false, false, true, true, 'ROLE_SUPERVISOR');
