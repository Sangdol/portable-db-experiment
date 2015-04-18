package com.sangdol.experiment.portableDb;

import org.h2.jdbcx.JdbcConnectionPool;
import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

import static com.sangdol.experiment.portableDb.ViewTable.TABLE_PREFIX;

/**
 * @author hugh
 */
public class ViewDao {
    public static final DateTimeFormatter fmt = DateTimeFormat.forPattern("yyyy-MM-dd HH:mm:ss.SSS");
    private static final JdbcConnectionPool cp = JdbcConnectionPool.create("jdbc:h2:./view", "sa", ""); // TODO move to config
    private final ViewQuery viewQuery;

    public ViewDao(ViewQuery viewQuery) throws ClassNotFoundException {
        this.viewQuery = viewQuery;

        // Need to load the driver first
        // http://www.h2database.com/html/tutorial.html#connecting_using_jdbc
        Class.forName("org.h2.Driver");

        cp.setMaxConnections(50);   // TODO What would be a good max connection count?
        createTableIfNotExist();
    }

    private void createTableIfNotExist() {
        try (Connection connection = cp.getConnection()) {
            if (hasCreatedTables(connection))
                return;

            PreparedStatement statement = connection.prepareStatement(
                    viewQuery.getCreateTables());
            statement.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    private boolean hasCreatedTables(Connection connection) throws SQLException {
        // You need to search tables in upper case even if you created tables in lower case
        ResultSet meta = connection.getMetaData().getTables(null, null,
                TABLE_PREFIX.toUpperCase() + "%", new String[]{"TABLE"});
        meta.last();
        return meta.getRow() > 0;
    }


    public List<View> getLatest10Visitors(int userId) {
        List<View> views = new ArrayList<>();
        try (Connection connection = cp.getConnection()) {
            PreparedStatement statement = connection.prepareStatement(
                    viewQuery.getSelectLatest10(userId));
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
                    viewQuery.getInsert(hostId));
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
                    viewQuery.getDeleteOver10());
            statement.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

//    public Map<String, Integer> getViewCount() {
//        try (Connection connection = cp.getConnection()) {
//            PreparedStatement statement = connection.prepareStatement(
//                    viewQuery.getSelectCount(table));
//            ResultSet rs = statement.executeQuery();
//            rs.next();
//            return String.valueOf(rs.getInt("cnt"));
//        } catch (SQLException e) {
//            throw new RuntimeException(e);
//        }
//    }
}
