package ru.itis.prot.gameEntities.soldiers;

public class HorseKnight extends AbstractRadiusAttacker {
    public HorseKnight() {
        super(4,15,1);
        health=15;
        damage=5;
        movementradius=4;
    }

    @Override
    public void action(AbstractSoldier soldier) {
        soldier.setHealth(soldier.getHealth()-damage);
    }
}
