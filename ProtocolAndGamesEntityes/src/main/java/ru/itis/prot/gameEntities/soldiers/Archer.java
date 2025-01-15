package ru.itis.prot.gameEntities.soldiers;

public class Archer extends AbstractRadiusAttacker {

    public Archer() {
        super(2,6,5);
        health=6;
        damage=3;
        movementradius=3;
    }

    @Override
    public void action(AbstractSoldier soldier) {
        soldier.setHealth(soldier.getHealth()-damage);
    }
}
