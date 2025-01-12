package ru.itis.prot.gameEntities;


import ru.itis.prot.gameEntities.soldiers.*;

import java.util.ArrayList;
import java.util.List;

public class SoldierSpeciality {
    private static final List<Class<? extends AbstractSoldier>> damageOpponent;

    static{
        damageOpponent = new ArrayList<>();
        damageOpponent.add(HeavyKnight.class);
        damageOpponent.add(HorseKnight.class);
        damageOpponent.add(Archer.class);
        damageOpponent.add(Mortar.class);
    }

    public static boolean isActionForOpponent(AbstractSoldier soldier){
        return damageOpponent.contains(soldier.getClass());
    }

    public static boolean oneDoingByHod(AbstractSoldier soldier){

        return soldier.getClass().equals(Archer.class) || soldier.getClass().equals(Mortar.class);
    }
}
