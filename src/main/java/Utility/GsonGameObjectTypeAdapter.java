package Utility;

import Jade.Component;
import Jade.GameObject;
import Jade.Transform;
import com.google.gson.*;

import java.lang.reflect.Type;

public class GsonGameObjectTypeAdapter implements JsonDeserializer<GameObject> {
    @Override
    public GameObject deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
        JsonObject jsonObject = json.getAsJsonObject();
        String name = jsonObject.get("name").getAsString();
        JsonArray components = jsonObject.getAsJsonArray("components");

        GameObject go = new GameObject(name);
        for(JsonElement element : components) {
            Component component = context.deserialize(element, Component.class);
            go.addComponent(component);
        }
        go.setTransform(go.getComponent(Transform.class));
        return go;
    }
}
