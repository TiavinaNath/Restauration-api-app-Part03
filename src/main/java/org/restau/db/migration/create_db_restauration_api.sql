CREATE DATABASE restauration_api4;

\c restauration_api4;

CREATE TYPE unit AS ENUM ('G', 'L', 'U');
CREATE TYPE movement AS ENUM ('IN', 'OUT');

CREATE TABLE Dish (
        id_dish SERIAL PRIMARY KEY,
        name VARCHAR(255) UNIQUE NOT NULL,
        unit_price NUMERIC(10,2) NOT NULL
);

CREATE TABLE Ingredient (
        id_ingredient SERIAL PRIMARY KEY,
        name VARCHAR(255) UNIQUE NOT NULL,
        update_datetime TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE Price (
        id_price SERIAL PRIMARY KEY,
        id_ingredient INT NOT NULL,
        date DATE NOT NULL,
        UNIQUE (id_ingredient, date),
        unit_price NUMERIC(10,2) NOT NULL,
        unit unit NOT NULL,
        FOREIGN KEY (id_ingredient) REFERENCES Ingredient(id_ingredient) ON DELETE CASCADE
);

CREATE TABLE Dish_Ingredient (
        id_dish INT NOT NULL,
        id_ingredient INT NOT NULL,
        id_price INT NOT NULL,
        required_quantity NUMERIC(10,3) NOT NULL,
        unit unit NOT NULL,
        PRIMARY KEY (id_dish, id_ingredient),
        FOREIGN KEY (id_dish) REFERENCES Dish(id_dish) ON DELETE CASCADE,
        FOREIGN KEY (id_ingredient) REFERENCES Ingredient(id_ingredient) ON DELETE CASCADE,
        FOREIGN KEY (id_price) REFERENCES Price(id_price) ON DELETE CASCADE
);

CREATE TABLE Stock_Movement (
        id_stock_movement SERIAL PRIMARY KEY,
        id_ingredient INT NOT NULL,
        movement movement NOT NULL,
        quantity NUMERIC(10,3) NOT NULL,
        unit unit NOT NULL,
        movement_datetime TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
        FOREIGN KEY (id_ingredient) REFERENCES Ingredient(id_ingredient) ON DELETE CASCADE
);


CREATE TYPE status_order AS ENUM ('CREATED', 'CONFIRMED', 'IN_PREPARATION', 'COMPLETED', 'SERVED');
CREATE TYPE status_dish_order AS ENUM ('CREATED', 'CONFIRMED', 'IN_PREPARATION', 'COMPLETED', 'SERVED');

-- Table Order
CREATE TABLE "order" (
    id_order SERIAL PRIMARY KEY,
    reference VARCHAR(50) UNIQUE NOT NULL,
    creation_date_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP NOT NULL
);

-- Table OrderStatusHistory
CREATE TABLE order_status_history (
    id_order_status SERIAL PRIMARY KEY,
    id_order INT NOT NULL,
    status status_order NOT NULL,
    creation_date_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP NOT NULL,
    FOREIGN KEY (id_order) REFERENCES "order" (id_order) ON DELETE CASCADE
);

-- Table DishOrder
CREATE TABLE dish_order (
   id_dish_order SERIAL PRIMARY KEY,
   id_order INT NOT NULL,
   id_dish INT NOT NULL,
   quantity INT NOT NULL CHECK (quantity > 0),
   FOREIGN KEY (id_order) REFERENCES "order" (id_order) ON DELETE CASCADE,
   FOREIGN KEY (id_dish) REFERENCES dish (id_dish) ON DELETE CASCADE
);

-- Table DishOrderStatusHistory
CREATE TABLE dish_order_status_history (
     id_dish_order_status SERIAL PRIMARY KEY,
     id_dish_order INT NOT NULL,
     status status_dish_order NOT NULL,
     creation_date_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
     FOREIGN KEY (id_dish_order) REFERENCES dish_order(id_dish_order) ON DELETE CASCADE
);












CREATE FUNCTION update_timestamp()
    RETURNS TRIGGER AS $$
BEGIN
    IF NEW.update_datetime IS NULL THEN
        NEW.update_datetime = CURRENT_TIMESTAMP;
END IF;
RETURN NEW;
END;
$$ LANGUAGE plpgsql;

CREATE TRIGGER update_ingredient_timestamp
    BEFORE UPDATE ON Ingredient
    FOR EACH ROW
    EXECUTE FUNCTION update_timestamp();

CREATE OR REPLACE FUNCTION update_ingredient_timestamp_on_price()
RETURNS TRIGGER AS $$
BEGIN
UPDATE Ingredient
SET update_datetime = CURRENT_TIMESTAMP
WHERE id_ingredient = NEW.id_ingredient;
RETURN NEW;
END;
$$ LANGUAGE plpgsql;

CREATE TRIGGER trg_update_ingredient_on_price_insert
    AFTER INSERT ON Price
    FOR EACH ROW
    EXECUTE FUNCTION update_ingredient_timestamp_on_price();


/*
CREATE OR REPLACE FUNCTION update_current_price()
RETURNS TRIGGER AS $$
BEGIN
UPDATE Ingredient
SET current_unit_price = NEW.unit_price,
    unit = NEW.unit
WHERE id_ingredient = NEW.id_ingredient;
RETURN NEW;
END;
$$ LANGUAGE plpgsql;

CREATE TRIGGER trg_update_current_price
    AFTER INSERT ON Price
    FOR EACH ROW EXECUTE FUNCTION update_current_price();
*/
