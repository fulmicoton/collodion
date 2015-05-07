package com.fulmicoton.collodion.server.tasks;


import com.fulmicoton.collodion.CollodionAnalyzer;
import com.fulmicoton.collodion.common.Utils;
import com.google.gson.JsonElement;
import org.apache.lucene.analysis.TokenStream;

public class ToJSON extends AnalysisTask<JsonElement> {

    public ToJSON(final CollodionAnalyzer analyzer) {
        super(analyzer);
    }

    @Override
    public JsonElement process(TokenStream tokenStream) throws Exception {
        return Utils.toJson(tokenStream);
    }
}
