package com.fulmicoton.collodion.common;


import com.fulmicoton.collodion.corpus.DocumentAdapter;
import com.fulmicoton.collodion.processors.Annotation;
import com.fulmicoton.collodion.processors.ProcessorBuilder;
import com.fulmicoton.collodion.CollodionAnalyzer;
import com.fulmicoton.collodion.processors.debug.DebugFilter;
import com.fulmicoton.collodion.processors.numberparser.NumberParserFilter;
import com.fulmicoton.collodion.processors.tokenpattern.TokenPatternFilter;
import com.fulmicoton.collodion.processors.vocabularymatcher.MatchingMethod;
import com.fulmicoton.collodion.processors.vocabularymatcher.Rule;
import com.fulmicoton.collodion.processors.vocabularymatcher.VocabularyFilter;
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
import org.apache.lucene.document.Document;
import org.json.JSONObject;
import org.yaml.snakeyaml.Yaml;

import java.io.Reader;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class JSON {


    private static Gson GSON = gsonBuilder().create();

    public static <T> T fromJson(final Reader input, Class<T> klass) {
        return GSON.fromJson(input, klass);
    }

    public static <T> T fromJson(final String str, Class<T> klass) {
        return GSON.fromJson(str, klass);
    }

    public static <T> String toJson(final T obj) {
        return GSON.toJson(obj);
    }

    public static <T> T fromYAML(final Reader yamlReader, Class<T> objType) {
        final Yaml yaml = new Yaml();
        final Map map = (Map) yaml.load(yamlReader);
        final JSONObject json = new JSONObject(map);
        return GSON.fromJson(json.toString(), objType);
    }


    static {
        ProcessorBuilderAdapter.register("vocabulary", VocabularyFilter.Builder.class);
        ProcessorBuilderAdapter.register("debug", DebugFilter.Builder.class);
        ProcessorBuilderAdapter.register("stem", StemFilter.Builder.class);
        ProcessorBuilderAdapter.register("tokenpattern", TokenPatternFilter.Builder.class);
        ProcessorBuilderAdapter.register("numberparser", NumberParserFilter.Builder.class);
    }

    public static GsonBuilder gsonBuilder() {
        final GsonBuilder gsonBuilder = new GsonBuilder()
            .registerTypeAdapter(CollodionAnalyzer.class, new CollodionAnalyzerAdapter())
            .registerTypeAdapter(ProcessorBuilder.class, new ProcessorBuilderAdapter())
            .registerTypeAdapter(Rule.class, new RuleAdapter())
            .registerTypeAdapter(Document.class, new DocumentAdapter())
            .setPrettyPrinting();
        registerAttributesAdapter(gsonBuilder);
        return gsonBuilder;
    }

    private static void registerAttributesAdapter(GsonBuilder gsonBuilder) {
        /*gsonBuilder.registerTypeAdapter(TypeAttribute.class, new JsonSerializer<TypeAttribute>() {
        })*/
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

    public static class CollodionAnalyzerAdapter implements JsonDeserializer<CollodionAnalyzer>, JsonSerializer<CollodionAnalyzer> {

        @Override
        public CollodionAnalyzer deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
            JsonObject jsonObj = json.getAsJsonObject();
            List<ProcessorBuilder> processors = new ArrayList<>();
            JsonArray processorsJson = jsonObj.get("processors").getAsJsonArray();
            for (JsonElement processorJson: processorsJson) {
                if (!processorJson.isJsonNull()) {
                    ProcessorBuilder processorBuilder = context.deserialize(processorJson, ProcessorBuilder.class);
                    processors.add(processorBuilder);
                }
            }
            return new CollodionAnalyzer(processors);
        }

        @Override
        public JsonElement serialize(CollodionAnalyzer src, Type typeOfSrc, JsonSerializationContext context) {
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
            if (processorType == null) {
                throw new IllegalArgumentException("Could not find processor of type <" + processorTypeName + ">. Have you registered it in the ProcessorBuilderAdapter?");
            }
            return context.deserialize(json, processorType);
        }

    }


}
