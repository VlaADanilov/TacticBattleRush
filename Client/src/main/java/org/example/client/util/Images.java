package org.example.client.util;

import javafx.scene.image.Image;

public class Images {

    public synchronized static Image getSoldierImage(int i){
        return switch (i){
            case 1 -> new Image(Images.class.getResourceAsStream("/image/меч.png"));
            case 2 -> new Image(Images.class.getResourceAsStream("/image/лук.png"));
            case 3 -> new Image(Images.class.getResourceAsStream("/image/лечение.png"));
            case 4 -> new Image(Images.class.getResourceAsStream("/image/лошадь.png"));
            case 5 -> new Image(Images.class.getResourceAsStream("/image/мортира.png"));
            default -> throw new IllegalStateException("Unexpected value: " + i);
        };
    }

    public synchronized static Image getElementImage(int i){
        return switch (i){
            case 1 -> new Image(Images.class.getResourceAsStream("/image/камень.png"));
            case 2 -> new Image(Images.class.getResourceAsStream("/image/дерево.png"));
            default -> throw new IllegalStateException("Unexpected value: " + i);
        };
    }

    public synchronized static Image getGrassImage(){
        return new Image(Images.class.getResourceAsStream("/image/трава.png"));
    }

    public synchronized static Image getRezultImage(int i){
        return switch (i){
            case 1 -> new Image(Images.class.getResourceAsStream("/image/winner.png"));
            case 0 -> new Image(Images.class.getResourceAsStream("/image/loser.png"));
            default -> throw new IllegalStateException("Unexpected value: " + i);
        };
    }
}
