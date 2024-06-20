CREATE DATABASE IF NOT EXISTS RailwayReservation;
use RailwayReservation;

CREATE TABLE IF NOT exists passengers (
      id INT AUTO_INCREMENT PRIMARY KEY,
      name VARCHAR(255) NOT NULL,
      age INT NOT NULL,
      berth_preference CHAR(1),
      status VARCHAR(10)
);

CREATE TABLE IF NOT exists rac_passengers (
      id INT AUTO_INCREMENT PRIMARY KEY,
      name VARCHAR(255) NOT NULL,
      age INT NOT NULL,
      berth_preference CHAR(1),
      status VARCHAR(10)
);

CREATE TABLE IF NOT exists waiting_list (
    id INT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    age INT NOT NULL,
    berth_preference CHAR(1),
    status VARCHAR(10)
);

DELETE FROM Passengers;