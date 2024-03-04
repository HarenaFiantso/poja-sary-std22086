CREATE TABLE IF NOT EXISTS image
(
    currency_id                UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    black_and_white_bucket_key VARCHAR(255),
    original_bucket_key        VARCHAR(255),
    creation_datetime          TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP NOT NULL,
    update_datetime            TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP NOT NULL
);
