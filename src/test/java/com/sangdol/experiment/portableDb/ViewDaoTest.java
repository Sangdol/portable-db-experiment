package com.sangdol.experiment.portableDb;

import org.h2.jdbcx.JdbcConnectionPool;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.*;
import static org.hamcrest.Matchers.*;
import static org.mockito.Mockito.*;

/**
 * Integration tests with actual queries, but with test tables
 */
public class ViewDaoTest {
    private List<String> tables = new ArrayList<>();
    private ViewTable viewTable = mock(ViewTable.class);
    private ViewSimpleQuery viewSimpleQuery = new ViewSimpleQuery(viewTable);
    private ViewBatchQuery viewBatchQuery = new ViewBatchQuery(viewTable);
    private JdbcConnectionPool cp = JdbcConnectionPool.create("jdbc:h2:mem:h2-test-db", "sa", "");
    private ViewDao viewDao;

    @Before
    public void setup() throws Exception {
        tables.add("table1");
        tables.add("table2");

        when(viewTable.getAll()).thenReturn(tables);
        when(viewTable.get(anyInt())).thenReturn("table1");

        viewDao = new ViewDao(cp, viewSimpleQuery, viewBatchQuery);
    }

    @After
    public void dropTestTables() {
        try (Connection connection = cp.getConnection()) {
            String queries = "";
            for (String table : tables) {
                queries += String.format("DROP TABLE %s;", table);
            }
            PreparedStatement statement = connection.prepareStatement(queries);
            statement.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    public void shouldReturnRecentTop10Visitors() {
        int hostId = 1;
        for (int i = 1; i <= 30; i++) {
            viewDao.createView(hostId, i);
        }

        List<View> views = viewDao.getLatest10Visitors(hostId);
        assertThat(views, hasSize(10));
        assertThat(views.get(0).getUserId(), is(30));
        assertThat(views.get(9).getUserId(), is(21));
    }

    @Test
    public void shouldViewCountReducedTo10AfterClear() {
        int hostId = 1;
        for (int i = 1; i <= 30; i++) {
            viewDao.createView(hostId, i);
        }

        List<Integer> viewCounts = viewDao.getAllViewCounts();
        assertThat(viewCounts.get(0), is(30));

        viewDao.clear();
        viewCounts = viewDao.getAllViewCounts();
        assertThat(viewCounts.get(0), is(10));
    }
}