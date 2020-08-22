CREATE TABLE Category
(
    id   INT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(32) NOT NULL UNIQUE
);

CREATE TABLE Product
(
    id     INT AUTO_INCREMENT PRIMARY KEY,
    name   VARCHAR(32) NOT NULL,
    description VARCHAR(255) NOT NULL,
    price DOUBLE NOT NULL,
    release_date DATE NOT NULL,
    category_id INT NOT NULL,

    CONSTRAINT fk_category
        FOREIGN KEY (category_id)
            REFERENCES Category (id)
);

CREATE TABLE User
(
    id   INT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(32) NOT NULL UNIQUE,
    password VARCHAR(255) NOT NULL,
    admin_permissions BOOLEAN NOT NULL
);

INSERT INTO Category(name)
VALUES ('footwear'), ('clothes');

INSERT INTO Product(name, description, price, release_date, category_id)
VALUES
('Core Red', 'Example of text', 100.2, parsedatetime('17-09-2018', 'dd-MM-yyyy'), 1),
('TUC', 'Example of text', 69.3, parsedatetime('6-11-2019', 'dd-MM-yyyy'), 2);

INSERT INTO User(name, password, admin_permissions)
VALUES ('admin', '$2a$10$sOl0xuyeqvtH/5SZTXwLLeJtB/nMulcJKoLPuoKqFevCgUTOIK9SC', true);
-- password = 'admin' for this user
