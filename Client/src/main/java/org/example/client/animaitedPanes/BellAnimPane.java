package org.example.client.animaitedPanes;

import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.scene.image.Image;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.scene.transform.Rotate;
import javafx.util.Duration;
import org.example.client.util.Images;

import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import java.io.BufferedInputStream;
import java.io.File;


public class BellAnimPane extends AbstractAnimatedPane {
    private Timeline timeline;
    private boolean flag;
    public BellAnimPane() {
        super(new Image(Images.class.getResourceAsStream("/image/колокол.png")));
        flag = true;
        Rotate rotate = new Rotate(0, imageView.getFitWidth() / 2, 0); // Вращение вокруг верхней средней точки
        imageView.getTransforms().add(rotate);
        // Анимация звона
        Timeline timeline = new Timeline(
                new KeyFrame(Duration.millis(0), new KeyValue(rotate.angleProperty(), 0)),
                new KeyFrame(Duration.millis(100), new KeyValue(rotate.angleProperty(), -30)),
                new KeyFrame(Duration.millis(300), new KeyValue(rotate.angleProperty(), 30)),
                new KeyFrame(Duration.millis(400), new KeyValue(rotate.angleProperty(), 0))
        );
        timeline.setCycleCount(2);
        timeline.setAutoReverse(false);
        this.timeline = timeline;
    }

    @Override
    public void startAnim() {
        if(flag){
            playSound();
        }
        timeline.play();
    }

    private void playSound() {
        try {
            Media media = new Media(BellAnimPane.class.getResource("/audio/bell.mp3").toURI().toString());
            MediaPlayer mediaPlayer = new MediaPlayer(media);
            mediaPlayer.play();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public boolean changeSoundFlag(){
        flag = !flag;
        return flag;
    }
}
