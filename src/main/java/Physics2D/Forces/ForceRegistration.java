package Physics2D.Forces;

import Physics2D.RigidBody.RigidBody2D;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter @Setter @AllArgsConstructor
public class ForceRegistration {
    private ForceGenerator forceGen;
    private RigidBody2D rigidBody2D;

    @Override
    public boolean equals(Object object) {
        if (object == null)
            return false;
        if (object.getClass() != ForceRegistration.class)
            return false;
        ForceRegistration test = (ForceRegistration) object;
        return this.forceGen == test.forceGen && this.rigidBody2D == test.rigidBody2D;
    }
}
