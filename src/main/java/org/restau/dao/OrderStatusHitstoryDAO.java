package org.restau.dao;

import lombok.AllArgsConstructor;
import org.restau.db.DbConnection;
import org.restau.entity.OrderStatusHistory;
import org.restau.entity.StatusOrder;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

@AllArgsConstructor
public class OrderStatusHitstoryDAO {
    private DbConnection dbConnection;

    public OrderStatusHitstoryDAO() {
        this.dbConnection = new DbConnection();
    }

    public List<OrderStatusHistory> getOrderStatusHistoryByOrderId(Long orderId) {
        List<OrderStatusHistory> statusHistory = new ArrayList<>();
        String sql = "SELECT * FROM order_status_history WHERE id_order = ?";

        try (Connection con = dbConnection.getConnection();
             PreparedStatement pstmt = con.prepareStatement(sql)) {

            pstmt.setLong(1, orderId);

            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    OrderStatusHistory status = new OrderStatusHistory();
                    status.setIdOrderStatus(rs.getLong("id_order_status"));
                    status.setStatus(StatusOrder.valueOf(rs.getString("status")));
                    status.setCreationDateTime(rs.getTimestamp("creation_date_time").toInstant());
                    statusHistory.add(status);
                }
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }

        return statusHistory;
    }

}
