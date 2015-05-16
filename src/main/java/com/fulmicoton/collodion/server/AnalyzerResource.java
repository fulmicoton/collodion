package com.fulmicoton.collodion.server;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import java.util.concurrent.ExecutionException;

@Path("analyzer")
public class AnalyzerResource {

    public AnalyzerResource() {
        super();
    }

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Path("/reload/")
    public String reload() throws ExecutionException {
        CollodionApplication.reload();
        return "success";
    }
}
