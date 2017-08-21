package com.mx.cs.listener;

import com.mx.cs.R;
import android.view.View;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class ListenerListViewScrollHandler implements OnScrollListener {
	
	private boolean isLastRow = false;
	private ListView deliverylist;
	private RelativeLayout pageVboxLayout; //计数用   例： 1/4
	private TextView pageText;
	
	public ListenerListViewScrollHandler(ListView lv, RelativeLayout rl)
	{
		deliverylist = lv;
		pageVboxLayout = rl;
		pageText = (TextView) pageVboxLayout.findViewById(R.id.pageText);
	}
	
	
	@Override
	public void onScroll(AbsListView view, int firstVisibleItem,
			int visibleItemCount, int totalItemCount) {
		int totalCount = deliverylist.getCount() - 1; //减去 customListView 增加的一条用于刷新的child
		
		if (pageVboxLayout.getVisibility() == View.VISIBLE) {
			pageText.setText(deliverylist.getLastVisiblePosition() + "/"
					+ totalCount);
		}
		if (firstVisibleItem + visibleItemCount == totalItemCount
				&& totalItemCount > 0) {
			isLastRow = true;
		}
	}

	@Override
	public void onScrollStateChanged(AbsListView view, int scrollState) {
		int totalCount = deliverylist.getCount() - 1;
		
		if (scrollState == AbsListView.OnScrollListener.SCROLL_STATE_TOUCH_SCROLL) {
			pageVboxLayout.setVisibility(View.VISIBLE);
			pageText.setText(deliverylist.getLastVisiblePosition() + "/"
					+ totalCount);
		}
		if (scrollState == AbsListView.OnScrollListener.SCROLL_STATE_FLING) {
			pageVboxLayout.setVisibility(View.VISIBLE);
			pageText.setText(deliverylist.getLastVisiblePosition() + "/"
					+ totalCount);
		}
		if (scrollState == AbsListView.OnScrollListener.SCROLL_STATE_IDLE) {
			pageVboxLayout.setVisibility(View.GONE);
			pageText.setText("");
		}
		if (isLastRow == true
				&& scrollState == AbsListView.OnScrollListener.SCROLL_STATE_FLING) {
			pageVboxLayout.setVisibility(View.GONE);
			pageText.setText("");
		}
	}

	 

}
