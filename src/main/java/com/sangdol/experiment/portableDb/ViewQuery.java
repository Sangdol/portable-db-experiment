package com.sangdol.experiment.portableDb;

/**
 * Query generating class.
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
            sb.append(" CREATE TABLE ");
            sb.append(table);
            sb.append(" ( ");
            sb.append(" id BIGINT PRIMARY KEY AUTO_INCREMENT, ");
            sb.append(" host_id INT, ");
            sb.append(" visitor_id INT, ");
            sb.append(" date DATETIME ");
            sb.append(" ); ");
            // Create Index
            sb.append(" CREATE INDEX ON ");
            sb.append(table);
            sb.append(" (host_id); ");
        }

        return sb.toString();
    }

    public String getDeleteOver10() {
        StringBuilder sb = new StringBuilder();
        for (String table : viewTable.getAll()) {
            sb.append(" DELETE FROM ");
            sb.append(table);
            sb.append(" where id IN ");
            sb.append(" (SELECT id FROM ");
            sb.append(" (SELECT id, (SELECT COUNT(*) FROM ");
            sb.append(table);
            sb.append(" v2 WHERE v1.id <= v2.id AND v1.host_id = v2.host_id) AS rank ");
            sb.append(" FROM ");
            sb.append(table);
            sb.append(" v1 ORDER BY id DESC) sub ");
            sb.append(" WHERE rank > 10); ");
        }
        return sb.toString();
    }

    public String getSelectLatest10(int userId) {
        return String.format(
                " SELECT visitor_id, date FROM %s " +
                " WHERE host_id = ? AND date > DATEADD('DAY', -10, NOW()) " +
                " ORDER BY id DESC LIMIT 10 ", viewTable.get(userId));
    }

    public String getInsert(int userId) {
        return String.format("INSERT INTO %s (host_id, visitor_id, date) VALUES (?, ?, NOW())",
                viewTable.get(userId));
    }
}
