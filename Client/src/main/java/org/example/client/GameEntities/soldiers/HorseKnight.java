package org.example.client.GameEntities.soldiers;

public class HorseKnight extends AbstractSoldier{
    public HorseKnight() {
        super(4);
        health=15;
        damage=5;
        movementradius=4;
        damageRadius=1;
    }

    @Override
    public void action(AbstractSoldier soldier) {
        soldier.setHealth(soldier.getHealth()-damage);
    }
}
