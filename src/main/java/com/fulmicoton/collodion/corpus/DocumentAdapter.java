package com.fulmicoton.collodion.corpus;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.FieldType;
import org.apache.lucene.index.IndexableField;

import java.lang.reflect.Type;
import java.util.Map;

public class DocumentAdapter implements JsonDeserializer<Document>, JsonSerializer<Document> {

    @Override
    public Document deserialize(JsonElement jsonElement, Type type, JsonDeserializationContext jsonDeserializationContext) throws JsonParseException {
        final Document doc = new Document();
        final JsonObject jsonObj = jsonElement.getAsJsonObject();
        final FieldType fieldType = new FieldType();
        fieldType.setStored(true);
        for (Map.Entry<String, JsonElement> e: jsonObj.entrySet()) {
            final String key = e.getKey();
            final String val = e.getValue().getAsString();
            final Field field = new Field(key, val, fieldType);
            doc.add(field);
        }
        return doc;
    }

    @Override
    public JsonElement serialize(Document doc, Type type, JsonSerializationContext jsonSerializationContext) {
        JsonObject jsonObject = new JsonObject();
        for (IndexableField field: doc) {
            final String key = field.name();
            final String val = field.stringValue();
            jsonObject.addProperty(key, val);
        }
        return jsonObject;
    }
}
