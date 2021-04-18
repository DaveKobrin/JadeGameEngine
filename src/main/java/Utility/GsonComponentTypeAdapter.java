package Utility;

import Jade.Component;
import com.google.gson.*;

import java.lang.reflect.Type;

/**
 * GsonComponentTypeAdapter - custom code to serialize and deserialize Jade.Component objects
 *                            this is needed because Component is an abstract base class
 *                            inherited by several different classes.
 */
public class GsonComponentTypeAdapter implements JsonSerializer<Component>, JsonDeserializer<Component> {

    @Override
    public Component deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
        JsonObject jsonObject = json.getAsJsonObject();
        String type = jsonObject.get("type").getAsString();
        JsonElement properties = jsonObject.get("properties");

        try {
            return context.deserialize(properties, Class.forName(type));
        } catch (ClassNotFoundException e) {
            throw new JsonParseException("Unknown type " + type, e);
        }
    }

    @Override
    public JsonElement serialize(Component src, Type typeOfSrc, JsonSerializationContext context) {
        JsonObject result = new JsonObject();
        result.add("type", new JsonPrimitive(src.getClass().getCanonicalName()));
        result.add("properties", context.serialize(src, src.getClass()));
        return result;
    }
}
