CREATE TABLE posts (
   id BIGSERIAL PRIMARY KEY,
   content TEXT NOT NULL,
   author_user_id INT NOT NULL REFERENCES users(id),
   created_at TIMESTAMP WITH TIME ZONE DEFAULT NOW()
);

CREATE TABLE feed_materialize (
   id BIGSERIAL PRIMARY KEY,
   post_id BIGINT NOT NULL REFERENCES posts(id),
   content TEXT NOT NULL,
   subscriber_id BIGINT NOT NULL REFERENCES users(id),
   author_id BIGINT NOT NULL REFERENCES users(id)
);

CREATE TABLE subscribers (
    author_id INT NOT NULL REFERENCES users(id),
    subscriber_id INT NOT NULL REFERENCES users(id)
);

ALTER TABLE users ADD COLUMN is_seleb BOOLEAN;