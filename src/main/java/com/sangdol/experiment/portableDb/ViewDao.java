package com.sangdol.experiment.portableDb;

import org.h2.jdbcx.JdbcConnectionPool;
import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * @author hugh
 */
public class ViewDao {
    public static final DateTimeFormatter fmt = DateTimeFormat.forPattern("yyyy-MM-dd HH:mm:ss.SSS");
    private static final JdbcConnectionPool cp = JdbcConnectionPool.create("jdbc:h2:./view", "sa", "");

    public ViewDao() throws ClassNotFoundException {
        Class.forName("org.h2.Driver");
        cp.setMaxConnections(50);
    }

    public List<View> getLatest10Visitors(int userId) {
        List<View> views = new ArrayList<>();
        try (Connection connection = cp.getConnection()) {
            PreparedStatement statement = connection.prepareStatement(
                    " SELECT visitor_id, date FROM view " +
                    " WHERE host_id = ? AND date > DATEADD('DAY', -10, NOW()) " +
                    " ORDER BY id DESC LIMIT 10");
            statement.setInt(1, userId);

            ResultSet rs = statement.executeQuery();

            while (rs.next()) {
                int visitorId = rs.getInt("visitor_id");
                DateTime date = fmt.parseDateTime(rs.getString("date"));
                views.add(new View(visitorId, date));
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        return views;
    }

    public View createView(int hostId, int visitorId) {
        // TODO think about the discrepancy between date in db and returning date
        DateTime now = DateTime.now();

        try (Connection connection = cp.getConnection()) {
            PreparedStatement statement = connection.prepareStatement(
                    "INSERT INTO view (host_id, visitor_id, date) VALUES (?, ?, NOW())");
            statement.setInt(1, hostId);
            statement.setInt(2, visitorId);
            statement.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        return new View(visitorId, now);
    }

    public void clear() {
        try (Connection connection = cp.getConnection()) {
            PreparedStatement statement = connection.prepareStatement(
                    " DELETE FROM view where id IN " +
                        " (SELECT id FROM " +
                            " (SELECT id, (SELECT count(*) FROM view v2 WHERE v1.id <= v2.id AND v1.host_id = v2.host_id) AS rank " +
                                " FROM view v1 ORDER BY id DESC) sub " +
                    " WHERE rank > 10) "
            );

            statement.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
}
