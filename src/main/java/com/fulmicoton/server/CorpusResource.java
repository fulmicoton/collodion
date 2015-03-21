package com.fulmicoton.server;

import com.fulmicoton.corpus.Fragment;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

@Path("corpus")
public class CorpusResource {

    @GET
    @Produces(MediaType.TEXT_PLAIN)
    @Path("/doc/{docId}")
    public String getDocText(@PathParam("docId") Integer i) {
        return Application.get().getDocument(i).getText();
    }

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Path("/doc/")
    public CollectionMeta getNbDocuments() {
        return new CollectionMeta(Application.get().getNbDocuments());
    }


    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Path("/fragment/{fragmentId}")
    public Fragment getFragment(@PathParam("fragmentId") Integer i) {
        return Application.get().getFragment(i);
    }

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Path("/fragment/")
    public CollectionMeta getNbFragments() {
        return new CollectionMeta(Application.get().getNbFragments());
    }


}
