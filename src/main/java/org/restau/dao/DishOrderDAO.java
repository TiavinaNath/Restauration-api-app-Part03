package org.restau.dao;

import lombok.AllArgsConstructor;
import org.restau.db.DbConnection;
import org.restau.entity.DishOrder;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@AllArgsConstructor
public class DishOrderDAO {
    private DbConnection dbconnection;
    private DishDAO dishDAO;
    private DishOrderStatusHitstoryDAO dishOrderStatusHitstoryDAO;

    public DishOrderDAO() {
        this.dbconnection = new DbConnection();
        this.dishDAO = new DishDAO();
        this.dishOrderStatusHitstoryDAO = new DishOrderStatusHitstoryDAO();
    }

    public Optional<DishOrder> findById(Long id) {
        String sql = "select * from dish_order where id_dish_order = ?";
        DishOrder dishOrder = new DishOrder();

        try (Connection con = dbconnection.getConnection();
             PreparedStatement pstmt = con.prepareStatement(sql)) {

            pstmt.setLong(1, id);

            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    dishOrder.setIdDishOrder(rs.getLong("id_dish_order"));
                    dishOrder.setDish(dishDAO.findById(rs.getLong("id_dish")).orElseThrow());
                    dishOrder.setQuantity(rs.getDouble("quantity"));
                    dishOrder.setStatusHistory(dishOrderStatusHitstoryDAO.getOrderStatusHistoryByOrderId(rs.getLong("id_dish_order")));
                }
            }
        }
        catch (Exception ex) {
            ex.printStackTrace();
        }
        return Optional.of(dishOrder);
    }

        public DishOrder save(DishOrder dishOrder) {
            String sql = """
            INSERT INTO dish_order (id_dish_order, id_dish, quantity) 
            VALUES (?, ?, ?)
            ON CONFLICT (id_dish_order) DO UPDATE 
            SET id_dish = EXCLUDED.id_dish, quantity = EXCLUDED.quantity
            RETURNING id_dish_order;
        """;

            try (Connection con = dbconnection.getConnection();
                 PreparedStatement pstmt = con.prepareStatement(sql)) {

                pstmt.setObject(1, dishOrder.getIdDishOrder());
                pstmt.setLong(2, dishOrder.getDish().getIdDish());
                pstmt.setDouble(3, dishOrder.getQuantity());

                try (ResultSet rs = pstmt.executeQuery()) {
                    if (rs.next()) {
                        dishOrder.setIdDishOrder(rs.getLong("id_dish_order"));
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }

            return dishOrder;
        }

        public List<DishOrder> saveAll(List<DishOrder> dishOrders) {
            String sql = """
            INSERT INTO dish_order (id_dish_order, id_dish, quantity) 
            VALUES (?, ?, ?)
            ON CONFLICT (id_dish_order) DO UPDATE 
            SET id_dish = EXCLUDED.id_dish, quantity = EXCLUDED.quantity
            RETURNING id_dish_order;
        """;

            try (Connection con = dbconnection.getConnection();
                 PreparedStatement pstmt = con.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

                for (DishOrder dishOrder : dishOrders) {
                    pstmt.setObject(1, dishOrder.getIdDishOrder());
                    pstmt.setLong(2, dishOrder.getDish().getIdDish());
                    pstmt.setDouble(3, dishOrder.getQuantity());
                    pstmt.addBatch();
                }

                pstmt.executeBatch();

                try (ResultSet generatedKeys = pstmt.getGeneratedKeys()) {
                    int index = 0;
                    while (generatedKeys.next() && index < dishOrders.size()) {
                        dishOrders.get(index).setIdDishOrder(generatedKeys.getLong(1));
                        index++;
                    }
                }

            } catch (Exception e) {
                e.printStackTrace();
            }

            return dishOrders;
        }

    public void saveAll(List<DishOrder> dishOrders, Long idOrder) {
        String sql = """
        INSERT INTO dish_order (id_dish, quantity, id_order)
        VALUES (?, ?, ?)
        RETURNING id_dish_order;
    """;

        try (Connection con = dbconnection.getConnection();
             PreparedStatement pstmt = con.prepareStatement(sql)) {

            for (DishOrder dishOrder : dishOrders) {
                pstmt.setLong(1, dishOrder.getDish().getIdDish());
                pstmt.setDouble(2, dishOrder.getQuantity());
                pstmt.setLong(3, idOrder);

                pstmt.addBatch();
            }

            pstmt.executeBatch();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public List<DishOrder> getDishOrdersByOrderId(Long orderId) {
        List<DishOrder> dishOrders = new ArrayList<>();
        String sql = "SELECT * FROM dish_order WHERE id_order = ?";

        try (Connection con = dbconnection.getConnection();
             PreparedStatement pstmt = con.prepareStatement(sql)) {

            pstmt.setLong(1, orderId);

            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    DishOrder dishOrder = new DishOrder();
                    dishOrder.setIdDishOrder(rs.getLong("id_dish_order"));
                    dishOrder.setDish(dishDAO.findById(rs.getLong("id_dish")).orElseThrow());
                    dishOrder.setQuantity(rs.getDouble("quantity"));
                    dishOrder.setStatusHistory(dishOrderStatusHitstoryDAO.getOrderStatusHistoryByOrderId(rs.getLong("id_dish_order")));
                    dishOrders.add(dishOrder);
                }
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }

        return dishOrders;
    }

}


