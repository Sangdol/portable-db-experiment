package com.sangdol.experiment.portableDb;

import com.sangdol.experiment.portableDb.ViewTable;
import org.junit.Test;

import static org.junit.Assert.*;
import static org.hamcrest.Matchers.*;

public class ViewTableTest {

    private ViewTable viewTable = new ViewTable("true_view", 100);

    @Test
    public void shouldHave100Names() {
        assertThat(viewTable.getAll(), hasSize(100));
    }

    @Test
    public void testGet() {
        assertThat(viewTable.get(1), is("true_view0"));
        assertThat(viewTable.get(100), is("true_view0"));
        assertThat(viewTable.get(101), is("true_view1"));
        assertThat(viewTable.get(199), is("true_view1"));
        assertThat(viewTable.get(200), is("true_view1"));
        assertThat(viewTable.get(201), is("true_view2"));
        assertThat(viewTable.get(1000), is("true_view9"));
        assertThat(viewTable.get(1100), is("true_view10"));
    }
}