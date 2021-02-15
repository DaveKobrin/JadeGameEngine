package Jade;

import java.util.ArrayList;
import java.util.List;

public class GameObject {
    private String name;
    private List<Component> components = new ArrayList<>();
    protected Transform transform;
    private int zIndex = 0;

    public GameObject(String name) {
        this.name = name;
        this.transform = new Transform();
        this.zIndex = 0;
    }

    public GameObject(String name, Transform transform, int zIndex) {
        this.name = name;
        this.transform = new Transform(transform);
        this.zIndex = zIndex;
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
        this.components.add(c);
        c.gameObject = this;
    }

    public void update(float dt) {
        for (int i=0; i<this.components.size(); ++i) {
            this.components.get(i).update(dt);
        }
    }

    public void start() {
        for (int i=0; i<this.components.size(); ++i) {
            this.components.get(i).start();
        }
    }

    public Transform getTransform() {
        return transform;
    }

    public int getzIndex() {
        return this.zIndex;
    }

    public void setzIndex(int zIndex) {
        this.zIndex = zIndex;
    }
}
