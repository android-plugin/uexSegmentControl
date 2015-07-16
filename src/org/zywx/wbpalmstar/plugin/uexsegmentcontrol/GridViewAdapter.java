package org.zywx.wbpalmstar.plugin.uexsegmentcontrol;

import android.content.Context;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import org.zywx.wbpalmstar.engine.universalex.EUExUtil;

import java.util.List;

public class GridViewAdapter extends BaseAdapter {

	private Context context;
	private EUExSegmentControl activity;
	private List<String> titles;
	private boolean edit;

	public GridViewAdapter(Context context, EUExSegmentControl activity,
			List<String> titles) {
		this.context = context;
		this.activity = activity;
		this.titles = titles;
		edit = false;
	}

	public void setEdit(boolean edit) {
		this.edit = edit;
		notifyDataSetInvalidated();
	}

	@Override
	public int getCount() {
		return titles.size();
	}

	@Override
	public Object getItem(int position) {
		return titles.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(final int position, View convertView, ViewGroup parent) {
		View view = null;
		Holder holder = null;
		if (convertView == null) {
			holder = new Holder();
			view = View.inflate(context, EUExUtil.getResLayoutID("plugin_uexsegmentcontrol_item_edit"), null);
			holder.xx = (ImageView) view.findViewById(EUExUtil.getResIdID("plugin_item_edit_iv"));
			holder.tv = (TextView) view.findViewById(EUExUtil.getResIdID("plugin_item_bt"));
			view.setTag(holder);
		} else {
			view = convertView;
			holder = (Holder) view.getTag();
		}
		if (edit) {
			if (position == 0) {
				holder.xx.setVisibility(View.INVISIBLE);
			} else {
				holder.xx.setVisibility(View.VISIBLE);
			}
		} else {
			holder.xx.setVisibility(View.INVISIBLE);
		}

		holder.tv.setText(titles.get(position));
		holder.xx.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				String title = titles.remove(position);
				notifyDataSetInvalidated();
				activity.refreshView(title);
			}
		});
		return view;
	}

	private class Holder {
		public ImageView xx;
		public TextView tv;
	}

	public void exchange(int dragPostion, int dropPostion) {
		String dragItem = (String) getItem(dragPostion);
		if (dragPostion < dropPostion) {
			titles.add(dropPostion + 1, dragItem);
			titles.remove(dragPostion);
		} else {
			titles.add(dropPostion, dragItem);
			titles.remove(dragPostion + 1);
		}
		notifyDataSetChanged();
	}

}
