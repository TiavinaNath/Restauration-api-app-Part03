package org.restau.dao;

import lombok.AllArgsConstructor;
import org.restau.db.DbConnection;
import org.restau.entity.DishOrder;
import org.restau.entity.Order;
import org.restau.entity.OrderStatusHistory;
import org.restau.entity.StatusOrder;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@AllArgsConstructor
public class OrderDAO {
    private DbConnection dbconnection;
    private DishOrderDAO dishOrderDAO;
    private OrderStatusHitstoryDAO orderStatusHitstoryDAO;


    public OrderDAO() {
        this.dbconnection = new DbConnection();
        this.dishOrderDAO = new DishOrderDAO();
        this.orderStatusHitstoryDAO = new OrderStatusHitstoryDAO();
    }

/*    public void save(Order order) throws SQLException {
        String sql = "INSERT INTO \"order\" (reference, creation_date_time) VALUES (?, ?) RETURNING id_order";
        try (Connection con = dbconnection.getConnection();
             PreparedStatement pstmt = con.prepareStatement(sql)) {

            pstmt.setString(1, order.getReference());
            pstmt.setTimestamp(2, Timestamp.from(order.getCreationDatetime()));
            pstmt.executeUpdate();

        }
    }

    public void updateStatus(Long orderId, StatusOrder status) throws SQLException {
        String sql = "INSERT INTO order_status_history (id_order, status, creation_date_time) VALUES (?, ?, ?)";
        try (Connection con = dbconnection.getConnection();
             PreparedStatement pstmt = con.prepareStatement(sql)){
            pstmt.setLong(1, orderId);
            pstmt.setString(2, status.name());
            pstmt.setTimestamp(3, Timestamp.from(Instant.now()));
            pstmt.executeUpdate();
        }
    }*/

    public Optional<Order> findById(Long id) {
        String sql = "SELECT * FROM \"order\" WHERE id_order = ?";  // Query to find an order by ID
        Order order = new Order();

        try (Connection con = dbconnection.getConnection();
             PreparedStatement pstmt = con.prepareStatement(sql)) {

            pstmt.setLong(1, id);  // Set the ID parameter

            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    order.setIdOrder(rs.getLong("id_order"));
                    order.setReference(rs.getString("reference"));
                    order.setCreationDatetime(rs.getTimestamp("creation_date_time").toInstant());
                    order.setDishOrders(dishOrderDAO.getDishOrdersByOrderId(id));
                    order.setStatusHistory(orderStatusHitstoryDAO.getOrderStatusHistoryByOrderId(id));
                }
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }

        return Optional.of(order);
    }

    public Order save(Order order) {
        String sqlInsert = """
        INSERT INTO "order" (reference, creation_date_time)
        VALUES (?, ?)
        RETURNING id_order;
    """;

        String sqlUpsert = """
        INSERT INTO "order" (id_order, reference, creation_date_time)
        VALUES (?, ?, ?)
        ON CONFLICT (id_order) DO UPDATE 
        SET reference = EXCLUDED.reference, creation_date_time = EXCLUDED.creation_date_time
        RETURNING id_order;
    """;

        String sql = (order.getIdOrder() == null) ? sqlInsert : sqlUpsert;

        try (Connection con = dbconnection.getConnection();
             PreparedStatement pstmt = con.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            if (order.getIdOrder() == null) {
                pstmt.setString(1, order.getReference());
                pstmt.setTimestamp(2, Timestamp.from(order.getCreationDatetime()));
            } else {
                pstmt.setLong(1, order.getIdOrder());
                pstmt.setString(2, order.getReference());
                pstmt.setTimestamp(3, Timestamp.from(order.getCreationDatetime()));
            }



            pstmt.executeUpdate();

            try (ResultSet rs = pstmt.getGeneratedKeys()) {
                if (rs.next()) {
                    order.setIdOrder(rs.getLong("id_order"));
                }
            }
            dishOrderDAO.saveAll(order.getDishOrders(), order.getIdOrder());
            orderStatusHitstoryDAO.saveAll(order.getStatusHistory(), order.getIdOrder());
           /* if (order.getStatusHistory().isEmpty()) {
                orderStatusHitstoryDAO.saveAll( List.of(
                        new OrderStatusHistory(StatusOrder.CREATED)
                ), order.getIdOrder());
            }*/

        } catch (Exception e) {
            e.printStackTrace();
        }
        return order;
    }

    public List<Order> saveAll(List<Order> orders) {
        List<Order> ordersSaved = new ArrayList<>();
        for (Order order: orders) {
            Order orderSaved = save(order);
            ordersSaved.add(findById(orderSaved.getIdOrder()).orElseThrow());
        }
        return ordersSaved;
    }
}