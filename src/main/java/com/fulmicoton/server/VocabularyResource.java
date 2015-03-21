package com.fulmicoton.server;

import com.fulmicoton.semantic.Vocabulary;


import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import java.io.IOException;

@Path("vocabulary")
public class VocabularyResource {

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Vocabulary getVocabulary() {
        return Application.get().getVocabulary();
    }

    @POST
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    public Vocabulary setVocabulary(Vocabulary vocabulary) throws IOException {
        Application.get().setVocabulary(vocabulary);
        return Application.get().getVocabulary();
    }
}
