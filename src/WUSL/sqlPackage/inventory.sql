CREATE TABLE customerInformation(
cId VARCHAR(10) NOT NULL,
cName VARCHAR(100),
cAddress VARCHAR(100),
CONSTRAINT PRIMARY KEY(cId)
);

CREATE TABLE itemInfo(
itemCode VARCHAR(10) NOT NULL,
description VARCHAR(100),
unitPrice DOUBLE,
qty INT,
CONSTRAINT PRIMARY KEY(itemCode)
);

CREATE TABLE orderInfo(
oId INT ,
custId VARCHAR (10),
orderDate DATE,
CONSTRAINT PRIMARY KEY(oId),
CONSTRAINT FOREIGN KEY (custId) REFERENCES customerinformation(cId)
);

CREATE TABLE orderDetails(
orderId INT,
iCode VARCHAR(10) NOT NULL,
descrptn VARCHAR(100),
price DOUBLE,
qunty INT,
netTot DOUBLE ,
CONSTRAINT FOREIGN KEY(orderId) REFERENCES orderinfo(oId),
CONSTRAINT FOREIGN KEY(iCode) REFERENCES iteminfo(itemCode)
);
drop table orderdetails
drop table orderinfo;

INSERT INTO orderInfo VALUES ('1','1','2018-11-15');

SELECT * FROM orderdetails;
SELECT * FROM orderinfo;
SELECT * FROM iteminfo;
SELECT * FROM customerinformation;

ALTER TABLE orderdetails ADD COLUMN cusID VARCHAR (10);
ALTER TABLE orderDetails DROP COLUMN cusID;
ALTER TABLE orderinfo MODIFY COLUMN oId int ;

 TRUNCATE TABLE orderinfo;
 TRUNCATE TABLE orderdetails;

 DESC orderinfo;
INSERT INTO orderdetails VALUES ('1','3','soap','10','2','20');

SELECT COUNT(oId) FROM orderinfo