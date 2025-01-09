package org.example.GameEntities.soldiers;

import lombok.Getter;
import lombok.Setter;
import org.example.GameEntities.AbstractEntity;

@Getter
@Setter
public abstract class AbstractSoldier extends AbstractEntity {
    protected int INDEX;
    protected int health;
    protected int damage;
    protected int movementradius;
    protected final int MAXHEALTH;


    public AbstractSoldier(int INDEX, int MAXHEALTH) {
        this.INDEX = INDEX;
        this.MAXHEALTH = MAXHEALTH;
    }

    public abstract void action(AbstractSoldier soldier);
}
