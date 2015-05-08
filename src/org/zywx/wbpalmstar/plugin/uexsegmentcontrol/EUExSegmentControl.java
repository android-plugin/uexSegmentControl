package org.zywx.wbpalmstar.plugin.uexsegmentcontrol;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.zywx.wbpalmstar.engine.EBrowserView;
import org.zywx.wbpalmstar.engine.universalex.EUExBase;
import org.zywx.wbpalmstar.engine.universalex.EUExUtil;
import org.zywx.wbpalmstar.plugin.uexsegmentcontrol.DragGridView.OnItemEditLongClickListener;
import org.zywx.wbpalmstar.plugin.uexsegmentcontrol.ZYWXTabIndicator.OnTabViewClickListener;

import android.app.Activity;
import android.content.Context;
import android.view.Gravity;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.RelativeLayout.LayoutParams;
import android.widget.TextView;
import android.widget.Toast;

public class EUExSegmentControl extends EUExBase implements OnClickListener,
		OnItemClickListener, OnTabViewClickListener, OnItemEditLongClickListener {
	
	static final String ONITEMCLICK = "uexSegmentControl.onItemClick";
	private TextView finish, tv, tips;
	private LinearLayout back, front;
	private ImageView iv;
	private ZYWXTabIndicator indicator;
	private DragGridView gvt, gvp;
	private GridViewAdapter gvat, gvap;
	private boolean clicked;
	private boolean edit;
	private int currentItem = 0;
	private List<String> pools = new ArrayList<String>();
	private List<String> shows = new ArrayList<String>();
	private List<String> hiddens = new ArrayList<String>();
	private View view;
	private int flag = 1;
	private FrameLayout fl;

	public EUExSegmentControl(Context context, EBrowserView eBrowserView) {
		super(context, eBrowserView);
		clicked = false;
		edit = false;
	}

	public void open(String[] params) {
		if(params != null && params.length == 1) {
			if(view != null) {
				return;
			}
			if(pools == null || pools.size() == 0) {
				return;
			}
			try {
				JSONObject json = new JSONObject(params[0]);
				final float x = Float.parseFloat(json.getString(ESegmentControlUtils.SEGMENTCONTROL_PARAMS_JSON_KEY_X));
				final float y = Float.parseFloat(json.getString(ESegmentControlUtils.SEGMENTCONTROL_PARAMS_JSON_KEY_Y));
				final float w = Float.parseFloat(json.getString(ESegmentControlUtils.SEGMENTCONTROL_PARAMS_JSON_KEY_W));
				final float h = Float.parseFloat(json.getString(ESegmentControlUtils.SEGMENTCONTROL_PARAMS_JSON_KEY_H));
				((Activity)mContext).runOnUiThread(new Runnable() {

					@Override
					public void run() {
						view = initView();
						initData();
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
						
						LayoutParams param = new LayoutParams((int)w, (int)h);
						param.leftMargin = (int)x;
						param.topMargin = (int)y;
						addView2CurrentWindow(view, param);
					}
				});
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
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
	
	public void setData(String[] params) {
		if(params != null && params.length == 1) {
			try {
				if(pools != null) {
					pools.clear();
				}
				JSONObject obj = new JSONObject(params[0]);
				if(obj.has(ESegmentControlUtils.SEGMENTCONTROL_PARAMS_JSON_KEY_FLAG)) {
					flag  = Integer.parseInt(obj.getString(ESegmentControlUtils.SEGMENTCONTROL_PARAMS_JSON_KEY_FLAG));
				}
				JSONArray array = obj.getJSONArray(ESegmentControlUtils.SEGMENTCONTROL_PARAMS_JSON_KEY_DATA);
				for (int i = 0; i < array.length(); i++) {
					pools.add(array.getString(i));
				}
			} catch (JSONException e) {
				e.printStackTrace();
			}
		}
	}
	
	public void setCurrentItem(String[] params) {
		if(params != null && params.length == 1) {
			currentItem = Integer.parseInt(params[0]);
			((Activity)mContext).runOnUiThread(new Runnable() {
				
				@Override
				public void run() {
					if(indicator != null) {
						indicator.setCurrentItem(currentItem);
					}
				}
			});
		} 
	}
	
	public void close(String[] params) {
		((Activity)mContext).runOnUiThread(new Runnable() {
			
			@Override
			public void run() {
				clean();
			}
		});
	}
	
	private void initData() {
		if(shows != null) {
			shows.clear();
		}
		if(hiddens != null) {
			hiddens.clear();
		}
		if(flag == 0) {
			for (int i = 0; i < pools.size(); i++) {
				shows.add(pools.get(i));
			}
		}
		if(flag == 1) {
			for (int i = 0; i < pools.size(); i++) {
				if (i < 18) {
					shows.add(pools.get(i));
				} else {
					hiddens.add(pools.get(i));
				}
			}
		}
	}

	private View initView() {
		View view = View.inflate(mContext, EUExUtil.getResLayoutID("plugin_main"), null);
		fl = (FrameLayout) view.findViewById(EUExUtil.getResIdID("plugin_fl"));
		indicator = (ZYWXTabIndicator) view.findViewById(EUExUtil.getResIdID("plugin_ll_indicator"));
		tv = (TextView) view.findViewById(EUExUtil.getResIdID("plugin_ll_title"));
		iv = (ImageView) view.findViewById(EUExUtil.getResIdID("plugin_ll_iv"));
		finish = (TextView) view.findViewById(EUExUtil.getResIdID("plugin_ll_finish"));
		back = (LinearLayout) view.findViewById(EUExUtil.getResIdID("plugin_fl_back"));
		gvt = (DragGridView) view.findViewById(EUExUtil.getResIdID("plugin_fl_back_titles"));
		gvp = (DragGridView) view.findViewById(EUExUtil.getResIdID("plugin_fl_back_pools"));
		tips = (TextView) view.findViewById(EUExUtil.getResIdID("plugin_fl_back_tips"));
		front = (LinearLayout) view.findViewById(EUExUtil.getResIdID("plugin_fl_ll_front"));
		if(flag == 0) {
			iv.setVisibility(View.GONE);
		}
		return view;
	}

	@Override
	public void onClick(View v) {
		if(finish.getId() == v.getId()) {
			edit = false;
			indicator.setIndicatorData(shows);
			
			gvt.isEditFlag(edit);
			iv.setVisibility(View.VISIBLE);
			finish.setVisibility(View.GONE);
			tips.setVisibility(View.VISIBLE);
			front.setVisibility(View.VISIBLE);
			gvat.setEdit(edit);
		}
		if(iv.getId() == v.getId()) {
			if (!clicked) {
				clicked = true;
				fl.setVisibility(View.VISIBLE);
				iv.setImageResource(EUExUtil.getResDrawableID("btn_up"));
				indicator.setVisibility(View.GONE);
				tv.setText(shows.size() + "个未读栏目点击进入");
				tv.setVisibility(View.VISIBLE);
				back.setVisibility(View.VISIBLE);
			} else {
				clicked = false;
				fl.setVisibility(View.GONE);
				iv.setImageResource(EUExUtil.getResDrawableID("btn_down"));
				indicator.setVisibility(View.VISIBLE);
				tv.setVisibility(View.GONE);
				back.setVisibility(View.GONE);
			}
		}
	}

	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position,
			long id) {
		if (parent == gvt && !edit) {
			clicked = false;
			fl.setVisibility(View.GONE);
			iv.setImageResource(EUExUtil.getResDrawableID("btn_down"));
			indicator.setVisibility(View.VISIBLE);
			indicator.setCurrentItem(position);
			tv.setVisibility(View.GONE);
			back.setVisibility(View.GONE);
		}
		if (parent == gvp) {
			if (shows.size() < 24) {
				shows.add(hiddens.get(position));
				gvat.notifyDataSetInvalidated();
				indicator.setIndicatorData(shows);
				hiddens.remove(position);
				gvap.notifyDataSetInvalidated();
			} else {
				Toast.makeText(mContext, "最多只能添加24个栏目", 0).show();
			}
		}
	}

	public void refreshView(String title) {
		hiddens.add(title);
		gvap.notifyDataSetInvalidated();
	}

	@Override
	public void onTabViewClick(int position, TextView view) {
		onCallback(SCRIPT_HEADER + "if(" + ONITEMCLICK + "){"+ ONITEMCLICK + "("+position+", '"+view.getText()+"');}");
	}

	@Override
	protected boolean clean() {
		if(view != null) {
			removeViewFromCurrentWindow(view);
			view = null;
		}
		if(pools != null) {
			pools.clear();
		}
		if(shows != null) {
			shows.clear();
		}
		if(hiddens != null) {
			hiddens.clear();
		}
		if(indicator != null) {
			indicator.clean();
		}
		clicked = false;
		edit = false;
		return false;
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
}
