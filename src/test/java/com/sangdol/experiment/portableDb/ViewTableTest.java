package com.sangdol.experiment.portableDb;

import com.sangdol.experiment.portableDb.ViewTable;
import org.junit.Test;

import static org.junit.Assert.*;
import static org.hamcrest.Matchers.*;

public class ViewTableTest {

    @Test
    public void shouldHave100Names() {
        assertThat(ViewTable.getNames(), hasSize(100));
    }

}