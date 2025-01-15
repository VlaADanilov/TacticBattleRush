package ru.itis.prot.gameEntities.fabrica;


import ru.itis.prot.gameEntities.elements.AbstractElement;
import ru.itis.prot.gameEntities.elements.Stone;
import ru.itis.prot.gameEntities.elements.Tree;

import java.util.Random;

public class ElementFabrica {
    public static synchronized AbstractElement getElement() {
        Random rand = new Random();
        if(rand.nextBoolean()){
            return new Stone();
        }else{
            return new Tree();
        }
    }

    public static synchronized AbstractElement getElement(int index) {
        AbstractElement element = null;
        switch (index) {
            case 1: element = new Stone(); break;
            case 2: element = new Tree(); break;
        }
        return element;
    }
}
