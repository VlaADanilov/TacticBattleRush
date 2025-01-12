package ru.itis.prot.gameEntities.fabrica;



import ru.itis.prot.gameEntities.soldiers.*;

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
        int i = rand.nextInt(1,5 + 1);
        return getSoldier(i);
    }
    public static synchronized AbstractSoldier getSoldier(int i){
        AbstractSoldier soldier = switch (i) {
            case 2 -> getArcher();
            case 1 -> getHeavyKnight();
            case 3 -> getHiller();
            case 4 -> getHorseKnight();
            case 5 -> getMortar();
            default -> null;
        };
        return soldier;
    }

    private static AbstractSoldier getMortar() {
        return new Mortar();
    }
}
