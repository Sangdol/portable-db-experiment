package com.sangdol.experiment.portableDb;

import io.dropwizard.Application;
import io.dropwizard.setup.Bootstrap;
import io.dropwizard.setup.Environment;

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
        final ViewDao viewDao = new ViewDao();
        final ViewService viewService = new ViewService(viewDao);
        final ViewResource resource = new ViewResource(viewService);
        environment.jersey().register(resource);
    }
}
