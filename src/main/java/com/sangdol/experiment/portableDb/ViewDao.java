package com.sangdol.experiment.portableDb;

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
    public List<View> getLatest10Visitors(int userId) {
        List<View> views = new ArrayList<>();
        try (Connection connection = getConnection()) {
            Statement statement = connection.createStatement();
            statement.setQueryTimeout(30); // TODO set globally

            ResultSet rs = statement.executeQuery(String.format(
                    "SELECT visitor_id, date FROM view WHERE host_id = %d ORDER BY id DESC LIMIT 10", userId));

            while (rs.next()) {
                int visitorId = rs.getInt("visitor_id");
                DateTimeFormatter fmt = DateTimeFormat.forPattern("yyyy-MM-dd HH:mm:ss");
                DateTime date = fmt.parseDateTime(rs.getString("date"));
                views.add(new View(visitorId, date));
            }
        } catch (SQLException e) {
            // TODO exception handling. e.g. no table exist
            System.err.println(e.getMessage());
        }

        return views;
    }

    public View createView(int hostId, int visitorId) {
        DateTime now = DateTime.now();

        try (Connection connection = getConnection()) {
            Statement statement = connection.createStatement();
            statement.setQueryTimeout(30); // TODO set globally

            DateTimeFormatter fmt = DateTimeFormat.forPattern("yyyy-MM-dd HH:mm:ss");
            String date = now.toString(fmt);
            statement.executeUpdate(String.format(
                   "INSERT INTO view (host_id, visitor_id, date) VALUES (%d, %d, '%s')", hostId, visitorId, date));

        } catch (SQLException e) {
            // TODO exception handling
            System.err.println(e.getMessage());
        }

        return new View(visitorId, now);
    }

    private Connection getConnection() throws SQLException {
        // TODO take the connection string out
        return DriverManager.getConnection("jdbc:sqlite:view.db");
    }
}
