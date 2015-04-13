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

    @POST
    @Path("{id}")
    @Timed
    public View createView(@PathParam("id") int hostId, @QueryParam("visitor_id") int visitorId) {
        if (visitorId == 0)
            return null;

        return viewService.createView(hostId, visitorId);
    }

    @POST
    @Path("clear")
    @Timed
    public String clear() {
        viewService.clear();
        return "Success";
    }

    @GET
    @Path("view-count")
    @Timed
    public String viewCount() {
        return viewService.getViewCount();
    }
}
