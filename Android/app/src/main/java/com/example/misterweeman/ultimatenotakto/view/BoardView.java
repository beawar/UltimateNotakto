package com.example.misterweeman.ultimatenotakto.view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

import com.example.misterweeman.ultimatenotakto.model.Board;

/**
 * Created by Bea on 17/05/2017.
 * View per la griglia di gioco
 */

public class BoardView extends View {
    private int gridSize = 3;
    private int cellSize;
    private Paint xOnBoard = new Paint();
    private Board grid;
    private Paint blackPaint = new Paint();

    public BoardView(Context context) {
        this(context, null);
    }

    public BoardView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        blackPaint.setStyle(Paint.Style.FILL_AND_STROKE);
        drawXStyle(Color.RED);
    }

    // Calcola dimensione cella in base a lato minore dello schermo e inizializza la griglia
    private void calculateDimension() {
        cellSize = Math.min(getWidth(), getHeight()) / gridSize;
        invalidate();
    }

    public int getGridSize() {
        return gridSize;
    }

    public void setGridSize(int gridSize) {
        if (gridSize >= 3 && gridSize <= 6) {
            this.gridSize = gridSize;
            grid = new Board(gridSize);
        }
        calculateDimension();
    }

    // Imposta lo stile della X nella griglia di gioco
    private void drawXStyle(int color) {
        xOnBoard.setColor(color);
        xOnBoard.setStrokeWidth(16);
        xOnBoard.setAntiAlias(true);
        xOnBoard.setStrokeCap(Paint.Cap.BUTT);
        xOnBoard.setStyle(Paint.Style.STROKE);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        canvas.drawColor(Color.WHITE);
        drawCells(canvas);
        drawGrid(canvas);
    }

    private void drawGrid(Canvas canvas) {
        // Disegna la griglia di gioco
        int i = 1;
        for (; i < gridSize; i++) {
            canvas.drawLine(i * cellSize, 0, i * cellSize, getHeight(), blackPaint);
            canvas.drawLine(0, i * cellSize, getWidth(), i * cellSize, blackPaint);
        }
        canvas.drawLine(0, i * cellSize, getWidth(), i * cellSize, blackPaint);
    }

    private void drawCells(Canvas canvas) {
        // Disegna la X
        for (int i = 0; i < gridSize; i++) {
            for (int j = 0; j < gridSize; j++) {
                if (grid.at(i, j)) {
                    canvas.drawLine((i * cellSize), (j * cellSize),
                            ((i + 1) * cellSize), ((j + 1) * cellSize),
                            xOnBoard);
                    canvas.drawLine((i + 1) * cellSize, j * cellSize,
                            i * cellSize, (j + 1) * cellSize,
                            xOnBoard);
                }
            }
        }
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        setMeasuredDimension(widthMeasureSpec, heightMeasureSpec);
    }

    // Setta la cella toccata con l'opposto del suo valore
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (event.getAction() == MotionEvent.ACTION_DOWN) {
            int column = (int) (event.getX() / cellSize);
            int row = (int) (event.getY() / cellSize);
            if (column < 6 && row < 6) {
                grid.setChecked(column, row);
                invalidate();
            }
        }
        return true;
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        calculateDimension();
    }
}
