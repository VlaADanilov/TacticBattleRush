package org.example.client.board;

import lombok.val;
import org.example.client.GameEntities.fabrica.SoldierFabrica;
import org.example.client.GameEntities.soldiers.AbstractSoldier;
import org.example.client.GameEntities.AbstractEntity;
import org.example.client.GameEntities.elements.AbstractElement;
import org.example.client.GameEntities.soldiers.Hiller;
import org.example.client.board.tools.SoldierWithIndexAndCoordinats;

import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;



public class BoardSingleton {
    private AbstractEntity[][] board;
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
