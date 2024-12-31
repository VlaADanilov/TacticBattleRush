package org.example.GameEntities.fabrica;



import org.example.GameEntities.soldiers.*;

import java.util.Random;

public class SoldierFabrica {

    public static synchronized Archer getArcher(){
        return new Archer();
    }

    public static synchronized HeavyKnight getHeavyKnight(){
        return new HeavyKnight();
    }

    public static synchronized Hiller getHiller(){
        return new Hiller();
    }

    public static synchronized HorseKnight getHorseKnight(){
        return new HorseKnight();
    }

    public static synchronized AbstractSoldier getSoldier(){
        Random rand = new Random();
        int i = rand.nextInt(1,4 + 1);
        return getSoldier(i);
    }
    public static synchronized AbstractSoldier getSoldier(int i){
        AbstractSoldier soldier = switch (i) {
            case 2 -> getArcher();
            case 1 -> getHeavyKnight();
            case 3 -> getHiller();
            case 4 -> getHorseKnight();
            default -> null;
        };
        return soldier;
    }
}
