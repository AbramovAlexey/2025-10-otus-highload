CREATE TABLE users (
   id BIGSERIAL PRIMARY KEY,
   first_name VARCHAR(50) NOT NULL,
   second_name VARCHAR(50) NOT NULL,
   birth_date DATE,
   biography VARCHAR(100),
   city VARCHAR(100),
   password VARCHAR(100)
);

CREATE TABLE messages (
    id BIGSERIAL,
    sender BIGINT NOT NULL REFERENCES users(id),
    recipient BIGINT NOT NULL REFERENCES users(id),
    conversation_id VARCHAR NOT NULL,
    content TEXT NOT NULL,
    sent_at TIMESTAMP WITH TIME ZONE DEFAULT NOW(),
    PRIMARY KEY (id, conversation_id)
);
