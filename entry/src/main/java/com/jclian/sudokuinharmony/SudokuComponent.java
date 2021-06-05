package com.jclian.sudokuinharmony;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import ohos.agp.components.AttrSet;
import ohos.agp.components.Component;
import ohos.agp.components.DragEvent;
import ohos.agp.render.*;
import ohos.agp.utils.*;
import ohos.agp.window.dialog.ToastDialog;
import ohos.app.Context;
import ohos.data.DatabaseHelper;
import ohos.data.preferences.Preferences;
import ohos.hiviewdfx.HiLog;
import ohos.hiviewdfx.HiLogLabel;
import ohos.multimodalinput.event.MmiPoint;
import ohos.multimodalinput.event.TouchEvent;

import java.lang.reflect.Type;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import static com.jclian.sudokuinharmony.BuildConfig.DEBUG;
import static ohos.app.Context.MODE_PRIVATE;

public class SudokuComponent extends Component implements Component.DrawTask, Component.EstimateSizeListener, Component.TouchEventListener {

    private Color highLightTextColor = new Color(Color.getIntColor("#191919"));
    private Color fillTextColor = new Color(Color.getIntColor("#d6d6d6"));
    private Color pinedTextColor = new Color(Color.getIntColor("#a0a0a0"));
    private Color selectedCircleColor = new Color(Color.getIntColor("#9c915d"));
    private Color pinedCircleColor = new Color(Color.getIntColor("#2d2d2d"));
    private Color highlightCircleColor = new Color(Color.getIntColor("#666355"));

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
        // 自定义属性。获取前，调用.isPresent()判断是否能
        if (attrSet.getAttr("textColor_highlight").isPresent()) {
            highLightTextColor = attrSet.getAttr("textColor_highlight").get().getColorValue();
        }
        if (attrSet.getAttr("textColor_pined").isPresent()) {
            pinedTextColor = attrSet.getAttr("textColor_pined").get().getColorValue();
        }
        if (attrSet.getAttr("textColor_fill").isPresent()) {
            fillTextColor = attrSet.getAttr("textColor_fill").get().getColorValue();
        }

        setEstimateSizeListener(this);
        setTouchEventListener(this);
        addDrawTask(this);

    }

    @Override
    public boolean onDrag(Component component, DragEvent event) {
        return super.onDrag(component, event);
    }

    @Override
    public void onDraw(Component component, Canvas canvas) {
        HiLog.debug(LABEL_LOG, "onDraw");
        canvas.drawColor(highLightTextColor.getValue(), BlendMode.SRC);
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
        paint.setColor(new Color(Color.getIntColor("#9c915d")));
        paint.setStrokeWidth(2f);
        paint.setStyle(Paint.Style.STROKE_STYLE);

        Paint dashPaint = new Paint();
        dashPaint.setStrokeWidth(2f);
        paint.setColor(new Color(Color.getIntColor("#2f2e2b")));
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

            float left = (num - 1) % 5 / 5f * getWidth();
            float top = ((num - 1) / 5) * getWidth() / 5f + getWidth();
            float textHeight = paint.descent() - paint.ascent();
            float textOffset = textHeight / 2 - paint.descent();
            RectFloat bounds = new RectFloat(left, top, left + wMenu, top + wMenu);
            if (menuNum == num) {
                paint.setColor(new Color(Color.getIntColor("#9c915d")));
                paint.setStyle(Paint.Style.FILLANDSTROKE_STYLE);
                canvas.drawCircle(bounds.getCenter().getPointX(), bounds.getCenter().getPointY(), menuCircleRadius, paint);
                paint.setColor(new Color(Color.getIntColor("#FFFFFF")));
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
        HiLog.debug(LABEL_LOG, "start");
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

        invalidate();
    }


    /**
     * 缓存数度
     */
    public void dump() {
        HiLog.debug(LABEL_LOG, "dump");

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
        HiLog.debug(LABEL_LOG, "block width =" + wBlock + ",height =" + hBlock);
        wMenu = width / 5f;
        menuCircleRadius = wMenu / 2 * 0.8f;
        yBtmFun = width + wMenu * 2;
        wBtmFun = width / 4f;
        return true;
    }

    @Override
    public boolean onTouchEvent(Component component, TouchEvent event) {
        if (event.getAction() == TouchEvent.POINT_MOVE) {
            return false;
        }
        // 点击的位置
        MmiPoint position = event.getPointerPosition(0);
        int width = getWidth();
        if (position.getY() < getWidth()) {
            // 九宫区域
            if (event.getAction() == TouchEvent.PRIMARY_POINT_DOWN) {
                int x = (int) (position.getX() / (width / 9));
                int y = (int) (position.getY() / (width / 9));

                String key = x + "," + y;

                boolean isPin = initData.containsKey(key);

                if (menuNum != -1) {
                    if (!isPin) {
                        if (fillData.containsKey(key)) {
                            fillData.remove(key);
                        } else {
                            fillData.put(key, menuNum);
                        }
                    }
                } else {
                    if (x == posX && y == posY) {
                        posX = -1;
                        posY = -1;
                    } else {
                        posX = x;
                        posY = y;
                    }
                }

                if (DEBUG) {
                    HiLog.debug(LABEL_LOG, "event = " + event + ", posX =" + posX + " posY = " + posY);
                }
            }
        } else if (position.getY() > width && position.getY() < width + 2f * wMenu) {
            // 触摸区域在功能菜单
            int y = (int) ((position.getY() - width) / wMenu);
            int x = (int) (position.getX() / wMenu);
            float left = wMenu * x;
            float right = (wMenu * (x + 1));
            float top = (y * wMenu + width);
            float bottom = ((y + 1) * wMenu + width);
            RectFloat rect = new RectFloat(left, top, right, bottom);
            // 通过计算触摸点距离圆心距离，确定触摸点是否在圆内
            boolean isInside = Math.pow((position.getX() - rect.getCenter().getPointX()), 2) + Math.pow((position.getY() - rect.getCenter().getPointY())
                    , 2) <= Math.pow(menuCircleRadius, 2);
            if (isInside) {
                //在圆形菜单内
                int temp = x + 5 * y + 1;
                String key = posX + "," + posY;

                boolean isPin = initData.containsKey(key);
                if (posX != -1 && posY != -1) {
                    if (event.getAction() == TouchEvent.PRIMARY_POINT_DOWN) {
                        if (menuNum == temp || temp == 10) {
                            menuNum = -1;
                        } else {
                            menuNum = temp;
                        }
                        if (!isPin) {
                            String lkey = posX + "," + posY;
                            if (menuNum == -1 || (fillData.containsKey(lkey) && fillData.get(lkey) == menuNum)) {
                                fillData.remove(lkey);
                            } else {
                                fillData.put(lkey, temp);
                            }
                        }
                    } else if (event.getAction() == TouchEvent.PRIMARY_POINT_UP) {
                        menuNum = -1;
                    }
                } else {
                    if (event.getAction() == TouchEvent.PRIMARY_POINT_UP) {
                        if (menuNum == temp) {
                            menuNum = -1;
                        } else {
                            menuNum = temp;
                        }
                    }
                }

            }
        }
        if (event.getAction() == TouchEvent.PRIMARY_POINT_UP) {
            if ((fillData.size() + initData.size()) >= 81) {
                Map<String, Integer> solvedData = new HashMap<String, Integer>();
                solvedData.putAll(initData);
                solvedData.putAll(fillData);
                if (solvedData.size() >= 81) {
                    checkSudoku(solvedData);
                }
            }
        }
        invalidate();

        return true;
    }


    private void checkSudoku(Map<String, Integer> data) {
        if (Sudoku.check(data)) {
            new ToastDialog(getContext())
                    .setText(getContext().getString(ResourceTable.String_solved))
                    .setAlignment(LayoutAlignment.CENTER)
                    .show();
        } else {
            new ToastDialog(getContext())
                    .setText(getContext().getString(ResourceTable.String_un_solved))
                    .setAlignment(LayoutAlignment.CENTER)
                    .show();

        }
    }

}
