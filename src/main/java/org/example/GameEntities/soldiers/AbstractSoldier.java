package org.example.GameEntities.soldiers;

import lombok.Getter;
import lombok.Setter;
import org.example.GameEntities.AbstractEntity;

@Getter
@Setter
public abstract class AbstractSoldier extends AbstractEntity {
    protected int health;
    protected int damage;
    protected int movementradius;
    protected int damageRadius;

    public abstract void action(AbstractSoldier soldier);
}
