package com.sangdol.experiment.portableDb;

import javax.ws.rs.WebApplicationException;
import java.util.ArrayList;
import java.util.List;

/**
 * Manage names of horizontally split table.
 * To reduce database latencies, this class uses a divide and conquer strategy by separating tables.
 * Each HOST_COUNT_IN_TABLE number of hosts are allocated to a table.
 *
 * @author hugh
 */
public class ViewTable {
    private static final int HOST_COUNT_IN_TABLE = 1000;
    private final List<String> names = new ArrayList<>();
    public final String tablePrefix;
    public final int tableCount;

    public ViewTable(String tablePrefix, int tableCount) {
        this.tablePrefix = tablePrefix;
        this.tableCount = tableCount;

        for (int i = 0; i < tableCount; i++) {
            names.add(tablePrefix + i);
        }
    }

    /**
     * Returns all table names with designated TABLE_PREFIX and TABLE_COUNT.
     */
    public List<String> getAll() {
        return names;
    }

    /**
     * Returns the allocated table name to the userId.
     * The index of table name is 0-based, e.g. true_view0, true_view1...
     */
    public String get(int userId) {
        int tableNumber = (userId - 1) / HOST_COUNT_IN_TABLE;

        if (tableNumber >= tableCount)
            throw new WebApplicationException(404);

        return tablePrefix + tableNumber;
    }

    public int getCount() {
        return tableCount;
    }

    public String getPrefix() {
        return tablePrefix;
    }
}
