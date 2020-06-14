package com.admanager.musicplayer.tasks;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.RectF;
import android.os.Handler;
import android.text.TextPaint;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

import com.admanager.musicplayer.R;
import com.admanager.musicplayer.listeners.MPBassBoostViewListener;

import java.util.Random;

public class BassBoostView extends View {
    private static final int MAX_IMAGE_LEVEL = 12;
    private static final int MAX_BASS_LEVEL = 100;

    private static final int BOOST_TOP_BOTTOM_PADDING = 60;
    private static final float BOOST_HEIGHT_WIDTH_RATIO = 2.0f;

    private static final int COLUMN_NUMBER = 1;
    private static final int ROW_NUMBER = 12;

    private static final int LEFT_RIGHT_PADDING = 4;
    private static final int TOP_BOTTOM_PADDING = 8;

    private static final float ROUND_VALUE = 10.0f;
    private final Rect bounds = new Rect();
    private final RectF rectF = new RectF();
    private int[] light_colors = {R.color.mplight_red, R.color.mplight_red,
            R.color.mplight_orange, R.color.mplight_orange,
            R.color.mplight_yellow, R.color.mplight_yellow, R.color.mplight_yellow,
            R.color.mplight_blue, R.color.mplight_blue, R.color.mplight_blue, R.color.mplight_blue,
            R.color.mplight_green, R.color.mplight_green, R.color.mplight_green, R.color.mplight_green, R.color.mplight_green};
    private int[] dark_colors = {R.color.mpdark_red, R.color.mpdark_red,
            R.color.mpdark_orange, R.color.mpdark_orange,
            R.color.mpdark_yellow, R.color.mpdark_yellow, R.color.mpdark_yellow,
            R.color.mpdark_blue, R.color.mpdark_blue, R.color.mpdark_blue, R.color.mpdark_blue,
            R.color.mpdark_green, R.color.mpdark_green, R.color.mpdark_green, R.color.mpdark_green, R.color.mpdark_green};
    private Paint[] light_paints;
    private Paint[] dark_paints;
    private int imageLevel = 0;
    private int bassLevel = 0;
    private boolean playing = false;
    private Random random;
    private int width;
    private float centreX, centreY;
    private int boostWidth, boostHeight;
    private float oneLevelHeight;
    private float bassOneLevelHeight;
    private float levelY;
    private float textX;
    private MPBassBoostViewListener MPBassBoostViewListener;
    private boolean enabled;
    private Paint linePaint, textPaint;
    private Handler m_Handler;
    private final Runnable m_Runnable = () -> {
        postInvalidate();
        startHandler();
    };

    public BassBoostView(Context context) {
        super(context);
        init(context, null);
    }

    public BassBoostView(Context context, AttributeSet foo) {
        super(context, foo);
        if (!isInEditMode()) {
            init(context, foo);
        }
    }

    private void init(Context context, AttributeSet foo) {
        m_Handler = new Handler();

        //Light Paints
        Paint lightRedPaint = new Paint();
        lightRedPaint.setColor(context.getResources().getColor(R.color.mplight_red));

        Paint lightOrangePaint = new Paint();
        lightOrangePaint.setColor(context.getResources().getColor(R.color.mplight_orange));

        Paint lightYellowPaint = new Paint();
        lightYellowPaint.setColor(context.getResources().getColor(R.color.mplight_yellow));

        Paint lightBluePaint = new Paint();
        lightBluePaint.setColor(context.getResources().getColor(R.color.mplight_blue));

        Paint lightGreenPaint = new Paint();
        lightGreenPaint.setColor(context.getResources().getColor(R.color.mplight_green));

        //Dark Paints
        Paint darkRedPaint = new Paint();
        darkRedPaint.setColor(context.getResources().getColor(R.color.mpdark_red));

        Paint darkOrangePaint = new Paint();
        darkOrangePaint.setColor(context.getResources().getColor(R.color.mpdark_orange));

        Paint darkYellowPaint = new Paint();
        darkYellowPaint.setColor(context.getResources().getColor(R.color.mpdark_yellow));

        Paint darkBluePaint = new Paint();
        darkBluePaint.setColor(context.getResources().getColor(R.color.mpdark_blue));

        Paint darkGreenPaint = new Paint();
        darkGreenPaint.setColor(context.getResources().getColor(R.color.mpdark_green));

        light_paints = new Paint[]{lightRedPaint, lightRedPaint,
                lightOrangePaint, lightOrangePaint,
                lightYellowPaint, lightYellowPaint,
                lightBluePaint, lightBluePaint, lightBluePaint,
                lightGreenPaint, lightGreenPaint, lightGreenPaint};

        dark_paints = new Paint[]{darkRedPaint, darkRedPaint,
                darkOrangePaint, darkOrangePaint,
                darkYellowPaint, darkYellowPaint,
                darkBluePaint, darkBluePaint, darkBluePaint,
                darkGreenPaint, darkGreenPaint, darkGreenPaint};

        setBoostHeightAndWidth(getHeight());

        linePaint = new Paint();
        linePaint.setStyle(Paint.Style.STROKE);
        linePaint.setStrokeWidth(context.getResources().getDimension(R.dimen.mp_bass_boost_line_stroke_width));
        linePaint.setAntiAlias(false);
        linePaint.setColor(Color.WHITE);

        textPaint = new TextPaint();
        textPaint.setTextSize(getResources().getDimension(R.dimen.mp_bass_boost_text_size));
        textPaint.setColor(Color.WHITE);

        random = new Random();
    }

    private int getRandomLevel() {
        if (imageLevel > 0) {
            int randomLevel = random.nextInt(imageLevel + 1);
            if (randomLevel >= 0 && randomLevel <= MAX_IMAGE_LEVEL) {
                return randomLevel;
            }
        } else {
            return 0;
        }

        return 0;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        int tempLevel;
        if (playing && enabled) {
            tempLevel = getRandomLevel();
        } else {
            tempLevel = 0;
        }

        int viewHeight = getMeasuredHeight() - BOOST_TOP_BOTTOM_PADDING;
        int viewWidth = (int) (viewHeight / BOOST_HEIGHT_WIDTH_RATIO);

        int totalColorHeight = viewHeight - ((ROW_NUMBER + 1) * (TOP_BOTTOM_PADDING * 2));
        int height_interval = totalColorHeight / ROW_NUMBER;

        int totalColorWidth = viewWidth - ((COLUMN_NUMBER + 1) * (LEFT_RIGHT_PADDING * 2));
        int width_interval = totalColorWidth / COLUMN_NUMBER;

        //Draw Dark Paints
        int left = 0;
        int right;

        for (int j = 0; j < COLUMN_NUMBER; j++) {
            if (j == 0) {
                left = LEFT_RIGHT_PADDING * 2;
            } else {
                left = left + LEFT_RIGHT_PADDING * 2 + width_interval;
            }

            right = left + width_interval;

            int top = 0;
            int bottom;

            for (int i = 0; i < ROW_NUMBER; i++) {
                if (i == 0) {
                    top = TOP_BOTTOM_PADDING * 2;
                } else {
                    top = top + TOP_BOTTOM_PADDING * 2 + height_interval;
                }

                bottom = top + height_interval;

                rectF.left = centreX + left;
                rectF.top = top + (BOOST_TOP_BOTTOM_PADDING / 2.0f);
                rectF.right = centreX + right;
                rectF.bottom = bottom + (BOOST_TOP_BOTTOM_PADDING / 2.0f);
                //rectF = new RectF(centreX+left, top + (BOOST_TOP_BOTTOM_PADDING/2), centreX+right, bottom + (BOOST_TOP_BOTTOM_PADDING/2));

                canvas.drawRoundRect(rectF, ROUND_VALUE, ROUND_VALUE, dark_paints[i]);
            }
        }

        //Draw Light Paints
        left = 0;

        for (int j = 0; j < COLUMN_NUMBER; j++) {
            if (j == 0) {
                left = LEFT_RIGHT_PADDING * 2;
            } else {
                left = left + LEFT_RIGHT_PADDING * 2 + width_interval;
            }

            right = left + width_interval;

            int top = 0;
            int bottom;

            if (tempLevel > 0) {
                top = TOP_BOTTOM_PADDING * 2;
                top = top + (TOP_BOTTOM_PADDING * 2 + height_interval) * (ROW_NUMBER - tempLevel - 1);
            }

            for (int i = ROW_NUMBER - tempLevel; i < ROW_NUMBER; i++) {
                if (i == 0) {
                    top = TOP_BOTTOM_PADDING * 2;
                } else {
                    top = top + TOP_BOTTOM_PADDING * 2 + height_interval;
                }

                bottom = top + height_interval;

                rectF.left = centreX + left;
                rectF.top = top + (BOOST_TOP_BOTTOM_PADDING / 2.0f);
                rectF.right = centreX + right;
                rectF.bottom = bottom + (BOOST_TOP_BOTTOM_PADDING / 2.0f);
                //rectF = new RectF(centreX+left, top + (BOOST_TOP_BOTTOM_PADDING/2), centreX+right, bottom + (BOOST_TOP_BOTTOM_PADDING/2));

                canvas.drawRoundRect(rectF, ROUND_VALUE, ROUND_VALUE, light_paints[i]);
            }
        }

        if (levelY >= 0) {
            canvas.drawLine(0, levelY, width, levelY, linePaint);

            String text = getTextPercent();

            textPaint.getTextBounds(text, 0, text.length(), bounds);

            canvas.drawText(text, textX - (bounds.width() / 2.0f), levelY - 4, textPaint);
        }
    }

    private String getTextPercent() {
        if (bassLevel >= 0 && bassLevel <= MAX_BASS_LEVEL) {
            return bassLevel + "%";
        } else {
            return "";
        }
    }

    public void onSizeChanged(int w, int h, int oldWidth, int oldHeight) {
        super.onSizeChanged(w, h, oldWidth, oldHeight);

        this.width = w;

        setBoostHeightAndWidth(h);

        centreX = (width - boostWidth) / 2.0f;
        centreY = (h - boostHeight) / 2.0f;

        setLevelYForLine();

        textX = (width - boostWidth) / 4.0f;
    }

    private void setBoostHeightAndWidth(int height) {
        this.boostHeight = height - BOOST_TOP_BOTTOM_PADDING;
        this.boostWidth = (int) (boostHeight / BOOST_HEIGHT_WIDTH_RATIO);

        oneLevelHeight = (boostHeight * 1.0f) / (MAX_IMAGE_LEVEL * 1.0f);
        bassOneLevelHeight = (boostHeight * 1.0f) / (MAX_BASS_LEVEL * 1.0f);
    }

    public boolean onTouchEvent(MotionEvent motionEvent) {
        if (!this.enabled) {
            return false;
        }

        switch (motionEvent.getAction()) {
            case MotionEvent.ACTION_DOWN:
            case MotionEvent.ACTION_UP:

                break;
            case MotionEvent.ACTION_MOVE:

                float y = motionEvent.getY();

                int curBassLevel = bassLevel;

                if (y >= centreY + boostHeight) {
                    imageLevel = 0;
                    bassLevel = 0;
                } else if (y <= centreY) {
                    imageLevel = MAX_IMAGE_LEVEL;
                    bassLevel = MAX_BASS_LEVEL;
                } else {
                    imageLevel = (int) ((centreY + boostHeight - y) / oneLevelHeight);
                    bassLevel = (int) ((centreY + boostHeight - y) / bassOneLevelHeight);
                }

                if (bassLevel >= 0 && bassLevel <= MAX_BASS_LEVEL && curBassLevel != bassLevel) {
                    setLevelYForLine();

                    if (MPBassBoostViewListener != null)
                        MPBassBoostViewListener.valueChanged(bassLevel);

                    postInvalidate();
                }

                break;
        }

        return true;
    }

    @Override
    public void setEnabled(boolean enable) {
        this.enabled = enable;

        super.setEnabled(enabled);

        if (!enabled) {
            imageLevel = 0;

            levelY = -1;

            postInvalidate();

            stopHandler();
        } else {
            startHandler();
        }
    }

    public void setMPBassBoostViewListener(MPBassBoostViewListener MPBassBoostViewListener) {
        this.MPBassBoostViewListener = MPBassBoostViewListener;
    }

    public void setBassLevel(int bassLevel) {
        if (enabled) {
            this.bassLevel = bassLevel;

            this.imageLevel = (bassLevel * MAX_IMAGE_LEVEL) / 100;

            setLevelYForLine();

            postInvalidate();
        }
    }

    private void setLevelYForLine() {
        if (enabled) {
            if (bassLevel == MAX_BASS_LEVEL) levelY = centreY;
            else if (bassLevel == 0) levelY = centreY + boostHeight;
            else {
                levelY = centreY + ((MAX_BASS_LEVEL - bassLevel) * bassOneLevelHeight);
            }
        }
    }

    private void startHandler() {
        synchronized (this) {
            if (m_Handler != null) {
                m_Handler.removeCallbacks(m_Runnable);
                m_Handler.postDelayed(m_Runnable, 100);
            }
        }
    }

    private void stopHandler() {
        if (m_Handler != null) {
            m_Handler.removeCallbacks(m_Runnable);
        }
    }

    public void setPlaying(boolean playing) {
        this.playing = playing;

        postInvalidate();
    }
}
