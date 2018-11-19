CREATE TABLE RENTER(ID INT PRIMARY KEY AUTO_INCREMENT,
   NAME VARCHAR(30) NOT NULL,
  SURNAME VARCHAR(30) NOT NULL,
  PATRONYMIC VARCHAR(30) NOT NULL,
ADDRESS  VARCHAR(255) NOT NULL,
BIRTHDAY VARCHAR(30) NOT NULL,
PERSON  VARCHAR(30) NOT NULL
);

DROP TABLE IF EXISTS RENTER;

CREATE TABLE RENTER(ID INT PRIMARY KEY AUTO_INCREMENT,
   NAME VARCHAR(30) NOT NULL
);

CREATE TABLE BUILDING(ID INT PRIMARY KEY AUTO_INCREMENT,
   "TYPE" VARCHAR(30) NOT NULL,
  SQUARE FLOAT NOT NULL,
  cost_balance FLOAT NOT NULL,
cost_residue  FLOAT NOT NULL
);

UPDATE RENTER
SET name=?, surname=?,name=?, patronymic=?, address=?, birthday=?, person=?
WHERE id=?;

CREATE TABLE CONTRACT(ID INT PRIMARY KEY AUTO_INCREMENT,
  date_start date NOT NULL,
  date_end date NOT NULL,
  id_renter int NOT NULL,
  id_building int NOT NULL,
  FOREIGN KEY (id_renter) REFERENCES RENTER(ID),
  FOREIGN KEY (id_building) REFERENCES BUILDING(ID)
);

INSERT INTO CONTRACT VALUES(null, '2018-11-01', '2019-11-01', 2, 2);

DROP TABLE IF EXISTS CONTRACT;

SELECT * FROM CONTRACT,RENTER, BUILDING WHERE CONTRACT.id=1 AND CONTRACT.id_renter=RENTER.id AND CONTRACT.id_building=BUILDING.id;

CREATE TABLE MONTH(ID INT PRIMARY KEY AUTO_INCREMENT,
  number int NOT NULL,
  date date NOT NULL,
  cost double NOT NULL,
  fine double NOT NULL,
  cost_water double NOT NULL,
  cost_electricity double NOT NULL,
  cost_heading double NOT NULL,
  paid_rent boolean NOT NULL,
  paid_communal boolean NOT NULL,
  id_contract int NOT NULL,
  index_water double NOT NULL,
  index_electricity double NOT NULL,
  index_heading double NOT NULL,
  FOREIGN KEY (id_contract) REFERENCES CONTRACT(ID)
);