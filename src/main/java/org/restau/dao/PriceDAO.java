package org.restau.dao;

import lombok.AllArgsConstructor;
import org.restau.db.DbConnection;
import org.restau.entity.Ingredient;
import org.restau.entity.Price;
import org.restau.entity.Unit;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@AllArgsConstructor
public class PriceDAO implements CrudDAO<Price> {
    private DbConnection dbConnection;

    public PriceDAO() {
        this.dbConnection = new DbConnection();
    }

    @Override
    public List<Price> getAllPaginated(int page, int size) {
        List<Price> prices = new ArrayList<>();
        String sql = """
            SELECT p.id_price, p.id_ingredient, p.date, p.unit_price, p.unit, i.name, i.update_datetime
            FROM price p
            JOIN ingredient i ON p.id_ingredient = i.id_ingredient
            LIMIT ? OFFSET ?
        """;

        try (Connection con = dbConnection.getConnection();
             PreparedStatement pstmt = con.prepareStatement(sql)) {

            pstmt.setInt(1, size);
            pstmt.setInt(2, page * size);

            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    prices.add(priceRsMapper(rs));
                }
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
        }

        return prices;
    }

    @Override
    public Optional<Price> findById(Long id) {
        Price price = null;
        String sql = """
            SELECT p.id_price, p.id_ingredient, p.date, p.unit_price, p.unit, i.name, i.update_datetime
            FROM price p
            JOIN ingredient i ON p.id_ingredient = i.id_ingredient
            WHERE p.id_price = ?
        """;

        try (Connection con = dbConnection.getConnection();
             PreparedStatement pstmt = con.prepareStatement(sql)) {

            pstmt.setLong(1, id);

            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    price = priceRsMapper(rs);
                }
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
        }

        return Optional.ofNullable(price);
    }

    @Override
    public Price save(Price entity) {
        String sql = """
            INSERT INTO price (id_ingredient, date, unit_price, unit)
            VALUES (?, ?, ?, ?)
            ON CONFLICT (id_ingredient, date) 
            DO UPDATE SET unit_price = EXCLUDED.unit_price, unit = EXCLUDED.unit
            RETURNING id_price
        """;

        try (Connection con = dbConnection.getConnection();
             PreparedStatement pstmt = con.prepareStatement(sql)) {

            pstmt.setLong(1, entity.getIngredient().getIdIngredient());
            pstmt.setDate(2, Date.valueOf(entity.getDate()));
            pstmt.setDouble(3, entity.getUnitPrice());
            pstmt.setString(4, entity.getUnit().name());

            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    entity.setIdPrice(rs.getLong("id_price"));
                }
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
        }

        return entity;
    }

    @Override
    public List<Price> saveAll(List<Price> entities) {
        for (Price price : entities) {
            save(price);
        }
        return entities;
    }

    @Override
    public boolean delete(Long id) {
        String sql = "DELETE FROM price WHERE id_price = ?";
        try (Connection con = dbConnection.getConnection();
             PreparedStatement pstmt = con.prepareStatement(sql)) {

            pstmt.setLong(1, id);
            return pstmt.executeUpdate() > 0;
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
        return false;
    }

    private Price priceRsMapper(ResultSet rs) throws SQLException {
        Ingredient ingredient = new Ingredient();
        ingredient.setIdIngredient(rs.getLong("id_ingredient"));
        ingredient.setName(rs.getString("name"));
        ingredient.setUpdateDatetime(rs.getTimestamp("update_datetime").toLocalDateTime());

        Price price = new Price();
        price.setIdPrice(rs.getLong("id_price"));
        price.setIngredient(ingredient);
        price.setDate(rs.getDate("date").toLocalDate());
        price.setUnitPrice(rs.getDouble("unit_price"));
        price.setUnit(Unit.valueOf(rs.getString("unit")));

        return price;
    }
}
