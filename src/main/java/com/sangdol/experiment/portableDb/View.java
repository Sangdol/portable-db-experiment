package com.sangdol.experiment.portableDb;

import com.fasterxml.jackson.annotation.JsonProperty;
import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

/**
 * @author hugh
 */
public class View {
    private int userId;
    private DateTime date;

    @SuppressWarnings("unused") // Used by Jackson
    public View() {}

    public View(int userId, DateTime date) {
        this.userId = userId;
        this.date = date;
    }

    @SuppressWarnings("unused")
    @JsonProperty
    public int getUserId() {
        return userId;
    }

    @SuppressWarnings("unused")
    @JsonProperty
    public String getDate() {
        return date.toString(ViewDao.fmt);
    }
}
