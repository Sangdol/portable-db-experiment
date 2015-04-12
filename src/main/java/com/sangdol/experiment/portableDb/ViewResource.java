package com.sangdol.experiment.portableDb;

import com.codahale.metrics.annotation.Timed;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import java.util.List;

/**
 * @author hugh
 */
@Path("/user")
@Produces(MediaType.APPLICATION_JSON)
public class ViewResource {
    private ViewService viewService;

    public ViewResource(ViewService viewService) {
        this.viewService = viewService;
    }

    @GET
    @Path("{id}")
    @Timed
    public List<View> viewList(@PathParam("id") int userId) {
        return viewService.getLatest10Visitors(userId);
    }
}
