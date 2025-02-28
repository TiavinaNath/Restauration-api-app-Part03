package org.restau.dao;

import lombok.AllArgsConstructor;
import org.restau.db.DbConnection;
import org.restau.entity.Ingredient;
import org.restau.entity.Movement;
import org.restau.entity.StockMovement;
import org.restau.entity.Unit;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@AllArgsConstructor

public class StockMovementDAO {
    private DbConnection dbConnection;

    public StockMovementDAO() {
        this.dbConnection = new DbConnection();
    }

    public List<StockMovement> getAll() {
        String sql = """
                 select sm.id_stock_movement, sm.id_ingredient, sm.movement, sm.quantity, sm.unit, sm.movement_datetime, i.name, i.update_datetime 
                 from stock_movement sm 
                 join ingredient i on sm.id_ingredient = i.id_ingredient 
                """;
        List<StockMovement> stockMovements = new ArrayList<>();

        try (Connection con = dbConnection.getConnection();
             PreparedStatement pstmt = con.prepareStatement(sql);
             ResultSet rs = pstmt.executeQuery()) {

            while (rs.next()) {
                StockMovement stockMovement = stockMovementMapper(rs);
                stockMovements.add(stockMovement);
            }

        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return stockMovements;
    }

    public Optional<StockMovement> findById(Long id) {
        String sql = """
                 select sm.id_stock_movement, sm.id_ingredient, sm.movement, sm.quantity, sm.unit, sm.movement_datetime, i.name, i.update_datetime 
                 from stock_movement sm 
                 join ingredient i on sm.id_ingredient = i.id_ingredient
                 where sm.id_stock_movement = ? 
                """;
        Optional<StockMovement> stockMovement = Optional.empty();

        try (Connection con = dbConnection.getConnection();
             PreparedStatement pstmt = con.prepareStatement(sql)) {

            pstmt.setLong(1, id);

            try(ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    stockMovement = Optional.of(stockMovementMapper(rs));
                }
            }

        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return stockMovement;
    }

    public List<StockMovement> findStockMovementByIdIngredient(Long idIngredient) {
        String sql = """
                 select sm.id_stock_movement, sm.id_ingredient, sm.movement, sm.quantity, sm.unit, sm.movement_datetime, i.name, i.update_datetime 
                 from stock_movement sm 
                 join ingredient i on sm.id_ingredient = i.id_ingredient
                 where sm.id_ingredient = ? 
                """;
        List<StockMovement> stockMovements = new ArrayList<>();

        try (Connection con = dbConnection.getConnection();
             PreparedStatement pstmt = con.prepareStatement(sql)) {

            pstmt.setLong(1, idIngredient);

            try(ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    StockMovement stockMovement = stockMovementMapper(rs);
                    stockMovements.add(stockMovement);
                }
            }

        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return stockMovements;
    }

    public StockMovement save (StockMovement stockMovement) {

        String sql = """
                insert into Stock_Movement (id_ingredient, movement, quantity, unit, movement_datetime) values (?,?,?,?,?) returning id_stock_movement
                """;

        try (Connection con = dbConnection.getConnection();
            PreparedStatement pstmt = con.prepareStatement(sql)){

            pstmt.setLong(1, stockMovement.getIdIngredient());
            pstmt.setObject(2, String.valueOf(stockMovement.getMovement()), java.sql.Types.OTHER);
            pstmt.setBigDecimal(3, stockMovement.getQuantity());
            pstmt.setObject(4, String.valueOf(stockMovement.getUnit()), java.sql.Types.OTHER);
            pstmt.setTimestamp(5, Timestamp.valueOf(stockMovement.getMovementDatetime()));

            try (ResultSet rs = pstmt.executeQuery()) {

                if (rs.next()) {
                    stockMovement = new StockMovement(
                            rs.getLong("id_stock_movement"),
                            stockMovement.getIdIngredient(),
                            stockMovement.getMovement(),
                            stockMovement.getQuantity(),
                            stockMovement.getUnit(),
                            stockMovement.getMovementDatetime()
                    );
                }

            }
        }
        catch (Exception ex) {
            ex.printStackTrace();
        }

        return stockMovement;
    }


    public StockMovement stockMovementMapper (ResultSet rs) throws SQLException {
       /* Ingredient ingredient = new Ingredient();
        ingredient.setIdIngredient(rs.getLong("id_ingredient"));
        ingredient.setName(rs.getString("name"));
        ingredient.setUpdateDatetime(rs.getTimestamp("update_datetime").toLocalDateTime());*/

        StockMovement stockMovement = new StockMovement(
                rs.getLong("id_stock_movement"),
                rs.getLong("id_ingredient"),
                Movement.valueOf(rs.getString("movement")),
                rs.getBigDecimal("quantity"),
                Unit.valueOf(rs.getString("unit")),
                rs.getTimestamp("movement_datetime").toLocalDateTime()
        );
        /*
        stockMovement.setIdStockMovement(rs.getLong("id_stock_movement"));
        stockMovement.setIngredient(ingredient);
        stockMovement.setMovement(Movement.valueOf(rs.getString("movement")));
        stockMovement.setQuantity(rs.getBigDecimal("quantity"));
        stockMovement.setUnit(Unit.valueOf(rs.getString("unit")));
        stockMovement.setMovementDatetime(rs.getTimestamp("movement_datetime").toLocalDateTime());*/

        return stockMovement;
    }
}


