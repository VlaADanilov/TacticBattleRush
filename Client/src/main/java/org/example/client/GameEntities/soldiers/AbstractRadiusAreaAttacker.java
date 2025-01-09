package org.example.client.GameEntities.soldiers;

import lombok.Getter;

@Getter
public abstract class AbstractRadiusAreaAttacker extends AbstractSoldier {
    protected int damageRadius;
    protected int minDamageRadius;
    public AbstractRadiusAreaAttacker(int INDEX,int MAXHEALTH, int damageRadius,int minDamageRadius) {
        super(INDEX, MAXHEALTH);
        this.damageRadius = damageRadius;
        this.minDamageRadius = minDamageRadius;
    }
}
