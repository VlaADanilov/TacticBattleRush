package org.example.client.util;

import javafx.scene.image.Image;

public class Images {

    public synchronized static Image getSoldierImage(int i){
        return switch (i){
            case 1 -> new Image(Images.class.getResourceAsStream("/image/меч.jpg"));
            case 2 -> new Image(Images.class.getResourceAsStream("/image/лук.png"));
            case 3 -> new Image(Images.class.getResourceAsStream("/image/лечение.png"));
            case 4 -> new Image(Images.class.getResourceAsStream("/image/лошадь.png"));
            default -> throw new IllegalStateException("Unexpected value: " + i);
        };
    }

    public synchronized static Image getElementImage(int i){
        return switch (i){
            case 1 -> new Image(Images.class.getResourceAsStream("/image/камень.jpg"));
            case 2 -> new Image(Images.class.getResourceAsStream("/image/дерево.png"));
            default -> throw new IllegalStateException("Unexpected value: " + i);
        };
    }
}
