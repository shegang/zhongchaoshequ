package com.example.qiumishequouzhan;

/**
 * Created with IntelliJ IDEA.
 * User: jinxing
 * Date: 14-3-19
 * Time: 上午11:32
 * To change this template use File | Settings | File Templates.
 */

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.FrameLayout;
import android.widget.TextView;
import com.example.qiumishequouzhan.R;

import java.util.List;

public class MenuAdapter extends BaseAdapter {

    public interface MenuListener {

        void onActiveViewChanged(View v);
    }

    private Context mContext;

    private List<Object> mItems;

    private MenuListener mListener;

    private int mActivePosition = -1;

     public static String titleCount[] = {"0", "0", "0","0", "0","0","0","0"};


    public MenuAdapter(Context context, List<Object> items) {
        mContext = context;
        mItems = items;
    }

    public void setListener(MenuListener listener) {
        mListener = listener;
    }

    public void setActivePosition(int activePosition) {
        mActivePosition = activePosition;
    }

    @Override
    public int getCount() {
        return mItems.size();
    }

    @Override
    public Object getItem(int position) {
        return mItems.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public int getItemViewType(int position) {
        return getItem(position) instanceof Item ? 0 : 1;
    }

    @Override
    public int getViewTypeCount() {
        return 2;
    }

    @Override
    public boolean isEnabled(int position) {
        return getItem(position) instanceof Item;
    }

    @Override
    public boolean areAllItemsEnabled() {
        return false;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View v = convertView;
        Object item = getItem(position);

        if (item instanceof Category) {
            if (v == null) {
                v = LayoutInflater.from(mContext).inflate(R.layout.menu_row_category, parent, false);
            }

//            ((TextView) v).setText(((Category) item).mTitle);

        } else {
            if (v == null) {
                v = LayoutInflater.from(mContext).inflate(R.layout.menu_row_item, parent, false);
            }

            TextView tv = (TextView) v.findViewById(R.id.tv_item);// (TextView) v;
            tv.setText(((Item) item).mTitle);
            FrameLayout img = (FrameLayout) v.findViewById(R.id.tipicon);
            TextView titlecount = (TextView) v.findViewById(R.id.icon_item);
            img.setVisibility(View.GONE);
//            tv.setCompoundDrawablesWithIntrinsicBounds(((Item) item).mIconRes, 0, 0, 0);
            if (Integer.valueOf(titleCount[position]) > 0)
            {
                img.setVisibility(View.VISIBLE);
                titlecount.setText(titleCount[position]);
            }
          /*  if (Integer.valueOf(titleCount) > 0) {
                img.setVisibility(View.VISIBLE);
                titlecount.setText(titleCount);
            }*/
            if (position == mActivePosition) {
                v.setBackgroundColor(mContext.getResources().getColor(R.color.red));
            } else {
                v.setBackgroundColor(mContext.getResources().getColor(R.color.black));
            }
        }

        v.setTag(R.id.mdActiveViewPosition, position);

        if (position == mActivePosition) {
            mListener.onActiveViewChanged(v);
        }

        return v;
    }
}
