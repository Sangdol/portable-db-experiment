package com.sangdol.experiment.portableDb;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import java.util.List;

/**
 * @author hugh
 */
@Path("/user")
@Produces(MediaType.APPLICATION_JSON)
public class ViewResource {
    private ViewDao viewDao;

    public ViewResource(ViewDao viewDao) {
        this.viewDao = viewDao;
    }

    /**
     * Returns 10 visitors with date in the order in which they recently visited.
     */
    @GET
    @Path("{id}")
    public List<View> viewList(@PathParam("id") int hostId) {
        checkValidity(hostId);

        return viewDao.getLatest10Visitors(hostId);
    }

    @POST
    @Path("{id}")
    public int createView(@PathParam("id") int hostId, @QueryParam("visitor_id") int visitorId) {
        checkValidity(visitorId);

        return viewDao.createView(hostId, visitorId);
    }

    private void checkValidity(int userId) {
        if (userId < 1)
            throw new WebApplicationException(404);
    }

    /**
     * Clears redundant data in the database.
     */
    @POST
    @Path("clear")
    public String clear() {
        viewDao.clear();
        return "Success";
    }

    /**
     * Returns record count of each table in the database.
     */
    @GET
    @Path("view-counts")
    public List<Integer> viewCount() {
        return viewDao.getAllViewCounts();
    }
}
