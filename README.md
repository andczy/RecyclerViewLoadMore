# RecyclerViewLoadMore

通过继承LoadMoreAdapter类来是实现recyclerview的加载更多功能。
通过LoadMoreAdapter设置OnLoadMoreListener接口来监听加载更多 操作。
同时可以设置setOnItemClickListener来监听item的点击事件。

#PullDownLayout
项目中还 包含动画下拉刷新控件PullDownLayout.
可以包含在任意想要实现下拉刷新的控件外。
PullDownLayout下拉时包含3段动画:
1、下拉放大阶段。
2、下拉动画变化阶段。
3、下拉加载数据阶段。
和美团的下拉刷新动画类型。

#LoadMoreAdapter实现
下面的IntAdapter就是一个点击加载更多的实现。

public class IntAdapter extends LoadMoreAdapter<RecyclerView.ViewHolder , IntModel> {

    @Override
    public RecyclerView.ViewHolder onCreateHolder(ViewGroup parent, int viewType) {
        TextView textView = new TextView(parent.getContext());
        int pad = parent.getResources().getDimensionPixelSize(R.dimen.activity_horizontal_margin);
        textView.setPadding(pad , pad , pad , pad );
        return new MyHolder(textView);
    }


    @Override
    public void onBindHolder(RecyclerView.ViewHolder holder, int position) {
        IntModel model = getItem(position);
        TextView textView = (TextView) holder.itemView;
        textView.setText("this is int adapter = " + model.id );
    }


    @Override
    public int getViewType(int position) {
        return 0;
    }
    
}

用法简单，具体参照MainActivity.
