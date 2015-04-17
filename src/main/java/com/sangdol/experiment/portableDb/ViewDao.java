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

    public ViewDao() throws ClassNotFoundException {
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

            PreparedStatement statement = connection.prepareStatement(getCreateTablesQuery());
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

    /**
     * Refer to
     * - Create Table http://www.h2database.com/html/grammar.html#create_table
     * - Create Index http://www.h2database.com/html/grammar.html#create_index
     */
    private String getCreateTablesQuery() {
        StringBuilder sb = new StringBuilder();
        for (String tableName : ViewTable.getNames()) {
            sb.append(String.format(" CREATE TABLE %s ( ", tableName));
            sb.append(" id BIGINT PRIMARY KEY AUTO_INCREMENT, ");
            sb.append(" host_id INT, ");
            sb.append(" visitor_id INT, ");
            sb.append(" date DATETIME ");
            sb.append(" ); ");
            sb.append(String.format(" CREATE INDEX ON %s (host_id); ", tableName));
        }

        return sb.toString();
    }

    public List<View> getLatest10Visitors(int userId) {
        List<View> views = new ArrayList<>();
        try (Connection connection = cp.getConnection()) {
            PreparedStatement statement = connection.prepareStatement(String.format(
                    " SELECT visitor_id, date FROM %s " +
                            " WHERE host_id = ? AND date > DATEADD('DAY', -10, NOW()) " +
                            " ORDER BY id DESC LIMIT 10", TABLE_PREFIX));
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
            PreparedStatement statement = connection.prepareStatement(String.format(
                    "INSERT INTO %s (host_id, visitor_id, date) VALUES (?, ?, NOW())", TABLE_PREFIX));
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
            PreparedStatement statement = connection.prepareStatement(String.format(
                    " DELETE FROM %s where id IN " +
                        " (SELECT id FROM " +
                            " (SELECT id, (SELECT COUNT(*) FROM %s v2 WHERE v1.id <= v2.id AND v1.host_id = v2.host_id) AS rank " +
                                " FROM %s v1 ORDER BY id DESC) sub " +
                    " WHERE rank > 10) "
            , TABLE_PREFIX, TABLE_PREFIX, TABLE_PREFIX));

            statement.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public String getViewCount() {
        try (Connection connection = cp.getConnection()) {
            PreparedStatement statement = connection.prepareStatement("SELECT COUNT(*) cnt FROM view");
            ResultSet rs = statement.executeQuery();
            rs.next();
            return String.valueOf(rs.getInt("cnt"));
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
}
