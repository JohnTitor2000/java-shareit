DROP TABLE IF EXISTS users, items, booking, comments, requests CASCADE;

CREATE TABLE IF NOT EXISTS users (
  id BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
  name VARCHAR(255) NOT NULL,
  email VARCHAR(512) NOT NULL,
  CONSTRAINT UQ_USER_EMAIL UNIQUE (email)
);

CREATE TABLE IF NOT EXISTS requests (
  id BIGINT GENERATED BY DEFAULT AS IDENTITY NOT NULL,
  description VARCHAR(255) NOT NULL,
  created timestamp NOT NULL,
  requester_id BIGINT NOT NULL,
  CONSTRAINT pk_requests PRIMARY KEY (id),
  CONSTRAINT fk_requester_user_id FOREIGN KEY (requester_id) REFERENCES users(id)
);

CREATE TABLE IF NOT EXISTS items (
  id BIGINT GENERATED BY DEFAULT AS IDENTITY NOT NULL,
  name VARCHAR(255) NOT NULL,
  description VARCHAR(512) NOT NULL,
  available boolean NOT NULL,
  user_id BIGINT NOT NULL,
  request_id BIGINT,
  CONSTRAINT pk_item PRIMARY KEY (id),
  CONSTRAINT fk_user_id FOREIGN KEY (user_id) REFERENCES users(id),
  CONSTRAINT fk_request_id FOREIGN KEY (request_id) REFERENCES requests(id)
);

CREATE TABLE IF NOT EXISTS booking (
    id BIGINT GENERATED BY DEFAULT AS IDENTITY NOT NULL,
    start_time timestamp NOT NULL,
    end_time timestamp NOT NULL,
    item_id BIGINT NOT NULL,
    booker_id BIGINT NOT NULL,
    status VARCHAR(80) NOT NULL,
    CONSTRAINT pk_booking PRIMARY KEY (id),
    CONSTRAINT fk_item_id FOREIGN KEY (item_id) REFERENCES items(id),
    CONSTRAINT fk_booker_id FOREIGN KEY (booker_id) REFERENCES users(id)
);

CREATE TABLE IF NOT EXISTS comments (
  id BIGINT GENERATED BY DEFAULT AS IDENTITY NOT NULL,
  text VARCHAR(255) NOT NULL,
  created timestamp NOT NULL,
  user_id BIGINT NOT NULL,
  item_id BIGINT NOT NULL,
  CONSTRAINT pk_comments PRIMARY KEY (id),
  CONSTRAINT fk_comments_user_id FOREIGN KEY (user_id) REFERENCES users(id),
  CONSTRAINT fk_comments_item_id FOREIGN KEY (item_id) REFERENCES items(id)
);