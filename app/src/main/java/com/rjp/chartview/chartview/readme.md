### 一、效果图展示

![chart_view.gif](https://upload-images.jianshu.io/upload_images/5994029-e614043ba8ff2386.gif?imageMogr2/auto-orient/strip)


### 二、分析
有的彩种比如双色球，他是有红蓝两个号码的走势图，需要在一张表格里面展示出来。这个需求也不难实现，我们只需要稍微修改下简单版的代码就可以实现了。
    
### 三、代码实现
我们在ChartView下面增加一个方法实现：
```
    /**
     * 新增一个隔壁的表格
     * @param blueCeilsGroup
     */
    public void addAnotherChart(List<CeilGroup> blueCeilsGroup){
        int columnCount = 0;
        int groupSize = ceilGroups.size();
        for (int i = 0; i < groupSize; i++) {
            List<Ceil> oldCeils = ceilGroups.get(i).getCeils();
            CeilGroup ceilGroup = blueCeilsGroup.get(i);
            List<Ceil> newCeils = ceilGroup.getCeils();
            //中间增加一个空的ceil
            oldCeils.add(new Ceil());
            for (Ceil newCeil : newCeils) {
                oldCeils.add(newCeil);
            }
            columnCount = oldCeils.size();
        }
        maxWidth = columnCount * ceilWidth;
        maxHeight = groupSize * ceilHeight;
        computeCeilLocation(0, 0);
    }
```
遍历已有的ceilGroups，对应的取出 blueCeilsGroup 每一行所有的Ceil加入到之前已经存在的 oldCeils 下，这里有一个技巧，增加一个空的Ceil，这样显示的时候会有一个空竖行，便于区分。
绘制球的时候，因为有两个颜色了，所以颜色这个属性需要加入到 Ceil 当中，绘制的时候判断取值：
```
    /**
     * 设置ceil选中的效果
     *
     * @param canvas
     * @param ceil
     */
    private void drawCeilSelected(Canvas canvas, Ceil ceil) {
        if (ceil.isSelected()) {
            selectedPaint.setColor(ceil.getColor());
            canvas.drawOval(new RectF(ceil.getLeft(), ceil.getTop(), ceil.getRight(), ceil.getBottom()), selectedPaint);
        }
    }

    /**
     * 绘制ceil的连线
     *
     * @param canvas
     * @param ceil
     */
    private void drawCeilLinkLine(Canvas canvas, Ceil ceil) {
        Ceil nextCeil = ceil.getNextCeil();
        if (nextCeil != null) {
            linkLinePaint.setColor(ceil.getColor());
            canvas.drawLine(nextCeil.getCenterX(), nextCeil.getCenterY(), ceil.getCenterX(), ceil.getCenterY(), linkLinePaint);
        }
    }
```
以上就是所有的代码了。绘制的里面因为加了边界判断，超出屏幕的部分其实不是实时绘制的，所以整体还是很顺滑。但是这个三层循环真的不是一个很好的解决方案，如果有什么可以优化的，私信告诉我。

[简书地址](https://www.jianshu.com/p/79884f0bd08a)