package com.sangdol.experiment.portableDb;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.dropwizard.Configuration;
import org.h2.jdbcx.JdbcDataSource;

import javax.sql.ConnectionPoolDataSource;
import java.util.Map;

/**
 * @author hugh
 */
public class ViewConfiguration extends Configuration {

    private ConnectionPoolDataSource dataSource;

    @JsonProperty("database")
    public ConnectionPoolDataSource getDataSource() {
        return dataSource;
    }

    @JsonProperty("database")
    public void setDataSource(Map<String, String> dataSourceMap) {
        // It's not possible to inject properties directly to JdbcDataSource
        // because it has duplicated setters(`setUrl` and `setURL`)
        // which cause "conflicting setter definitions for property" exception.:(
        JdbcDataSource dataSource = new JdbcDataSource();
        dataSource.setUser(dataSourceMap.get("user"));
        dataSource.setPassword(dataSourceMap.get("password"));
        dataSource.setURL(dataSourceMap.get("url"));
        this.dataSource = dataSource;
    }
}
