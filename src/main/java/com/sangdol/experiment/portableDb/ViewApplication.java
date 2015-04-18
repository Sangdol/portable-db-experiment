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
    public void initialize(Bootstrap<ViewConfiguration> bootstrap) {

    }

    @Override
    public void run(ViewConfiguration configuration, Environment environment) throws ClassNotFoundException {
        final ViewTable viewTable = new ViewTable();
        final ViewQuery viewQuery = new ViewQuery(viewTable);
        final JdbcConnectionPool cp = JdbcConnectionPool.create("jdbc:h2:./view", "sa", ""); // TODO move to config
        final ViewDao viewDao = new ViewDao(cp, viewQuery);
        final ViewService viewService = new ViewService(viewDao);
        final ViewResource resource = new ViewResource(viewService);

        environment.jersey().register(resource);
    }
}
