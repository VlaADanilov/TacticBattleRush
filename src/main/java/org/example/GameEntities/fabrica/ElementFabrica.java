package org.example.GameEntities.fabrica;

import org.example.GameEntities.AbstractEntity;
import org.example.GameEntities.elements.Stone;
import org.example.GameEntities.elements.Tree;

import java.util.Random;

public class ElementFabrica {
    public static synchronized AbstractEntity getElement() {
        Random rand = new Random();
        if(rand.nextBoolean()){
            return new Stone();
        }else{
            return new Tree();
        }
    }

    public static synchronized AbstractEntity getElement(int index) {
        AbstractEntity element = null;
        switch (index) {
            case 1: element = new Stone(); break;
            case 2: element = new Tree(); break;
        }
        return element;
    }
}
