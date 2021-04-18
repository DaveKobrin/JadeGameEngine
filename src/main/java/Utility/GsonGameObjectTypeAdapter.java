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
        Transform transform = context.deserialize(jsonObject.get("transform"), Transform.class);
        int zIndex = context.deserialize(jsonObject.get("zIndex"), int.class);

        GameObject go = new GameObject(name,transform,zIndex);
        for(JsonElement element : components) {
            Component component = context.deserialize(element, Component.class);
            go.addComponent(component);
        }
        return go;
    }
}
