package com.sangdol.experiment.portableDb;

import java.util.ArrayList;
import java.util.List;

/**
 * Batch SQL queries generating class.
 * The queries are returned as list to separate them for avoiding OutOfMemoryError
 * which happens when executing too long queries at once.
 *
 * Please check the test code to see more readable queries.
 *
 * @author hugh
 */
public class ViewBatchQuery {
    // If "java.long.OutOfMemoryError: Java heap space" occurs while executing queries,
    // need to reduce QUERY_COUNT_AT_ONCE or increase the max heap space by setting `-Xmx` option.
    // When I tested on my machine, approximately 1000 queries / 1G was okay.
    // Please use ViewDaoPerformanceTest class for testing.
    private static final int QUERY_COUNT_AT_ONCE = 1000;
    private ViewTable viewTable;

    public ViewBatchQuery(ViewTable viewTable) {
        this.viewTable = viewTable;
    }

    interface ViewBatchQueryStrategy {
        String getQueries(List<String> strings);
    }

    private List<String> getQueriesList(ViewBatchQueryStrategy strategy) {
        List<String> list = new ArrayList<>();
        List<String> tables = viewTable.getAll();

        for (int i = 0; i < tables.size(); i += QUERY_COUNT_AT_ONCE) {
            int to = Math.min(i + QUERY_COUNT_AT_ONCE, tables.size());
            list.add(strategy.getQueries(tables.subList(i, to)));
        }

        return list;
    }

    /**
     * Refer to
     * - Create Table http://www.h2database.com/html/grammar.html#create_table
     * - Create Index http://www.h2database.com/html/grammar.html#create_index
     */
    public List<String> getCreateTablesList() {
        return getQueriesList(new ViewBatchQueryStrategy() {
            @Override
            public String getQueries(List<String> tables) {
                StringBuilder sb = new StringBuilder();
                for (String table : tables) {
                    // Create Table
                    sb.append(" CREATE TABLE ").append(table);
                    sb.append(" ( ");
                    sb.append(" id BIGINT PRIMARY KEY AUTO_INCREMENT, ");
                    sb.append(" host_id INT, ");
                    sb.append(" visitor_id INT, ");
                    sb.append(" date TIMESTAMP ");
                    sb.append(" ); ");
                    // Create Index
                    // It's not possible to add indices while creating tables with H2.
                    sb.append(" CREATE INDEX ON ").append(table);
                    sb.append(" (host_id); ");
                }

                return sb.toString();
            }
        });
    }

    /**
     * Returns queries which select count(*) of all tables.
     */
    public List<String> getSelectCountsList() {
        return getQueriesList(new ViewBatchQueryStrategy() {
            @Override
            public String getQueries(List<String> tables) {
                String UNION = "";
                StringBuilder sb = new StringBuilder();
                for (String table : tables) {
                    sb.append(UNION).append(" SELECT COUNT(*) FROM ").append(table);
                    UNION = " UNION ALL ";
                }

                return sb.toString();
            }
        });
    }

    /**
     * Returns queries which delete rows except recent 10 rows from all tables.
     *
     * Deleting operation is very slow because we need to use nested select statement.
     * This query can be improved if we use a database which supports better functions.
     * Lacking function of assignment inside CASE statement makes it impossible
     * to remove nested select statement.
     */
    public List<String> getDeleteTablesList() {
        return getQueriesList(new ViewBatchQueryStrategy() {
            @Override
            public String getQueries(List<String> tables) {
                StringBuilder sb = new StringBuilder();
                for (String table : tables) {
                    sb.append(" DELETE FROM ").append(table);
                    sb.append(" where id IN ");
                    sb.append(" (SELECT id FROM ");
                    sb.append(" (SELECT id, (SELECT COUNT(*) FROM ").append(table);
                    sb.append(" v2 WHERE v1.id <= v2.id AND v1.host_id = v2.host_id) AS rank ");
                    sb.append(" FROM ").append(table);
                    sb.append(" v1 ORDER BY id DESC) sub ");
                    sb.append(" WHERE rank > 10); ");
                }
                return sb.toString();
            }
        });
    }
}
