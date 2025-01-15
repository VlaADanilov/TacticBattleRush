package ru.itis.prot.gameEntities.soldiers;

public class Hiller extends AbstractRadiusAttacker {
    public Hiller() {
        super(3,3,2);
        health=3;
        damage=3;
        movementradius=2;
    }

    @Override
    public void action(AbstractSoldier soldier) {
        soldier.setHealth(soldier.getHealth()+damage);
        if(soldier.getHealth() > soldier.getMAXHEALTH()){
            soldier.setHealth(soldier.getMAXHEALTH());
        }
    }
}
