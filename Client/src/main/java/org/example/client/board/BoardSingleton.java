package org.example.client.board;

import javafx.util.Pair;
import org.example.client.board.tools.SoldierWithIndexAndCoordinats;
import ru.itis.prot.gameEntities.AbstractEntity;
import ru.itis.prot.gameEntities.soldiers.*;
import ru.itis.prot.gameEntities.fabrica.*;
import ru.itis.prot.gameEntities.elements.*;


import java.util.*;
import java.util.stream.Collectors;



public class BoardSingleton {
    private final AbstractEntity[][] board;
    private List<SoldierWithIndexAndCoordinats> mySoldiers;
    private List<SoldierWithIndexAndCoordinats> opponentSoldiers;

    private static BoardSingleton instance;
    private BoardSingleton() {
        board = new AbstractEntity[15][15];
        mySoldiers = new ArrayList<>();
        opponentSoldiers = new ArrayList<>();
    }
    public synchronized static BoardSingleton getInstance() {
        if (instance == null) {
            instance = new BoardSingleton();
        }
        return instance;
    }

    public void addElement(AbstractElement element, int column, int row) {
        board[row][column] = element;
    }

    public void addMySoldier(AbstractSoldier soldier, int column, int row) {
        board[row][column] = soldier;
        mySoldiers.add(new SoldierWithIndexAndCoordinats(soldier, row, column));
    }

    public void removeMySoldier(int column, int row){
        board[row][column] = null;
        mySoldiers = mySoldiers.stream().filter((s)->
                s.getCol() != column || s.getRow() != row).collect(Collectors.toList());
    }

    public boolean checkForNull(int column, int row){
        return board[row][column] == null;
    }

    public boolean checkForSoldier(int column, int row){
        return board[row][column] != null && board[row][column] instanceof AbstractSoldier;
    }

    public AbstractEntity[][] getBoard() {
        return board;
    }

    public byte[] getMySoldiersMessage(){
        byte[] arr = new byte[mySoldiers.size()*3];
        for(int i = 0; i < mySoldiers.size(); i++){
            SoldierWithIndexAndCoordinats soldier = mySoldiers.get(i);
            arr[i*3] = (byte) soldier.getSoldier().getINDEX();
            arr[i*3+1] = (byte) soldier.getCol();
            arr[i*3+2] = (byte) soldier.getRow();
        }
        return arr;
    }

    public int readCoordinateMessage(byte[] arr){
        int ret = arr[0];

        int cntOfUnitsOnePerson = mySoldiers.size();

        for(int i = 1; i <= cntOfUnitsOnePerson; i++){
            mySoldiers.get(i - 1).setIndex(arr[i]);
        }
        for(int i = cntOfUnitsOnePerson + 1; i < arr.length; i+=4){
            AbstractSoldier abstractSoldier = SoldierFabrica.getSoldier(arr[i+1]);
            SoldierWithIndexAndCoordinats soldier = new SoldierWithIndexAndCoordinats(
                    abstractSoldier,
                    arr[i+3],
                    arr[i+2]
            );
            soldier.setIndex(arr[i]);
            opponentSoldiers.add(soldier);
            board[arr[i+3]][arr[i+2]] = abstractSoldier;
        }
        return ret;
    }

    public boolean isMySoldier(AbstractSoldier soldier){
        for(SoldierWithIndexAndCoordinats s : mySoldiers){
            if(s.getSoldier() == soldier) return true;
        }
        return false;
    }

    public boolean isMySoldier(int index){
        for(SoldierWithIndexAndCoordinats s : mySoldiers){
            if(s.getIndex() == index) return true;
        }
        return false;
    }

    public List<java.util.Map.Entry<Integer,Integer>> getMySoldiersCoordinates(){
        List<java.util.Map.Entry<Integer,Integer>> list = new ArrayList<>();
        for(int i = 0; i < mySoldiers.size(); i++){
            SoldierWithIndexAndCoordinats soldier = mySoldiers.get(i);
            list.add(new AbstractMap.SimpleEntry<>(soldier.getCol(),soldier.getRow()));
        }
        return list;
    }

    public SoldierWithIndexAndCoordinats getMySoldierByCoordinates(int column, int row){
        List<SoldierWithIndexAndCoordinats> mySoldiers1 = mySoldiers;
        for(SoldierWithIndexAndCoordinats s : mySoldiers){
            if(s.getCol() == column && s.getRow() == row){
                return s;
            }
        }
        throw new RuntimeException();
    }

    public Set<Pair<Integer,Integer>> getMovementPositions(int index){
        Set<Pair<Integer,Integer>> movementPositions = new HashSet<>();
        SoldierWithIndexAndCoordinats soldierByIndex = getSoldierByIndex(index);
        int playerRow = soldierByIndex.getRow();
        int playerColumn = soldierByIndex.getCol();
        int movementRadius = soldierByIndex.getSoldier().getMovementradius();

        for (int row = playerRow - movementRadius; row <= playerRow + movementRadius; row++) {
            if(row < 0 || row >= board.length) continue;
            for(int column = playerColumn - movementRadius; column <= playerColumn + movementRadius; column++) {
                if(column < 0 || column >= board[0].length) continue;
                if(row == playerRow && column == playerColumn) continue;
                if(board[row][column] == null && !hasObstacleWalking(row, column, playerRow, playerColumn)){
                    movementPositions.add(new Pair<>(row, column));
                }
            }
        }

        return movementPositions;
    }

    private boolean hasObstacleWalking(int targetRow, int targetColumn, int unitRow, int unitColumn){
        // Если цель совпадает с персонажем, препятствий нет
        if (targetRow == unitRow && targetColumn == unitColumn) {
            return false;
        }

        // Определяем направление
        int dx = Integer.compare(targetRow, unitRow); // -1 (вверх), 0 (нет по строкам), 1 (вниз)
        int dy = Integer.compare(targetColumn, unitColumn); // -1 (влево), 0 (нет по столбцам), 1 (вправо)

//         Если цель по прямой, то проверяем все на пути
        if (dx == 0 || dy == 0){
            int currentX = unitRow + dx;
            int currentY = unitColumn + dy;

            // Движемся от персонажа к цели и проверяем каждую клетку на наличие препятствий
            while (currentX != targetRow || currentY != targetColumn) {
                if (isObstacle(currentX, currentY)) {
                    return true; // Если найдено препятствие, то возвращаем true
                }

                currentX += dx;
                currentY += dy;
            }
            return false;
        }
        // Если цель по диагонали, то проверяем только на наличие препятствия непосредственно в клетке перед целью
        int nextX = targetRow - dx;
        int nextY = targetColumn - dy;

        if(nextX == unitRow || nextY == unitColumn) {
            return false;
        }

        return isObstacle(nextX, nextY);
    }


    // Метод, который возвращает список позиций, в которые игрок может выстрелить
    public Set<Pair<Integer, Integer>> getShootablePositions(int index) {
        Set<Pair<Integer, Integer>> shootablePositions = new HashSet<>();
        SoldierWithIndexAndCoordinats soldierByIndex = getSoldierByIndex(index);
        int playerRow = soldierByIndex.getRow();
        int playerColumn = soldierByIndex.getCol();
        int damageRadius = 0;
        int minRadius = 0;
        if(soldierByIndex.getSoldier() instanceof AbstractRadiusAttacker){
            damageRadius = ((AbstractRadiusAttacker) soldierByIndex.getSoldier()).getDamageRadius();
        }
        if(soldierByIndex.getSoldier() instanceof AbstractRadiusAreaAttacker){
            minRadius = ((AbstractRadiusAreaAttacker) soldierByIndex.getSoldier()).getMinDamageRadius();
        }
        for (int row = playerRow - damageRadius; row <= playerRow + damageRadius; row++) {
            for (int column = playerColumn - damageRadius; column <= playerColumn + damageRadius; column++) {
                if(row >= playerRow - minRadius && row <= playerRow + minRadius && column >= playerColumn - minRadius && column <= playerColumn + minRadius){
                    continue;
                }
                // Проверяем, что позиция находится в пределах игрового поля
                if (row >= 0 && row < board.length && column >= 0 && column < board[0].length) {
                    Pair<Integer, Integer> targetPosition = new Pair<>(row, column);

                    //так как только мортира AbstractRadiusAreaAttacker
                    if(minRadius != 0){
                        if(!isObstacle(row, column)){
                            shootablePositions.add(targetPosition);
                            continue;
                        }
                    }else {
                        // Проверяем, не блокируется ли путь до цели
                        if (isPathClear(soldierByIndex.getRow(), soldierByIndex.getCol(), row, column)) {
                            shootablePositions.add(targetPosition);
                        }
                    }
                }
            }
        }
        return shootablePositions;
    }


    // Метод, который определяет, не блокирует ли путь до цели препятствие
    private boolean isPathClear(int unitRow, int unitColumn, int targetRow, int targetColumn) {
        // Идем по линии от стартовой точки к цели.
        if(unitRow == targetRow && unitColumn == targetColumn){
            return false;
        }


        int dx = Math.abs(targetRow - unitRow);
        int dy = Math.abs(targetColumn - unitColumn);
        int sx = (unitRow < targetRow) ? 1 : -1;
        int sy = (unitColumn < targetColumn) ? 1 : -1;
        int err = dx - dy;

        while (true) {
            if(board[unitRow][unitColumn] != null && board[unitRow][unitColumn] instanceof AbstractElement){
                return false;
            }

            if (unitRow == targetRow && unitColumn == targetColumn) {
                break; // Достигли цели, путь свободен
            }

            int e2 = 2 * err;
            if (e2 > -dy) {
                err -= dy;
                unitRow += sx;
            }
            if (e2 < dx) {
                err += dx;
                unitColumn += sy;
            }
        }

        return true; // путь свободен
    }

    private boolean isObstacle(int row, int column) {
        if (row >= 0 && row < board.length && column >= 0 && column < board[0].length) {
            if (board[row][column] == null) {
                return false;
            } else {
                AbstractEntity ent = board[row][column];
                return ent instanceof AbstractElement;
            }
        }
        return true; // Если координаты выходят за границы поля - считаем это препятствием
    }

    public SoldierWithIndexAndCoordinats getSoldierByIndex(int index){
        SoldierWithIndexAndCoordinats soldier = null;
        for(SoldierWithIndexAndCoordinats s : mySoldiers){
            if(s.getIndex() == index){
                soldier = s;
                break;
            }
        }
        if(soldier == null){
            for(SoldierWithIndexAndCoordinats s : opponentSoldiers){
                if(s.getIndex() == index){
                    soldier = s;
                    break;
                }
            }
        }
        return soldier;
    }

    public boolean isOpponentSoldier(int index){
        for(SoldierWithIndexAndCoordinats soldier : opponentSoldiers){
            if(soldier.getIndex() == index){
                return true;
            }
        }
        return false;
    }

    public SoldierWithIndexAndCoordinats getMySoldierByIndex(int index){
        return mySoldiers.stream().filter((s) -> s.getIndex() == index).findFirst().orElseThrow();
    }

    public void move(int soldierIndex, int column, int row){
        SoldierWithIndexAndCoordinats soldier = getSoldierByIndex(soldierIndex);
        board[soldier.getRow()][soldier.getCol()] = null;
        board[row][column] = soldier.getSoldier();
        soldier.setCol(column);
        soldier.setRow(row);
    }

    public SoldierWithIndexAndCoordinats action(int attacker, int defender){
        AbstractSoldier attack = getSoldierByIndex(attacker).getSoldier();
        AbstractSoldier def = getSoldierByIndex(defender).getSoldier();
        attack.action(def);
        SoldierWithIndexAndCoordinats sold = getSoldierByIndex(defender);
        if(def.getHealth() <= 0){
            def.setHealth(0);
            mySoldiers.remove(sold);
            opponentSoldiers.remove(sold);
            board[sold.getRow()][sold.getCol()] = null;
        }
        return sold;
    }

    public SoldierWithIndexAndCoordinats getSoldier(int column, int row){
        for(SoldierWithIndexAndCoordinats s : mySoldiers){
            if(s.getCol() == column && s.getRow() == row){
                return s;
            }
        }
        for(SoldierWithIndexAndCoordinats s : opponentSoldiers){
            if(s.getCol() == column && s.getRow() == row){
                return s;
            }
        }
        throw new RuntimeException();
    }

    public List<SoldierWithIndexAndCoordinats> getMySoldiers() {
        return mySoldiers;
    }

    public List<SoldierWithIndexAndCoordinats> getOpponentSoldiers() {
        return opponentSoldiers;
    }

    public void clear(){
        instance = null;
    }
}
