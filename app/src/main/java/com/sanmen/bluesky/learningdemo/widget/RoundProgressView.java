package com.sanmen.bluesky.learningdemo.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.View;

import com.sanmen.bluesky.learningdemo.R;

/**
 * @author lxt_bluesky
 * @date 2018/9/21
 * @description
 */
public class RoundProgressView extends View {
    /**
     * 外圆宽度
     */
    private float mBorderWidth;
    /**
     * 圆环颜色
     */
    private int mBorderColor;
    /**
     * 实心圆颜色
     */
    private int mRadiusColor;
    /**
     * 圆弧颜色
     */
    private int mArcColor;
    /**
     * 文字颜色
     */
    private int mTextColor;
    /**
     * 中间文字
     */
    private String mCenterText = "0S";
    /**
     * 文字大小
     */
    private float mTextSize;
    /**
     * 总进度
     */
    private int mTotalProgress = 100;
    /**
     * 当前进度
     */
    private int mProgress=0;

    private Paint mRadiusPaint = new Paint(Paint.ANTI_ALIAS_FLAG);

    private Paint mBorderPaint = new Paint(Paint.ANTI_ALIAS_FLAG);

    private Paint mCenterTextPaint = new Paint(Paint.ANTI_ALIAS_FLAG);

    private Paint mArcSPaint = new Paint(Paint.ANTI_ALIAS_FLAG);

    private static final int DEFAULT_BORDER_WIDTH = 10;

    private static final int DEFAULT_TEXT_SIZE = 20;


    public RoundProgressView(Context context) {
        this(context,null);
    }

    public RoundProgressView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs,0);
    }

    public RoundProgressView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        initAttrs(context,attrs);
        initVariable();
    }

    private void initAttrs(Context context, AttributeSet attrs) {
        TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.RoundProgressView);
        mBorderWidth = typedArray.getDimensionPixelSize(R.styleable.RoundProgressView_mBorderWidth,dip2px(DEFAULT_BORDER_WIDTH));
        mBorderColor = typedArray.getColor(R.styleable.RoundProgressView_mBorderColor,Color.parseColor("#f5f3f3"));
        mRadiusColor = typedArray.getColor(R.styleable.RoundProgressView_mRadiusColor,Color.parseColor("#ffffff"));
        mArcColor = typedArray.getColor(R.styleable.RoundProgressView_mArcColor,Color.parseColor("#d50f09"));
        mTextColor = typedArray.getColor(R.styleable.RoundProgressView_mTextColor,Color.parseColor("#d50f09"));
        mTextSize = typedArray.getDimensionPixelSize(R.styleable.RoundProgressView_mTextSize,dip2px(DEFAULT_TEXT_SIZE));

    }

    private void initVariable() {

        //设置mBorderPaint画笔
        //设置为绘制描边
        mBorderPaint.setStyle(Paint.Style.STROKE);
        mBorderPaint.setStrokeWidth(mBorderWidth);
        mBorderPaint.setColor(mBorderColor);
        mBorderPaint.setAntiAlias(true);

        //设置mRadiusPaint画笔
        //设置绘制实心圆
        mRadiusPaint.setStyle(Paint.Style.FILL);
        mRadiusPaint.setColor(mRadiusColor);
        //抗锯齿
        mRadiusPaint.setAntiAlias(true);

        //设置mCenterTextPaint画笔
        mCenterTextPaint.setColor(mTextColor);
        mCenterTextPaint.setStyle(Paint.Style.FILL);
        //设置文本大小
        mCenterTextPaint.setTextSize(mTextSize);
        mCenterTextPaint.setAntiAlias(true);


        //设置mArcSPaint画笔
        mArcSPaint.setStyle(Paint.Style.STROKE);
        mArcSPaint.setColor(mArcColor);
        mArcSPaint.setAntiAlias(true);
        mArcSPaint.setStrokeWidth(mBorderWidth);
        //设置圆弧线帽样式
//        mArcSPaint.setStrokeCap(Paint.Cap.SQUARE);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        //计算绘制的宽高
        int viewWidth = getWidth();
        int viewHeight = getHeight();
        int viewMinSize = Math.min(viewHeight,viewWidth);
        //描边绘制半径
        float mRingRadius = (viewMinSize-mBorderWidth)/2.0f;
        //计算中心点坐标
        float centerX = viewWidth/2.0f;
        float centerY = viewHeight/2.0f;

        //矩阵
        RectF oval = new RectF();
        oval.left = centerX-mRingRadius;
        oval.top = centerY-mRingRadius;
        oval.right = centerX+mRingRadius;
        oval.bottom = centerY+mRingRadius;
        //外圆环
//        canvas.drawCircle(radius,radius,mRingRadius,mBorderPaint);
        canvas.drawArc(oval,0f,360f,false,mBorderPaint);
        //内实心圆
        canvas.drawCircle(centerX,centerY,mRingRadius-mBorderWidth/2.0f,mRadiusPaint);

        //计算文字绘制X坐标
        int textX= toCalculateTextX(mCenterTextPaint,mCenterText);
        //计算文本绘制高度
        Paint.FontMetricsInt fm = mCenterTextPaint.getFontMetricsInt();
        int textY =(getHeight()-(fm.descent-fm.ascent))/2-fm.ascent;
        if(mProgress>0){
            //圆弧
            canvas.drawArc(oval,270f,((float) mProgress/mTotalProgress)*360f,false,mArcSPaint);
            mCenterText=mProgress+"S";

            textX= toCalculateTextX(mCenterTextPaint,mCenterText);
        }
        //中间文字
        canvas.drawText(mCenterText,textX,textY,mCenterTextPaint);
    }



    /**
     * 设置圆弧线帽样式
     * @param cap
     */
    public void setStokeCap(Paint.Cap cap){
        mRadiusPaint.setStrokeCap(cap);

    }

    /**
     * 设置进度
     * @param progress
     */
    public void setProgress(int progress) {
        mProgress = progress;
        //重新绘制
        postInvalidate();
    }

    /**
     * 计算文字绘制X坐标
     * @param text
     * @return
     */
    private int toCalculateTextX(Paint paint,String text) {

        int textWidth = (int) paint.measureText(text);
        if (text!=null){
            return (getMeasuredWidth()-textWidth)/2;
        }
        return 0;
    }

    /**
     * dp转px
     * @param dipVal
     * @return
     */
    private int dip2px(int dipVal){
        float scale = getResources().getDisplayMetrics().density;
        return (int) (dipVal*scale+0.5f);
    }


    private class ProgressRunable implements Runnable{

        @Override
        public void run() {

        }
    }
}
