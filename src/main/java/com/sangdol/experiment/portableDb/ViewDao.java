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
            Statement statement = connection.createStatement();

            String tenDaysAgo = DateTime.now().minusDays(10).toString(fmt);
            ResultSet rs = statement.executeQuery(String.format(
                    " SELECT visitor_id, date FROM view " +
                    " WHERE host_id = %d AND date > '%s' " +
                    " ORDER BY id DESC LIMIT 10", userId, tenDaysAgo));

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
            Statement statement = connection.createStatement();

            statement.executeUpdate(String.format(
                   "INSERT INTO view (host_id, visitor_id, date) VALUES (%d, %d, NOW())", hostId, visitorId));

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        return new View(visitorId, now);
    }

    public void clear() {
        try (Connection connection = cp.getConnection()) {
            Statement statement = connection.createStatement();
            statement.executeUpdate(
                    " DELETE FROM view where id IN " +
                        " (SELECT id FROM " +
                            " (SELECT id, (SELECT count(*) FROM view v2 WHERE v1.id <= v2.id AND v1.host_id = v2.host_id) AS rank " +
                                " FROM view v1 ORDER BY id DESC) sub " +
                    " WHERE rank > 10) "
            );
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
}
