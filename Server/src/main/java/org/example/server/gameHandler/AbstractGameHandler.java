package org.example.server.gameHandler;

import ru.itis.prot.gameEntities.fabrica.ElementFabrica;

import static java.util.Map.Entry;
import java.util.*;

public abstract class AbstractGameHandler implements GameHandlerInterface{
    protected Byte[] initBoard(int boardRows, int boardColumns){
        Integer[][] field = new Integer[boardRows][boardColumns];
        Random rand = new Random();
        int size = rand.nextInt(75);
        push(field,size);

        while(hasClosedContours(field) || !opponentsCanChoicing(field)){
            field = new Integer[boardRows][boardColumns];
            push(field,size);
        }



        List<Byte> list = new ArrayList<Byte>();

        for(int row = 0; row < boardRows; row++){
            for(int col = 0; col < boardColumns; col++){
                if(field[row][col] != null){
                    list.add((byte)col);
                    list.add((byte)row);
                    list.add((byte) ElementFabrica.getElement().getIndex());
                }
            }
        }
        Byte[] bytes = new Byte[list.size()];
        for(int i = 0; i < list.size(); i++){
            bytes[i] = list.get(i);
        }
        return bytes;
    }

    private void push(Integer[][] field, int size){
        int boardRows = field.length;
        int boardColumns = field[0].length;
        Random rand = new Random();
        for(int i = 0; i< size; i++){
            int x = rand.nextInt(boardRows);
            int y = rand.nextInt(boardColumns);
            field[x][y] = 1;
        }
    }

    private boolean hasClosedContours(Integer[][] field) {
        int boardRows = field.length;
        int boardColumns = field[0].length;
        boolean[][] visited = new boolean[boardRows][boardColumns];
        for (int i = 0; i < boardRows; i++) {
            for (int j = 0; j < boardColumns; j++) {
                if (field[i][j] != null && !visited[i][j]) {
                    visited = new boolean[boardRows][boardColumns];
                    Set<Entry<Integer,Integer>> contour = new HashSet<>();
                    if (dfs(i, j, -1, -1,field, visited, contour) && isValid(contour,field)) {
                        return true;
                    }
                }
            }
        }
        return false;
    }

    private boolean dfs(int row, int col, int prevRow, int prevCol, Integer[][] field, boolean[][] visited,Set<Entry<Integer,Integer>> contour) {
        int boardRows = field.length;
        int boardColumns = field[0].length;
        if (row < 0 || row >= boardRows || col < 0 || col >= boardColumns || field[row][col] == null) {
            return false;
        }

        if(visited[row][col]){
            //Если клетка посещена, но не являлась предыдущей, значит это контур
            return (prevRow != row || prevCol != col);
        }

        visited[row][col] = true;
        contour.add(new AbstractMap.SimpleEntry<>(row, col));

        // Проходим по 4 направлениям
        int[][] directions = {{0, 1}, {0, -1}, {1, 0}, {-1, 0}};

        for (int[] dir : directions) {
            int newRow = row + dir[0];
            int newCol = col + dir[1];
            if(dfs(newRow,newCol,row,col,field,visited,contour)){
                return true;
            }
        }
        return false;
    }

    private boolean opponentsCanChoicing(Integer[][] field){
        int cnt1 = 0, cnt2 = 0;
        for(int column = 0; column < field[0].length; column++){
            for(int row = 0; row < field.length; row++){
                if(column > 2 && column < field[0].length - 3){
                    break;
                }else{
                    if(field[row][column] == null){
                        if(column <= 2){
                            cnt1++;
                        }else{
                            cnt2++;
                        }
                    }
                }
            }
        }
        return cnt1 >= 5 && cnt2 >= 5;
    }

    // Метод для проверки, что контур имеет внутреннее пространство
    private boolean isValid(Set<Entry<Integer,Integer>> contour,Integer[][] field) {
        if (contour.size() < 4) return false; // Контур минимум из 4 элементов

        // Находим границы контура
        int minRow = field.length, maxRow = -1, minCol = field[0].length, maxCol = -1;
        for (Entry<Integer,Integer> p : contour) {
            minRow = Math.min(minRow, p.getKey());
            maxRow = Math.max(maxRow, p.getKey());
            minCol = Math.min(minCol, p.getValue());
            maxCol = Math.max(maxCol, p.getValue());
        }

        // Проверяем, есть ли свободное пространство внутри контура
        for (int row = minRow + 1; row < maxRow; row++) {
            for (int col = minCol + 1; col < maxCol; col++) {
                if (!contour.contains(new AbstractMap.SimpleEntry<>(row, col))) {
                    if (Objects.equals(field[row][col],1)) {
                        return true; // Найдено свободное место
                    }
                }

            }
        }

        return false; // Нет внутреннего пространства
    }
}
