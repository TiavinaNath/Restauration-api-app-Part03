package org.restau.dao;


import lombok.AllArgsConstructor;
import org.restau.db.DbConnection;
import org.restau.entity.DishOrderStatusHistory;
import org.restau.entity.StatusDishOrder;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

@AllArgsConstructor
public class DishOrderStatusHitstoryDAO {

    private DbConnection dbConnection;

    public DishOrderStatusHitstoryDAO() {
        this.dbConnection = new DbConnection();
    }

    public List<DishOrderStatusHistory> getOrderStatusHistoryByOrderId(Long orderId) {
        List<DishOrderStatusHistory> dishOrderStatusHistories = new ArrayList<>();
        String sql = "SELECT * FROM order_status_history WHERE id_order = ?";

        try (Connection con = dbConnection.getConnection();
             PreparedStatement pstmt = con.prepareStatement(sql)) {

            pstmt.setLong(1, orderId);

            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    DishOrderStatusHistory status = new DishOrderStatusHistory();
                    status.setIdDishOrderStatus(rs.getLong("id_dish_order_status"));
                    status.setStatus(StatusDishOrder.valueOf(rs.getString("status")));
                    status.setCreationDateTime(rs.getTimestamp("creation_date_time").toInstant());
                    dishOrderStatusHistories.add(status);
                }
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }

        return dishOrderStatusHistories;
    }
}
