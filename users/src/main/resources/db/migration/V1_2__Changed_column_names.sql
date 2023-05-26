ALTER TABLE `users`
    CHANGE COLUMN `pozicija` `job_position` VARCHAR(255),
    CHANGE COLUMN `broj_telefona` `phone` VARCHAR(255),
    CHANGE COLUMN `aktivan` `active` TINYINT(1),
    ADD CONSTRAINT `email` UNIQUE (email),
    MODIFY COLUMN `password` VARCHAR(500);


ALTER TABLE `permissions`
    CHANGE COLUMN `permission_name` `permission_name` INT