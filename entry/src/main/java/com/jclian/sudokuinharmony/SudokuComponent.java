package com.jclian.sudokuinharmony;

import ohos.agp.colors.Color;
import ohos.agp.components.Component;
import ohos.agp.components.DragEvent;
import ohos.agp.render.Canvas;
import ohos.agp.render.Paint;
import ohos.agp.render.Path;
import ohos.agp.render.PathEffect;
import ohos.app.Context;

public class SudokuComponent extends Component implements Component.DrawTask {
    private float yBtmFun = -1f;
    private float wBtmFun = -1f;
    private int menuNum = -1;
    private float menuCircleRadius = 0f;
    private float wMenu = 0f;

    // 记录选中九宫数字的坐标
    private int posY = -1;
    private int posX = -1;

    private Paint paint = new Paint();

    private int DEFAULT_SIZE = 450;

    private int wBlock = DEFAULT_SIZE - 10 / 10;
    private int hBlock = DEFAULT_SIZE - 10 / 10;

    public SudokuComponent(Context context) {
        super(context);
    }

    @Override
    public boolean onDrag(Component component, DragEvent event) {
        return super.onDrag(component, event);
    }

    @Override
    public void onDraw(Component component, Canvas canvas) {
        drawLines(canvas);
    }

    /**
     * 画九宫线
     */
    private void drawLines(Canvas canvas) {
        Path dashPath = new Path();
        Paint paint = new Paint();
        paint.setColor(new ohos.agp.utils.Color(0x9c915d));
        paint.setStrokeWidth(2f);
        paint.setStyle(Paint.Style.STROKE_STYLE);

        Paint dashPaint = new Paint();
        dashPaint.setStrokeWidth(2f);
        dashPaint.setColor((new ohos.agp.utils.Color(0x2f2e2b)));
        dashPaint.setStyle(Paint.Style.STROKE_STYLE);
        // todo 点类型
//        dashPaint.setPathEffect(new PathEffect(floatArrayOf(wBlock * 0.6f, wBlock * 0.4f), wBlock * -0.2f));
        dashPaint.setPathEffect(new PathEffect(null , wBlock * 0.6f, wBlock * 0.4f, PathEffect.Style.MORPH_STYLE));


        for (int i = 0; i <= 8; i++) {
            dashPath.reset();

            Paint p;
            if (i == 3 || i == 6) {
                p = paint;
            } else {
                p = dashPaint;
            }
            // 横线
            dashPath.moveTo(0f, (i * hBlock));
            dashPath.lineTo(getWidth(), (i * hBlock));
            canvas.drawPath(dashPath, p);
            // 竖线
            dashPath.moveTo((i * hBlock), 0f);
            dashPath.lineTo((i * hBlock), getWidth());
            canvas.drawPath(dashPath, p);

        }

    }
}
