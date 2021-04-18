package Jade;

import Utility.TypeClassMap;
import com.google.gson.annotations.JsonAdapter;
import imgui.ImGui;
import org.joml.Vector2f;
import org.joml.Vector3f;
import org.joml.Vector4f;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;

/**
 * Component - base class that allows for polymorphic processing of GameObject components
 */
@JsonAdapter(Utility.GsonComponentTypeAdapter.class)
public abstract class Component {

    private static int ID_COUNTER = 0; //generate unique universal id (uid) for each component
    private int uid = -1;   //the universal ID for this component

    public transient GameObject gameObject = null;

    /**
     * start() - is called once on each component AFTER all components are created for a
     *           GameObject. This allows for components to check for and rely on the existence
     *           of other components while initializing.
     */
    public void start(){
        /* Base class does nothing here.
         * subclasses can override for init code
         * requiring existing of other components
         */
    }

    /**
     * imGui() - Uses reflection to allow level editor to access members of component types
     *           to modify attributes
     */
    public void imGui() {
        try {
            Field[] fields = this.getClass().getDeclaredFields();
            for (Field field: fields) {

                boolean isTransient = Modifier.isTransient(field.getModifiers());
                if (isTransient)
                    continue;

                boolean isPrivate = Modifier.isPrivate(field.getModifiers());
                if (isPrivate) {
                    field.setAccessible(true);
                }

                Class rawType = field.getType();
                Object value = field.get(this);
                String name = field.getName();

                TypeClassMap.Types type = TypeClassMap.classToEnumType(rawType);
                switch (type) {
                    case STRING:
                        break;
                    case BOOLEAN:
                        boolean imBool = (boolean) value;
                        if (ImGui.checkbox(name + ":", imBool)) {
                            field.set(this, !imBool);
                        }
                        break;
                    case BYTE:
                        break;
                    case SHORT:
                        break;
                    case INT:
                        int[] imInt = {(int)value};
                        if (ImGui.dragInt(name + ":",imInt)) {
                            field.set(this, imInt[0]);
                        }
                        break;
                    case LONG:
                        break;
                    case FLOAT:
                        float[] imFloat = {(float)value};
                        if (ImGui.dragFloat(name + ":",imFloat)) {
                            field.set(this, imFloat[0]);
                        }
                        break;
                    case DOUBLE:
                        break;
                    case VECTOR2F:
                        Vector2f val2 = (Vector2f) value;
                        float[] imVec2 = {val2.x, val2.y};
                        if (ImGui.dragFloat2(name + ":", imVec2)) {
                            val2.set(imVec2[0], imVec2[1]);
                        }
                        break;
                    case VECTOR3F:
                        Vector3f val3 = (Vector3f) value;
                        float[] imVec3 = {val3.x, val3.y, val3.z};
                        if (ImGui.dragFloat3(name + ":", imVec3)) {
                            val3.set(imVec3[0], imVec3[1], imVec3[2]);
                        }
                        break;
                    case VECTOR4F:
                        Vector4f val4 = (Vector4f) value;
                        float[] imVec4 = {val4.x, val4.y, val4.z};
                        if (ImGui.dragFloat4(name + ":", imVec4)) {
                            val4.set(imVec4[0], imVec4[1], imVec4[2], imVec4[3]);
                        }
                        break;
                    case OBJECT:
                        break;
                    default:
                }

                if (isPrivate) {
                    field.setAccessible(false);
                }
            }
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
    }

    /**
     * update(dt) - is called every frame on each GameObject and all its associated components.
     *              This is for the game logic processing of all objects.
     * @param dt - (delta time) elapsed time in milliseconds since the last frame finished
     */
    public void update(float dt) {
        /* Base class does nothing here.
         * subclasses should override for updates
         * called each frame
         */
    }

    public static void setIdCounter(int newMinCounter) {
        ID_COUNTER = newMinCounter;
    }

    public void generateUID() {
        if (this.uid == -1) {
            uid = ID_COUNTER++;
        }
    }

    public int getUid() { return this.uid; }

}
