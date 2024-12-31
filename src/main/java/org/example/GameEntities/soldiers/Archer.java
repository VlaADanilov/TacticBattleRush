package org.example.GameEntities.soldiers;

public class Archer extends AbstractSoldier{

    public Archer() {
        super(2);
        health=6;
        damage=3;
        movementradius=3;
        damageRadius=5;
    }

    @Override
    public void action(AbstractSoldier soldier) {
        soldier.setHealth(soldier.getHealth()-damage);
    }
}
