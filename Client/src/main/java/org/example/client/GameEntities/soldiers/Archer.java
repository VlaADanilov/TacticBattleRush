package org.example.client.GameEntities.soldiers;

public class Archer extends AbstractSoldier{

    public Archer() {
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
