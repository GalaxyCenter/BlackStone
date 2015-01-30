package apollo.widget;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import apollo.app.PostActivity;
import apollo.app.R;

public class DialogMoreAdapter extends BaseAdapter {
	
	private String[] mItems = null;
	private LayoutInflater mInflater;
	
	public class DialogMoreViewHolder {
		ImageView mImage;
		TextView mNum;
		TextView mText;
		ProgressBar mProgress;
	}
	
	public DialogMoreAdapter(Context context, String[] items) {
		this.mInflater = LayoutInflater.from(context);
		this.mItems = items;
	}
	
	@Override
	public int getCount() {
		return mItems.length;
	}

	@Override
	public Object getItem(int position) {
		return null;
	}

	@Override
	public long getItemId(int position) {
		return 0;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		DialogMoreViewHolder vh = null;
		
		if (convertView == null) {
			convertView = this.mInflater.inflate(R.layout.view_dialog_more_item, null);
			vh = new DialogMoreViewHolder();
			vh.mText = (TextView) convertView.findViewById(R.id.text);
			vh.mNum = (TextView) convertView.findViewById(R.id.number);
			vh.mProgress = (ProgressBar) convertView.findViewById(R.id.progress);
			vh.mImage = (ImageView) convertView.findViewById(R.id.image);
			convertView.setTag(vh);
		} else {
			vh = (DialogMoreViewHolder) convertView.getTag();
		}
		
		vh.mNum.setVisibility(View.GONE);
		vh.mProgress.setVisibility(View.GONE);
		vh.mImage.setVisibility(View.GONE);
		//if (position == mMoreItemCurIdx && mMoreItemCurIdx == 0) {
		//	vh.mImage.setVisibility(View.VISIBLE);
		//}
		vh.mText.setText(mItems[position]);
		return convertView;
	}
}