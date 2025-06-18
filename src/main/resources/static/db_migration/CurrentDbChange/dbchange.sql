Date: 13-9-2021

---User master

ALTER TABLE user_company
 ADD`login_count` integer;
 
 ----------------------------------------------------
 
 Date: 9-9-2021
 
 ---User sub
 
 ALTER TABLE hour_log_file
ADD`remark` text;

----------------------------------------------------

 Date: 8-9-2021
 
 ---User sub
 
ALTER TABLE hour_log
ADD`notes` text;

----------------------------------------------------

 Date: 2-9-2021
 
 ---User sub
 
ALTER TABLE user_file
ADD`file_name` varchar(255) DEFAULT NULL;

----------------------------------------------------

 Date: 28-8-2021
 
 ---User master 

ALTER TABLE user
ADD`file_folder` varchar(255) DEFAULT NULL;

---User sub

ALTER TABLE user
ADD`file_folder` varchar(255) DEFAULT NULL;

--- User sub

ALTER TABLE user_detail
ADD`file_folder` varchar(255) DEFAULT NULL;
