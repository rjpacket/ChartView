package com.rjp.chartview.chartview;

import java.util.List;

/**
 * author : Gimpo create on 2018/4/2 16:43
 * email  : jimbo922@163.com
 */

public class CellGroup {
    private int left;
    private int top;
    private int right;
    private int bottom;

    private int centerX;
    private int centerY;

    public void setLocation(int left, int right, int top, int bottom){
        this.setLeft(left);
        this.setRight(right);
        this.setTop(top);
        this.setBottom(bottom);

        setCenterX((left + right) / 2);
        setCenterY((top + bottom) / 2);
    }

    private String title;
    private List<Cell> cells;

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public List<Cell> getCells() {
        return cells;
    }

    public void setCells(List<Cell> cells) {
        this.cells = cells;
    }

    public int getLeft() {
        return left;
    }

    public void setLeft(int left) {
        this.left = left;
    }

    public int getTop() {
        return top;
    }

    public void setTop(int top) {
        this.top = top;
    }

    public int getRight() {
        return right;
    }

    public void setRight(int right) {
        this.right = right;
    }

    public int getBottom() {
        return bottom;
    }

    public void setBottom(int bottom) {
        this.bottom = bottom;
    }

    public int getCenterX() {
        return centerX;
    }

    public void setCenterX(int centerX) {
        this.centerX = centerX;
    }

    public int getCenterY() {
        return centerY;
    }

    public void setCenterY(int centerY) {
        this.centerY = centerY;
    }
}
