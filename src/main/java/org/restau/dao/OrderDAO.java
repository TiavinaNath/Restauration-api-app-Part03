package org.restau.dao;

import lombok.AllArgsConstructor;
import org.restau.db.DbConnection;
import org.restau.entity.Order;

import java.sql.*;
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

}