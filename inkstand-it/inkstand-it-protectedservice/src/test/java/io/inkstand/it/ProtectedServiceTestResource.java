package io.inkstand.it;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.core.Response;

/**
 * Test resource
 * Created by Gerald on 27.05.2015.
 */
@Path("/test")
public class ProtectedServiceTestResource {

    @GET
    public Response test(){
        return Response.ok("test").build();
    }
}
