package com.fulmicoton.collodion.server;

import com.fulmicoton.collodion.common.AnalysisExecutor;
import com.fulmicoton.collodion.common.Utils;
import com.google.gson.JsonElement;
import org.apache.lucene.analysis.TokenStream;

public enum AnalysisTasks implements AnalysisExecutor.AnalysisTask<JsonElement> {
    ToJSON {
        @Override
        public JsonElement process(TokenStream tokenStream) throws Exception {
            return Utils.toJson(tokenStream);
        }
    }
}
