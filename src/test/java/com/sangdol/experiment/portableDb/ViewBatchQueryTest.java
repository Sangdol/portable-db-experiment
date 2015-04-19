package com.sangdol.experiment.portableDb;

import com.sangdol.experiment.portableDb.ViewBatchQuery;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.*;
import static org.hamcrest.Matchers.*;
import static org.mockito.Mockito.*;

public class ViewBatchQueryTest {
    private ViewTable viewTable = mock(ViewTable.class);
    private ViewBatchQuery viewBatchQuery = new ViewBatchQuery(viewTable);

    @Before
    public void setup() {
        List<String> tables = new ArrayList<>();
        tables.add("table1");
        tables.add("table2");

        when(viewTable.getAll()).thenReturn(tables);
    }

    @Test
    public void testGetCreateTables() {
        String queries =
                " CREATE TABLE table1 ( " +
                        " id BIGINT PRIMARY KEY AUTO_INCREMENT, " +
                        " host_id INT, " +
                        " visitor_id INT, " +
                        " date TIMESTAMP " +
                " ); " +
                " CREATE INDEX ON table1 (host_id); " +

                " CREATE TABLE table2 ( " +
                        " id BIGINT PRIMARY KEY AUTO_INCREMENT, " +
                        " host_id INT, " +
                        " visitor_id INT, " +
                        " date TIMESTAMP " +
                " ); " +
                " CREATE INDEX ON table2 (host_id); ";

        assertThat(viewBatchQuery.getCreateTablesList().get(0), is(queries));
    }

    @Test
    public void testGetDeleteOver10() {
        String queries =
                " DELETE FROM table1 where id IN " +
                        " (SELECT id FROM " +
                                " (SELECT id, (SELECT COUNT(*) FROM table1 v2 WHERE v1.id <= v2.id AND v1.host_id = v2.host_id) AS rank " +
                                " FROM table1 v1 ORDER BY id DESC) sub " +
                " WHERE rank > 10); " +

                " DELETE FROM table2 where id IN " +
                        " (SELECT id FROM " +
                                " (SELECT id, (SELECT COUNT(*) FROM table2 v2 WHERE v1.id <= v2.id AND v1.host_id = v2.host_id) AS rank " +
                                " FROM table2 v1 ORDER BY id DESC) sub " +
                " WHERE rank > 10); ";

        assertThat(viewBatchQuery.getDeleteTablesList().get(0), is(queries));
    }

    @Test
    public void testGetSelectAllCounts() {
        String queries =
                " SELECT COUNT(*) FROM table1" +
                        " UNION ALL " +
                " SELECT COUNT(*) FROM table2";

        assertThat(viewBatchQuery.getSelectCountsList().get(0), is(queries));
    }
}