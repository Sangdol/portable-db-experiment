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

    public static List<String> getNames() {
        return names;
    }
}
