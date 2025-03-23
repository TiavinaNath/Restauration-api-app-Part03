package org.restau.dao;

import lombok.AllArgsConstructor;
import org.restau.db.DbConnection;
import org.restau.entity.DishOrder;
import org.restau.entity.DishOrderStatusHistory;
import org.restau.entity.OrderStatusHistory;
import org.restau.entity.StatusOrder;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Timestamp;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

import static java.sql.Types.OTHER;

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

    public void saveAll(List<OrderStatusHistory> statusHistories, Long orderId) {
        String sql = """
        INSERT INTO order_status_history (id_order, status, creation_date_time)
        VALUES (?, ?, ?)
    """;

        List<OrderStatusHistory> existingStatuses = getOrderStatusHistoryByOrderId(orderId);

        try (Connection con = dbConnection.getConnection();
             PreparedStatement pstmt = con.prepareStatement(sql)) {

            for (OrderStatusHistory status : statusHistories) {

                if (existingStatuses.contains(status)) {
                    continue;
                }

                pstmt.setLong(1, orderId);
                pstmt.setObject(2, status.getStatus(), OTHER);
                pstmt.setTimestamp(3, Timestamp.from(status.getCreationDateTime()==null? Instant.now() : status.getCreationDateTime()));

                pstmt.addBatch();
            }
            pstmt.executeBatch();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
