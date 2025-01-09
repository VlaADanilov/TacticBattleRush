package org.example.client.GameEntities.soldiers;

public class Mortar extends AbstractRadiusAreaAttacker{

    public Mortar() {
        super(5, 10, 10, 3);
        health=10;
        damage=5;
        movementradius=1;
    }

    @Override
    public void action(AbstractSoldier soldier) {
        soldier.setHealth(soldier.getHealth()-damage);
    }
}
