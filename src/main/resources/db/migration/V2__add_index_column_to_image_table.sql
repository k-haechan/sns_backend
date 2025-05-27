ALTER TABLE image
    ADD `index` INT NULL;

ALTER TABLE member
    MODIFY `role` VARCHAR (255) NOT NULL;
