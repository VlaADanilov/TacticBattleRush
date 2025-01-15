package ru.itis.prot.gameEntities;


import ru.itis.prot.gameEntities.fabrica.SoldierFabrica;
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

    public static List<AbstractSoldier> allSoldiers(){
        List<AbstractSoldier> soldiers = new ArrayList<>();
        for(int i = 1; i <= 5; i++){
            soldiers.add(SoldierFabrica.getSoldier(i));
        }
        return soldiers;
    }

    public static String nameSoldier(AbstractSoldier soldier){
        if (soldier instanceof HeavyKnight) {
            return "Heavy knight";
        }
        if (soldier instanceof Archer) {
            return "Archer";
        }
        if (soldier instanceof Hiller) {
            return "Hiller";
        }
        if (soldier instanceof HorseKnight) {
            return "Horse knight";
        }
        if(soldier instanceof Mortar){
            return "Mortar";
        }
        return "WTF";
    }

    public static boolean indexInRight(AbstractSoldier soldier){
        if(soldier instanceof HeavyKnight || soldier instanceof Archer){
            return true;
        }
        return false;
    }
}
