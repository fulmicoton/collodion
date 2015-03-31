package com.fulmicoton;


import com.fulmicoton.semantic.Annotation;
import com.fulmicoton.semantic.ProcessorBuilder;
import com.fulmicoton.semantic.SemanticAnalyzer;
import com.fulmicoton.semantic.vocabularymatcher.MatchingMethod;
import com.fulmicoton.semantic.vocabularymatcher.Rule;
import com.fulmicoton.semantic.vocabularymatcher.VocabularyFilter;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;
import org.apache.lucene.analysis.en.StemFilter;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class JSON {


    public static Gson GSON = gsonBuilder().create();

    static {
        ProcessorBuilderAdapter.register("vocabulary", VocabularyFilter.Builder.class);
        ProcessorBuilderAdapter.register("stem", StemFilter.Builder.class);
    }

    public static GsonBuilder gsonBuilder() {
        return new GsonBuilder()
        .registerTypeAdapter(SemanticAnalyzer.class, new SemanticAnalyzerAdapter())
        .registerTypeAdapter(ProcessorBuilder.class, new ProcessorBuilderAdapter())
        .registerTypeAdapter(Rule.class, new RuleAdapter())
        .setPrettyPrinting();
    }


    public static class RuleAdapter implements JsonDeserializer<Rule>, JsonSerializer<Rule> {

        @Override
        public Rule deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
            final JsonObject jsonObj = json.getAsJsonObject();
            final String methodString = jsonObj.get("method").getAsString();
            final MatchingMethod matchingMethod = MatchingMethod.valueOf(methodString.toUpperCase());
            final String value = jsonObj.get("value").getAsString();
            final String annotationString = jsonObj.get("annotation").getAsString();
            final Annotation annotation = Annotation.of(annotationString);
            return new Rule(matchingMethod, value, annotation);
        }

        @Override
        public JsonElement serialize(Rule src, Type typeOfSrc, JsonSerializationContext context) {
            final JsonObject jsonObj = new JsonObject();
            jsonObj.addProperty("method", src.method.name().toLowerCase());
            jsonObj.addProperty("value", src.value);
            jsonObj.addProperty("annotation", src.annotation.toString());
            return jsonObj;

        }
    }

    public static class SemanticAnalyzerAdapter implements JsonDeserializer<SemanticAnalyzer>, JsonSerializer<SemanticAnalyzer> {

        @Override
        public SemanticAnalyzer deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
            JsonObject jsonObj = json.getAsJsonObject();
            List<ProcessorBuilder> processors = new ArrayList<>();
            JsonArray processorsJson = jsonObj.get("processors").getAsJsonArray();
            for (JsonElement processorJson: processorsJson) {
                if (!processorJson.isJsonNull()) {
                    ProcessorBuilder processorBuilder = context.deserialize(processorJson, ProcessorBuilder.class);
                    processors.add(processorBuilder);
                }
            }
            return new SemanticAnalyzer(processors);
        }

        @Override
        public JsonElement serialize(SemanticAnalyzer src, Type typeOfSrc, JsonSerializationContext context) {
            JsonObject jsonObj = new JsonObject();
            JsonArray processorsArr = new JsonArray();
            for (ProcessorBuilder processor: src.processorBuilders) {
                final JsonElement processorJson = context.serialize(processor, ProcessorBuilder.class);
                processorsArr.add(processorJson);
            }
            jsonObj.add("processors", processorsArr);
            return jsonObj;
        }
    }

    public static class ProcessorBuilderAdapter implements JsonDeserializer<ProcessorBuilder>, JsonSerializer<ProcessorBuilder> {

        private final static Map<String, Class<? extends ProcessorBuilder>> nameToType = new HashMap<>();
        private final static Map<Class<? extends ProcessorBuilder>, String> typeToName = new HashMap<>();

        public static void register(String typeName, Class<? extends ProcessorBuilder> processorType) {
            nameToType.put(typeName, processorType);
            typeToName.put(processorType, typeName);
        }

        @Override
        public JsonElement serialize(ProcessorBuilder src, Type typeOfSrc, JsonSerializationContext context) {
            final Class<? extends ProcessorBuilder> processorType = src.getClass();
            JsonElement json = context.serialize(src, processorType);
            JsonObject jsonObj = json.getAsJsonObject();
            if (jsonObj.has("type")) {
                throw new IllegalArgumentException("Processor Builder may not have a property named type.");
            }
            jsonObj.addProperty("type", typeToName.get(processorType));
            return jsonObj;
        }

        @Override
        public ProcessorBuilder deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
            JsonObject jsonObject = json.getAsJsonObject();
            String processorTypeName = jsonObject.get("type").getAsString();
            final Class<? extends ProcessorBuilder> processorType = nameToType.get(processorTypeName);
            return context.deserialize(json, processorType);
        }

    }


}
