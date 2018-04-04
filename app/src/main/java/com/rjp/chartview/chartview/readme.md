### 一、效果图展示



### 二、分析
如果要绘制这样带侧栏和顶栏的效果，就需要知道侧栏和顶栏的宽度和高度，给走势图区域设置向左和向上的偏移量就可以。但是侧栏和顶栏是不可以随着滚动的，所以他们在重绘的时候需要知道View滚出去的距离，再加上这个距离就可以做到位置固定不动了。
    
### 三、代码实现
结合上一篇来看，我们只需要在 onDraw() 里面增加左侧栏和顶部栏的绘制。
首先，确定左栏和顶栏的宽高，这个值不一定是写死的，可以设置进来：
```
    private int leftBarWidth = 240;  //左边栏的高度应该和cell的保持一致就好
    private int topBarHeight = 160;  //顶部栏的宽度应该和cell的保持一致就好
```
这个时候我们首先要给所有的Cell计算的时候加一个偏移量：
```
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
        List<Cell> cells = topCellGroup.getCells();
        topCellGroup.setLocation(0, leftBarWidth, 0, topBarHeight);
        int size = cells.size();
        for (int i = 0; i < size; i++) {
            Cell cell = cells.get(i);
            cell.setLocation(cellWidth * i + leftBarWidth, cellWidth * (i + 1) + leftBarWidth, 0, topBarHeight);
        }
    }
```
CellGroup里面也需要增加属性，记录左边栏的位置信息和标题，cell设置location的时候需要加上 leftBarWidth 和 topBarHeight。顶部栏的cell也需要计算位置信息。

计算完了就需要绘制了，我们 onDraw() 下增加几个方法：
```
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
```
分别绘制左边栏和顶部栏，还有左上角一个空白的区域（为什么绘制这个空白区域？可以尝试注释这句话，就能明白）。先看左边栏：
```
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
```
这里加上的getScrollX 和 getScrollY 就是能够保持左侧栏一直显示在可视范围内的秘密。顶部栏一样的道理。
三个判断区域是否在屏幕内的方法都有调整：
```
    /**
     * 顶部栏是否出现在屏幕中
     * @param cell
     * @return
     */
    private boolean isTopBarVisiable(Cell cell) {
        return cell.getRight() > leftBarWidth + getScrollX() && cell.getLeft() < viewWidth + getScrollX();
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
```

整体上就是这些代码。我在之前的主干分支上切了一个 “带侧栏和顶栏版” 的分支，可以下载下来看一看，好用给个star。3q


[简书地址](https://www.jianshu.com/p/79884f0bd08a)