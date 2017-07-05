package com.example.misterweeman.ultimatenotakto.view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;

import com.example.misterweeman.ultimatenotakto.model.Board;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by Bea on 17/05/2017.
 * View per la griglia di gioco
 */

public class BoardView extends View {
    private static final String TAG = "BoardView";

    private static final String ARG_GRIDSIZE = "gridSize";
    private static final String ARG_GRID = "grid";
    private static final String ARG_SUPERSTATE = "superState";

    private int gridSize = 3;
    private int cellSize;
    private Paint xOnBoard = new Paint();

    private Board grid;
    private Paint blackPaint = new Paint();
    private int selectedColor;
    private Map<Path, Paint> colorsMap = new HashMap<Path, Paint>();
    private ArrayList <Path> paths = new ArrayList<Path>();

    private static int[] colors = {Color.RED, Color.BLUE, Color.GREEN, Color.YELLOW};

    private int xTouch = Integer.MAX_VALUE, yTouch = Integer.MAX_VALUE;

    public BoardView(Context context) {
        this(context, null);
    }

    public BoardView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        blackPaint.setStyle(Paint.Style.FILL_AND_STROKE);
        drawXStyle(Color.BLACK);
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

    // Imposta lo stile della X nella griglia di gioco
    private void drawXStyle(int color) {
        Log.d(TAG, "drawXStyle()");
        selectedColor = color;
        /*xOnBoard.setColor(color);
        xOnBoard.setStrokeWidth(16);
        xOnBoard.setAntiAlias(true);
        xOnBoard.setStrokeCap(Paint.Cap.ROUND);
        xOnBoard.setStyle(Paint.Style.STROKE);*/
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
        for (int i = 0; i < gridSize + 1; i++) {
            canvas.drawLine(i * cellSize, 0, i * cellSize, gridSize * cellSize, blackPaint);
            canvas.drawLine(0, i * cellSize, gridSize * cellSize, i * cellSize, blackPaint);
        }
    }

    private void drawCells(Canvas canvas) {
        // Disegna la X
        Path x = new Path();
        Paint mPaint = new Paint();
        for (int i = 0; i < gridSize; i++) {
            for (int j = 0; j < gridSize; j++) {
                if (grid.at(i, j)) {
                    x.moveTo((i * cellSize) + (cellSize / 6), (j * cellSize) + (cellSize / 6));
                    x.lineTo(((i + 1) * cellSize) - (cellSize / 6), ((j + 1) * cellSize) - (cellSize / 6));
                    x.moveTo((i + 1) * cellSize - (cellSize / 6), j * cellSize + (cellSize / 6));
                    x.lineTo(i * cellSize + (cellSize / 6), (j + 1) * cellSize - (cellSize / 6));
                }
            }
        }
        mPaint.setColor(selectedColor);
        mPaint.setAntiAlias(true);
        mPaint.setStyle(Paint.Style.STROKE);
        mPaint.setStrokeJoin(Paint.Join.ROUND);
        mPaint.setStrokeCap(Paint.Cap.ROUND);
        mPaint.setStrokeWidth(16);
        paths.add(x);
        colorsMap.put(x,mPaint);
        for (Path p : paths)
        {
            Log.d(TAG, "drawXStyle() "+colorsMap.get(p));
            canvas.drawPath(p, colorsMap.get(p));
        }
        //mPaint.setColor(selectedColor);
        //canvas.drawPath(x, mPaint);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        setMeasuredDimension(widthMeasureSpec, heightMeasureSpec);
    }

    // Setta la cella toccata con l'opposto del suo valore
    @Override
    public boolean onTouchEvent(MotionEvent event) {
//        if (event.getAction() == MotionEvent.ACTION_DOWN) {
        xTouch = (int) (event.getX() / cellSize);
        yTouch = (int) (event.getY() / cellSize);
//            if (xTouch < gridSize && yTouch < gridSize && grid.setChecked(xTouch, yTouch)) {
//                invalidate();
//                return true;
//            }
//        }
//        return false;
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

    public boolean updateBoard (int x, int y, int color) {
        if (x < gridSize && y < gridSize && grid.setChecked(x, y)) {
            drawXStyle(color);
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
