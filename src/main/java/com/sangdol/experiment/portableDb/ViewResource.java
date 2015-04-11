package com.sangdol.experiment.portableDb;

import com.codahale.metrics.annotation.Timed;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * @author hugh
 */
@Path("/user")
@Produces(MediaType.APPLICATION_JSON)
public class ViewResource {
    @GET
    @Path("{id}")
    @Timed
    public List<View> viewList(@PathParam("id") int id) {
        List<View> list = new ArrayList<>();
        list.add(new View(id, new Date()));

        return list;
    }
}
