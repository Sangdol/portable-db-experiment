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
    public List<View> viewList(@PathParam("id") int hostId) {
        checkValidity(hostId);

        return viewService.getLatest10Visitors(hostId);
    }

    @POST
    @Path("{id}")
    @Timed
    public int createView(@PathParam("id") int hostId, @QueryParam("visitor_id") int visitorId) {
        checkValidity(visitorId);

        return viewService.createView(hostId, visitorId);
    }

    private void checkValidity(int userId) {
        if (userId < 1)
            throw new WebApplicationException(404);
    }

    @POST
    @Path("clear")
    @Timed
    public String clear() {
        viewService.clear();
        return "Success";
    }

    @GET
    @Path("view-counts")
    @Timed
    public List<Integer> viewCount() {
        return viewService.getAllViewCounts();
    }
}
