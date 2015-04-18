package com.sangdol.experiment.portableDb;

import java.util.ArrayList;
import java.util.List;

/**
 * @author hugh
 */
public class ViewTable {
    public static final String TABLE_PREFIX = "true_view";
    private static final int TABLE_COUNT = 100;
    private static final List<String> names = new ArrayList<>();

    static {
        for (int i = 0; i < TABLE_COUNT; i++) {
            names.add(TABLE_PREFIX + i);
        }
    }

    public List<String> getAll() {
        return names;
    }

    // TODO throw exception when trying to get a not existing table
    public String get(int userId) {
        final int HOST_COUNT_IN_TABLE = 100;
        int tableNumber = (userId - 1) / HOST_COUNT_IN_TABLE;
        return TABLE_PREFIX + tableNumber;
    }
}
