package org.example.GameEntities.soldiers;

import lombok.Getter;

@Getter
public abstract class AbstractRadiusAreaAttacker extends AbstractRadiusAttacker {
    protected int minDamageRadius;
    public AbstractRadiusAreaAttacker(int INDEX,int MAXHEALTH, int damageRadius,int minDamageRadius) {
        super(INDEX, MAXHEALTH,damageRadius);
        this.minDamageRadius = minDamageRadius;
    }
}
