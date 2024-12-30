package org.example.GameEntities.soldiers;

public class Hiller extends AbstractSoldier{
    public Hiller() {
        health=3;
        damage=3;
        movementradius=2;
        damageRadius=2;
    }

    @Override
    public void action(AbstractSoldier soldier) {
        soldier.setHealth(soldier.getHealth()+damage);
    }
}
