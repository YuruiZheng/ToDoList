CREATE TABLE board
(
    id          SERIAL PRIMARY KEY,
    name        VARCHAR(63),
    password    VARCHAR(127),
    background  INTEGER,
    last_Update BIGINT
);

CREATE TABLE list
(
    id         SERIAL PRIMARY KEY,
    board_id   INTEGER,
    title      VARCHAR(63),
    background INTEGER,
    FOREIGN KEY (board_id) REFERENCES board
        ON DELETE CASCADE
        ON UPDATE CASCADE
);

CREATE TABLE tag
(
    id       SERIAL PRIMARY KEY,
    board_id INTEGER,
    title    VARCHAR(63),
    color    INTEGER,
    FOREIGN KEY (board_id) REFERENCES board
        ON DELETE CASCADE
        ON UPDATE CASCADE
);



CREATE TABLE card
(

    id          SERIAL PRIMARY KEY,
    list_id     INTEGER,
    title       VARCHAR(63),
    description VARCHAR(1000),
    background  INTEGER,
    FOREIGN KEY (list_id) REFERENCES list
        ON DELETE CASCADE
        ON UPDATE CASCADE
);

CREATE TABLE card_tag
(
    id      SERIAL PRIMARY KEY,
    card_id INTEGER,
    tag_id  INTEGER,
    FOREIGN KEY (card_id) REFERENCES card
        ON DELETE CASCADE,
    FOREIGN KEY (tag_id) REFERENCES tag
        ON DELETE CASCADE
);

CREATE TABLE subtask
(
    id      SERIAL PRIMARY KEY,
    card_id INTEGER,
    title   VARCHAR(20),
    status  BOOLEAN,
    FOREIGN KEY (card_id) REFERENCES card
        ON DELETE CASCADE
);

ALTER TABLE card
    ADD
        priority INTEGER;

ALTER TABLE BOARD ALTER COLUMN background varchar(6);
ALTER TABLE CARD ALTER COLUMN background varchar(6);
ALTER TABLE LIST ALTER COLUMN background varchar(6);
ALTER TABLE TAG ALTER COLUMN color varchar(6);

ALTER TABLE subtask ADD priority INTEGER;
ALTER TABLE BOARD ADD font varchar(6);
ALTER TABLE LIST ADD font varchar(6);

ALTER TABLE BOARD ADD listBG varchar(6);
ALTER TABLE BOARD ADD listF varchar(6);
ALTER TABLE LIST DROP COLUMN background;
ALTER TABLE LIST DROP COLUMN font;

CREATE TABLE color_preset (
    id SERIAL   PRIMARY KEY,
    board_id    INTEGER,
    name        varchar(20),
    background  varchar(6),
    font        varchar(6),
    FOREIGN KEY (board_id) REFERENCES board
        ON DELETE CASCADE
);

ALTER TABLE board ADD default_preset INTEGER;
ALTER TABLE board ADD FOREIGN KEY(default_preset)
    REFERENCES color_preset(id) ON DELETE SET NULL;

ALTER TABLE card ADD preset INTEGER;
ALTER TABLE card ADD FOREIGN KEY(preset)
    REFERENCES color_preset(id) ON DELETE SET NULL;


