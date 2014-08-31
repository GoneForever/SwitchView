package hd.switchview;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.LinearLayout;
import android.widget.Scroller;

/**
 * 选项
 * <p/>
 * Created by apple on 14-8-10.
 */
public class SwitchView extends ViewGroup implements View.OnTouchListener {


    private LinearLayout scrollView;//滑块
    private int scrollWidth = 0;
    private Scroller scroller;
    private boolean checked = false;
    private View switchView;


    private Animation gone = AnimationUtils.loadAnimation(getContext(), R.anim.alpha_gone);
    private Animation show = AnimationUtils.loadAnimation(getContext(), R.anim.alpha_show);

    private OnCheckedChangedListener onCheckedChangedListener;//监听选择变化
    private View bgView;

    public SwitchView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
        TypedArray array = context.obtainStyledAttributes(attrs, R.styleable.SwitchView);
        switchView.setBackgroundResource(array.getResourceId(R.styleable.SwitchView_bg, R.drawable.switch_btn_bg));
        int checkedBg = array.getResourceId(R.styleable.SwitchView_checked_bg, R.drawable.switch_bg_checked_bg);
        int normalBg = array.getResourceId(R.styleable.SwitchView_normal_bg, R.drawable.switch_bg_normal);
        setBackgroundResource(normalBg);

        Animation anim = new AlphaAnimation(0, 0);
        anim.setDuration(0);
        anim.setFillAfter(true);
        bgView.startAnimation(anim);
        bgView.setBackgroundResource(checkedBg);
    }

    /**
     * 初始化
     */
    private void init() {
        addView();
        scroller = new Scroller(getContext());
        scrollView.setOnTouchListener(this);
        show.setDuration(250);
        show.setFillAfter(true);
        gone.setDuration(250);
        gone.setFillAfter(true);
    }

    private int lastX = 0;
    private int marginLeft = 0;//按压点距离左边的距离

    private long lastTime = 0;

    @Override
    public boolean onTouch(View view, MotionEvent motionEvent) {
        int x = (int) motionEvent.getX();
        switch (motionEvent.getAction()) {
            case MotionEvent.ACTION_DOWN:
                if (lastX == 0)
                    lastX = x;
                if (marginLeft == 0)
                    marginLeft = x + scrollView.getScrollX();
                lastTime = System.currentTimeMillis();
                break;
            case MotionEvent.ACTION_MOVE:
                int moveX = marginLeft - x;
                if (moveX > 0)
                    scrollView.scrollTo(0, 0);
                else if (moveX < -scrollWidth)
                    scrollView.scrollTo(-scrollWidth, 0);
                else
                    scrollView.scrollTo(moveX, 0);
                lastX = x;
                break;
            case MotionEvent.ACTION_UP:
                marginLeft = 0;
                if (System.currentTimeMillis() - lastTime <= 100) {
                    if (checked)
                        close();
                    else
                        open();
                    return true;
                }
                int center = scrollWidth / 2 - scrollView.getScrollX();//滑块中间的X坐标
                if (center > scrollWidth) {//超过中线
                    open();
                } else {
                    close();
                }
                break;
        }
        return true;
    }

    /**
     * 关闭动画
     */
    private void close() {
        int startX = scrollView.getScrollX();
        int dx = -startX;
        scroller.startScroll(startX, 0, dx, 0, 250);
        if (checked)
            bgView.startAnimation(gone);
        invalidate();
        checked = false;
        onCheckedChanged();
    }

    /**
     * 开启动画
     */
    private void open() {
        int startX = scrollView.getScrollX();
        int dx = scrollWidth + startX;
        scroller.startScroll(startX, 0, -dx, 0, 250);
        if (!checked)
            bgView.startAnimation(show);
        invalidate();
        checked = true;
        onCheckedChanged();
    }

    /**
     * 打开或者关闭时候调用
     */
    private void onCheckedChanged() {
        if (onCheckedChangedListener != null)
            onCheckedChangedListener.onCheckedChanged(checked);
    }

    @Override
    public void computeScroll() {
        if (scroller.computeScrollOffset()) {
            scrollView.scrollTo(scroller.getCurrX(), scroller.getCurrY());
            postInvalidate();
        }
    }

    /**
     * 是否打开
     *
     * @return bool
     */
    public boolean isChecked() {
        return checked;
    }

    /**
     * 添加滑块
     */
    private void addView() {
        LayoutParams params = new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
        addView(bgView = new View(getContext()));
        scrollView = new LinearLayout(getContext());
        scrollView.addView(switchView = new View(getContext()), new LinearLayout.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT, 1));
        scrollView.addView(new View(getContext()), new LinearLayout.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT, 1));
        addView(scrollView, params);
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        View child = getChildAt(0);
        int height = child.getMeasuredHeight();
        int width = child.getMeasuredWidth();
        child.layout(0, 0, width, height);
        getChildAt(1).layout(0, 0, width, height);
        scrollWidth = width / 2;
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        int width = MeasureSpec.getSize(widthMeasureSpec);
        int height = MeasureSpec.getSize(heightMeasureSpec);
        setMeasuredDimension(width, height);
        getChildAt(0).measure(widthMeasureSpec, heightMeasureSpec);
        getChildAt(1).measure(widthMeasureSpec, heightMeasureSpec);
    }

    /**
     * 设置监听
     *
     * @param listener listener
     */
    public void setOnCheckedChangedListener(OnCheckedChangedListener listener) {
        onCheckedChangedListener = listener;
    }

    public interface OnCheckedChangedListener {
        public void onCheckedChanged(boolean isChecked);
    }

}
