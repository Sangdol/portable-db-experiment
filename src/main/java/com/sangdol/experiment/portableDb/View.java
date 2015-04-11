package com.sangdol.experiment.portableDb;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.Date;

/**
 * @author hugh
 */
public class View {
    private int userId;
    private Date date;

    public View() {}

    public View(int userId, Date date) {
        this.userId = userId;
        this.date = date;
    }

    @JsonProperty
    public int getUserId() {
        return userId;
    }

    @JsonProperty
    public Date getDate() {
        return date;
    }
}
