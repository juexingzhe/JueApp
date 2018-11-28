package com.example.juexingzhe.jueapp.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.example.juexingzhe.jueapp.R;
import com.example.juexingzhe.jueapp.bean.IBaseInfoBean;
import com.example.juexingzhe.jueapp.util.HandlerUtil;

import java.util.ArrayList;
import java.util.List;

public abstract class BaseLoadAdapter<T extends IBaseInfoBean> extends RecyclerView.Adapter {
    private static final String TAG = BaseLoadAdapter.class.getSimpleName() + "_log";

    private static final int TYPE_BOTTOM = 0x502;
    private static final int STATE_LOADING = 0x012;
    public static final int STATE_NOMORE = 0x013;
    private static final int STATE_ERR = 0x014;

    protected Context context;
    protected List<T> list;
    private RecyclerView recyclerView;
    private boolean isLoading = false;
    private int state = STATE_LOADING;

    private RecyclerView.ViewHolder bottomHolder;


    public BaseLoadAdapter(Context context, List<T> data) {
        super();
        this.context = context;
        this.list = data;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (viewType == TYPE_BOTTOM) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.bottom_layout, parent, false);
            return new BottomViewHolder(view);
        } else {
            return setViewHolder(parent, viewType);
        }
    }

    public abstract RecyclerView.ViewHolder setViewHolder(ViewGroup parent, int viewTyp);

    @Override
    public void onBindViewHolder(final RecyclerView.ViewHolder holder, int position) {
        if (getItemViewType(position) == TYPE_BOTTOM && holder instanceof BottomViewHolder) {
            final ProgressBar progressBar = ((BottomViewHolder) holder).progressBar;
            final TextView bottomTextView = ((BottomViewHolder) holder).bottomTextView;
            final ImageView bottomIcon = ((BottomViewHolder) holder).bottomIcon;

            if (holder.itemView == null) {
                return;
            }

            if (holder.itemView.getVisibility() == View.GONE) {
                holder.itemView.setVisibility(View.VISIBLE);
            }

            bottomHolder = holder;

            switch (state) {
                case STATE_LOADING:
                    progressBar.setVisibility(View.VISIBLE);
                    bottomTextView.setText(R.string.loading_tip);
                    bottomIcon.setVisibility(View.GONE);
                    holder.itemView.setOnClickListener(null);
                    // HandlerUtil.getExecutor().executeDelay(() -> hideBottomView(bottomHolder), 3000);
                    break;
                case STATE_NOMORE:
                    progressBar.setVisibility(View.GONE);
                    bottomTextView.setText(R.string.load_nomore_tip);
                    bottomIcon.setImageResource(R.drawable.info_icon);
                    holder.itemView.setOnClickListener(null);
                     HandlerUtil.getExecutor().executeDelay(() -> {
                         hideBottomView(bottomHolder);
                         state = STATE_LOADING;
                     }, 2000);
                    break;
                case STATE_ERR:
                    progressBar.setVisibility(View.VISIBLE);
                    bottomTextView.setText(R.string.load_err_tip);
                    bottomIcon.setImageResource(R.drawable.error_icon);
                    holder.itemView.setOnClickListener(v -> {
                        progressBar.setVisibility(View.VISIBLE);
                        bottomTextView.setText(R.string.loading_tip);
                        bottomIcon.setVisibility(View.GONE);
                        loadMore();
                    });
                    break;
                default:
                    break;
            }
        } else {
            bindItemViewHolder(holder, position);
        }
    }

    private void hideBottomView(final RecyclerView.ViewHolder holder) {
        if (holder == null) {
            return;
        }
        if (holder.itemView == null || holder.itemView.getVisibility() == View.GONE) {
            return;
        }
        holder.itemView.post(() -> holder.itemView.setVisibility(View.GONE));
    }

    public abstract void bindItemViewHolder(RecyclerView.ViewHolder holder, int position);

    public abstract int getNormalItemViewType(int position);

    @Override
    public int getItemViewType(int position) {
        // 最后一个数据
        if (position == list.size()) {
            return TYPE_BOTTOM;
        } else {
            return getNormalItemViewType(position);
        }
    }

    @Override
    public final int getItemCount() {
        if (null == list || list.isEmpty()) {
            return 0;
        }

        return list.size() + 1;
    }


    /**
     * 子类实现需要调用super.loadMore()
     */
    public void loadMore() {
        if (isLoading()) {
            return;
        }
        setLoading(true);
    }

    public boolean isLoading() {
        return isLoading;
    }

    public void setLoading(boolean loading) {
        isLoading = loading;
        if (!isLoading) {
            hideBottomView(bottomHolder);
        }
        state = STATE_LOADING;
        notifyItemChanged(list.size(), TAG);
    }

    public void setErrorStatus() {
        state = STATE_ERR;
        notifyItemChanged(list.size(), TAG);
        setLoading(false);
    }

    public void setLastedStatus() {
        isLoading = false;
        state = STATE_NOMORE;
        notifyItemChanged(list.size(), TAG);
    }

    public void clearData() {
        if (this.list != null && !this.list.isEmpty()) {
            this.list.clear();
        }
    }


    @Override
    public void onAttachedToRecyclerView(RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);
        this.recyclerView = recyclerView;
    }

    @Override
    public void onDetachedFromRecyclerView(RecyclerView recyclerView) {
        super.onDetachedFromRecyclerView(recyclerView);
        destroy();
    }

    public void destroy() {
        clearData();

        if (context != null) {
            context = null;
        }

        if (recyclerView != null) {
            recyclerView.removeAllViews();
            ((ViewGroup) recyclerView.getParent()).removeView(recyclerView);
            recyclerView = null;
        }
    }

    public int getState() {
        return state;
    }
}
