package Jade;

import com.google.gson.annotations.JsonAdapter;
import imgui.ImGui;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

/**
 * GameObject - An object in the game world. Maintains its position, scale, name, and all
 *              of its components
 */
@JsonAdapter(Utility.GsonGameObjectTypeAdapter.class)
public class GameObject {

    private static int ID_COUNTER = 0; //generate unique universal id (uid) for each game object
    private int uid = -1;   //the universal ID for this game object

    private String name;
    @Getter @Setter
    protected transient Transform transform;
//    private int zIndex = 0;
    private List<Component> components = new ArrayList<>();
    @Getter
    private boolean doSerialize = true;


    public GameObject(String name) {
//            public GameObject(String name, Transform transform, int zIndex) {
        this.name = name;
//        this.transform = new Transform(transform);
//        this.zIndex = zIndex;
        this.uid = ID_COUNTER++;
    }
    public <T extends Component> T getComponent(Class<T> componentClass) {
        for (Component c : components) {
            if (componentClass.isAssignableFrom(c.getClass())) {
                try {
                    return componentClass.cast(c);
                } catch (ClassCastException e) {
                    e.printStackTrace();
                    assert false : "Error casting component.";
                }
            }
        }
        return null;
    }

    public <T extends Component> void removeComponent(Class<T> componentClass) {
        //TODO possible concurrency issues here!
        for (int i=0; i<this.components.size(); ++i) {
            Component c = this.components.get(i);
            if (componentClass.isAssignableFrom(c.getClass())) {
                this.components.remove(i);
                return;
            }
        }
    }

    public void addComponent(Component c) {
        c.generateUID();
        this.components.add(c);
        c.gameObject = this;
    }

    public void update(float dt) {
        for (Component component : this.components) {
            component.update(dt);
        }
    }

    public void start() {
        for (Component component : this.components) {
            component.start();
        }
    }

    public void imGui() {
        for (Component c : components) {
            if(ImGui.collapsingHeader(c.getClass().getSimpleName()))
                c.imGui();
        }
    }

//    public int getzIndex() {
//        return this.zIndex;
//    }

//    public void setzIndex(int zIndex) {
//        //TODO must move associated sprite to different render batch with correct z index
//        this.zIndex = zIndex;
//    }

    public static void setIdCounter(int newMinCounter) {
        ID_COUNTER = newMinCounter;
    }

    public int getUid() { return this.uid; }

    public int getMaxCompUID() {
        int maxCompUID = -1;
        for (Component c : components) {
            if (c.getUid() > maxCompUID)
                maxCompUID = c.getUid();
        }
        return maxCompUID;
    }

    public void setNoSerialize(){
        doSerialize = false;
    }

}
