package com.example.misterweeman.ultimatenotakto.model;

/**
 * Created by Bea on 18/05/2017.
 * Entità che rappresenta la griglia di gioco
 */

public class Board {
    private boolean[][] grid;
    private int size = 3;

    public Board() {
        this(3);
    }

    public Board(int size) {
        this.size = size;
        grid = new boolean[size][size];
        for (int i = 0; i < size; i++) {
            for (int j = 0; j < size; j++) {
                grid[i][j] = false;
            }
        }
    }

    public boolean setChecked(int col, int row) {
        if (!grid[col][row]) {
            grid[col][row] = true;
            return true;
        }
        return false;
    }

    public boolean isChecked(int col, int row) {
        return grid[col][row];
    }

    public boolean at(int col, int row) {
        return grid[col][row];
    }

    public boolean[][] getGrid() {
        return grid;
    }

    public void setGrid(boolean[][] grid) {
        this.grid = grid;
    }

    public int getSize() {
        return size;
    }

    public void setSize(int size) {
        this.size = size;
    }

    public boolean[] getGridAsArray() {
        boolean[] temp = new boolean[size * size];
        for (int i = 0; i < size; i++) {
            System.arraycopy(grid[i], 0, temp, size * i, size);
        }
        return temp;
    }

    public void setGridFromArray(boolean[] array) {
        int gridSize = (int) Math.sqrt(array.length);
        for (int i = 0; i < array.length; i++) {
            grid[i / gridSize][i % gridSize] = array[i];
        }
    }
}
