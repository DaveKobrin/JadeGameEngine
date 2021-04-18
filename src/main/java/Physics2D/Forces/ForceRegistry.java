package Physics2D.Forces;

import Physics2D.RigidBody.RigidBody2D;

import java.util.ArrayList;
import java.util.List;

public class ForceRegistry {
    private List<ForceRegistration> forceRegistrations = new ArrayList<>();

    public void add(ForceGenerator forceGen, RigidBody2D rigidBody) {
        ForceRegistration forceRegistration = new ForceRegistration(forceGen,rigidBody);
        this.forceRegistrations.add(forceRegistration);
    }

    public void remove(ForceGenerator forceGen, RigidBody2D rigidBody) {
        ForceRegistration forceRegistration = new ForceRegistration(forceGen,rigidBody);
        this.forceRegistrations.remove(forceRegistration);
    }
    public void clear(){
        this.forceRegistrations.clear();
    }

    public void updateForces(float dt) {
        for (ForceRegistration forceReg : forceRegistrations) {
            forceReg.getForceGen().updateForce(forceReg.getRigidBody2D(), dt);
        }
    }
}
