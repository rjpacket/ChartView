package com.rjp.chartview.chartview;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.support.annotation.Nullable;
import android.support.annotation.Px;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.VelocityTracker;
import android.view.View;
import android.view.ViewConfiguration;
import android.widget.Scroller;

import com.rjp.chartview.R;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import static android.graphics.Color.WHITE;

/**
 * 越界的表格View
 * author : Gimpo create on 2018/4/2 16:29
 * email  : jimbo922@163.com
 */

public class ChartView extends View {

    public static final int MODE_EQUAL = 1;
    public static final int MODE_APPEND = 2;

    public int mode = MODE_APPEND;
    private List<CellGroup> cellGroups = new ArrayList<>();

    private int cellWidth = 100;
    private int cellHeight = 100;
    private int screenWidth;
    private int preX;
    private int preY;
    private Paint linePaint;
    private Paint selectedPaint;
    private Paint linkLinePaint;
    private Paint textPaint;
    private Paint backgroundPaint;
    private int viewWidth;
    private int viewHeight;

    private int maxWidth;
    private int maxHeight;
    private Scroller mScroller;
    private int mMinimumVelocity;
    private int mMaximumVelocity;
    private VelocityTracker mVelocityTracker;
    private int columns;
    private float numberTextSize;
    private int ballColor;
    private int numberNormalColor;
    private int numberSelectedColor;
    private int linkLineColor;
    private int backgroundColor;
    private int strokeColor;
    private float linkLineWidth;
    private float tempcellWidth;
    private float tempcellHeight;
    private float txtMidValue;

    //顶部栏可以看成一个CellGroup
    private List<Cell> topCells;
    private int leftBarWidth = 240;  //左边栏的高度应该和cell的保持一致就好
    private int topBarHeight = 160;  //顶部栏的宽度应该和cell的保持一致就好
    private Paint leftTextPaint;
    private Paint leftBackgroundPaint;
    private float leftTxtMidValue;
    private Paint emptyPaint;

    public ChartView(Context context) {
        this(context, null);
    }

    public ChartView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        initView(context, attrs);
    }

    private void initView(Context context, AttributeSet attrs) {
        if (attrs != null) {
            TypedArray array = context.obtainStyledAttributes(attrs, R.styleable.ChartView);
            mode = array.getInt(R.styleable.ChartView_mode, MODE_APPEND);
            columns = array.getInt(R.styleable.ChartView_columns, 8);
            tempcellWidth = array.getDimension(R.styleable.ChartView_cell_width, 100);
            tempcellHeight = array.getDimension(R.styleable.ChartView_cell_height, 100);
            numberTextSize = array.getDimension(R.styleable.ChartView_number_size, 32);
            linkLineWidth = array.getDimension(R.styleable.ChartView_link_line_width, 4);
            ballColor = array.getColor(R.styleable.ChartView_ball_color, Color.RED);
            numberNormalColor = array.getColor(R.styleable.ChartView_number_normal_color, Color.BLACK);
            numberSelectedColor = array.getColor(R.styleable.ChartView_number_selected_color, WHITE);
            linkLineColor = array.getColor(R.styleable.ChartView_link_line_color, Color.RED);
            strokeColor = array.getColor(R.styleable.ChartView_stroke_color, Color.BLACK);
            backgroundColor = array.getColor(R.styleable.ChartView_background_color, Color.GRAY);
        }

        screenWidth = context.getResources().getDisplayMetrics().widthPixels;
        if (mode == MODE_EQUAL) {
            cellWidth = cellHeight = screenWidth / columns;
        } else if (mode == MODE_APPEND) {
            cellWidth = (int) tempcellWidth;
            cellHeight = (int) tempcellHeight;
        }

        linePaint = new Paint();
        linePaint.setAntiAlias(true);
        linePaint.setColor(strokeColor);

        linkLinePaint = new Paint();
        linkLinePaint.setAntiAlias(true);
        linkLinePaint.setStrokeWidth(linkLineWidth);
        linkLinePaint.setColor(linkLineColor);

        backgroundPaint = new Paint();
        backgroundPaint.setAntiAlias(true);
        backgroundPaint.setColor(backgroundColor);

        selectedPaint = new Paint();
        selectedPaint.setAntiAlias(true);
        selectedPaint.setColor(ballColor);

        textPaint = new Paint();
        textPaint.setAntiAlias(true);
        textPaint.setTextAlign(Paint.Align.CENTER);
        textPaint.setColor(numberNormalColor);
        textPaint.setTextSize(numberTextSize);
        Paint.FontMetrics fontMetrics = textPaint.getFontMetrics();
        txtMidValue = (fontMetrics.top + fontMetrics.bottom) / 2;

        leftTextPaint = new Paint();
        leftTextPaint.setAntiAlias(true);
        leftTextPaint.setTextAlign(Paint.Align.CENTER);
        leftTextPaint.setColor(WHITE);
        leftTextPaint.setTextSize(40);
        Paint.FontMetrics fontMetrics1 = leftTextPaint.getFontMetrics();
        leftTxtMidValue = (fontMetrics1.top + fontMetrics1.bottom) / 2;

        leftBackgroundPaint = new Paint();
        leftBackgroundPaint.setAntiAlias(true);
        leftBackgroundPaint.setColor(Color.CYAN);

        emptyPaint = new Paint();
        emptyPaint.setAntiAlias(true);
        emptyPaint.setColor(Color.WHITE);

        // 新增部分 start
        ViewConfiguration viewConfiguration = ViewConfiguration.get(context);
        mScroller = new Scroller(context);
        mMinimumVelocity = viewConfiguration.getScaledMinimumFlingVelocity();
        mMaximumVelocity = viewConfiguration.getScaledMaximumFlingVelocity();

        //顶部栏初始化的时候传递
        topCells = new ArrayList<>();
        for (int j = 0; j < 40; j++) {
            Cell e1 = new Cell();
            e1.setNumber(String.valueOf(j + 1));
            topCells.add(e1);
        }

        Cell precell = null;
        Random random = new Random();
        for (int i = 0; i < 300; i++) {
            CellGroup e = new CellGroup();
            ArrayList<Cell> cells = new ArrayList<>();
            int selectedIndex = random.nextInt(30);
            for (int j = 0; j < 40; j++) {
                Cell e1 = new Cell();
                e1.setNumber(String.valueOf(j));
                if (j == selectedIndex) {
                    e1.setSelected(true);
                    e1.setColor(Color.RED);
                }
                cells.add(e1);
                if (precell != null && e1.isSelected()) {
                    e1.setNextCell(i == 0 ? null : precell);
                }
                if (e1.isSelected()) {
                    precell = e1;
                }
            }
            e.setCells(cells);
            cellGroups.add(e);
        }
        maxWidth = 40 * cellWidth + leftBarWidth;
        maxHeight = 300 * cellHeight + topBarHeight;
    }

    /**
     * 新增一个隔壁的表格
     *
     * @param bluecellsGroup
     */
    public void addAnotherChart(List<CellGroup> bluecellsGroup) {
        boolean isTopBarAdd = false;
        //顶部栏也加一个空的cell
        topCells.add(new Cell());
        int columnCount = 0;
        int groupSize = cellGroups.size();
        for (int i = 0; i < groupSize; i++) {
            List<Cell> oldcells = cellGroups.get(i).getCells();
            CellGroup cellGroup = bluecellsGroup.get(i);
            List<Cell> newcells = cellGroup.getCells();
            //中间增加一个空的cell
            oldcells.add(new Cell());
            int size = newcells.size();
            for (int j = 0; j < size; j++) {
                if(!isTopBarAdd){
                    Cell tc = new Cell();
                    tc.setNumber(String.valueOf(j + 1));
                    topCells.add(tc);
                }
                oldcells.add(newcells.get(j));
            }
            isTopBarAdd = true;  //顶部栏的cell只需要添加一次就好
            columnCount = oldcells.size();
        }
        maxWidth = columnCount * cellWidth + leftBarWidth;
        maxHeight = groupSize * cellHeight + topBarHeight;
        computecellLocation();
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        obtainVelocityTracker();
        int action = event.getAction();
        int x = (int) event.getX();
        int y = (int) event.getY();
        switch (action) {
            case MotionEvent.ACTION_DOWN:
                preX = x;
                preY = y;
                if (!mScroller.isFinished()) {
                    mScroller.abortAnimation();
                }
                break;
            case MotionEvent.ACTION_MOVE:
                int dx = x - preX;
                int dy = y - preY;
                scrollTo(getScrollX() - dx, getScrollY() - dy);
                preX = x;
                preY = y;
                break;
            case MotionEvent.ACTION_UP:
            case MotionEvent.ACTION_CANCEL:
                mVelocityTracker.computeCurrentVelocity(1000, mMaximumVelocity);
                int initialVelocityX = (int) mVelocityTracker.getXVelocity();
                int initialVelocityY = (int) mVelocityTracker.getYVelocity();
                if ((Math.abs(initialVelocityX) > mMinimumVelocity) || (Math.abs(initialVelocityY) > mMinimumVelocity)) {
                    flingXY(-initialVelocityX, -initialVelocityY);
                }
                releaseVelocityTracker();
                break;
        }
        if (mVelocityTracker != null) {
            mVelocityTracker.addMovement(event);
        }
        return true;
    }

    @Override
    public void computeScroll() {
        if (mScroller.computeScrollOffset()) {
            int x = mScroller.getCurrX();
            int y = mScroller.getCurrY();
            scrollTo(x, y);
        }
    }

    /**
     * 惯性滑动
     *
     * @param velocityX
     * @param velocaityY
     */
    public void flingXY(int velocityX, int velocaityY) {
        mScroller.fling(getScrollX(), getScrollY(), velocityX, velocaityY, 0, maxWidth - viewWidth, 0, maxHeight - viewHeight);
    }

    /**
     * 初始化 速度追踪器
     */
    private void obtainVelocityTracker() {
        if (mVelocityTracker == null) {
            mVelocityTracker = VelocityTracker.obtain();
        }
    }

    /**
     * 释放 速度追踪器
     */
    private void releaseVelocityTracker() {
        if (mVelocityTracker != null) {
            mVelocityTracker.recycle();
            mVelocityTracker = null;
        }
    }

    @Override
    public void scrollTo(@Px int x, @Px int y) {
        if (x < 0) {
            x = 0;
        }
        if (x > maxWidth - viewWidth) {
            x = maxWidth - viewWidth;
        }
        if (y < 0) {
            y = 0;
        }
        if (y > maxHeight - viewHeight) {
            y = maxHeight - viewHeight;
        }
        super.scrollTo(x, y);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        viewWidth = MeasureSpec.getSize(widthMeasureSpec);
        viewHeight = MeasureSpec.getSize(heightMeasureSpec);
        setMeasuredDimension(viewWidth, viewHeight);
        computecellLocation();
    }

    /**
     * 计算每一个cell的位置
     */
    private void computecellLocation() {
        int groupSize = cellGroups.size();
        for (int i = 0; i < groupSize; i++) {
            CellGroup cellGroup = cellGroups.get(i);
            cellGroup.setTitle("第" + (i + 1) + "期");
            cellGroup.setLocation(0, leftBarWidth, cellHeight * i + topBarHeight, cellHeight * (i + 1) + topBarHeight);
            List<Cell> cells = cellGroup.getCells();
            int cellSize = cells.size();
            for (int j = 0; j < cellSize; j++) {
                Cell cell = cells.get(j);
                cell.setLocation(cellWidth * j + leftBarWidth, cellWidth * (j + 1) + leftBarWidth, cellHeight * i + topBarHeight, cellHeight * (i + 1) + topBarHeight);
            }
        }

        //计算顶部栏的位置
        int size = topCells.size();
        for (int i = 0; i < size; i++) {
            Cell cell = topCells.get(i);
            cell.setLocation(cellWidth * i + leftBarWidth, cellWidth * (i + 1) + leftBarWidth, 0, topBarHeight);
        }
    }

    @Override
    protected void onDraw(Canvas canvas) {
        int groupSize = cellGroups.size();
        for (int i = 0; i < groupSize; i++) {
            CellGroup cellGroup = cellGroups.get(i);
            drawCellGroupLeftTitle(canvas, cellGroup);
            List<Cell> cells = cellGroup.getCells();
            int cellSize = cells.size();
            for (int j = 0; j < cellSize; j++) {
                Cell cell = cells.get(j);
                if (isCellVisiable(cell)) {
                    drawCellTopLine(canvas, cell);
                    drawCellLeftLine(canvas, cell);
                    drawCellBackground(canvas, cell);
                }
            }
        }

        for (int i = 0; i < groupSize; i++) {
            CellGroup cellGroup = cellGroups.get(i);
            List<Cell> cells = cellGroup.getCells();
            int cellSize = cells.size();
            for (int j = 0; j < cellSize; j++) {
                Cell cell = cells.get(j);
                drawCellLinkLine(canvas, cell);
            }
        }

        for (int i = 0; i < groupSize; i++) {
            CellGroup cellGroup = cellGroups.get(i);
            List<Cell> cells = cellGroup.getCells();
            int cellSize = cells.size();
            for (int j = 0; j < cellSize; j++) {
                Cell cell = cells.get(j);
                if (isCellVisiable(cell)) {
                    drawCellSelected(canvas, cell);
                    drawCellText(canvas, cell);
                }
            }
        }

        //最后绘制左边栏和顶部栏，可以有一个覆盖的效果
        //左边栏
        for (int i = 0; i < groupSize; i++) {
            CellGroup cellGroup = cellGroups.get(i);
            drawCellGroupLeftTitle(canvas, cellGroup);
        }


        for (Cell cell : topCells) {
            drawTopBarTitle(canvas, cell);
        }

        //绘制左上角的空白区域
        canvas.drawRect(getScrollX(), getScrollY(), leftBarWidth + getScrollX(), topBarHeight + getScrollY(), emptyPaint);
    }

    /**
     * 绘制顶部栏
     * @param canvas
     * @param cell
     */
    private void drawTopBarTitle(Canvas canvas, Cell cell) {
        if (isTopBarVisiable(cell)) {
            String number = cell.getNumber();
            if(!TextUtils.isEmpty(number)) {
                canvas.drawRect(cell.getLeft(), cell.getTop() + getScrollY(), cell.getRight(), cell.getBottom() + getScrollY(), leftBackgroundPaint);
                canvas.drawText(number, cell.getCenterX(), cell.getCenterY() - leftTxtMidValue + getScrollY(), leftTextPaint);
            }else{
                //如果顶部栏是空，就绘制一个白块
                canvas.drawRect(cell.getLeft(), cell.getTop() + getScrollY(), cell.getRight(), cell.getBottom() + getScrollY(), emptyPaint);
            }
        }
    }

    /**
     * 顶部栏是否出现在屏幕中
     * @param cell
     * @return
     */
    private boolean isTopBarVisiable(Cell cell) {
        return cell.getRight() > leftBarWidth + getScrollX() && cell.getLeft() < viewWidth + getScrollX();
    }

    /**
     * 绘制左边栏的标题
     * @param canvas
     * @param cellGroup
     */
    private void drawCellGroupLeftTitle(Canvas canvas, CellGroup cellGroup) {
        if (isLeftBarVisiable(cellGroup)) {
            canvas.drawRect(cellGroup.getLeft() + getScrollX(), cellGroup.getTop(), cellGroup.getRight() + getScrollX(), cellGroup.getBottom(), leftBackgroundPaint);
            canvas.drawText(cellGroup.getTitle(), cellGroup.getCenterX() + getScrollX(), cellGroup.getCenterY() - leftTxtMidValue, leftTextPaint);
        }
    }

    /**
     * 左边栏是否出现在屏幕中
     * @param cellGroup
     * @return
     */
    private boolean isLeftBarVisiable(CellGroup cellGroup) {
        return cellGroup.getBottom() > topBarHeight + getScrollY() && cellGroup.getTop() < viewHeight + getScrollY();
    }

    /**
     * 判断cell是否可见
     *
     * @param cell
     * @return
     */
    private boolean isCellVisiable(Cell cell) {
        return cell.getRight() > getScrollX() + leftBarWidth && cell.getBottom() > getScrollY() + topBarHeight && cell.getLeft() < viewWidth + getScrollX() && cell.getTop() < viewHeight + getScrollY();
    }

    /**
     * 绘制cell的文字
     *
     * @param canvas
     * @param cell
     */
    private void drawCellText(Canvas canvas, Cell cell) {
        textPaint.setColor(cell.isSelected() ? numberSelectedColor : numberNormalColor);
        String number = cell.getNumber();
        if (!TextUtils.isEmpty(number)) {
            canvas.drawText(number, cell.getCenterX(), cell.getCenterY() - txtMidValue, textPaint);
        }
    }

    /**
     * 绘制cell的背景
     *
     * @param canvas
     * @param cell
     */
    private void drawCellBackground(Canvas canvas, Cell cell) {
        canvas.drawRect(cell.getLeft(), cell.getTop(), cell.getRight(), cell.getBottom(), backgroundPaint);
    }

    /**
     * 设置cell选中的效果
     *
     * @param canvas
     * @param cell
     */
    private void drawCellSelected(Canvas canvas, Cell cell) {
        if (cell.isSelected()) {
            selectedPaint.setColor(cell.getColor());
            canvas.drawOval(new RectF(cell.getLeft(), cell.getTop(), cell.getRight(), cell.getBottom()), selectedPaint);
        }
    }

    /**
     * 绘制cell的连线
     *
     * @param canvas
     * @param cell
     */
    private void drawCellLinkLine(Canvas canvas, Cell cell) {
        Cell nextcell = cell.getNextCell();
        if (nextcell != null) {
            linkLinePaint.setColor(cell.getColor());
            canvas.drawLine(nextcell.getCenterX(), nextcell.getCenterY(), cell.getCenterX(), cell.getCenterY(), linkLinePaint);
        }
    }

    /**
     * 绘制cell左边的分割线
     *
     * @param canvas
     * @param cell
     */
    private void drawCellLeftLine(Canvas canvas, Cell cell) {
        canvas.drawLine(cell.getLeft(), cell.getTop(), cell.getLeft(), cell.getBottom(), linePaint);
    }

    /**
     * 绘制cell顶部的分割线
     *
     * @param canvas
     * @param cell
     */
    private void drawCellTopLine(Canvas canvas, Cell cell) {
        canvas.drawLine(cell.getLeft(), cell.getTop(), cell.getRight(), cell.getTop(), linePaint);
    }
}
