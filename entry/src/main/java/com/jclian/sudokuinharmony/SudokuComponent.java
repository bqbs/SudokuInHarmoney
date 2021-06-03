package com.jclian.sudokuinharmony;

import ohos.agp.components.Component;
import ohos.agp.components.DragEvent;
import ohos.agp.render.Canvas;
import ohos.agp.render.Paint;
import ohos.agp.render.Path;
import ohos.agp.render.PathEffect;
import ohos.agp.utils.Color;
import ohos.agp.utils.RectFloat;
import ohos.agp.utils.TextAlignment;
import ohos.app.Context;

import java.util.Arrays;
import java.util.Collections;

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
        dashPaint.setPathEffect(new PathEffect(new float[]{wBlock * 0.6f, wBlock * 0.4f}, wBlock * -0.2f));


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

    private void drawNumPad(Canvas canvas) {
        paint.reset();
        //设定字体大小和对齐方式
        paint.setTextSize((int) (getWidth() / 5f * 0.6f));
        paint.setTextAlign(TextAlignment.CENTER);
        // 数字按钮区域
        for (int num = 1; num <= 10; num++) {
            String text;
            if (num == 10) {
                text = "X";
            } else {
                text = String.valueOf(num);
            }

            float left = (num - 1) % 5 / 5f * getWidth();
            float top = ((num - 1) / 5) * getWidth() / 5f + getWidth();
            float textHeight = paint.descent() - paint.ascent();
            float textOffset = textHeight / 2 - paint.descent();
            RectFloat bounds = new RectFloat(left, top, left + wMenu, top + wMenu);
            if (menuNum == num) {
                paint.setColor(new Color(0x9c915d));
                paint.setStyle(Paint.Style.FILLANDSTROKE_STYLE);
                canvas.drawCircle(bounds.getCenter().getPointX(), bounds.getCenter().getPointY(), menuCircleRadius, paint);
                paint.setColor(new Color(0xFFFFFF));
                canvas.drawText(paint, text, bounds.getCenter().getPointX(), bounds.getCenter().getPointY() + textOffset);
            } else {
                paint.setColor(Color.WHITE);
                paint.setStyle(Paint.Style.STROKE_STYLE);
                canvas.drawCircle(bounds.getCenter().getPointX(), bounds.getCenter().getPointY(), menuCircleRadius, paint);
                paint.setStyle(Paint.Style.FILLANDSTROKE_STYLE);
                canvas.drawText(
                        paint,
                        text,
                        bounds.getCenter().getPointX(),
                        bounds.getCenter().getPointY() + textOffset
                );
            }

        }
    }
}
