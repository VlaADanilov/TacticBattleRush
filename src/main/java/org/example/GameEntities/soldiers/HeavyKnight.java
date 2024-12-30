package org.example.GameEntities.soldiers;

public class HeavyKnight extends AbstractSoldier{
    public HeavyKnight() {
        health=25;
        damage=7;
        movementradius=1;
        damageRadius=1;
    }

    @Override
    public void action(AbstractSoldier soldier) {
        soldier.setHealth(soldier.getHealth()-damage);
    }
}
