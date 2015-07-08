package com.sangdol.experiment.portableDb;

import com.sangdol.experiment.portableDb.ViewTable;
import org.junit.Test;

import static org.junit.Assert.*;
import static org.hamcrest.Matchers.*;

public class ViewTableTest {

    private ViewTable viewTable = new ViewTable("view", 100);

    @Test
    public void shouldHave100Names() {
        assertThat(viewTable.getAll(), hasSize(100));
    }

    @Test
    public void testGet() {
        assertThat(viewTable.get(1), is("view0"));
        assertThat(viewTable.get(1000), is("view0"));
        assertThat(viewTable.get(1001), is("view1"));
        assertThat(viewTable.get(1999), is("view1"));
        assertThat(viewTable.get(2000), is("view1"));
        assertThat(viewTable.get(2001), is("view2"));
        assertThat(viewTable.get(10000), is("view9"));
        assertThat(viewTable.get(11000), is("view10"));
    }
}