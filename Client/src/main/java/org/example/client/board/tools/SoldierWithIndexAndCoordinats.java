package org.example.client.board.tools;

import lombok.Getter;
import lombok.Setter;
import ru.itis.prot.gameEntities.soldiers.AbstractSoldier;


@Getter
@Setter
public class SoldierWithIndexAndCoordinats {
    private int index;
    private AbstractSoldier soldier;
    private int row;
    private int col;

    public SoldierWithIndexAndCoordinats(AbstractSoldier soldier, int row, int col) {
        this.soldier = soldier;
        this.row = row;
        this.col = col;
    }
}
