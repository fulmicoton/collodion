package com.fulmicoton.collodion.server;

import com.fulmicoton.collodion.common.JSON;
import com.fulmicoton.collodion.common.Utils;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.document.Document;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import java.io.IOException;

@Path("corpus")
public class CorpusResource {

    public CorpusResource() {
        super();
    }

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Path("/{docId}/")
    public String getDoc(@PathParam("docId") Integer i) {
        final Document doc = Application.get().getCorpus().get(i);
        final String docJson = JSON.toJson(doc);
        return docJson;
    }


    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Path("/{docId}/processed/")
    public String getDocProcessed(@PathParam("docId") Integer i) throws IOException {
        final Document doc = Application.get().getCorpus().get(i);
        final String text = doc.get("text");
        System.out.println("TEXT:" + text);
        final TokenStream tokenStream = Application.get().getAnalyzer().tokenStream("text", text);
        tokenStream.reset();
        final String tokenStreamJson  = Utils.toJson(tokenStream).toString();
        return tokenStreamJson;
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
    public CorpusMeta index() {
        final long nbDocs = Application.get().getNbDocuments();
        return new CorpusMeta(nbDocs);
    }





}

