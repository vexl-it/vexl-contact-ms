CREATE TABLE users (
  id BIGSERIAL primary key NOT NULL,
  public_key bytea NOT NULL,
  hash bytea NOT NULL
);

CREATE TABLE user_contact (
  id BIGSERIAL primary key NOT NULL,
  hash_from bytea NOT NULL,
  hash_to bytea NOT NULL
);