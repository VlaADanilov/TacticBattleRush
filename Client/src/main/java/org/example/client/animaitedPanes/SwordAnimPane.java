package org.example.client.animaitedPanes;

import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.scene.image.Image;
import javafx.scene.transform.Rotate;
import javafx.util.Duration;
import org.example.client.util.Images;

public class SwordAnimPane extends AbstractAnimatedPane{
    protected Timeline timeline;
    public SwordAnimPane(boolean left) {
        super(new Image(Images.class.getResourceAsStream("/image/anim_sword.png")));
        double swordWidth = imageView.getImage().getWidth();
        double swordHeight = imageView.getImage().getHeight();
        double pivotX = imageView.getX() + swordWidth / 2;
        double pivotY = imageView.getY() + swordHeight;

        Rotate rotate = new Rotate(0, pivotX,pivotY); // Вращение вокруг верхней средней точки

        imageView.getTransforms().add(rotate);
        if(!left) {

            imageView.getTransforms().add(rotate);
            // Анимация звона
            timeline = new Timeline(
                    new KeyFrame(Duration.millis(0), new KeyValue(rotate.angleProperty(), 0)),
                    new KeyFrame(Duration.millis(100), new KeyValue(rotate.angleProperty(), -30)),
                    new KeyFrame(Duration.millis(200), new KeyValue(rotate.angleProperty(), 0))
            );

        }else {
            timeline = new Timeline(
                    new KeyFrame(Duration.millis(0), new KeyValue(rotate.angleProperty(), 0)),
                    new KeyFrame(Duration.millis(100), new KeyValue(rotate.angleProperty(), 30)),
                    new KeyFrame(Duration.millis(200), new KeyValue(rotate.angleProperty(), 0))
            );
        }
        timeline.setCycleCount(2);
        timeline.setAutoReverse(false);
    }

    @Override
    public void startAnim() {
        timeline.play();
    }
}
