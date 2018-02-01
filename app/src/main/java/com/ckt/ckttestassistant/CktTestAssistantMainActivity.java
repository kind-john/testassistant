package com.ckt.ckttestassistant;

import android.os.Handler;
import android.os.HandlerThread;
import android.os.Message;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.ckt.ckttestassistant.fragment.FragmentAdapter;
import com.ckt.ckttestassistant.testitems.TestItemBase;
import java.util.ArrayList;

public class CktTestAssistantMainActivity extends AppCompatActivity {

    private static final String TAG = "CktTestAssistantMainActivity";
    private ArrayList<TestItemBase> mSelectedTestItems;
    private UseCaseManager mUseCaseManager;

    public Handler mHandler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            //update UI
        }
    };
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ckt_test_assistant_main);

        TabLayout tabLayout = (TabLayout) findViewById(R.id.tab);
        ViewPager viewPager = (ViewPager) findViewById(R.id.fragment);
        mUseCaseManager = UseCaseManager.getInstance(getApplicationContext());
        mUseCaseManager.init();

        FragmentAdapter adapter = new FragmentAdapter(getSupportFragmentManager(), getTabTitles());
        viewPager.setAdapter(adapter);
        tabLayout.setupWithViewPager(viewPager);
    }

    private String[] getTabTitles() {
        return getResources().getStringArray(R.array.tabTitles);
    }
}
