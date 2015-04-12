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
        Connection connection = null;
        List<View> views = new ArrayList<>();
        try {
            // TODO take the connection string out
            connection = DriverManager.getConnection("jdbc:sqlite:view.db");
            Statement statement = connection.createStatement();
            statement.setQueryTimeout(30);

            ResultSet rs = statement.executeQuery(String.format(
                    "SELECT visitor_id, date FROM view WHERE host_id = %d LIMIT 10", userId));

            while (rs.next()) {
                int visitorId = rs.getInt("visitor_id");
                DateTimeFormatter fmt = DateTimeFormat.forPattern("yyyy-MM-dd HH:mm:ss");
                DateTime date = fmt.parseDateTime(rs.getString("date"));
                views.add(new View(visitorId, date));
            }
        } catch (SQLException e) {
            // TODO exception handling
            System.err.println(e.getMessage());
        } finally {
            try {
                if (connection != null)
                    connection.close();
            } catch (SQLException e) {
                // TODO exception handling
                System.err.println(e);
            }
        }

        return views;
    }
}
