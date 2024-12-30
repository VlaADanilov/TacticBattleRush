package org.example.client.GameEntities.soldiers;


import lombok.Getter;
import lombok.Setter;
import org.example.client.GameEntities.AbstractEntity;

@Getter
@Setter
public abstract class AbstractSoldier extends AbstractEntity {
    protected final int INDEX;
    protected int health;
    protected int damage;
    protected int movementradius;
    protected int damageRadius;

    protected AbstractSoldier(int index) {
        INDEX = index;
    }

    public abstract void action(AbstractSoldier soldier);
}
