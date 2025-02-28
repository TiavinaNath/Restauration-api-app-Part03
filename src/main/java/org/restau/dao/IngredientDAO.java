package org.restau.dao;

import lombok.AllArgsConstructor;
import org.restau.db.DbConnection;
import org.restau.entity.Ingredient;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@AllArgsConstructor

public class IngredientDAO {
    private DbConnection dbConnection;
    private StockMovementDAO stockMovementDAO;

    public IngredientDAO() {
        this.dbConnection = new DbConnection();
        this.stockMovementDAO = new StockMovementDAO();
    }

    public Optional<Ingredient> findById(Long id) {
        Ingredient ingredient = new Ingredient();

        String sql = """
                select i.id_ingredient, i.name, i.update_datetime, sm.id_stock_movement, sm.movement, sm.quantity, sm.unit, sm.movement_datetime 
                from Ingredient i
                join Stock_Movement sm on i.id_ingredient = sm.id_ingredient
                where i.id_ingredient = ?
                """;
        try (Connection con = dbConnection.getConnection();
             PreparedStatement pstmt = con.prepareStatement(sql)) {

            pstmt.setLong(1, id);

            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    ingredient.setIdIngredient(rs.getLong("id_ingredient"));
                    ingredient.setName(rs.getString("name"));
                    ingredient.setUpdateDatetime(rs.getTimestamp("update_datetime").toLocalDateTime());
                    ingredient.setStockMovements(stockMovementDAO.findStockMovementByIdIngredient(id));
                }
            }
        }
        catch (Exception ex) {
            ex.printStackTrace();
        }
        return Optional.of(ingredient);
    }




}
