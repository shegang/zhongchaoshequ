package com.example.qiumishequouzhan;

import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import com.example.qiumishequouzhan.R;
import net.simonvt.menudrawer.MenuDrawer;
import net.simonvt.menudrawer.Position;

import java.util.ArrayList;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: jinxing
 * Date: 14-3-19
 * Time: 上午11:30
 * To change this template use File | Settings | File Templates.
 */
public abstract class BaseListMenu extends FragmentActivity implements MenuAdapter.MenuListener {
    private static final String STATE_ACTIVE_POSITION =
            "net.simonvt.menudrawer.samples.LeftDrawerSample.activePosition";

    protected MenuDrawer mMenuDrawer;

    protected MenuAdapter mAdapter;
    protected ListView mList;

    private int mActivePosition = 0;

    @Override
    protected void onCreate(Bundle inState) {
        super.onCreate(inState);

        if (inState != null) {
            mActivePosition = inState.getInt(STATE_ACTIVE_POSITION);
        }

        mMenuDrawer = MenuDrawer.attach(this, MenuDrawer.Type.BEHIND, getDrawerPosition(), getDragMode());

        List<Object> items = new ArrayList<Object>();
        items.add(new Item("中超资讯", R.drawable.ic_action_refresh_dark));
        items.add(new Item("中超竞猜", R.drawable.ic_action_refresh_dark));
        items.add(new Item("中超球队", R.drawable.ic_action_refresh_dark));
        items.add(new Item("语音聊球", R.drawable.ic_action_refresh_dark));
        items.add(new Item("一猜到底", R.drawable.ic_action_refresh_dark));
        items.add(new Item("摇球星卡", R.drawable.ic_action_refresh_dark));
        items.add(new Item("个人中心", R.drawable.ic_action_refresh_dark));
        items.add(new Item("关于我们", R.drawable.ic_action_refresh_dark));
        items.add(new Category("Cat 1"));

        mList = new ListView(this);
        mList.setDivider(null);
        mAdapter = new MenuAdapter(this, items);
        mAdapter.setListener(this);
        mAdapter.setActivePosition(mActivePosition);

        mList.setAdapter(mAdapter);

        mList.setOnItemClickListener(mItemClickListener);

        mMenuDrawer.setMenuView(mList);
    }

    protected abstract void onMenuItemClicked(int position, Item item);

    protected abstract int getDragMode();

    protected abstract Position getDrawerPosition();

    private AdapterView.OnItemClickListener mItemClickListener = new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            onMenuItemClicked(position, (Item) mAdapter.getItem(position));
            if (position == 3 || position == 6)
            {
                String uid = LocalDataObj.GetUserLocalData("UserID");
                if (uid.equalsIgnoreCase("100") == true)
                {
                       return;
                }
                if (position == 3)
                    return;
            }
            mActivePosition = position;
            mMenuDrawer.setActiveView(view, position);
            mAdapter.setActivePosition(position);

            mAdapter.notifyDataSetInvalidated();
        }
    };

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt(STATE_ACTIVE_POSITION, mActivePosition);
    }

    @Override
    public void onActiveViewChanged(View v) {
        mMenuDrawer.setActiveView(v, mActivePosition);
    }

}
