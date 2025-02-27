INSERT INTO Ingredient (name, update_datetime) VALUES
('Saucisse', CURRENT_TIMESTAMP),
('Huile', CURRENT_TIMESTAMP),
('Oeuf', CURRENT_TIMESTAMP),
('Pain', CURRENT_TIMESTAMP);

INSERT INTO Price (id_ingredient, date, unit_price, unit) VALUES
(1, CURRENT_DATE, 20, 'G'),     -- Saucisse
(2, CURRENT_DATE, 10000, 'L'),  -- Huile
(3, CURRENT_DATE, 1000, 'U'),   -- Oeuf
(4, CURRENT_DATE, 1000, 'U');   -- Pain

INSERT INTO Dish (name, unit_price) VALUES
('Hot Dog', 15000);

INSERT INTO Dish_Ingredient (id_dish, id_ingredient, id_price, required_quantity, unit) VALUES
(1, 1, 1, 100, 'G'),   -- 100g de Saucisse
(1, 2, 2, 0.15, 'L'),  -- 0.15L d'Huile
(1, 3, 3, 1, 'U'),     -- 1 Oeuf
(1, 4, 4, 1, 'U');     -- 1 Pain