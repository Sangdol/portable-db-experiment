package com.sangdol.experiment.portableDb;

/**
 * Simple SQL queries generating class.
 * Please check the test code to see more readable queries.
 *
 * @author hugh
 */
public class ViewSimpleQuery {

    private ViewTable viewTable;

    public ViewSimpleQuery(ViewTable viewTable) {
        this.viewTable = viewTable;
    }

    /**
     * Returns query which select recent 10 visitors of the host.
     */
    public String getSelectLatest10(int hostId) {
        return String.format(
                " SELECT visitor_id, date FROM %s " +
                        " WHERE host_id = ? " +
                        " ORDER BY id DESC LIMIT 10 ", viewTable.get(hostId));
    }

    /**
     * Returns query which insert view data into a table.
     */
    public String getInsert(int userId) {
        return String.format("INSERT INTO %s (host_id, visitor_id, date) VALUES (?, ?, NOW())",
                viewTable.get(userId));
    }
}
