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

    public void setChecked(int col, int row) {
        grid[col][row] = !grid[col][row];
        System.out.println("grid[" + col + "][" + row + "]: " + grid[col][row]);
    }

    public boolean at(int col, int row) {
        return grid[col][row];
    }
}
