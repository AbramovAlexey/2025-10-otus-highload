CREATE TABLE posts (
   id BIGSERIAL PRIMARY KEY,
   content TEXT NOT NULL,
   author_user_id INT
);