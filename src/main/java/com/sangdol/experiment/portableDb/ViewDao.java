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
    private final JdbcConnectionPool cp;
    private final ViewSimpleQuery viewSimpleQuery;
    private final ViewBatchQuery viewBatchQuery;
    private final int tableCount;
    private final String tablePrefix;

    public ViewDao(JdbcConnectionPool cp, ViewSimpleQuery viewSimpleQuery,
            ViewBatchQuery viewBatchQuery, ViewTable viewTable)
            throws ClassNotFoundException {
        this.cp = cp;
        this.viewSimpleQuery = viewSimpleQuery;
        this.viewBatchQuery = viewBatchQuery;
        this.tableCount = viewTable.getCount();
        this.tablePrefix = viewTable.getPrefix();

        createTablesIfNotExist();
    }

    /**
     * Creates tables to run the server properly.
     * In practice, we need some other batch jobs for creating tables.
     */
    private void createTablesIfNotExist() {
        try (Connection connection = cp.getConnection()) {
            if (hasCreatedTables(connection))
                return;

            for (String queries : viewBatchQuery.getCreateTablesList()) {
                PreparedStatement statement = connection.prepareStatement(queries);
                statement.executeUpdate();
                statement.close();
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    private boolean hasCreatedTables(Connection connection) throws SQLException {
        // You need to search tables in upper case even if you created tables in lower case
        ResultSet meta = connection.getMetaData().getTables(null, null,
                tablePrefix.toUpperCase() + "%", new String[]{"TABLE"});

        meta.last();
        int rowCount = meta.getRow();
        if (rowCount == 0) {
            return false;
        } else if (rowCount == tableCount) {
            return true;
        } else {
            throw new IllegalStateException(String.format(
                    "The number existing tables is different from TABLE_COUNT. " +
                    "Please remove the database file manually and try again. " +
                    "(Existing table count: %d, TABLE_COUNT: %d)", rowCount, tableCount));
        }
    }


    public List<View> getLatest10Visitors(int hostId) {
        List<View> views = new ArrayList<>();
        try (Connection connection = cp.getConnection()) {
            PreparedStatement statement = connection.prepareStatement(
                    viewSimpleQuery.getSelectLatest10(hostId));
            statement.setInt(1, hostId);

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

    public int createView(int hostId, int visitorId) {
        try (Connection connection = cp.getConnection()) {
            PreparedStatement statement = connection.prepareStatement(
                    viewSimpleQuery.getInsert(hostId));
            statement.setInt(1, hostId);
            statement.setInt(2, visitorId);
            statement.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        return hostId;
    }

    public void clear() {
        try (Connection connection = cp.getConnection()) {
            for (String queries : viewBatchQuery.getDeleteTablesList()) {
                PreparedStatement statement = connection.prepareStatement(queries);
                statement.executeUpdate();
                statement.close();
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public List<Integer> getAllViewCounts() {
        try (Connection connection = cp.getConnection()) {
            List<Integer> counts = new ArrayList<>();
            for (String queries : viewBatchQuery.getSelectCountsList()) {
                PreparedStatement statement = connection.prepareStatement(queries);
                ResultSet rs = statement.executeQuery();

                while (rs.next()) {
                    counts.add(rs.getInt(1));
                }
                statement.close();
            }
            return counts;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
}
