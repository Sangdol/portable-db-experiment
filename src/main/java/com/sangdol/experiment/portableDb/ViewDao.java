package com.sangdol.experiment.portableDb;

import org.h2.jdbcx.JdbcDataSource;
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

    public List<View> getLatest10Visitors(int userId) {
        List<View> views = new ArrayList<>();
        try (Connection connection = getConnection()) {
            Statement statement = connection.createStatement();
            statement.setQueryTimeout(30); // TODO set globally

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
        DateTime now = DateTime.now();

        try (Connection connection = getConnection()) {
            Statement statement = connection.createStatement();
            statement.setQueryTimeout(30); // TODO set globally

            statement.executeUpdate(String.format(
                   "INSERT INTO view (host_id, visitor_id, date) VALUES (%d, %d, NOW())", hostId, visitorId));

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        return new View(visitorId, now);
    }

    private Connection getConnection() throws SQLException {
        // TODO take the connection string out
        JdbcDataSource ds = new JdbcDataSource();
        ds.setURL("jdbc:h2:./view");
        ds.setUser("sa");
        ds.setPassword("");
        return ds.getConnection();
    }
}
