CREATE TABLE comment
(
    id         BIGINT AUTO_INCREMENT NOT NULL,
    post_id    BIGINT   NOT NULL,
    content    VARCHAR(255) NULL,
    created_at datetime NOT NULL,
    updated_at datetime NOT NULL,
    CONSTRAINT pk_comment PRIMARY KEY (id)
);

CREATE TABLE comment_like
(
    id         BIGINT AUTO_INCREMENT NOT NULL,
    member_id  BIGINT   NOT NULL,
    comment_id BIGINT   NOT NULL,
    created_at datetime NOT NULL,
    CONSTRAINT pk_commentlike PRIMARY KEY (id)
);

CREATE TABLE follow
(
    id          BIGINT AUTO_INCREMENT NOT NULL,
    sender_id   BIGINT NOT NULL,
    receiver_id BIGINT NOT NULL,
    is_accepted BIT(1) NOT NULL,
    CONSTRAINT pk_follow PRIMARY KEY (id)
);

CREATE TABLE image
(
    id      BIGINT AUTO_INCREMENT NOT NULL,
    post_id BIGINT NOT NULL,
    `path`  VARCHAR(255) NULL,
    CONSTRAINT pk_image PRIMARY KEY (id)
);

CREATE TABLE member
(
    id                 BIGINT AUTO_INCREMENT NOT NULL,
    username           VARCHAR(255) NOT NULL,
    name               VARCHAR(255) NOT NULL,
    password           VARCHAR(255) NOT NULL,
    email              VARCHAR(255) NULL,
    phone              VARCHAR(255) NULL,
    profile_image_path VARCHAR(255) NULL,
    profile_message    VARCHAR(255) NULL,
    open               BIT(1)       NOT NULL,
    `role`             VARCHAR(255) NULL,
    created_at         datetime     NOT NULL,
    updated_at         datetime     NOT NULL,
    CONSTRAINT pk_member PRIMARY KEY (id)
);

CREATE TABLE post
(
    id         BIGINT AUTO_INCREMENT NOT NULL,
    member_id  BIGINT       NOT NULL,
    title      VARCHAR(255) NOT NULL,
    content    VARCHAR(255) NOT NULL,
    is_open    BIT(1)       NOT NULL,
    created_at datetime     NOT NULL,
    updated_at datetime     NOT NULL,
    CONSTRAINT pk_post PRIMARY KEY (id)
);

CREATE TABLE post_like
(
    id         BIGINT AUTO_INCREMENT NOT NULL,
    member_id  BIGINT   NOT NULL,
    post_id    BIGINT   NOT NULL,
    created_at datetime NOT NULL,
    CONSTRAINT pk_postlike PRIMARY KEY (id)
);

ALTER TABLE member
    ADD CONSTRAINT uc_member_email UNIQUE (email);

ALTER TABLE member
    ADD CONSTRAINT uc_member_phone UNIQUE (phone);

ALTER TABLE member
    ADD CONSTRAINT uc_member_username UNIQUE (username);

ALTER TABLE comment_like
    ADD CONSTRAINT FK_COMMENT_LIKE_COMMENT FOREIGN KEY (comment_id) REFERENCES comment (id);

ALTER TABLE comment_like
    ADD CONSTRAINT FK_COMMENT_LIKE_MEMBER FOREIGN KEY (member_id) REFERENCES member (id);

ALTER TABLE comment
    ADD CONSTRAINT FK_COMMENT_POST FOREIGN KEY (post_id) REFERENCES post (id);

ALTER TABLE follow
    ADD CONSTRAINT FK_FOLLOW_RECEIVER FOREIGN KEY (receiver_id) REFERENCES member (id);

ALTER TABLE follow
    ADD CONSTRAINT FK_FOLLOW_SENDER FOREIGN KEY (sender_id) REFERENCES member (id);

ALTER TABLE image
    ADD CONSTRAINT FK_IMAGE_POST FOREIGN KEY (post_id) REFERENCES post (id);

ALTER TABLE post_like
    ADD CONSTRAINT FK_POST_LIKE_MEMBER FOREIGN KEY (member_id) REFERENCES member (id);

ALTER TABLE post_like
    ADD CONSTRAINT FK_POST_LIKE_POST FOREIGN KEY (post_id) REFERENCES post (id);

ALTER TABLE post
    ADD CONSTRAINT FK_POST_MEMBER FOREIGN KEY (member_id) REFERENCES member (id);
