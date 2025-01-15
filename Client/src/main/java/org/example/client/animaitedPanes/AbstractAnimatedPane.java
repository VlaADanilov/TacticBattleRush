package org.example.client.animaitedPanes;

import javafx.animation.Timeline;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Pane;

public abstract class AbstractAnimatedPane extends Pane {

    protected ImageView imageView;

    public AbstractAnimatedPane(Image image) {
        this.imageView = new ImageView(image);
        imageView.setFitHeight(image.getHeight());
        imageView.setFitWidth(image.getWidth());
        imageView.setPreserveRatio(true);
        getChildren().add(imageView);
    }

    public abstract void startAnim();
}
