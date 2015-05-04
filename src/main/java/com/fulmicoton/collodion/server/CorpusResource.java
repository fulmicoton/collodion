package com.fulmicoton.collodion.server;

import com.fulmicoton.collodion.common.JSON;
import com.fulmicoton.collodion.corpus.Corpus;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import org.apache.lucene.document.Document;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import java.util.concurrent.ExecutionException;

@Path("corpus")
public class CorpusResource {

    public CorpusResource() {
        super();
    }

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Path("/{docId}/")
    public String getDoc(@PathParam("docId") Integer i,
                         @QueryParam("q") final String query) throws ExecutionException {
        final Document doc = Application.get().getCorpus(query).get(i);
        return JSON.toJson(doc);
    }


    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Path("/{docId}/processed/")
    public String getDocProcessed(@PathParam("docId") Integer i,
                                  @QueryParam("q") final String query) throws Exception {
        final Corpus corpus = Application.get().getCorpus(query);
        final Document doc = corpus.get(i);

        final String text = doc.get("text");
        final JsonObject resp = new JsonObject();
        final JsonElement tokenStreamJson = Application.get().executor().call("text", text, AnalysisTasks.ToJSON);
        resp.add("tokens", tokenStreamJson);
        resp.addProperty("text", text);
        return resp.toString();
    }

    public static class CorpusMeta {
        public final long nbDocs;
        public CorpusMeta(long nbDocs) {
            this.nbDocs = nbDocs;
        }
    }

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Path("/" )
    public CorpusMeta index(@QueryParam("q") final String query) throws ExecutionException {
        final long nbDocs = Application.get().getCorpus(query).size();
        return new CorpusMeta(nbDocs);
    }

}
