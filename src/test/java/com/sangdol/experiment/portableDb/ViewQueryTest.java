package com.sangdol.experiment.portableDb;

import com.sangdol.experiment.portableDb.ViewQuery;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.*;
import static org.hamcrest.Matchers.*;
import static org.mockito.Mockito.*;

public class ViewQueryTest {
    private static final int USER_ID = 1;
    private ViewTable viewTable = mock(ViewTable.class);
    private ViewQuery viewQuery = new ViewQuery(viewTable);

    @Before
    public void setup() {
        List<String> tables = new ArrayList<>();
        tables.add("table1");
        tables.add("table2");

        when(viewTable.getAll()).thenReturn(tables);
        when(viewTable.get(USER_ID)).thenReturn("table1");
    }

    @Test
    public void testGetCreateTables() {

        String queries =
                " CREATE TABLE table1 ( " +
                        " id BIGINT PRIMARY KEY AUTO_INCREMENT, " +
                        " host_id INT, " +
                        " visitor_id INT, " +
                        " date DATETIME " +
                " ); " +
                " CREATE INDEX ON table1 (host_id); " +

                " CREATE TABLE table2 ( " +
                        " id BIGINT PRIMARY KEY AUTO_INCREMENT, " +
                        " host_id INT, " +
                        " visitor_id INT, " +
                        " date DATETIME " +
                        " ); " +
                " CREATE INDEX ON table2 (host_id); ";

        assertThat(viewQuery.getCreateTables(), is(queries));
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

        assertThat(viewQuery.getDeleteOver10(), is(queries));
    }

    @Test
    public void testGetSelectLatest10() {
        String query =
                " SELECT visitor_id, date FROM table1 " +
                " WHERE host_id = ? AND date > DATEADD('DAY', -10, NOW()) " +
                " ORDER BY id DESC LIMIT 10 ";

        assertThat(viewQuery.getSelectLatest10(USER_ID), is(query));
    }

    @Test
    public void testGetInsert() {
        String query = "INSERT INTO table1 (host_id, visitor_id, date) VALUES (?, ?, NOW())";
        assertThat(viewQuery.getInsert(USER_ID), is(query));
    }
}