package ru.itis.prot.gameEntities.soldiers;

import lombok.Getter;

@Getter
public abstract class AbstractRadiusAttacker extends AbstractSoldier {
    protected int damageRadius;

    public AbstractRadiusAttacker(int INDEX,int MAXHEALTH, int damageRadius) {
        super(INDEX, MAXHEALTH);
        this.damageRadius = damageRadius;
    }
}
