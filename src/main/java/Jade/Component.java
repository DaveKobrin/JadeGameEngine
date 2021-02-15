package Jade;

public abstract class Component {

    public GameObject gameObject = null;

    public void start(){
        /* Base class does nothing here.
         * subclasses can override for init code
         * requiring existing of other components
         */
    }

    public abstract void update(float dt);  // called for each for every frame

}
