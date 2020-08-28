create table users (
	user_id SERIAL PRIMARY KEY NOT NULL,
	first_name VARCHAR(50),
	last_name VARCHAR(50),
	email VARCHAR(50) NOT NULL,
	username VARCHAR(50) NOT NULL,
	password VARCHAR(50) NOT NULL,
	ip_address VARCHAR(20)
)