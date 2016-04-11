/*
 *  Copyright (C) 2014 The AppCan Open Source Project.
 *
 *  This program is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU Lesser General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 *  (at your option) any later version.

 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU Lesser General Public License for more details.

 *  You should have received a copy of the GNU Lesser General Public License
 *  along with this program.  If not, see <http://www.gnu.org/licenses/>.
 *
 */

package org.zywx.wbpalmstar.plugin.uexsegmentcontrol;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.widget.HorizontalScrollView;
import android.widget.LinearLayout;
import android.widget.TextView;

import org.zywx.wbpalmstar.engine.universalex.EUExUtil;

import java.util.List;

import static android.view.ViewGroup.LayoutParams.MATCH_PARENT;
import static android.view.ViewGroup.LayoutParams.WRAP_CONTENT;

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
        	if(onTabViewClickListener != null){
        		onTabViewClickListener.onTabViewClick(mSelectedTabIndex, (TextView)((LinearLayout)view).getChildAt(0));
        	}
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

    public int getSelectedTabIndex() {
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
    	LinearLayout ll = (LinearLayout) View.inflate(getContext(),
                EUExUtil.getResLayoutID("plugin_uexsegmentcontrol_indicator_item"), null);
    	TextView tabView = (TextView) ll.findViewById(EUExUtil.getResIdID("plugin_uexsegmentcontrol_hlv_tv"));
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
        final int tabCount = mTabLayout.getChildCount();
        if(item >= tabCount){
        	item = tabCount - 1;
        }
        mSelectedTabIndex = item;
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
