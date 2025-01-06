package org.example.client.util;

import org.example.client.GameEntities.soldiers.AbstractSoldier;
import org.example.client.GameEntities.soldiers.Archer;
import org.example.client.GameEntities.soldiers.HeavyKnight;
import org.example.client.GameEntities.soldiers.HorseKnight;

import java.util.ArrayList;
import java.util.List;

public class SoldierSpeciality {
    private static List<Class<? extends AbstractSoldier>> damageOpponent;

    static{
        damageOpponent = new ArrayList<>();
        damageOpponent.add(HeavyKnight.class);
        damageOpponent.add(HorseKnight.class);
        damageOpponent.add(Archer.class);
    }

    public static boolean isActionForOpponent(AbstractSoldier soldier){
        return damageOpponent.contains(soldier.getClass());
    }
}
