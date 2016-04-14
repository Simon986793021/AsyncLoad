/**
 * 
 */
package com.alpha.imooc;

import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

/**
 * @author Simon
 */
public class MyBaseAdapter extends BaseAdapter {
    private final List<NewBean> mylist;
    private final LayoutInflater mInflater;
    private final ImageLoader imageLoader = new ImageLoader();

    public MyBaseAdapter(Context context, List<NewBean> mylist) {
        // TODO Auto-generated constructor stub
        this.mylist = mylist;
        mInflater = LayoutInflater.from(context);
    }

    /*
     * (non-Javadoc)
     * 
     * @see android.widget.Adapter#getCount()
     */

    @Override
    public int getCount() {
        // TODO Auto-generated method stub
        return mylist.size();
    }

    /*
     * (non-Javadoc)
     * 
     * @see android.widget.Adapter#getItem(int)
     */
    @Override
    public Object getItem(int position) {
        // TODO Auto-generated method stub
        return mylist.get(position);
    }

    /*
     * (non-Javadoc)
     * 
     * @see android.widget.Adapter#getItemId(int)
     */
    @Override
    public long getItemId(int position) {
        // TODO Auto-generated method stub
        return position;
    }

    /*
     * (non-Javadoc)
     * 
     * @see android.widget.Adapter#getView(int, android.view.View,
     * android.view.ViewGroup)
     */
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // TODO Auto-generated method stub
        ViewHolder viewHolder = null;
        /*
         * 首先讲下ListView的原理：ListView中的每一个Item显示都需要Adapter调用一次getView()的方法，
         * 这个方法会传入一个convertView的参数
         * ，这个方法返回的View就是这个Item显示的View。如果当Item的数量足够大，再为每一个Item都创建一个View对象
         * ，必将占用很多内存空间，即创建View对象（mInflater.inflate(R.layout.lv_item,
         * null);从xml中生成View
         * ，这是属于IO操作）是耗时操作，所以必将影响性能。Android提供了一个叫做Recycler(反复循环)
         * 的构件，就是当ListView的Item从滚出屏幕视角之外
         * ，对应Item的View会被缓存到Recycler中，相应的会从生成一个Item，
         * 而此时调用的getView中的convertView参数就是滚出屏幕的缓存Item的View
         * ，所以说如果能重用这个convertView，
         * 就会大大改善性能。当这个convertView不存在时，即第一次使用它，我们就创建一个item布局的View对象并赋给convertView
         * ，
         * 以后使用convertView时，只需从convertView中getTag取出来就可以，不需要再次创建item的布局对象了，这样便提高了性能
         * 。
         */
        if (convertView == null) {
            viewHolder = new ViewHolder();
            convertView = mInflater.inflate(R.layout.listview_item, null);
            viewHolder.tlTextView = (TextView) convertView
                    .findViewById(R.id.tv_course_title);
            viewHolder.ctTextView = (TextView) convertView
                    .findViewById(R.id.tv_course_detail);
            viewHolder.picImageView = (ImageView) convertView
                    .findViewById(R.id.iv_course_picture);
            convertView.setTag(viewHolder);// 将viewholder对象放入Tag中
        } else {
            viewHolder = (ViewHolder) convertView.getTag();// convertView 复用
        }

        /*
         * 我们都知道在getView()方法中的操作是这样的：先从xml中创建view对象（inflate操作，我们采用了重用convertView方法优化
         * ），然后在这个view去findViewById，找到每一个item的子View的控件对象，如：ImageView、TextView等。
         * 这里的findViewById操作是一个树查找过程
         * ，也是一个耗时的操作，所以这里也需要优化，就是使用ViewHolder，把每一个item的子View控件对象都放在Holder中
         * ，当第一次创建convertView对象时
         * ，便把这些item的子View控件对象findViewById实例化出来并保存到ViewHolder对象中
         * 。然后用convertView的setTag将viewHolder对象设置到Tag中，
         * 当以后加载ListView的item时便可以直接从Tag中取出复用ViewHolder对象中的
         * ，不需要再findViewById找item的子控件对象了
         */
        viewHolder.picImageView.setImageResource(R.drawable.ic_launcher);
        String urlString = mylist.get(position).picture;
        viewHolder.picImageView.setTag(urlString);
        // new ImageLoader().showImage(viewHolder.picImageView,
        // mylist.get(position).picture);
        imageLoader.showImageByAsyncTask(viewHolder.picImageView, urlString);
        viewHolder.tlTextView.setText(mylist.get(position).title);
        viewHolder.ctTextView.setText(mylist.get(position).content);
        return convertView;
    }

    class ViewHolder {
        public TextView tlTextView, ctTextView;
        public ImageView picImageView;
    }
}
