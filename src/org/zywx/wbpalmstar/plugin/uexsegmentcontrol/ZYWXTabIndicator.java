package org.zywx.wbpalmstar.plugin.uexsegmentcontrol;

import static android.view.ViewGroup.LayoutParams.MATCH_PARENT;
import static android.view.ViewGroup.LayoutParams.WRAP_CONTENT;

import java.util.List;

import org.zywx.wbpalmstar.engine.universalex.EUExUtil;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.widget.HorizontalScrollView;
import android.widget.LinearLayout;
import android.widget.TextView;

public class ZYWXTabIndicator extends HorizontalScrollView {
	private static final CharSequence EMPTY_TITLE = "";
    private Runnable mTabSelector;
    private LinearLayout mTabLayout;
    private int mSelectedTabIndex;
    private OnTabViewClickListener onTabViewClickListener;

    private final OnClickListener mTabClickListener = new OnClickListener() {
        public void onClick(View view) {
        	mSelectedTabIndex = view.getId();
        	setCurrentItem(mSelectedTabIndex);
            onTabViewClickListener.onTabViewClick(mSelectedTabIndex, (TextView)((LinearLayout)view).getChildAt(0));
        }
    };

    public ZYWXTabIndicator(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
	}

	public ZYWXTabIndicator(Context context, AttributeSet attrs) {
		super(context, attrs);
		setHorizontalScrollBarEnabled(false);
		mTabLayout = new LinearLayout(context);
		addView(mTabLayout, new ViewGroup.LayoutParams(WRAP_CONTENT, MATCH_PARENT));
	}

	public ZYWXTabIndicator(Context context) {
		super(context);
	}
	
    public void setOnTabViewClickListener(
			OnTabViewClickListener onTabViewClickListener) {
		this.onTabViewClickListener = onTabViewClickListener;
	}

    public int getmSelectedTabIndex() {
		return mSelectedTabIndex;
	}

	private void animateToTab(final int position) {
        final View tabView = mTabLayout.getChildAt(position);
        if (mTabSelector != null) {
            removeCallbacks(mTabSelector);
        }
        mTabSelector = new Runnable() {
            public void run() {
                final int scrollPos = tabView.getLeft() - (getWidth() - tabView.getWidth()) / 2;
                smoothScrollTo(scrollPos, 0);
                mTabSelector = null;
            }
        };
        post(mTabSelector);
    }

    @Override
    public void onAttachedToWindow() {
        super.onAttachedToWindow();
        if (mTabSelector != null) {
            post(mTabSelector);
        }
    }

    @Override
    public void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        if (mTabSelector != null) {
            removeCallbacks(mTabSelector);
        }
    }

    private void addTab(int index, CharSequence text) {
    	LinearLayout ll = (LinearLayout) View.inflate(getContext(), EUExUtil.getResLayoutID("plugin_indicator_item"), null);
    	TextView tabView = (TextView) ll.findViewById(EUExUtil.getResIdID("plugin_hlv_tv"));
        tabView.setText(text);
        ll.setId(index);
        ll.setFocusable(true);
        ll.setOnClickListener(mTabClickListener);
        mTabLayout.addView(ll, new LinearLayout.LayoutParams(0, MATCH_PARENT, 1));
    }
    
    public void setIndicatorData(List<String> tabs) {
        mTabLayout.removeAllViews();
        final int count = tabs.size();
        for (int i = 0; i < count; i++) {
        	CharSequence title = tabs.get(i);
            if (title == null) {
                title = EMPTY_TITLE;
            }
            addTab(i, title);
        }
        if (mSelectedTabIndex > count) {
            mSelectedTabIndex = count - 1;
        }
        setCurrentItem(mSelectedTabIndex);
        requestLayout();
    }

    public void setCurrentItem(int item) {
        mSelectedTabIndex = item;

        final int tabCount = mTabLayout.getChildCount();
        for (int i = 0; i < tabCount; i++) {
            final View child = mTabLayout.getChildAt(i);
            final boolean isSelected = (i == item);
            child.setSelected(isSelected);
            if (isSelected) {
                animateToTab(item);
            }
        }
    }
    
    public void clean() {
    	mTabLayout.removeAllViews();
    }

    public interface OnTabViewClickListener {
		void onTabViewClick(int position, TextView view);
	}
}
