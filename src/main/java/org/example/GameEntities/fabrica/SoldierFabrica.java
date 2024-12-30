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
        AbstractSoldier soldier = null;
        switch(i){
            case 1: soldier = getArcher(); break;
            case 2: soldier = getHeavyKnight(); break;
            case 3: soldier = getHiller(); break;
            case 4: soldier = getHorseKnight(); break;
        }
        return soldier;
    }
}
