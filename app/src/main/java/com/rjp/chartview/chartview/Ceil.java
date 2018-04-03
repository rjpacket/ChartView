package com.rjp.chartview.chartview;

/**
 * author : Gimpo create on 2018/4/2 18:04
 * email  : jimbo922@163.com
 */

public class Ceil {
    private int left;
    private int top;
    private int right;
    private int bottom;

    private int centerX;
    private int centerY;

    private boolean isSelected;

    private Ceil nextCeil;

    private String number;

    private int color;

    public void setLocation(int left, int right, int top, int bottom){
        this.left = left;
        this.right = right;
        this.top = top;
        this.bottom = bottom;

        setCenterX((left + right) / 2);
        setCenterY((top + bottom) / 2);
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

    public boolean isSelected() {
        return isSelected;
    }

    public void setSelected(boolean selected) {
        isSelected = selected;
    }

    public Ceil getNextCeil() {
        return nextCeil;
    }

    public void setNextCeil(Ceil nextCeil) {
        this.nextCeil = nextCeil;
    }

    public String getNumber() {
        return number;
    }

    public void setNumber(String number) {
        this.number = number;
    }

    public int getColor() {
        return color;
    }

    public void setColor(int color) {
        this.color = color;
    }
}
