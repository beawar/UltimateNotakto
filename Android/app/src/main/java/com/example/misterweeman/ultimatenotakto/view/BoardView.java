package com.example.misterweeman.ultimatenotakto.view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.util.SparseArray;
import android.util.SparseIntArray;
import android.view.MotionEvent;
import android.view.View;

import com.example.misterweeman.ultimatenotakto.model.Board;

public class BoardView extends View {
    private static final String TAG = "BoardView";

    private static final String ARG_GRIDSIZE = "gridSize";
    private static final String ARG_GRID = "grid";
    private static final String ARG_SUPERSTATE = "superState";

    private int gridSize = 3;
    private int cellSize;
    private Board grid;
    private Paint blackPaint = new Paint();

    private SparseArray<Paint> xOnBoardMap;
    private SparseIntArray colorBoardHelper;
    private static int[] colors = {Color.RED, Color.BLUE, Color.GREEN, Color.YELLOW};

    private int xTouch = Integer.MAX_VALUE, yTouch = Integer.MAX_VALUE;

    public BoardView(Context context) {
        this(context, null);
    }

    public BoardView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        blackPaint.setStyle(Paint.Style.FILL_AND_STROKE);
        drawXStyles();
    }

    public BoardView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
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

    public Board getGrid() {
        return grid;
    }

    public void setGrid(Board grid) {
        this.grid = grid;
        if (grid != null) {
            gridSize = grid.getSize();
            calculateDimension();
        }
    }

    public int getXTouch() {
        return xTouch;
    }

    public int getYTouch() {
        return yTouch;
    }

    // Set the styles for the x (1 for every possible player)
    protected void drawXStyles(){
        colorBoardHelper = new SparseIntArray();
        xOnBoardMap = new SparseArray<>(4);
        xOnBoardMap.append(Color.RED, new Paint());
        xOnBoardMap.append(Color.BLUE, new Paint());
        xOnBoardMap.append(Color.GREEN, new Paint());
        xOnBoardMap.append(Color.YELLOW, new Paint());
        for (int i=0; i<xOnBoardMap.size(); ++i) {
            Paint xPaint = xOnBoardMap.valueAt(i);
            xPaint.setColor(xOnBoardMap.keyAt(i));
            xPaint.setStrokeWidth(16);
            xPaint.setAntiAlias(true);
            xPaint.setStrokeCap(Paint.Cap.ROUND);
            xPaint.setStyle(Paint.Style.STROKE);
        }
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        canvas.drawColor(Color.WHITE);
        drawGrid(canvas);
        drawCells(canvas);
    }

    private void drawGrid(Canvas canvas) {
        for (int i = 0; i < gridSize + 1; i++) {
            canvas.drawLine(i * cellSize, 0, i * cellSize, gridSize * cellSize, blackPaint);
            canvas.drawLine(0, i * cellSize, gridSize * cellSize, i * cellSize, blackPaint);
        }
    }

    private void drawCells(Canvas canvas) {
        // Draws every X
        for (int i = 0; i < gridSize; i++) {
            for (int j = 0; j < gridSize; j++) {
                if (grid.at(i, j)) {
                    int color = colorBoardHelper.get(3 * j + i, Color.BLACK);
                    canvas.drawLine((i * cellSize) + (cellSize / 6), (j * cellSize) + (cellSize / 6),
                            ((i + 1) * cellSize) - (cellSize / 6), ((j + 1) * cellSize) - (cellSize / 6),
                            xOnBoardMap.get(color));
                    canvas.drawLine((i + 1) * cellSize - (cellSize / 6), j * cellSize + (cellSize / 6),
                            i * cellSize + (cellSize / 6), (j + 1) * cellSize - (cellSize / 6),
                            xOnBoardMap.get(color));
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
        xTouch = (int) (event.getX() / cellSize);
        yTouch = (int) (event.getY() / cellSize);
        return super.onTouchEvent(event);
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        calculateDimension();
    }

    @Override
    protected Parcelable onSaveInstanceState() {
        Bundle bundle = new Bundle();
        bundle.putParcelable(ARG_SUPERSTATE, super.onSaveInstanceState());
        bundle.putInt(ARG_GRIDSIZE, this.gridSize);
        bundle.putBooleanArray(ARG_GRID, this.grid.getGridAsArray());
        return bundle;
    }

    @Override
    protected void onRestoreInstanceState(Parcelable state) {
        if (state instanceof Bundle) {
            Bundle bundle = (Bundle) state;
            this.gridSize = bundle.getInt(ARG_GRIDSIZE);
            this.grid = new Board();
            grid.setGridFromArray(bundle.getBooleanArray(ARG_GRID));
            state = bundle.getParcelable(ARG_SUPERSTATE);
        }
        super.onRestoreInstanceState(state);
    }

    public boolean updateBoard(int x, int y, int color) {
        if (x < gridSize && y < gridSize && grid.setChecked(x, y)) {
            colorBoardHelper.put(3 * y + x, color);
            invalidate();
            return true;
        }
        return false;
    }

    public static int[] getColors() {
        return colors;
    }

    public static void setColors(int[] colors) {
        BoardView.colors = colors;
    }
}
