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

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.RelativeLayout.LayoutParams;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;
import org.zywx.wbpalmstar.base.BUtility;
import org.zywx.wbpalmstar.engine.DataHelper;
import org.zywx.wbpalmstar.engine.EBrowserView;
import org.zywx.wbpalmstar.engine.universalex.EUExBase;
import org.zywx.wbpalmstar.engine.universalex.EUExUtil;
import org.zywx.wbpalmstar.plugin.uexsegmentcontrol.DragGridView.OnItemEditLongClickListener;
import org.zywx.wbpalmstar.plugin.uexsegmentcontrol.VO.DataInfoVO;
import org.zywx.wbpalmstar.plugin.uexsegmentcontrol.VO.OpenDataVO;
import org.zywx.wbpalmstar.plugin.uexsegmentcontrol.ZYWXTabIndicator.OnTabViewClickListener;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

public class EUExSegmentControl extends EUExBase implements OnClickListener,
        OnItemClickListener, OnTabViewClickListener, OnItemEditLongClickListener {

    private static final String TAG = "EUExSegmentControl";
    private TextView finish, tv, tv_add_tip, tips;
    private LinearLayout back, front;
    private ImageView iv;
    private ZYWXTabIndicator indicator;
    private DragGridView gvt, gvp;
    private GridViewAdapter gvat, gvap;
    private boolean clicked;
    private boolean edit;
    private int currentItem = 0;
    private List<String> pools = new ArrayList<String>();//allData
    private List<String> shows = new ArrayList<String>();//shows
    private List<String> hiddens = new ArrayList<String>();//hiddens
    private View view;
    private int isExpand = 1;
    private FrameLayout fl;
    private int maxCount;
    private String showedLable;
    private String addLable;
    private BitmapDrawable downIconBitmap;
    private BitmapDrawable upIconBitmap;

    public EUExSegmentControl(Context context, EBrowserView eBrowserView) {
        super(context, eBrowserView);
        clicked = false;
        edit = false;
    }

    public void open(String[] params) {
        if (params == null || params.length < 1) {
            errorCallback(0, 0, "error params!");
            return;
        }
        if (view != null) {
            errorCallback(0, 0, "already exist!");
            return;
        }
        String json = params[0];
        final OpenDataVO dataVO = DataHelper.gson.fromJson(json, OpenDataVO.class);
        if (dataVO.getDataInfo() == null) {
            errorCallback(0, 0, "error params!");
            return;
        }
        if (pools != null) {
            pools.clear();
        }
        DataInfoVO dataInfoVO = dataVO.getDataInfo();
        pools = dataInfoVO.getAllData();
        if (pools == null || pools.size() < 1) {
            errorCallback(0, 0, "error params!");
            return;
        }
        //init maxShow
        int maxShow = dataInfoVO.getMaxShow();
        if (maxShow < 0) {
            maxCount = pools.size();
        } else {
            maxCount = maxShow < pools.size() ? maxShow : pools.size();
        }
        //init flag
        isExpand = dataInfoVO.isExpand();
        //init shows and hiddens
        if (shows != null) {
            shows.clear();
        }
        if (hiddens != null) {
            hiddens.clear();
        }
        shows = dataInfoVO.getShowData();
        if (shows == null || shows.size() < 1) {
            for (int i = 0; i < pools.size(); i++) {
                hiddens.add(pools.get(i));
            }
            shows = new ArrayList<String>();
        } else {
            for (int i = 0; i < pools.size(); i++) {
                final String name = pools.get(i);
                boolean isShow = false;
                for (int j = 0; j < shows.size(); j++) {
                    if (shows.get(j).equals(name)) {
                        isShow = true;
                    }
                }
                if (!isShow) {
                    hiddens.add(name);
                }
            }
        }
        //init icon 
        String expandBtnOpenPath = dataInfoVO.getExpandOpenIcon();
        String expandBtnClosePath = dataInfoVO.getExpandCloseIcon();
        initIcon(expandBtnOpenPath, expandBtnClosePath);
        //init Edit title
        showedLable = dataInfoVO.getShowedLable();
        addLable = dataInfoVO.getAddLable();
        ((Activity) mContext).runOnUiThread(new Runnable() {

            @Override
            public void run() {
                view = initView();

                gvat = new GridViewAdapter(mContext, EUExSegmentControl.this, shows);
                gvap = new GridViewAdapter(mContext, EUExSegmentControl.this, hiddens);

                gvt.setAdapter(gvat);
                gvp.setAdapter(gvap);

                indicator.setIndicatorData(shows);
                indicator.setCurrentItem(0);
                indicator.setOnTabViewClickListener(EUExSegmentControl.this);

                iv.setOnClickListener(EUExSegmentControl.this);
                gvt.setOnItemClickListener(EUExSegmentControl.this);
                gvt.setOnItemEditLongClickListener(EUExSegmentControl.this);
                gvp.setOnItemClickListener(EUExSegmentControl.this);
                finish.setOnClickListener(EUExSegmentControl.this);

                if (isExpand == 0) {
                    iv.setVisibility(View.GONE);
                }
                if (downIconBitmap != null) {
                    iv.setImageDrawable(downIconBitmap);
                }
                if (!TextUtils.isEmpty(showedLable)) {
                    tv.setText(showedLable);
                }
                if (!TextUtils.isEmpty(addLable)) {
                    tv_add_tip.setText(addLable);
                }

                LayoutParams param = new LayoutParams(dataVO.getWidth(), dataVO.getHeight());
                param.leftMargin = dataVO.getLeft();
                param.topMargin = dataVO.getTop();
                addView2CurrentWindow(view, param);
            }
        });
    }

    public void setCurrentItem(String[] params) {
        if (params != null && params.length == 1) {
            String json = params[0];
            try {
                currentItem = Integer.parseInt(new JSONObject(json)
                        .getString(ESegmentControlUtils.SEGMENTCONTROL_PARAMS_JSON_KEY_ON_RESULT_INDEX));
            } catch (JSONException e) {
                e.printStackTrace();
            }
            ((Activity) mContext).runOnUiThread(new Runnable() {

                @Override
                public void run() {
                    if (indicator != null) {
                        indicator.setCurrentItem(currentItem);
                    }
                }
            });
        }
    }

    public void close(String[] params) {
        ((Activity) mContext).runOnUiThread(new Runnable() {

            @Override
            public void run() {
                clean();
            }
        });
    }

    private View initView() {
        View view = View.inflate(mContext, EUExUtil.getResLayoutID("plugin_uexsegmentcontrol_main"), null);
        fl = (FrameLayout) view.findViewById(EUExUtil.getResIdID("plugin_uexsegmentcontrol_fl"));
        indicator = (ZYWXTabIndicator) view.findViewById(EUExUtil.getResIdID("plugin_uexsegmentcontrol_ll_indicator"));
        tv = (TextView) view.findViewById(EUExUtil.getResIdID("plugin_uexsegmentcontrol_ll_title"));
        iv = (ImageView) view.findViewById(EUExUtil.getResIdID("plugin_uexsegmentcontrol_ll_iv"));
        finish = (TextView) view.findViewById(EUExUtil.getResIdID("plugin_uexsegmentcontrol_ll_finish"));
        back = (LinearLayout) view.findViewById(EUExUtil.getResIdID("plugin_uexsegmentcontrol_fl_back"));
        tv_add_tip = (TextView) view.findViewById(EUExUtil.getResIdID("plugin_uexsegmentcontrol_fl_add_title"));
        gvt = (DragGridView) view.findViewById(EUExUtil.getResIdID("plugin_uexsegmentcontrol_fl_back_titles"));
        gvp = (DragGridView) view.findViewById(EUExUtil.getResIdID("plugin_uexsegmentcontrol_fl_back_pools"));
        tips = (TextView) view.findViewById(EUExUtil.getResIdID("plugin_uexsegmentcontrol_fl_back_tips"));
        front = (LinearLayout) view.findViewById(EUExUtil.getResIdID("plugin_uexsegmentcontrol_fl_ll_front"));
        return view;
    }

    private void initIcon(String expandBtnOpenPath, String expandBtnClosePath) {
        if (TextUtils.isEmpty(expandBtnOpenPath) || TextUtils.isEmpty(expandBtnClosePath)) {
            return;
        }
        try {
            expandBtnOpenPath = BUtility.makeRealPath(BUtility.makeUrl(
                    mBrwView.getCurrentUrl(), expandBtnOpenPath), mBrwView
                    .getCurrentWidget().m_widgetPath, mBrwView
                    .getCurrentWidget().m_wgtType);
            expandBtnClosePath = BUtility.makeRealPath(BUtility.makeUrl(
                    mBrwView.getCurrentUrl(), expandBtnClosePath), mBrwView
                    .getCurrentWidget().m_widgetPath, mBrwView
                    .getCurrentWidget().m_wgtType);
        } catch (Exception e) {
        }
        InputStream input = null;
        try {
            if (expandBtnOpenPath.startsWith("/")) {
                input = new FileInputStream(expandBtnOpenPath);
            } else {
                input = mContext.getAssets().open(expandBtnOpenPath);
            }
            Bitmap bmp = BitmapFactory.decodeStream(input);
            downIconBitmap = new BitmapDrawable(mContext.getResources(), bmp);
        } catch (IOException e) {
            e.printStackTrace();
        } catch (OutOfMemoryError e) {
            e.printStackTrace();
        } finally {
            if (input != null) {
                try {
                    input.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        try {
            if (expandBtnClosePath.startsWith("/")) {
                input = new FileInputStream(expandBtnClosePath);
            } else {
                input = mContext.getAssets().open(expandBtnClosePath);
            }
            Bitmap bmp = BitmapFactory.decodeStream(input);
            upIconBitmap = new BitmapDrawable(mContext.getResources(), bmp);
        } catch (IOException e) {
            e.printStackTrace();
        } catch (OutOfMemoryError e) {
            e.printStackTrace();
        } finally {

            if (input != null) {
                try {
                    input.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    @Override
    public void onClick(View v) {
        if (finish.getId() == v.getId()) {
            edit = false;
            indicator.setIndicatorData(shows);
            gvt.isEditFlag(edit);
            iv.setVisibility(View.VISIBLE);
            finish.setVisibility(View.GONE);
            callBackOnDataChange();
            tips.setVisibility(View.VISIBLE);
            front.setVisibility(View.VISIBLE);
            gvat.setEdit(edit);
        } else if (iv.getId() == v.getId()) {
            if (!clicked) {
                clicked = true;
                fl.setVisibility(View.VISIBLE);
                if (upIconBitmap != null) {
                    iv.setImageDrawable(upIconBitmap);
                } else {
                    iv.setImageResource(EUExUtil.getResDrawableID("plugin_uexsegmentcontrol_btn_up"));
                }
                indicator.setVisibility(View.GONE);
                if (!TextUtils.isEmpty(showedLable)) {
                    tv.setText(showedLable);
                } else {
                    tv.setText(shows.size() + "个未读栏目点击进入");
                }
                tv.setVisibility(View.VISIBLE);
                back.setVisibility(View.VISIBLE);
            } else {
                clicked = false;
                fl.setVisibility(View.GONE);
                if (downIconBitmap != null) {
                    iv.setImageDrawable(downIconBitmap);
                } else {
                    iv.setImageResource(EUExUtil.getResDrawableID("plugin_uexsegmentcontrol_btn_down"));
                }
                indicator.setVisibility(View.VISIBLE);
                tv.setVisibility(View.GONE);
                back.setVisibility(View.GONE);
                indicator.setCurrentItem(indicator.getSelectedTabIndex());
            }
        }
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position,
                            long id) {
        if (parent == gvt && !edit) {
            clicked = false;
            fl.setVisibility(View.GONE);
            if (downIconBitmap != null) {
                iv.setImageDrawable(downIconBitmap);
            } else {
                iv.setImageResource(EUExUtil.getResDrawableID("plugin_uexsegmentcontrol_btn_down"));
            }
            indicator.setVisibility(View.VISIBLE);
            indicator.setCurrentItem(position);
            tv.setVisibility(View.GONE);
            back.setVisibility(View.GONE);
            try {
                TextView showView = (TextView) ((ViewGroup) ((ViewGroup) indicator
                        .getChildAt(0)).getChildAt(position)).getChildAt(0);
                onTabViewClick(position, showView);
            } catch (Exception e) {
            }
        } else if (parent == gvp) {
            if (shows.size() < maxCount) {
                shows.add(hiddens.get(position));
                gvat.notifyDataSetInvalidated();
                indicator.setIndicatorData(shows);
                hiddens.remove(position);
                updateShowsCount();
                gvap.notifyDataSetInvalidated();
            } else {
                Toast.makeText(mContext, "最多只能添加" + maxCount + "个栏目",
                        Toast.LENGTH_SHORT).show();
            }
        }
    }

    @Override
    public void onTabViewClick(int position, TextView view) {
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject
                    .put(ESegmentControlUtils.SEGMENTCONTROL_PARAMS_JSON_KEY_ON_RESULT_INDEX,
                            position);
            jsonObject
                    .put(ESegmentControlUtils.SEGMENTCONTROL_PARAMS_JSON_KEY_ON_RESULT_NAME,
                            view.getText());
        } catch (JSONException e) {
            e.printStackTrace();
        }
        callBackPluginJs(
                ESegmentControlUtils.SEGMENTCONTROL_CALLBAKC_ON_ITEM_CLICK,
                jsonObject.toString());
    }

    @Override
    public void onItemEditLongClick() {
        edit = true;
        iv.setVisibility(View.GONE);
        finish.setVisibility(View.VISIBLE);
        tips.setVisibility(View.INVISIBLE);
        front.setVisibility(View.INVISIBLE);
        gvat.setEdit(edit);
        gvt.isEditFlag(edit);
    }

    private void updateShowsCount() {
        if (!TextUtils.isEmpty(showedLable)) {
            tv.setText(showedLable);
        } else {
            tv.setText(shows.size() + "个未读栏目点击进入");
        }
        callBackOnDataChange();
    }

    public void refreshView(String title) {
        hiddens.add(title);
        gvap.notifyDataSetInvalidated();
        updateShowsCount();
    }

    private void callBackOnDataChange() {
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put(ESegmentControlUtils.SEGMENTCONTROL_PARAMS_JSON_KEY_ON_RESULT_SHOWS, DataHelper.gson.toJson(shows));
        } catch (JSONException e) {
            e.printStackTrace();
        }
        callBackPluginJs(ESegmentControlUtils.SEGMENTCONTROL_CALLBACK_ON_DATA_CHANGE, jsonObject.toString());
    }

    private void callBackPluginJs(String methodName, String jsonData) {
        String js = SCRIPT_HEADER + "if(" + methodName + "){"
                + methodName + "(" + jsonData + ");}";
        Log.i(TAG, "callBackPluginJs->js = " + js);
        onCallback(js);
    }

    private void addView2CurrentWindow(View child, RelativeLayout.LayoutParams parms) {
        int l = (int) (parms.leftMargin);
        int t = (int) (parms.topMargin);
        int w = parms.width;
        int h = parms.height;
        FrameLayout.LayoutParams lp = new FrameLayout.LayoutParams(w, h);
        lp.gravity = Gravity.NO_GRAVITY;
        lp.leftMargin = l;
        lp.topMargin = t;
        adptLayoutParams(parms, lp);
        mBrwView.addViewToCurrentWindow(child, lp);
    }

    @Override
    protected boolean clean() {
        if (view != null) {
            removeViewFromCurrentWindow(view);
            view = null;
        }
        if (pools != null) {
            pools.clear();
        }
        if (shows != null) {
            shows.clear();
        }
        if (hiddens != null) {
            hiddens.clear();
        }
        if (indicator != null) {
            indicator.clean();
        }
        clicked = false;
        edit = false;
        if (downIconBitmap != null) {
            downIconBitmap.getBitmap().recycle();
        }
        if (upIconBitmap != null) {
            upIconBitmap.getBitmap().recycle();
        }
        return false;
    }
}
