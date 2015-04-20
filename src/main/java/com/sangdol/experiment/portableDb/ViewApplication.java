package com.sangdol.experiment.portableDb;

import com.sangdol.experiment.portableDb.healthcheck.DatabaseHealthCheck;
import io.dropwizard.Application;
import io.dropwizard.setup.Bootstrap;
import io.dropwizard.setup.Environment;
import org.h2.jdbcx.JdbcConnectionPool;

/**
 * @author hugh
 */
public class ViewApplication extends Application<ViewConfiguration>{
    public static void main(String[] args) throws Exception{
        new ViewApplication().run(args);
    }

    @Override
    public String getName() {
        return "User view";
    }

    @Override
    public void initialize(Bootstrap<ViewConfiguration> bootstrap) {}

    @Override
    public void run(ViewConfiguration configuration, Environment environment) throws ClassNotFoundException {
        final ViewTable viewTable = new ViewTable("true_view", 100); // Set 100 for faster test
        final ViewSimpleQuery viewSimpleQuery = new ViewSimpleQuery(viewTable);
        final ViewBatchQuery viewBatchQuery = new ViewBatchQuery(viewTable);

        final JdbcConnectionPool cp = JdbcConnectionPool.create(configuration.getDataSource());
        cp.setMaxConnections(100);

        final ViewDao viewDao = new ViewDao(cp, viewSimpleQuery, viewBatchQuery, viewTable);
        final ViewResource resource = new ViewResource(viewDao);

        environment.healthChecks().register("database", new DatabaseHealthCheck(cp));
        environment.jersey().register(resource);
    }
}
