package com.sangdol.experiment.portableDb;

import com.codahale.metrics.health.HealthCheck;
import org.h2.jdbcx.JdbcConnectionPool;

import java.sql.Connection;
import java.sql.SQLException;

/**
 * @author hugh
 */
public class DatabaseHealthCheck extends HealthCheck {
    private JdbcConnectionPool cp;

    public DatabaseHealthCheck(JdbcConnectionPool cp) {
        this.cp = cp;
    }

    @Override
    protected Result check() throws Exception  {
        try (Connection connection = cp.getConnection()) {
            if (connection.isValid(1000)) {
                return Result.healthy();
            } else {
                return Result.unhealthy("Connection is not vaild.");
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
}
