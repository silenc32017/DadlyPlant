package liuliu.dadlyplant.method;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import liuliu.dadlyplant.R;

import static android.view.View.MeasureSpec.EXACTLY;
import static android.view.View.MeasureSpec.makeMeasureSpec;

/**
 * 自定义底部布局
 * Created by Administrator on 2016/10/7.
 */

public class BottomTabView extends ViewGroup {
    private final String TAG = "MyGridLayout";

    int margin = 0;// 每个格子的水平和垂直间隔
    int colums = 3;
    private int mMaxChildWidth = 0;
    private int mMaxChildHeight = 0;
    int count = 0;
    int now_click = 0;//当前点击item
    GridAdatper adapter;
    private List<BottomItemModel> val = new ArrayList<>();

    public BottomTabView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        mContext = context;
//        colums = val.size();
//        count = val.size();
    }

    public BottomTabView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public BottomTabView(Context context) {
        this(context, null);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        mMaxChildWidth = 0;
        mMaxChildHeight = 0;

        int modeW = MeasureSpec.UNSPECIFIED, modeH = MeasureSpec.UNSPECIFIED;
        if (MeasureSpec.getMode(widthMeasureSpec) != MeasureSpec.UNSPECIFIED)
            modeW = MeasureSpec.UNSPECIFIED;
        if (MeasureSpec.getMode(heightMeasureSpec) != MeasureSpec.UNSPECIFIED)
            modeH = MeasureSpec.UNSPECIFIED;

        final int childWidthMeasureSpec = MeasureSpec.makeMeasureSpec(
                MeasureSpec.getSize(widthMeasureSpec), modeW);
        final int childHeightMeasureSpec = MeasureSpec.makeMeasureSpec(
                MeasureSpec.getSize(heightMeasureSpec), modeH);

        count = getChildCount();
        if (count == 0) {
            super.onMeasure(childWidthMeasureSpec, childHeightMeasureSpec);
            return;
        }
        for (int i = 0; i < count; i++) {
            final View child = getChildAt(i);
            if (child.getVisibility() == GONE) {
                continue;
            }

            child.measure(childWidthMeasureSpec, childHeightMeasureSpec);

            mMaxChildWidth = Math.max(mMaxChildWidth, child.getMeasuredWidth());
            mMaxChildHeight = Math.max(mMaxChildHeight,
                    child.getMeasuredHeight());
        }
        setMeasuredDimension(resolveSize(mMaxChildWidth, widthMeasureSpec),
                resolveSize(mMaxChildHeight, heightMeasureSpec));
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        // TODO Auto-generated method stub
        int height = b - t;// 布局区域高度
        int width = r - l;// 布局区域宽度
        int rows = count % colums == 0 ? count / colums : count / colums + 1;// 行数
        if (count == 0)
            return;
        int gridW = (width - margin * (colums - 1)) / colums;// 格子宽度
        int gridH = (height - margin * rows) / rows;// 格子高度

        int left = 0;
        int top = margin;

        for (int i = 0; i < rows; i++) {// 遍历行
            for (int j = 0; j < colums; j++) {// 遍历每一行的元素
                View child = this.getChildAt(i * colums + j);
                if (child == null)
                    return;
                left = j * gridW + j * margin;
                // 如果当前布局宽度和测量宽度不一样，就直接用当前布局的宽度重新测量
                if (gridW != child.getMeasuredWidth()
                        || gridH != child.getMeasuredHeight()) {
                    child.measure(makeMeasureSpec(gridW, EXACTLY),
                            makeMeasureSpec(gridH, EXACTLY));
                }
                child.layout(left, top, left + gridW, top + gridH);
            }
            top += gridH + margin;
        }
    }

    public interface GridAdatper {
        View getView(int index);

        int getCount();
    }

    Context mContext;

    /**
     * 设置适配器
     */
    public void setGridAdapter(List<BottomItemModel> list, GridAdatper adapter) {
        this.adapter = adapter;
        this.val = list;

        // 动态添加视图
        int size = adapter.getCount();
        for (int i = 0; i < list.size(); i++) {
            View view = LayoutInflater.from(mContext).inflate(R.layout.view_bottom_tab,
                    null);
            BottomItemModel item = list.get(i);
            LinearLayout main_ll = (LinearLayout) view.findViewById(R.id.main_ll);
            ImageView iv = (ImageView) view.findViewById(R.id.iv);
            TextView tv = (TextView) view.findViewById(R.id.tv);
            if (i == 0) {
                iv.setImageResource(item.getPressed_img());
            } else {
                iv.setImageResource(item.getNormal_img());
            }
            tv.setText(item.getVal());
            int finalI = i;
            main_ll.setOnClickListener(v -> {
                Toast.makeText(mContext, "123", Toast.LENGTH_SHORT).show();
            });
            addView(view);
        }
    }

    public interface OnItemClickListener {
        void onItemClick(View v, int index);
    }

    public void setOnItemClickListener(final OnItemClickListener click) {
        if (this.adapter == null)
            return;
        for (int i = 0; i < adapter.getCount(); i++) {
            final int index = i;
            View view = getChildAt(i);
            view.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View v) {
                    // TODO Auto-generated method stub
                    click.onItemClick(v, index);
                }
            });
        }
    }

}

