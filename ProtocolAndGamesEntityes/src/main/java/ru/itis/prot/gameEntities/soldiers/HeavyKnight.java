package ru.itis.prot.gameEntities.soldiers;

public class HeavyKnight extends AbstractRadiusAttacker {
    public HeavyKnight() {
        super(1,25,1);
        health=25;
        damage=7;
        movementradius=1;
    }

    @Override
    public void action(AbstractSoldier soldier) {
        soldier.setHealth(soldier.getHealth()-damage);
    }
}
