package org.example.client.controllers;

import javafx.fxml.FXML;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import org.example.client.util.Images;
import ru.itis.prot.gameEntities.SoldierSpeciality;
import ru.itis.prot.gameEntities.soldiers.AbstractRadiusAreaAttacker;
import ru.itis.prot.gameEntities.soldiers.AbstractRadiusAttacker;
import ru.itis.prot.gameEntities.soldiers.AbstractSoldier;
import ru.itis.prot.gameEntities.soldiers.Hiller;

import java.util.List;

public class InfoController {
    @FXML
    private VBox box;

    public void initialize() {
        box.setSpacing(25);
        List<AbstractSoldier> abstractSoldiers = SoldierSpeciality.allSoldiers();
        for (AbstractSoldier abstractSoldier : abstractSoldiers) {
            HBox hBox = new HBox();
            hBox.setSpacing(10);
            ImageView imageView = new ImageView();
            imageView.setFitWidth(80);
            imageView.setFitHeight(80);
            imageView.setImage(Images.getSoldierImage(abstractSoldier.getINDEX()));
            hBox.getChildren().add(imageView);
            VBox vBox = new VBox();

            Text text = new Text(SoldierSpeciality.nameSoldier(abstractSoldier));
            text.setStyle("-fx-font-weight: bold; -fx-font-size: 15px;");
            vBox.getChildren().add(text);

            vBox.getChildren().add(new Text("Health = " + abstractSoldier.getHealth()));
            if(!(abstractSoldier instanceof Hiller)){
                vBox.getChildren().add(new Text("Damage = " + abstractSoldier.getDamage()));
            }else{
                vBox.getChildren().add(new Text("Hill = " + abstractSoldier.getDamage()));
            }
            vBox.getChildren().add(new Text("Move = " + abstractSoldier.getMovementradius()));
            if(abstractSoldier instanceof AbstractRadiusAreaAttacker){
                vBox.getChildren().add(new Text("MinDamageRadius = " +
                        ((AbstractRadiusAreaAttacker) abstractSoldier).getMinDamageRadius()));
                vBox.getChildren().add(new Text("MaxDamageRadius = " +
                        ((AbstractRadiusAreaAttacker) abstractSoldier).getDamageRadius()));
            }else{
                vBox.getChildren().add(new Text("DamageRadius = " +
                        ((AbstractRadiusAttacker) abstractSoldier).getDamageRadius()));
            }
            hBox.getChildren().add(vBox);
            box.getChildren().add(hBox);
        }
    }
}
