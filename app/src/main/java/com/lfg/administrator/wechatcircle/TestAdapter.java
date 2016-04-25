package com.lfg.administrator.wechatcircle;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.List;

/**
 * Created by Administrator on 2016/4/17.
 */
public class TestAdapter extends BaseAdapter {
    private Context context;
    private List<String> list;

    public TestAdapter(Context context, List<String> list) {
        this.context = context;
        this.list = list;
    }

    @Override
    public int getCount() {
        return list.size();
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
    public View getView(final int position, View convertView, ViewGroup parent) {
        convertView=View.inflate(context, R.layout.mypulltorefresh_lv_item,null);
        TextView test=(TextView)convertView.findViewById(R.id.test_tv);
        test.setText(list.get(position));

        return convertView;
    }


}
