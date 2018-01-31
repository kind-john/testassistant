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
    private TextView mUseCaseTextView;
    private Button mDeleteButton;
    private StringBuilder mShowPanelInfo = new StringBuilder();
    private Button mSaveButton;
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
        mUseCaseTextView = (TextView) findViewById(R.id.usecasetext);
        mUseCaseManager = UseCaseManager.getInstance(getApplicationContext());
        mUseCaseManager.init();
        mShowPanelInfo.append("use case : ");
        mDeleteButton = (Button) findViewById(R.id.delete);
        mDeleteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //do something
                /*int start = mShowPanelInfo.lastIndexOf(">") - 1;
                int end = mShowPanelInfo.length();
                LogUtils.d(TAG, "start = "+start+ "; end = "+end);
                mShowPanelInfo.delete(start, end);*/
                if(mSelectedTestItems != null){
                    mSelectedTestItems.remove(mSelectedTestItems.size() - 1);
                }
                generateShowPanelString(mSelectedTestItems);
                mUseCaseTextView.setText(mShowPanelInfo.toString());
            }
        });
        mSaveButton = (Button) findViewById(R.id.save);
        mSaveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //do something
                mUseCaseManager.saveUseCaseToXml(getApplicationContext(), mSelectedTestItems, "usecase");
            }
        });
        FragmentAdapter adapter = new FragmentAdapter(getSupportFragmentManager(), getTabTitles());
        viewPager.setAdapter(adapter);
        tabLayout.setupWithViewPager(viewPager);
    }



    public void setShowPanel(ArrayList<TestItemBase> selectItems){
        mSelectedTestItems = selectItems;
        generateShowPanelString(mSelectedTestItems);
        mUseCaseTextView.setText(mShowPanelInfo.toString());
    }

    private void generateShowPanelString(ArrayList<TestItemBase> selectItems) {
        if (selectItems != null && !selectItems.isEmpty()){
            mShowPanelInfo.delete(11, mShowPanelInfo.length());
            for (int i = 0; i < selectItems.size(); i++){
                mShowPanelInfo.append(" > " + selectItems.get(i).getTitle());
            }
        }
    }

    private String[] getTabTitles() {
        return getResources().getStringArray(R.array.tabTitles);
    }
}
