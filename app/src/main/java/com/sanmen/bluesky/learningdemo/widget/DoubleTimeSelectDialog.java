package com.sanmen.bluesky.learningdemo.widget;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;

import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextUtils;
import android.text.style.ForegroundColorSpan;
import android.text.style.RelativeSizeSpan;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.sanmen.bluesky.learningdemo.R;
import com.sanmen.bluesky.learningdemo.util.TimeUtil;

import java.util.Arrays;
import java.util.Calendar;
import java.util.List;

/**
 * @author lxt_bluesky
 * @date 2018/9/30
 * @description 双时间选择器
 */
public class DoubleTimeSelectDialog extends Dialog implements View.OnClickListener {
    /**
     * 默认可选择的最小日期
     */
    private static final String DEFAULT_SMALLEST_TIME = "1970-01-01 00:00:00";
    /**
     * 默认可选择的最大日期
     */
    private static final String DEFAULT_BIGGEST_TIME = "2037-12-31 23:59:59";

    /**
     * 显示类型
     */
    public enum TYPE {
        /**年月日时分*/
        YEAR_MONTH_DATE_HOUR_MINUTE,
        /**年月日*/
        YEAR_MONTH_DATE,
        /**年月*/
        YEAR_MONTH,
        /**月日*/
        MONTH_DATE,
        /**时分*/
        HOUR_MINUTE}

    private enum TIME_TYPE {
        /**开始时间*/
        TYPE_START,
        /**结束时间*/
        TYPE_END
    }

    /**
     * 开始、结束的年份,月份,天数,小时数,分钟数
     */
    private static int START_YEAR = 1970, END_YEAR = 2037;
    private static int START_MONTH = 1, END_MONTH = 12;
    private static int START_DAY = 1, END_DAY = 31;
    private static int START_HOUR = 0,END_HOUR=23;
    private static int START_MINUTE=0,END_MINUTE=59;

    /**
     * 当前显示类型,对应前面设置的显示类型TYPE
     */
    private int mType = 0;
    /**
     * 临时显示类型
     */
    private int curType=0;
    /**
     * 是否手动设置显示类型
     */
    private boolean isSetType=false;

    /**
     * 当前选择时间模式
     */
    private TIME_TYPE mTimeType = TIME_TYPE.TYPE_START;

    /**是否只选择本年 */
    private boolean isOnlyThisYear = false;

    /**是否只选择本月 */
    private boolean isOnlyThisMonth = false;

    /**最小时间*/
    private String mSmallestTime;
    /**最大时间*/
    private String mBiggestTime;
    /**默认选中时间*/
    private String defaultSelectedTime;
    /**
     * 选择的开始时间
     */
    private String mSelectStartTime;
    /**
     * 选择的结束时间
     */
    private String mSelectEndTime;

    /**
     * 当前日期
     */
    private int curYear;

    private int curMonth;

    private int curDay;

    private int curHour;

    private int curMinute;

    /**
     * 临时日期
     */
    private int year;

    private int month;

    private int day;

    private int hour;

    private int minute;
    /**
     * list列表(大月份)
     */
    private List<String> mListBig;
    /**
     * list列表(小月份)
     */
    private List<String> mListSmall;

    /**
     * 时间选取完毕监听
     */
    private OnDateSelectFinished onDateSelectFinished;

    private LinearLayout llBeginLayout;

    private LinearLayout llEndLayout;

    private TextView tvBeginValue;

    private TextView tvEndValue;

    private TextView tvTimeSelectCancel;

    private TextView tvTimeSelectFix;

    private WheelView mYearView;

    private WheelView mMonthView;

    private WheelView mDayView;

    private WheelView mHourView;

    private WheelView mMinuteView;

    private Context mContext;


    public DoubleTimeSelectDialog(@NonNull Context context) {
       this(context,DEFAULT_SMALLEST_TIME,DEFAULT_BIGGEST_TIME);
    }

    public DoubleTimeSelectDialog(@NonNull Context context,String smallestTime,String biggestTime) {
        this(context,smallestTime,biggestTime,TimeUtil.getCurTime());
    }

    public DoubleTimeSelectDialog(@NonNull Context context,String smallestTime,String biggestTime,String defaultSelectedTime) {
        super(context,R.style.PopBottomDialogStyle);
        this.mContext = context;
        this.mSmallestTime = smallestTime;
        this.mBiggestTime = biggestTime;
        this.defaultSelectedTime = defaultSelectedTime;

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initLayout();
        setData(defaultSelectedTime);
        initView();
        //初始化日期选择器
        initDatePicker();
        //设置显示的开始和结束时间值
        setShowValue(curYear,curMonth,curDay,curHour,curMinute,true);
    }

    /**
     * 初始化布局
     */
    private void initLayout() {
        setContentView(R.layout.view_double_time_select);
        //点击屏幕外部弹窗消失
        setCanceledOnTouchOutside(true);
        //设置弹窗的停靠方式
        Window mDialogWindow = getWindow();
        mDialogWindow.setGravity(Gravity.BOTTOM);
        WindowManager.LayoutParams lp =mDialogWindow.getAttributes();
        //到特定边的距离,(Gravity.BOTTOM)
        lp.y=0;
        lp.width =ViewGroup.LayoutParams.MATCH_PARENT;
        mDialogWindow.setAttributes(lp);
    }

    /**
     * 初始化控件
     */
    private void initView() {
        //控件初始化
        llBeginLayout = findViewById(R.id.llBeginLayout);
        llEndLayout = findViewById(R.id.llEndLayout);
        tvBeginValue = findViewById(R.id.tvBeginValue);
        tvEndValue = findViewById(R.id.tvEndValue);
        tvTimeSelectCancel = findViewById(R.id.tvTimeSelectCancel);
        tvTimeSelectFix = findViewById(R.id.tvTimeSelectFix);
        //数字滚轮
        mYearView = findViewById(R.id.year);
        mMonthView = findViewById(R.id.month);
        mDayView = findViewById(R.id.day);
        mHourView = findViewById(R.id.hour);
        mMinuteView = findViewById(R.id.minute);

        switch (mType){
            case 0:
                break;
            case 1:

                mHourView.setVisibility(View.GONE);
                mMinuteView.setVisibility(View.GONE);
                break;
            case 2:

                mDayView.setVisibility(View.GONE);
                mHourView.setVisibility(View.GONE);
                mMinuteView.setVisibility(View.GONE);
                break;
            case 3:

                mYearView.setVisibility(View.GONE);
                mHourView.setVisibility(View.GONE);
                mMinuteView.setVisibility(View.GONE);
                break;
            case 4:

                mYearView.setVisibility(View.GONE);
                mMonthView.setVisibility(View.GONE);
                mDayView.setVisibility(View.GONE);
                break;
            default:
                break;
        }

        findViewById(R.id.tv_yearUnit).setVisibility(mYearView.getVisibility());
        findViewById(R.id.v_yearAndMonth).setVisibility( mType==3||mType==4? View.GONE : View.VISIBLE);
        findViewById(R.id.tv_monthUnit).setVisibility(mMonthView.getVisibility());
        findViewById(R.id.v_monthAndDay).setVisibility( mType==2||mType==4? View.GONE : View.VISIBLE);
        findViewById(R.id.tv_dayUnit).setVisibility(mDayView.getVisibility());
        findViewById(R.id.v_dayAndHour).setVisibility( mType!=0? View.GONE : View.VISIBLE);
        findViewById(R.id.tv_hourUnit).setVisibility(mHourView.getVisibility());
        findViewById(R.id.v_hourAndMinute).setVisibility( mType!=0&&mType!=4? View.GONE : View.VISIBLE);
        findViewById(R.id.tv_minuteUnit).setVisibility(mMinuteView.getVisibility());

        mYearView.addChangingListener(yearChangedListener);
        mMonthView.addChangingListener(monthChangedListener);
        mDayView.addChangingListener(dayChangeListener);
        mHourView.addChangingListener(hourChangedListener);
        mMinuteView.addChangingListener(minuteChangedListener);

        //注册监听器
        llBeginLayout.setSelected(true);
        llBeginLayout.setOnClickListener(this);
        llEndLayout.setOnClickListener(this);
        tvTimeSelectCancel.setOnClickListener(this);
        tvTimeSelectFix.setOnClickListener(this);
    }

    /**
     * 数据初始化
     * @param selectedTime
     */
    private void setData(String selectedTime) {
        Calendar calendar = Calendar.getInstance();
        //如果未设置默认选择的时间,则将当前系统时间作为选中的值
        if (!TextUtils.isEmpty(selectedTime)){

            useTimeWithYearAndMinute(selectedTime);
            if (!isSetType){
                mType=curType;
            }
        }else {
            curYear = calendar.get(Calendar.YEAR);
            //月份从0开始
            curMonth = calendar.get(Calendar.MONTH)+1;
            curDay = calendar.get(Calendar.DATE);
            //采用24小时制
            curHour = calendar.get(Calendar.HOUR_OF_DAY);
            curMinute = calendar.get(Calendar.MINUTE);
            calendar.clear();
        }
    }

    /**
     * 分解时间字符串,获取年,月,日,时,分
     * @param defaultSelectedTime
     */
    private void useTimeWithYearAndMinute(String defaultSelectedTime) {
        String[] ymd = defaultSelectedTime.split("-");
        if (ymd.length>2){

            curType=TYPE.YEAR_MONTH_DATE.ordinal();
            curYear = Integer.parseInt(ymd[0]);
            curMonth = Integer.parseInt(ymd[1]);
            String[] dhm = ymd[2].split(" ");
            curDay= Integer.parseInt(dhm[0]);
            if (dhm.length>1){

                curType=TYPE.YEAR_MONTH_DATE_HOUR_MINUTE.ordinal();
                String[] hm =dhm[1].split(":");
                if (hm.length>1){
                    curHour = Integer.parseInt(hm[0]);
                    curMinute = Integer.parseInt(hm[1]);
                }
            }
        }else if (ymd.length>1){
            if (ymd[0].length()>2){

                curType=TYPE.YEAR_MONTH.ordinal();
                curYear=Integer.parseInt(ymd[0]);
                curMonth = Integer.parseInt(ymd[1]);
            }else {

                curType=TYPE.MONTH_DATE.ordinal();
                curMonth = Integer.parseInt(ymd[0]);
                curDay = Integer.parseInt(ymd[1]);
            }
        }else {
            String[] hm =defaultSelectedTime.split(":");
            if (hm.length>1){
                curType = TYPE.HOUR_MINUTE.ordinal();
                curHour = Integer.parseInt(hm[0]);
                curMinute = Integer.parseInt(hm[1]);
            }
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.llBeginLayout:
                //开始时间Item
                llBeginLayout.setSelected(true);
                llEndLayout.setSelected(false);
                mTimeType = TIME_TYPE.TYPE_START;
                setData(mSelectStartTime);
                break;
            case R.id.llEndLayout:
                //结束时间Item
                llBeginLayout.setSelected(false);
                llEndLayout.setSelected(true);
                mTimeType = TIME_TYPE.TYPE_END;
                setData(mSelectEndTime);
                break;
            case R.id.tvTimeSelectCancel:
                //取消按钮
                this.dismiss();
                break;
            case R.id.tvTimeSelectFix:
                //确定按钮,将设置好的时间段数据返回
                toReturnValue();

                break;
            default:
                break;
        }
    }

    /**
     * 返回选择的开始时间和结束时间
     */
    private void toReturnValue() {

        String formatStr;
        if (mType==0){
            formatStr = "yyyy-MM-dd HH:mm:ss";
        }else if (mType==1){
            formatStr = "yyyy-MM-dd";
        }else if (mType==2){
            formatStr = "yyyy-MM";
        }else if (mType==3){
            formatStr = "MM-dd";
        }else{
            formatStr = "HH:mm";
        }

        if (TimeUtil.getTimeValue(mSelectStartTime,formatStr).getTime() >
                TimeUtil.getTimeValue(mSelectEndTime,formatStr).getTime()) {
            Toast.makeText(mContext, R.string.timeSelectedAlarm, Toast.LENGTH_SHORT).show();
        } else {
            if (onDateSelectFinished != null) {
                onDateSelectFinished.onSelectFinished(mSelectStartTime, mSelectEndTime);
            }
            this.dismiss();
        }
    }

    /**
     * 设置显示类型
     * @param type
     */
    public void setType(TYPE type){

        this.mType = type.ordinal();
        this.isSetType = true;

        onCreate(new Bundle());
    }

    /**
     * 弹出日期时间选择器
     */
    private void initDatePicker() {
        Calendar calendar = Calendar.getInstance();
        year = calendar.get(Calendar.YEAR);
        month = calendar.get(Calendar.MONTH);
        day = calendar.get(Calendar.DATE);
        hour = calendar.get(Calendar.HOUR_OF_DAY);
        minute = calendar.get(Calendar.MINUTE);

        String[] ymdStart = mSmallestTime.split("-");

        if (ymdStart.length > 2) {
            START_YEAR = Integer.parseInt(ymdStart[0]);
            START_MONTH = Integer.parseInt(ymdStart[1]);
            String[] hdmStart = ymdStart[2].split(" ");
            START_DAY = Integer.parseInt(hdmStart[0]);
            if (hdmStart.length>1){
                String[] hmStart = hdmStart[1].split(":");
                if (hmStart.length>1){
                    START_HOUR = Integer.parseInt(hmStart[0]);
                    START_MINUTE = Integer.parseInt(hmStart[1]);
                }
            }
        }
        if (TextUtils.isEmpty(mBiggestTime)){
            mBiggestTime = TimeUtil.getCurTime();
        }
        String[] ymdEnd = mBiggestTime.split("-");
        if (ymdEnd.length > 2) {
            END_YEAR = Integer.parseInt(ymdEnd[0]);
            END_MONTH = Integer.parseInt(ymdEnd[1]);
            String[] hdmEnd = ymdEnd[2].split(" ");
            END_DAY = Integer.parseInt(hdmEnd[0]);
            if (hdmEnd.length>1){
                String[] hmEnd = hdmEnd[1].split(":");
                if (hmEnd.length>1){
                    END_HOUR = Integer.parseInt(hmEnd[0]);
                    END_MINUTE = Integer.parseInt(hmEnd[1]);
                }
            }
        }
        // 添加大小月月份并将其转换为list,方便之后的判断
        String[] monthsBig = {"1", "3", "5", "7", "8", "10", "12"};
        String[] monthsSmall = {"4", "6", "9", "11"};

        mListBig = Arrays.asList(monthsBig);
        mListSmall = Arrays.asList(monthsSmall);

        // 年
        // 设置"年"的显示数据
        mYearView.setAdapter(new NumericWheelAdapter(START_YEAR, END_YEAR));
        // 添加文字
        mYearView.setLabel("");
        int yearPos = isOnlyThisYear ? END_YEAR - START_YEAR : curYear != 0 ? curYear - START_YEAR : END_YEAR - START_YEAR;
        // 初始化时显示的数据 START_YEAR - END_YEAR
        mYearView.setCurrentItem(yearPos);
        // 循环滚动
        mYearView.setCyclic(false);

        // 月
        int startMonth = 1;
        if (isOnlyThisMonth) {
            startMonth = curMonth + 1;
        }

        //初始月份最大值应该是当年最大月
        int minMonth = (START_YEAR == curYear ? START_MONTH : 1);

        mMonthView.setAdapter(new NumericWheelAdapter(minMonth, END_YEAR == curYear ? END_MONTH : 12));
        mMonthView.setLabel("");
        mMonthView.setCurrentItem(isOnlyThisMonth ? 0 : curMonth != 0 ? curMonth - minMonth : month - minMonth);
        mMonthView.setCyclic(false);

        // 日
        //判断是否属于当前月份，如果不是，需要判断大小月，进行初始化
        int mEndDay = 31;
        // 判断大小月及是否闰年,用来确定"日"的数据
        if (mListBig.contains(String.valueOf(curMonth))) {
            mDayView.setAdapter(new NumericWheelAdapter(1, 31));
            mEndDay = 31;
        } else if (mListSmall.contains(String.valueOf(curMonth))) {
            mDayView.setAdapter(new NumericWheelAdapter(1, 30));
            mEndDay = 30;
        } else {
            if (((mYearView.getCurrentItem() + START_YEAR) % 4 == 0 && (mYearView.getCurrentItem() + START_YEAR) % 100 != 0)
                    || (mYearView.getCurrentItem() + START_YEAR) % 400 == 0) {
                mDayView.setAdapter(new NumericWheelAdapter(1, 29));
                mEndDay = 29;
            } else {
                mDayView.setAdapter(new NumericWheelAdapter(1, 28));
                mEndDay = 28;
            }

        }
        int minDay = 1;
        if ((curMonth == END_MONTH && curYear == END_YEAR) && (curMonth == START_MONTH && curYear == START_YEAR)) {
            mDayView.setAdapter(new NumericWheelAdapter(START_DAY, END_DAY));
            minDay = START_DAY;
        } else if ((curMonth == END_MONTH && curYear == END_YEAR)) {
            mDayView.setAdapter(new NumericWheelAdapter(1, END_DAY));
        } else if ((curMonth == START_MONTH && curYear == START_YEAR)) {
            mDayView.setAdapter(new NumericWheelAdapter(START_DAY, mEndDay));
            minDay = START_DAY;
        } else {
            mDayView.setAdapter(new NumericWheelAdapter(1, mEndDay));
        }

        mDayView.setLabel("");
        mDayView.setCurrentItem(curDay == 0 ? day - minDay : curDay - minDay);
        mDayView.setCyclic(true);

        // 时
        mHourView.setAdapter(new NumericWheelAdapter(START_HOUR, END_HOUR));
        mHourView.setLabel("");
        mHourView.setCurrentItem(hour);
        mHourView.setCyclic(true);

        // 分
        mMinuteView.setAdapter(new NumericWheelAdapter(START_MINUTE, END_MINUTE));
        mMinuteView.setLabel("");
        mMinuteView.setCurrentItem(minute);
        mMinuteView.setCyclic(true);

        // 选择器字体的大小
        int textSize = mContext.getResources().getDimensionPixelSize(R.dimen.ymd_text_size);
        mDayView.TEXT_SIZE = textSize;
        mMonthView.TEXT_SIZE = textSize;
        mYearView.TEXT_SIZE = textSize;
        mHourView.TEXT_SIZE = textSize;
        mMinuteView.TEXT_SIZE = textSize;
    }

    /**
     * 年滚轮数据变化监听
     */
    private OnWheelChangedListener yearChangedListener = new OnWheelChangedListener() {
        @Override
        public void onChanged(WheelView wheel, int oldValue, int newValue) {

            int year_num = newValue + START_YEAR;
            int month_start_num = 1;
            if (year_num < END_YEAR && year_num > START_YEAR) {
                mMonthView.setAdapter(new NumericWheelAdapter(1, 12));
                month_start_num = 1;
            } else if (year_num == START_YEAR) {
                mMonthView.setAdapter(new NumericWheelAdapter(START_MONTH, 12));
                month_start_num = START_MONTH;
            } else if (year_num == END_YEAR) {
                mMonthView.setAdapter(new NumericWheelAdapter(1, END_MONTH));
                month_start_num = 1;
            }
            mMonthView.setCurrentItem(0);

            // 判断大小月及是否闰年,用来确定"日"的数据
            int mEndDay = 31;
            if (mListBig.contains(String.valueOf(mMonthView.getCurrentItem() + month_start_num))) {
                mDayView.setAdapter(new NumericWheelAdapter(1, 31));
                mEndDay = 31;
            } else if (mListSmall.contains(String.valueOf(mMonthView.getCurrentItem() + month_start_num))) {
                mDayView.setAdapter(new NumericWheelAdapter(1, 30));
                mEndDay = 30;
            } else {
                if ((year_num % 4 == 0 && year_num % 100 != 0) || year_num % 400 == 0) {
                    mDayView.setAdapter(new NumericWheelAdapter(1, 29));
                    mEndDay = 29;
                } else {
                    mDayView.setAdapter(new NumericWheelAdapter(1, 28));
                    mEndDay = 28;
                }
            }
            Log.d("darren", "year_num:" + year_num);
            Log.d("darren", "START_YEAR:" + START_YEAR);
            int temp = mMonthView.getCurrentItem() + 1;
            Log.d("darren", "mMonthView.getCurrentItem() + 1:" + temp);
            Log.d("darren", "START_MONTH:" + START_MONTH);
            if (year_num == START_YEAR && mMonthView.getCurrentItem() + month_start_num == START_MONTH) {
                mDayView.setAdapter(new NumericWheelAdapter(START_DAY, mEndDay));
            } else if (year_num == END_YEAR && mMonthView.getCurrentItem() + month_start_num == END_MONTH) {
                mDayView.setAdapter(new NumericWheelAdapter(1, END_DAY));
            }
            onScroll();
            mMonthView.setCurrentItem(mMonthView.getCurrentItem());
            mDayView.setCurrentItem(mDayView.getCurrentItem());

        }
    };

    /**
     * 月滚轮数据变化监听
     */
    private OnWheelChangedListener monthChangedListener = new OnWheelChangedListener() {
        @Override
        public void onChanged(WheelView wheel, int oldValue, int newValue) {
            int addMonth = 1;
            if (mYearView.getCurrentItem() == 0) {
                addMonth = START_MONTH;
            }
            int month_num = newValue + addMonth;
            // 判断大小月及是否闰年,用来确定"日"的数据
            int mEndDay = 31;
            if (mListBig.contains(String.valueOf(month_num))) {
                mDayView.setAdapter(new NumericWheelAdapter(1, 31));
                mEndDay = 31;
            } else if (mListSmall.contains(String.valueOf(month_num))) {
                mDayView.setAdapter(new NumericWheelAdapter(1, 30));
                mEndDay = 30;
            } else {
                if (((mYearView.getCurrentItem() + START_YEAR) % 4 == 0 && (mYearView.getCurrentItem() + START_YEAR) % 100 != 0)
                        || (mYearView.getCurrentItem() + START_YEAR) % 400 == 0) {
                    mDayView.setAdapter(new NumericWheelAdapter(1, 29));
                    mEndDay = 29;
                } else {
                    mDayView.setAdapter(new NumericWheelAdapter(1, 28));
                    mEndDay = 28;
                }

            }
            if (month_num == START_MONTH && (mYearView.getCurrentItem() + START_YEAR) == START_YEAR) {
                mDayView.setAdapter(new NumericWheelAdapter(START_DAY, mEndDay));
            } else if (month_num == END_MONTH && (mYearView.getCurrentItem() + START_YEAR) == END_YEAR) {
                mDayView.setAdapter(new NumericWheelAdapter(1, END_DAY));
            }
            onScroll();
            mDayView.setCurrentItem(mDayView.getCurrentItem());
        }
    };

    /**
     * 天滚轮数据变化监听
     */
    private OnWheelChangedListener dayChangeListener = new OnWheelChangedListener() {
        @Override
        public void onChanged(WheelView wheel, int oldValue, int newValue) {
            mDayView.setCurrentItem(newValue);
            onScroll();
        }
    };

    /**
     * 时滚轮数据变化监听
     */
    private OnWheelChangedListener hourChangedListener = new OnWheelChangedListener() {
        @Override
        public void onChanged(WheelView wheel, int oldValue, int newValue) {
            mHourView.setCurrentItem(newValue);
            onScroll();
        }
    };

    /**
     * 分滚轮数据变化监听
     */
    private OnWheelChangedListener minuteChangedListener = new OnWheelChangedListener() {
        @Override
        public void onChanged(WheelView wheel, int oldValue, int newValue) {
            mMinuteView.setCurrentItem(newValue);
            onScroll();
        }
    };
    /**
     * 获取动态时间数据
     */
    private void onScroll() {

        int year = isOnlyThisYear ? Integer.parseInt(mYearView.getAdapter().getItem(0))
                : mYearView.getCurrentItem() + START_YEAR;
        int addMonth = 1;
        if (mYearView.getCurrentItem() == 0) {
            addMonth = START_MONTH;
        }
        int month = isOnlyThisMonth ? Integer.parseInt(mMonthView.getAdapter().getItem(0))
                : mMonthView.getCurrentItem() + addMonth;
        int addDay = 1;
        if ((mYearView.getCurrentItem() == 0 && mMonthView.getCurrentItem() == 0)) {
            addDay = START_DAY;
        }
        int day = mDayView.getCurrentItem() + addDay;

        int hour = mHourView.getCurrentItem();
        int minute = mMinuteView.getCurrentItem();

        setShowValue(year,month,day,hour,minute,false);

    }

    /**
     * 设置并显示开始和结束时间
     * @param year
     * @param month
     * @param day
     * @param hour
     * @param minute
     * @param isInit 当前是否为初始化
     */
    private void setShowValue(int year, int month, int day, int hour, int minute,boolean isInit) {

        String monthS = String.format("%02d", month);
        String dayS = String.format("%02d", day);
        String yearS = String.format("%02d", year);
        String hourS = String.format("%02d",hour);
        String minuteS = String.format("%02d",minute);

        switch (mType){
            case 0:
                String timeStr0 = yearS + "-" + monthS + "-" + dayS+ " " + hourS + ":" + minuteS;
                toSetValue(timeStr0,isInit);
                break;
            case 1:
                String timeStr1 = yearS + "-" + monthS + "-" + dayS;
                toSetValue(timeStr1,isInit);
                break;
            case 2:
                String timeStr2 = yearS + "-" + monthS;
                toSetValue(timeStr2,isInit);
                break;
            case 3:
                String timeStr3 = monthS + "-" + dayS;
                toSetValue(timeStr3,isInit);
                break;
            case 4:
                String timeStr4 =hourS + ":" + minuteS;
                toSetValue(timeStr4,isInit);
                break;
            default:
                break;
        }
    }

    /**
     * 设置时间
     * @param timeStr
     * @param isInit
     */
    private void toSetValue(String timeStr,boolean isInit) {
        if (isInit){
            mSelectStartTime =timeStr;
            tvBeginValue.setText(timeStr);
            mSelectEndTime = timeStr;
            tvEndValue.setText(timeStr);
            return;
        }

        if (mTimeType == TIME_TYPE.TYPE_START) {
            mSelectStartTime =timeStr;
            tvBeginValue.setText(timeStr);
        } else {
            mSelectEndTime = timeStr;
            tvEndValue.setText(timeStr);
        }
    }


    /**
     * 格式化显示的数据，必须返回SpannableString对象
     *
     * @param priFix  前缀
     * @param content 内容
     * @return 返回格式化的数据
     */
    private SpannableString makeFormatContent(String priFix, String content) {
        SpannableString spannableString = new SpannableString(priFix + content);
        spannableString.setSpan(new ForegroundColorSpan(mContext.getResources().getColor(R.color.black33)),
                priFix.length(), spannableString.length(), Spanned.SPAN_EXCLUSIVE_INCLUSIVE);
        spannableString.setSpan(new RelativeSizeSpan(1.33f),
                priFix.length(), spannableString.length(), Spanned.SPAN_EXCLUSIVE_INCLUSIVE);
        return spannableString;
    }

    /**
     * set监听
     *
     * @param onDateSelectFinished 完成监听
     */
    public void setOnDateSelectFinished(OnDateSelectFinished onDateSelectFinished) {
        this.onDateSelectFinished = onDateSelectFinished;
    }

    /**
     * 监听接口
     */
    public interface OnDateSelectFinished {
        /**
         * 监听方法
         *
         * @param startTime 开始时间
         * @param endTime   结束时间
         */
        void onSelectFinished(String startTime, String endTime);
    }

}
