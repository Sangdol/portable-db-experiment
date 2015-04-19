package com.sangdol.experiment.portableDb;

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
        final ViewTable viewTable = new ViewTable();
        final ViewSimpleQuery viewSimpleQuery = new ViewSimpleQuery(viewTable);
        final ViewBatchQuery viewBatchQuery = new ViewBatchQuery(viewTable);

        // AUTO_SERVER=TRUE enables Automatic Mixed Mode which allows access of multiple processes.
        // Refer to http://h2database.com/html/features.html#auto_mixed_mode
        final JdbcConnectionPool cp = JdbcConnectionPool.create("jdbc:h2:./view;AUTO_SERVER=TRUE", "sa", "");
        final ViewDao viewDao = new ViewDao(cp, viewSimpleQuery, viewBatchQuery);
        final ViewResource resource = new ViewResource(viewDao);

        environment.healthChecks().register("database", new DatabaseHealthCheck(cp));
        environment.jersey().register(resource);
    }
}
