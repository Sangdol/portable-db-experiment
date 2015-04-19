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
    public static final String TABLE_PREFIX = "true_view";
    private static final int TABLE_COUNT = 100;
    private static final int HOST_COUNT_IN_TABLE = 100;
    private static final List<String> names = new ArrayList<>();

    static {
        for (int i = 0; i < TABLE_COUNT; i++) {
            names.add(TABLE_PREFIX + i);
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

        if (tableNumber >= TABLE_COUNT)
            throw new WebApplicationException(404);

        return TABLE_PREFIX + tableNumber;
    }
}
