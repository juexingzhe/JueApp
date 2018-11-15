package com.example.juexingzhe.jueapp.util;

import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;

import com.example.juexingzhe.jueapp.presenter.CardAdapter;

import java.util.ArrayList;
import java.util.List;

public class CardCallBack extends ItemTouchHelper.SimpleCallback {

    private List<String> mDatas;
    private List<String> mCloneDatas;
    private CardAdapter adapter;
    private RecyclerView mRv;
    private ItemSwiped onItemSwiped;

    public CardCallBack(List<String> mDatas, CardAdapter adapter, RecyclerView mRv) {
        super(0, ItemTouchHelper.UP | ItemTouchHelper.DOWN);
        this.mDatas = mDatas;
        this.adapter = adapter;
        this.mRv = mRv;
        cloneDatas();
    }


    public CardCallBack() {
        /*
        * 即我们对哪些方向操作关心。如果我们关心用户向上拖动，可以将
         填充swipeDirs参数为LEFT | RIGHT 。0表示从不关心。
        * */
        super(0,
                ItemTouchHelper.UP | ItemTouchHelper.DOWN
        );
    }

    public void setOnItemSwiped(ItemSwiped itemSwiped) {
        this.onItemSwiped = itemSwiped;
    }

    @Override
    public int getMovementFlags(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder) {
        int dragFlags = ItemTouchHelper.UP | ItemTouchHelper.DOWN;        //允许上下的拖动
        int swipeFlags = ItemTouchHelper.UP | ItemTouchHelper.DOWN;   //只允许从右向左侧滑
        return makeMovementFlags(dragFlags, swipeFlags);
    }

    @Override
    public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target) {
        return false;
    }

    @Override
    public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction) {


        int currentPos = viewHolder.getLayoutPosition();

        if (onItemSwiped != null) {
            onItemSwiped.onSwiped(false);
        }

        if (direction == ItemTouchHelper.UP) {
            mDatas.remove(currentPos);
            adapter.notifyDataSetChanged();
        }


        if (direction == ItemTouchHelper.DOWN) {
            //当已经滑动删除了的时候会被回掉--删除数据，循环的效果
//            if (currentPos - second < first) {
//                if (onItemSwiped != null){
//                    onItemSwiped.onSwiped(true);
//                    return;
//                }
//            }
            String remove = mDatas.remove(currentPos);
            mDatas.add(0, mDatas.get(currentPos - 1));
            adapter.notifyDataSetChanged();
        }
    }

    private List<String> cloneDatas() {
        if (mCloneDatas == null) {
            mCloneDatas = new ArrayList<>(mDatas.size());
        }
        for (String txt : mDatas) {
            mCloneDatas.add(txt);
        }

        return mCloneDatas;
    }

    public interface ItemSwiped {
        void onSwiped(boolean swaped);
    }


}
