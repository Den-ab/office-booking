CREATE TABLE IF NOT EXISTS app_user (
    id UUID NOT NULL,
    email TEXT NOT NULL,
    first_name TEXT NOT NULL,
    last_name TEXT NOT NULL,
    password TEXT NOT NULL,
    role TEXT NOT NULL,
    CONSTRAINT "pk_app_user" PRIMARY KEY ("id")
);

CREATE TABLE IF NOT EXISTS area (
    id UUID NOT NULL,
    name TEXT NOT NULL,
    CONSTRAINT "pk_area" PRIMARY KEY ("id")
);

CREATE TABLE IF NOT EXISTS desk (
    id UUID NOT NULL,
    number INTEGER NOT NULL,
    area_id UUID NOT NULL,
    CONSTRAINT "pk_desk" PRIMARY KEY ("id")
);

CREATE TABLE IF NOT EXISTS booking (
    id UUID NOT NULL,
    user_id UUID NOT NULL,
    desk_id UUID NOT NULL,
    date TIMESTAMP NOT NULL,
    CONSTRAINT "pk_booking" PRIMARY KEY ("id")
);

-- Связи
ALTER TABLE IF EXISTS desk
    ADD CONSTRAINT fk_desk__area_id
        FOREIGN KEY (area_id) REFERENCES area (id)
            ON DELETE RESTRICT ON UPDATE RESTRICT;
ALTER TABLE IF EXISTS booking
    ADD CONSTRAINT fk_booking__app_user_id
        FOREIGN KEY (user_id) REFERENCES app_user (id)
            ON DELETE RESTRICT ON UPDATE RESTRICT;
ALTER TABLE IF EXISTS booking
    ADD CONSTRAINT fk_booking__desk_id
        FOREIGN KEY (desk_id) REFERENCES desk (id)
            ON DELETE RESTRICT ON UPDATE RESTRICT;
