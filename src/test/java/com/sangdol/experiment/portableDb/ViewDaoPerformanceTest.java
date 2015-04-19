package com.sangdol.experiment.portableDb;

import com.sangdol.experiment.portableDb.ViewBatchQuery;
import org.h2.jdbcx.JdbcConnectionPool;
import org.junit.After;
import org.junit.Ignore;
import org.junit.Test;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.*;
import static org.hamcrest.Matchers.*;
import static org.mockito.Mockito.*;

/**
 * To test how long it takes for each operation depends on the count of tables.
 * The result would be dependant on the performance of machine,
 * but we can get rough information from it.
 *
 * As this test is executed without any data, it'll take more time in practice.
 *
 * @author hugh
 */
@Ignore // May take too long
public class ViewDaoPerformanceTest {
    final int TABLE_SIZE = 100000;
    private String dbFile = "./h2-test-db";
    private ViewTable viewTable = mock(ViewTable.class);
    private ViewBatchQuery viewBatchQuery = new ViewBatchQuery(viewTable);
    private JdbcConnectionPool cp = JdbcConnectionPool.create("jdbc:h2:" + dbFile, "sa", "");
    private ViewDao viewDao;

    @SuppressWarnings("ResultOfMethodCallIgnored")
    @After
    public void deleteTestDbFile() {
        new File(dbFile + ".mv.db").delete();
    }

    @Test
    public void testRunningTimeOfSelectCounts() throws Exception {
        printMaxHeapMemory();

        long start = System.currentTimeMillis();
        when(viewTable.getAll()).thenReturn(getTablesOf());
        viewDao = new ViewDao(cp, null, viewBatchQuery);
        long end = System.currentTimeMillis();
        System.out.println(String.format("Table Creation took: %d milliseconds", end - start));

        start = System.currentTimeMillis();
        assertThat(viewDao.getAllViewCounts(), hasSize(TABLE_SIZE));
        end = System.currentTimeMillis();
        System.out.println(String.format("Select Counts took: %d milliseconds", end - start));
    }

    @Test
    public void testRunningTimeOfClear() throws Exception {
        printMaxHeapMemory();

        long start = System.currentTimeMillis();
        when(viewTable.getAll()).thenReturn(getTablesOf());
        viewDao = new ViewDao(cp, null, viewBatchQuery);
        long end = System.currentTimeMillis();
        System.out.println(String.format("Table Creation took: %d milliseconds", end - start));

        start = System.currentTimeMillis();
        viewDao.clear();
        end = System.currentTimeMillis();
        System.out.println(String.format("Select Counts took: %d milliseconds", end - start));
    }

    private void printMaxHeapMemory() {
        long maxBytes = Runtime.getRuntime().maxMemory();
        System.out.println("Max memory: " + maxBytes / 1024 / 1024 + "M");
    }

    private List<String> getTablesOf() {
        List<String> tables = new ArrayList<>();

        for (int i = 0; i < TABLE_SIZE; i++) {
            tables.add("true_view" + i);
        }

        return tables;
    }
}