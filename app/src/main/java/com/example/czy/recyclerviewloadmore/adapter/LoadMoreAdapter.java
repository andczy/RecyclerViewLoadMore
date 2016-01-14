package com.example.czy.recyclerviewloadmore.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import java.util.List;

/**
 * Created by czy on 2015/8/18.
 */
public abstract class LoadMoreAdapter<VH extends RecyclerView.ViewHolder , T> extends RecyclerView.Adapter implements View.OnClickListener{

    /***footerview type*/
    private static final int TYPE = 0x5552 ;
    /***是否可以加载更多 , 默认true*/
    private boolean canLoadMore = true ;
    String loadMoreString = "点击加载更多" ;
    String loadingString = "正在加载..." ;
    private boolean isLoading ;
    private ViewGroup footerView ;
    private OnLoadMoreListener loadMoreListener ;
    private AdapterView.OnItemClickListener onItemClickListener ;
    private List<T> models ;
    private TextView textView;
    private boolean noMore;

    public void setLoadMoreListener(OnLoadMoreListener loadMoreListener) {
        this.loadMoreListener = loadMoreListener;
    }
    public void setData(List<T> datas){
        if(models!=null){
            models.clear();
        }
        models = datas ;
        notifyDataSetChanged();
    }
    public synchronized void addData(List<T> adds)
    {
        if(models == null )
            models = adds ;
        else
            models.addAll(adds ) ;

        notifyDataSetChanged();
    }
    public T getItem(int position)
    {
        if(models!=null && position < models.size())
        {
            return models.get(position);
        }
        return null ;
    }
    public void showNoMore(){
//        noMore = true ;
//        notifyDataSetChanged();
    }
    public boolean isCanLoadMore() {
        return canLoadMore;
    }

    public void setCanLoadMore(boolean canLoadMore) {
        this.canLoadMore = canLoadMore;
    }

    public boolean isLoading() {
        return isLoading;
    }

    /***显示加载中*/
    public void loading(){
        if(loadMoreListener != null )
            loadMoreListener.loadMore();
        isLoading = true ;
        showLoadingFooter();
    }
    public void setOnItemClickListener(AdapterView.OnItemClickListener listener)
    {
        this.onItemClickListener = listener ;
    }
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if(viewType == TYPE)
        {
            Context context = parent.getContext() ;
            LinearLayout loadMoreParent = new LinearLayout(context) ;
            loadMoreParent.setOrientation(LinearLayout.HORIZONTAL);
            loadMoreParent.setGravity(Gravity.CENTER);

            ProgressBar bar = new ProgressBar(context) ;

            float density = context.getResources().getDisplayMetrics().density ;
            int size = (int) (24 * density);
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams( size , size ) ;
            loadMoreParent.addView(bar, params);

            textView = new TextView(context) ;
            LinearLayout.LayoutParams textParams = new LinearLayout.LayoutParams(
                    ViewGroup.LayoutParams.WRAP_CONTENT , ViewGroup.LayoutParams.WRAP_CONTENT  ) ;
            textParams.leftMargin = (int) (density * 6);
            int padding = (int) (density * 20);
            textView.setPadding(0, padding, 0, padding);
            loadMoreParent.addView(textView, textParams) ;
            loadMoreParent.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (!isLoading && canLoadMore) {
                        isLoading = true;
                        if(loadMoreListener != null )
                            loadMoreListener.loadMore();
                        showLoadingFooter();
                    }
                }
            });
            ViewGroup.LayoutParams parentParams = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT , ViewGroup.LayoutParams.WRAP_CONTENT) ;
            parent.addView(loadMoreParent , parentParams );
            footerView = loadMoreParent ;
            return new RecyclerView.ViewHolder(footerView) {
            };
        }
        else{
            return onCreateHolder(parent , viewType)  ;
        }
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        if(getItemViewType(position) == TYPE) {
            if (isLoading) {
                showLoadingFooter() ;
            }
            else if(noMore)
            {
                if(footerView!=null)
                {
                    footerView.getChildAt(0).setVisibility(View.GONE);
                    textView.setText("没有更多啦！");
                }
            }
            else{
                loadOver() ;
            }
        }
        else{
            if(onItemClickListener!=null)
            {
                holder.itemView.setTag(position);
                holder.itemView.setOnClickListener(this);
            }
            onBindHolder((VH) holder, position);
        }
    }

    private void showLoadingFooter(){
        if(footerView != null )
        {
            footerView.getChildAt(0).setVisibility(View.VISIBLE);
            textView.setText( loadingString );
        }
    }


    /***加载完成调用 */
    public void loadOver(){
        isLoading = false ;
        if(footerView != null )
        {
            footerView.getChildAt(0).setVisibility(View.GONE);
            textView.setText( loadMoreString );
        }
    }
    @Override
    public int getItemViewType(int position) {
        if( canLoadMore&& position == getItemCount() -1 ){
            return TYPE ;
        }
        else{
            return getViewType(position) ;
        }
    }
    public int getCount() {
        int count = models == null ? 0 : models.size() ;
        if(count == 0 )
        {
            canLoadMore = false ;
        }
        return  count;
    }
    @Override
    public int getItemCount() {
        return getCount() + (canLoadMore ? 1 : 0);
    }
    public abstract RecyclerView.ViewHolder onCreateHolder(ViewGroup parent, int viewType);
    public abstract void onBindHolder(VH holder, int position);
    public abstract int getViewType(int position) ;

    public interface OnLoadMoreListener{
        void loadMore() ;
    }

    @Override
    public void onClick(View v) {
        if(onItemClickListener!=null)
        {
            onItemClickListener.onItemClick( null , v , (Integer)v.getTag() , 0 );
        }
    }
}
