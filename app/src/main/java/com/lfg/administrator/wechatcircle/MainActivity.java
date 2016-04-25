package com.lfg.administrator.wechatcircle;

import android.app.Activity;
import android.os.Bundle;

import java.util.ArrayList;
import java.util.List;


public class MainActivity extends Activity {
    private MyPullToRefreshListView myPullToRefreshListView;
    private TestAdapter testAdapter;
    private List<String> date;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        init();
    }
    private void init(){
        myPullToRefreshListView=(MyPullToRefreshListView) findViewById(R.id.circle_lv);
        date=new ArrayList<>();
        for (int i=0;i<2;i++){
            date.add("item"+i);
        }
        testAdapter=new TestAdapter(this,date);
        myPullToRefreshListView.setAdapter(testAdapter);
        myPullToRefreshListView.setPullDownRefreshListener(new MyPullToRefreshListView.OnPullDownRefreshListener() {
            @Override
            public void onRefresh() {
                myPullToRefreshListView.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        for (int i=5;i<15;i++){
                            date.add("item"+i);
                        }
                        testAdapter.notifyDataSetChanged();
                        myPullToRefreshListView.pullDownRefreshCompleted();
                    }
                },3000);
            }
        });

    }
}
