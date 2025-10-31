CREATE TABLE users (
   id BIGSERIAL PRIMARY KEY,
   first_name VARCHAR(50) NOT NULL,
   second_name VARCHAR(50) NOT NULL,
   birth_date DATE,
   biography VARCHAR(100),
   city VARCHAR(100),
   password VARCHAR(100)
);