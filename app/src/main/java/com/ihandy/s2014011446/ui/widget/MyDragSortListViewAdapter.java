package com.ihandy.s2014011446.ui.widget;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.ihandy.s2014011446.R;

import java.util.List;

/**
 * Created by wuhanghang on 16-9-2.
 */

public class MyDragSortListViewAdapter extends BaseAdapter {

        private Context context;
        List<String> items;//适配器的数据源


        public MyDragSortListViewAdapter(Context context,List<String> list){
            this.context = context;
            this.items = list;
        }

        @Override
        public int getCount() {
            return items.size();
        }

        @Override
        public Object getItem(int arg0) {
            return items.get(arg0);
        }

        @Override
        public long getItemId(int arg0) {
            return arg0;
        }

        public void remove(int arg0) {
            items.remove(arg0);
            this.notifyDataSetChanged();//不要忘记更改适配器对象的数据源
        }

        public void insert(String item, int arg0) {
            items.add(arg0, item);
            this.notifyDataSetChanged();
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            String item = (String)getItem(position);
            ViewHolder viewHolder;
            if(convertView==null){
                viewHolder = new ViewHolder();
                convertView = LayoutInflater.from(context).inflate(R.layout.item_category_management, null);
                viewHolder.itemCategoryName = (TextView) convertView.findViewById(R.id.item_category_name);
                convertView.setTag(viewHolder);
            }else{
                viewHolder = (ViewHolder) convertView.getTag();
            }

            viewHolder.itemCategoryName.setText(items.get(position));
            return convertView;
        }

        class ViewHolder {
            TextView itemCategoryName;
        }
    }

