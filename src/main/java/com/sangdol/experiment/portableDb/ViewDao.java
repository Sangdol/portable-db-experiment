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
    private final JdbcConnectionPool cp;
    private final ViewSimpleQuery viewSimpleQuery;
    private final ViewBatchQuery viewBatchQuery;

    public ViewDao(JdbcConnectionPool cp, ViewSimpleQuery viewSimpleQuery, ViewBatchQuery viewBatchQuery) throws ClassNotFoundException {
        this.cp = cp;
        this.viewSimpleQuery = viewSimpleQuery;
        this.viewBatchQuery = viewBatchQuery;

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
                TABLE_PREFIX.toUpperCase() + "%", new String[]{"TABLE"});
        meta.last();
        // TODO check by comparing table counts.. then need to drop.. then throw exception
        return meta.getRow() > 0;
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
