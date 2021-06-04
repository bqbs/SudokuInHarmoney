package com.jclian.sudokuinharmony;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import ohos.agp.components.AttrSet;
import ohos.agp.components.Component;
import ohos.agp.components.DragEvent;
import ohos.agp.render.Canvas;
import ohos.agp.render.Paint;
import ohos.agp.render.Path;
import ohos.agp.render.PathEffect;
import ohos.agp.utils.*;
import ohos.app.Context;
import ohos.data.DatabaseHelper;
import ohos.data.preferences.Preferences;
import ohos.hiviewdfx.HiLog;
import ohos.hiviewdfx.HiLogLabel;
import ohos.multimodalinput.event.TouchEvent;

import java.lang.reflect.Type;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import static ohos.app.Context.MODE_PRIVATE;

public class SudokuComponent extends Component implements Component.DrawTask, Component.EstimateSizeListener, Component.TouchEventListener {

    private Color highLightTextColor = new Color(0x191919);
    private Color fillTextColor = new Color(0xd6d6d6);
    private Color pinedTextColor = new Color(0xa0a0a0);
    private Color selectedCircleColor = new Color(0x9c915d);
    private Color pinedCircleColor = new Color(0x2d2d2d);
    private Color highlightCircleColor = new Color(0x666355);

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

    private int wBlock = (DEFAULT_SIZE - 10) / 10;
    private int hBlock = (DEFAULT_SIZE - 10) / 10;

    private Map<String, Integer> initData = new HashMap<String, Integer>();
    private Map<String, Integer> fillData = new HashMap<String, Integer>();
    private HiLogLabel LABEL_LOG = new HiLogLabel(HiLog.LOG_APP, 0x001, "SudokuComponent");

    public SudokuComponent(Context context) {
        super(context);

    }

    public SudokuComponent(Context context, AttrSet attrSet) {
        super(context, attrSet);
        //0x191919
        highLightTextColor = attrSet.getAttr("textColor_highlight").get().getColorValue();
        pinedTextColor = attrSet.getAttr("textColor_pined").get().getColorValue();
        fillTextColor = attrSet.getAttr("textColor_fill").get().getColorValue();

        setEstimateSizeListener(this);
        addDrawTask(this);
        setTouchEventListener(this);

    }

    @Override
    public boolean onDrag(Component component, DragEvent event) {
        return super.onDrag(component, event);
    }

    @Override
    public void onDraw(Component component, Canvas canvas) {
        drawLines(canvas);
        drawCells(canvas);
        drawNumPad(canvas);
    }

    /**
     * 画九宫线
     */
    private void drawLines(Canvas canvas) {
        Path dashPath = new Path();
        Paint paint = new Paint();
        paint.setColor(new Color(0x9c915d));
        paint.setStrokeWidth(2f);
        paint.setStyle(Paint.Style.STROKE_STYLE);

        Paint dashPaint = new Paint();
        dashPaint.setStrokeWidth(2f);
        dashPaint.setColor((new Color(0x2f2e2b)));
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

    /**
     * 画九宫格中数字
     */
    private void drawCells(Canvas canvas) {
        Paint paint = new Paint();
        for (int i = 0; i <= 8; i++) {
            paint.reset();

            for (int j = 0; j <= 8; j++) {
                String key = i + "," + j;
                boolean pinNum = initData.containsKey(key);
                boolean fillNum = fillData.containsKey(key);
                boolean isSelected = posX == i && posY == j;
                if (pinNum) {

                }
                if (isSelected) {
                    paint.setColor(selectedCircleColor);
                } else {
                    paint.setColor(pinedCircleColor);

                }
                paint.setStyle(Paint.Style.FILLANDSTROKE_STYLE);
                float x = i * getWidth() / 9f + getWidth() / 18f;
                float y = j * getWidth() / 9f + getWidth() / 18f;
                float left = i * getWidth() / 9f;
                float top = j * getWidth() / 9f;

                if (pinNum || (i == posX && j == posY)) {
                    canvas.drawCircle(x, y, wBlock * 0.8f / 2, paint);
                }
                if (pinNum) {
                    canvas.drawCircle(x, y, wBlock * 0.8f / 2, paint);

                    int num = initData.get(key);
                    paint.setColor(pinedTextColor);
                    paint.setTextSize((int) (wBlock * 0.8 / 2));
                    paint.setTextAlign(TextAlignment.CENTER);
                    float textHeight = paint.descent() - paint.ascent();
                    float textOffset = textHeight / 2 - paint.descent();
                    RectFloat bounds = new RectFloat(left, top, left + wBlock, top + wBlock);
                    canvas.drawText(paint, String.valueOf(num), bounds.getCenter().getPointX(),
                            bounds.getCenter().getPointY() + textOffset);

                }
                boolean hasFillNum = fillData.containsKey(key);
                if (hasFillNum) {
                    int num = fillData.get(key);
                    paint.setColor(fillTextColor);
                    paint.setTextSize((int) (wBlock * 0.8f / 2));
                    paint.setTextAlign(TextAlignment.CENTER);
                    float textHeight = paint.descent() - paint.ascent();
                    float textOffset = textHeight / 2 - paint.descent();
                    RectFloat bounds = new RectFloat(left, top, left + wBlock, top + wBlock);
                    canvas.drawText(paint, String.valueOf(num), bounds.getCenter().getPointX(), bounds.getCenter().getPointY() + textOffset
                    );
                }
            }


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

            float left = (num - 1) % 5f / 5f * getWidth();
            float top = ((num - 1) / 5f) * getWidth() / 5f + getWidth();
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

    /**
     * 数独初始化
     */
    public void start() {
        DatabaseHelper helper = new DatabaseHelper(getContext());
        Preferences sp = helper.getPreferences("dump_sudoku");
        String initJson = sp.getString("initdata", null);
        String fillJson = sp.getString("filldata", null);
        initData.clear();
        Map<String, Integer> initMap;
        if (TextTool.isNullOrEmpty(initJson)) {
            initMap = Sudoku.gen();
        } else {
            Type type = new TypeToken<HashMap<String, Integer>>() {
            }.getType();
            initMap = new Gson().fromJson(initJson, type);
        }
        initData.putAll(initMap);
        fillData.clear();
        Map<String, Integer> fillMap;

        if (TextTool.isNullOrEmpty(fillJson)) {
            fillMap = new HashMap();
        } else {
            Type type = new TypeToken<HashMap<String, Integer>>() {
            }.getType();
            fillMap = new Gson().fromJson(fillJson, type);
        }
        fillData.putAll(fillMap);


    }


    /**
     * 缓存数度
     */
    public void dump() {
        Type type = new TypeToken<HashMap<String, Integer>>() {
        }.getType();
        String puzzleStr = new Gson().toJson(initData, type);
        String ansStr = new Gson().toJson(fillData, type);
        DatabaseHelper helper = new DatabaseHelper(getContext());
        Preferences sp = helper.getPreferences("dump_sudoku");
        sp.putString("initdata", puzzleStr).putString("filldata", ansStr).flush();
    }

    @Override
    public boolean onEstimateSize(int widthEstimateConfig, int heightEstimateConfig) {

        int width = Component.EstimateSpec.getSize(widthEstimateConfig);
        int height = Component.EstimateSpec.getSize(heightEstimateConfig);
        setEstimatedSize(
                Component.EstimateSpec.getChildSizeWithMode(width, width, Component.EstimateSpec.NOT_EXCEED),
                Component.EstimateSpec.getChildSizeWithMode(height, height, Component.EstimateSpec.NOT_EXCEED));

        HiLog.debug(LABEL_LOG, "", "");
        wBlock = width / 9;
        hBlock = wBlock;
        HiLog.debug(LABEL_LOG, "block width =$wBlock , height = $hBlock");
        wMenu = width / 5f;
        menuCircleRadius = wMenu / 2 * 0.8f;
        yBtmFun = width + wMenu * 2;
        wBtmFun = width / 4f;
        return true;
    }


    private int getMySize(int measureSpec) {
        int mode = EstimateSpec.getMode(measureSpec);
        int size = EstimateSpec.getSize(measureSpec);
        if (mode == EstimateSpec.NOT_EXCEED) {
            return size;

        } else if (mode == EstimateSpec.PRECISE) {
            return size;
        } else if (mode == EstimateSpec.UNCONSTRAINT) {
            return size;
        } else {
            return DEFAULT_SIZE;
        }
    }

    @Override
    public boolean onTouchEvent(Component component, TouchEvent touchEvent) {
        return false;
    }
}
