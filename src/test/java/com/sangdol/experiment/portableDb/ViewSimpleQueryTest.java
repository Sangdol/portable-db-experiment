package com.sangdol.experiment.portableDb;

import com.sangdol.experiment.portableDb.ViewSimpleQuery;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;
import static org.hamcrest.Matchers.*;
import static org.mockito.Mockito.*;

public class ViewSimpleQueryTest {
    private static final int USER_ID = 1;
    private ViewTable viewTable = mock(ViewTable.class);
    private ViewSimpleQuery viewSimpleQuery = new ViewSimpleQuery(viewTable);

    @Before
    public void setup() {
        when(viewTable.get(USER_ID)).thenReturn("table1");
    }

    @Test
    public void testGetSelectLatest10() {
        String query =
                " SELECT visitor_id, date FROM table1 " +
                " WHERE host_id = ? AND date > DATEADD('DAY', -10, NOW()) " +
                " ORDER BY id DESC LIMIT 10 ";

        assertThat(viewSimpleQuery.getSelectLatest10(USER_ID), is(query));
    }

    @Test
    public void testGetInsert() {
        String query = "INSERT INTO table1 (host_id, visitor_id, date) VALUES (?, ?, NOW())";
        assertThat(viewSimpleQuery.getInsert(USER_ID), is(query));
    }
}