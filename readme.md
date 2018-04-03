### 一、效果图展示

![chart_view.gif](https://upload-images.jianshu.io/upload_images/5994029-e614043ba8ff2386.gif?imageMogr2/auto-orient/strip)


### 二、分析
如果想要一张超大的表格，手机屏幕的空间肯定是不够的，那么需要自定义View，重写手指的onTouchEvent方法，调用View的scrollTo() 方法让View内部去滚动。既然能滚动，onMeasure的时候就不能计算去设置View的高度和宽度，让View等于设定的固定值或者充满父布局就可以。
    
### 三、代码实现
直接绘制一整张的表格对性能的要求比较高，我们可以把每一个小矩形当成一个对象：
```
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
    }
```
一整行的矩形我们保存在一个List列表中，但是有时候一整行还有其他的属性，我们可以将整行也抽成对象，里面维护一个List<Ceil>的列表：
```
    public class CeilGroup {
        private String title;
        private List<Ceil> ceils;
    }
```
为什么这么做？面向对象开发

然后是我们ChartView的代码，挑重点看，初始化的时候：
```
    Ceil preCeil = null;
        Random random = new Random();
        for (int i = 0; i < 300; i++) {
            CeilGroup e = new CeilGroup();
            ArrayList<Ceil> ceils = new ArrayList<>();
            int selectedIndex = random.nextInt(30);
            for (int j = 0; j < 40; j++) {
                Ceil e1 = new Ceil();
                e1.setNumber(String.valueOf(j));
                if (j == selectedIndex) {
                    e1.setSelected(true);
                }
                ceils.add(e1);
                if (preCeil != null && e1.isSelected()) {
                    e1.setNextCeil(i == 0 ? null : preCeil);
                }
                if (e1.isSelected()) {
                    preCeil = e1;
                }
            }
            e.setCeils(ceils);
            ceilGroups.add(e);
        }
```
这里制造的假数据，一共300行数据，每一行40列，然后随机一个Ceil设置为选中的号码，并且关联下一个选中号码。

再来看看onMeasure() 方法：
```
    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        viewWidth = MeasureSpec.getSize(widthMeasureSpec);
        viewHeight = MeasureSpec.getSize(heightMeasureSpec);
        setMeasuredDimension(viewWidth, viewHeight);
        computeCeilLocation(0, 0);
    }

    /**
     * 计算每一个ceil的位置
     *
     * @param dx
     * @param dy
     */
    private void computeCeilLocation(int dx, int dy) {
        int groupSize = ceilGroups.size();
        for (int i = 0; i < groupSize; i++) {
            CeilGroup ceilGroup = ceilGroups.get(i);
            List<Ceil> ceils = ceilGroup.getCeils();
            int ceilSize = ceils.size();
            for (int j = 0; j < ceilSize; j++) {
                Ceil ceil = ceils.get(j);
                ceil.setLocation(ceilWidth * j + dx, ceilWidth * (j + 1) + dx, ceilHeight * i + dy, ceilHeight * (i + 1) + dy);
            }
        }
    }
```
并没有把每一个Ceil的宽高加起来作为ChartView的宽高，因为这些Ceil的宽高加起来一定是超出手机的屏幕的，我们直接设置xml文件里设置的View宽高就行。
设置完宽高调用了计算方法 computeCeilLocation() ，意思是提前确定每一个 Ceil 在ChartView 绘制的位置数据。知道这些位置，我们看看绘制方法：
```
    @Override
    protected void onDraw(Canvas canvas) {
        int groupSize = ceilGroups.size();
        for (int i = 0; i < groupSize; i++) {
            CeilGroup ceilGroup = ceilGroups.get(i);
            List<Ceil> ceils = ceilGroup.getCeils();
            int ceilSize = ceils.size();
            for (int j = 0; j < ceilSize; j++) {
                Ceil ceil = ceils.get(j);
                if (isCeilVisiable(ceil)) {
                    drawCeilTopLine(canvas, ceil);
                    drawCeilLeftLine(canvas, ceil);
                    drawCeilBackground(canvas, ceil);
                }
            }
        }

        for (int i = 0; i < groupSize; i++) {
            CeilGroup ceilGroup = ceilGroups.get(i);
            List<Ceil> ceils = ceilGroup.getCeils();
            int ceilSize = ceils.size();
            for (int j = 0; j < ceilSize; j++) {
                Ceil ceil = ceils.get(j);
                drawCeilLinkLine(canvas, ceil);
            }
        }

        for (int i = 0; i < groupSize; i++) {
            CeilGroup ceilGroup = ceilGroups.get(i);
            List<Ceil> ceils = ceilGroup.getCeils();
            int ceilSize = ceils.size();
            for (int j = 0; j < ceilSize; j++) {
                Ceil ceil = ceils.get(j);
                if (isCeilVisiable(ceil)) {
                    drawCeilSelected(canvas, ceil);
                    drawCeilText(canvas, ceil);
                }
            }
        }
    }
```
3个两层循环绘制每一个 Ceil 需要绘制的部分。第一个循环我们绘制的是Ceil的顶部分割线和左边分割线以及背景，方法分别对应：
```
    /**
     * 绘制ceil左边的分割线
     *
     * @param canvas
     * @param ceil
     */
    private void drawCeilLeftLine(Canvas canvas, Ceil ceil) {
        canvas.drawLine(ceil.getLeft(), ceil.getTop(), ceil.getLeft(), ceil.getBottom(), linePaint);
    }

    /**
     * 绘制ceil顶部的分割线
     *
     * @param canvas
     * @param ceil
     */
    private void drawCeilTopLine(Canvas canvas, Ceil ceil) {
        canvas.drawLine(ceil.getLeft(), ceil.getTop(), ceil.getRight(), ceil.getTop(), linePaint);
    }
    
    /**
     * 绘制ceil的背景
     *
     * @param canvas
     * @param ceil
     */
    private void drawCeilBackground(Canvas canvas, Ceil ceil) {
        canvas.drawRect(ceil.getLeft(), ceil.getTop(), ceil.getRight(), ceil.getBottom(), backgroundPaint);
    }
```
第二个循环我们绘制的是选中的号码之间的连线：
```
    /**
     * 绘制ceil的连线
     *
     * @param canvas
     * @param ceil
     */
    private void drawCeilLinkLine(Canvas canvas, Ceil ceil) {
        Ceil nextCeil = ceil.getNextCeil();
        if (nextCeil != null) {
            canvas.drawLine(nextCeil.getCenterX(), nextCeil.getCenterY(), ceil.getCenterX(), ceil.getCenterY(), linkLinePaint);
        }
    }
```

第三个循环绘制的是选中的红球背景和红球数字号码：
```
    /**
     * 绘制ceil的文字
     *
     * @param canvas
     * @param ceil
     */
    private void drawCeilText(Canvas canvas, Ceil ceil) {
        textPaint.setColor(ceil.isSelected() ? numberSelectedColor : numberNormalColor);
        canvas.drawText(ceil.getNumber(), ceil.getCenterX(), ceil.getCenterY(), textPaint);
    }

    /**
     * 设置ceil选中的效果
     *
     * @param canvas
     * @param ceil
     */
    private void drawCeilSelected(Canvas canvas, Ceil ceil) {
        if (ceil.isSelected()) {
            canvas.drawOval(new RectF(ceil.getLeft(), ceil.getTop(), ceil.getRight(), ceil.getBottom()), selectedPaint);
        }
    }
```

> 为什么要三个循环而不是一个循环一起绘制呢？

因为绘制其实是有层级关系的，彼此是覆盖的，矩形背景在最底层，然后是连线层，最上层是红球背景加红球号码。否则会出现，矩形背景覆盖连线，或者连线遮盖红球号码的情况，这是bug级别的瑕疵。因此只能牺牲性能做三遍循环。
如果有什么好的优化方法，可以私信我修改。

[简书地址](https://www.jianshu.com/p/79884f0bd08a)




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