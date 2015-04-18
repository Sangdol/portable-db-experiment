package com.sangdol.experiment.portableDb;

/**
 * SQL queries generating class.
 * Please check the test code to see more readable queries.
 *
 * @author hugh
 */
public class ViewQuery {

    private ViewTable viewTable;

    public ViewQuery(ViewTable viewTable) {
        this.viewTable = viewTable;
    }

    /**
     * Refer to
     * - Create Table http://www.h2database.com/html/grammar.html#create_table
     * - Create Index http://www.h2database.com/html/grammar.html#create_index
     */
    public String getCreateTables() {
        StringBuilder sb = new StringBuilder();
        for (String table : viewTable.getAll()) {
            // Create Table
            sb.append(" CREATE TABLE ").append(table);
            sb.append(" ( ");
            sb.append(" id BIGINT PRIMARY KEY AUTO_INCREMENT, ");
            sb.append(" host_id INT, ");
            sb.append(" visitor_id INT, ");
            sb.append(" date DATETIME ");
            sb.append(" ); ");
            // Create Index
            sb.append(" CREATE INDEX ON ").append(table);
            sb.append(" (host_id); ");
        }

        return sb.toString();
    }

    /**
     * Returns queries which delete rows except recent 10 rows from all tables.
     */
    public String getDeleteAllExceptRecent10() {
        StringBuilder sb = new StringBuilder();
        for (String table : viewTable.getAll()) {
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

    /**
     * Returns query which select recent 10 visitors of the host.
     * Older than 10-day records are excluded.
     */
    public String getSelectLatest10(int hostId) {
        return String.format(
                " SELECT visitor_id, date FROM %s " +
                " WHERE host_id = ? AND date > DATEADD('DAY', -10, NOW()) " +
                " ORDER BY id DESC LIMIT 10 ", viewTable.get(hostId));
    }

    /**
     * Returns query which insert view data into a table.
     */
    public String getInsert(int userId) {
        return String.format("INSERT INTO %s (host_id, visitor_id, date) VALUES (?, ?, NOW())",
                viewTable.get(userId));
    }

    /**
     * Returns queries which select count(*) of all tables.
     */
    public String getSelectAllCounts() {
        String UNION = "";
        StringBuilder sb = new StringBuilder();
        for (String table : viewTable.getAll()) {
            sb.append(UNION).append(" SELECT COUNT(*) FROM ").append(table);
            UNION = " UNION ALL ";
        }

        return sb.toString();
    }
}
