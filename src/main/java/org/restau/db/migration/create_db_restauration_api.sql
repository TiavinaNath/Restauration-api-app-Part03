CREATE DATABASE restauration_api;

\c restauration_api;

CREATE TYPE unit AS ENUM ('G', 'L', 'U');

CREATE TABLE Dish (
        id_dish SERIAL PRIMARY KEY,
        name VARCHAR(255) NOT NULL,
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
        required_quantity NUMERIC(10,3) NOT NULL,
        unit unit NOT NULL,
        PRIMARY KEY (id_dish, id_ingredient),
        FOREIGN KEY (id_dish) REFERENCES Dish(id_dish) ON DELETE CASCADE,
        FOREIGN KEY (id_ingredient) REFERENCES Ingredient(id_ingredient) ON DELETE CASCADE
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
