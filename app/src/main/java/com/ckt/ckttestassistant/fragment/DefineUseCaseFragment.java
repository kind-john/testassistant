package com.ckt.ckttestassistant.fragment;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.ckt.ckttestassistant.R;
import com.ckt.ckttestassistant.TestCategory;
import com.ckt.ckttestassistant.UseCaseManager;
import com.ckt.ckttestassistant.adapter.CktItemDecoration;
import com.ckt.ckttestassistant.adapter.TestCategoryListAdapter;
import com.ckt.ckttestassistant.adapter.TestItemListAdapter;
import com.ckt.ckttestassistant.interfaces.OnItemClickListener;
import com.ckt.ckttestassistant.testitems.CktTestItem;
import com.ckt.ckttestassistant.testitems.Reboot;
import com.ckt.ckttestassistant.testitems.TestItemBase;
import com.ckt.ckttestassistant.testitems.WifiSwitchOn;
import com.ckt.ckttestassistant.usecases.UseCaseBase;
import com.ckt.ckttestassistant.utils.LogUtils;

import java.util.ArrayList;
import java.util.HashMap;

import static android.R.attr.id;

/**
 * Created by ckt on 18-1-30.
 */

public class DefineUseCaseFragment extends Fragment {
    private static final String TAG = "DefineUseCaseFragment";
    private Handler mHandler = null;
    private RecyclerView mTestCategoryList;
    private Context mContext;
    private ArrayList<TestCategory> mTestCategoryItems = new ArrayList<TestCategory>();
    private TestCategoryListAdapter mAdapter;
    private RecyclerView mTestItemList;
    private HashMap<String, ArrayList<TestItemBase>> mAllTestItems = new HashMap<String,ArrayList<TestItemBase>>();
    private ArrayList<TestItemBase> mSelectedTestItems = new ArrayList<TestItemBase>();
    private StringBuilder mShowPanelInfo = new StringBuilder();
    private String[] mTestCategory = {
        "camera",
        "switch",
        "system",
        "telecom",
        "launch app",
        "HW test",
        "current"
    };
    private String[][]mTestItems = {
        {
            "com.ckt.ckttestassistant.testitems.CameraCapture",
            "com.ckt.ckttestassistant.testitems.CameraContinuousCapture",
            "com.ckt.ckttestassistant.testitems.CameraEnterPhotoMode",
            "com.ckt.ckttestassistant.testitems.CameraEnterPreview",
            "com.ckt.ckttestassistant.testitems.CameraEnterVideoMode",
            "com.ckt.ckttestassistant.testitems.CameraFlashlight",
            "com.ckt.ckttestassistant.testitems.CameraFocus",
            "com.ckt.ckttestassistant.testitems.CameraHDR",
            "com.ckt.ckttestassistant.testitems.CameraPanorama",
            "com.ckt.ckttestassistant.testitems.CameraRecordVideo",
            "com.ckt.ckttestassistant.testitems.CameraSetBackground",
            "com.ckt.ckttestassistant.testitems.CameraSetFront",
            "com.ckt.ckttestassistant.testitems.CameraSettings",
            "com.ckt.ckttestassistant.testitems.CameraSwitchFrontBack",
            "com.ckt.ckttestassistant.testitems.CameraViewPhotos",
            "com.ckt.ckttestassistant.testitems.CameraZoomIn",
            "com.ckt.ckttestassistant.testitems.CameraZoomMax",
            "com.ckt.ckttestassistant.testitems.CameraZoomOut"
        },
        {
            "com.ckt.ckttestassistant.testitems.AirPlaneSwitchOff",
            "com.ckt.ckttestassistant.testitems.AirPlaneSwitchOn",
            "com.ckt.ckttestassistant.testitems.BTSwitchOff",
            "com.ckt.ckttestassistant.testitems.BTSwitchOn",
            "com.ckt.ckttestassistant.testitems.DataUsageSwitchOff",
            "com.ckt.ckttestassistant.testitems.DataUsageSwitchOn",
            "com.ckt.ckttestassistant.testitems.FlashlightSwitchOff",
            "com.ckt.ckttestassistant.testitems.FlashlightSwitchOn",
            "com.ckt.ckttestassistant.testitems.GpsSwitchOff",
            "com.ckt.ckttestassistant.testitems.GpsSwitchOn",
            "com.ckt.ckttestassistant.testitems.NFCSwitchOff",
            "com.ckt.ckttestassistant.testitems.NFCSwitchOn",
            "com.ckt.ckttestassistant.testitems.ScreenSaverSwitchOff",
            "com.ckt.ckttestassistant.testitems.ScreenSaverSwitchOn",
            "com.ckt.ckttestassistant.testitems.SharedNetSwitchOff",
            "com.ckt.ckttestassistant.testitems.SharedNetSwitchOn",
            "com.ckt.ckttestassistant.testitems.TorchSwitchOff",
            "com.ckt.ckttestassistant.testitems.TorchSwitchOn",
            "com.ckt.ckttestassistant.testitems.WifiSwitchOff",
            "com.ckt.ckttestassistant.testitems.WifiSwitchOn"
        },
        {
            "com.ckt.ckttestassistant.testitems.AutoScreenOff",
            "com.ckt.ckttestassistant.testitems.AutoScreenOn",
            "com.ckt.ckttestassistant.testitems.AutoUnlockScreen",
            "com.ckt.ckttestassistant.testitems.BrightnessSetting",
            "com.ckt.ckttestassistant.testitems.GetNetworkStatus",
            "com.ckt.ckttestassistant.testitems.GotoSleep",
            "com.ckt.ckttestassistant.testitems.RemoveRecentApp",
            "com.ckt.ckttestassistant.testitems.SimulateBackKey",
            "com.ckt.ckttestassistant.testitems.SimulateHomeKey",
            "com.ckt.ckttestassistant.testitems.SimulateMenuKey",
            "com.ckt.ckttestassistant.testitems.SimulatePowerKey",
            "com.ckt.ckttestassistant.testitems.WakeUp",
            "com.ckt.ckttestassistant.testitems.Reboot",
            "com.ckt.ckttestassistant.testitems.CheckMMIInformation"
        },
        {
            "com.ckt.ckttestassistant.testitems.AutoGenerateContacts",
            "com.ckt.ckttestassistant.testitems.AutoGenerateSMS",
            "com.ckt.ckttestassistant.testitems.ClickCallLog",
            "com.ckt.ckttestassistant.testitems.MMSAutoReceive",
            "com.ckt.ckttestassistant.testitems.MMSAutoSend",
            "com.ckt.ckttestassistant.testitems.ReadSms",
            "com.ckt.ckttestassistant.testitems.SMSAutoReceive",
            "com.ckt.ckttestassistant.testitems.SMSAutosend",
            "com.ckt.ckttestassistant.testitems.VoicecallAutoAnswer",
            "com.ckt.ckttestassistant.testitems.VoicecallAutoDial",
            "com.ckt.ckttestassistant.testitems.VoicecallAutoHangup"
        },
        {
            "com.ckt.ckttestassistant.testitems.LaunchBrowser",
            "com.ckt.ckttestassistant.testitems.LaunchCamera",
            "com.ckt.ckttestassistant.testitems.LaunchChrome",
            "com.ckt.ckttestassistant.testitems.LaunchContacts",
            "com.ckt.ckttestassistant.testitems.LaunchDialPlate",
            "com.ckt.ckttestassistant.testitems.LaunchEmail",
            "com.ckt.ckttestassistant.testitems.LaunchFacebook",
            "com.ckt.ckttestassistant.testitems.LaunchGallery",
            "com.ckt.ckttestassistant.testitems.LaunchGoogleMap",
            "com.ckt.ckttestassistant.testitems.LaunchMMITest",
            "com.ckt.ckttestassistant.testitems.LaunchSettings",
            "com.ckt.ckttestassistant.testitems.LaunchSMS",
            "com.ckt.ckttestassistant.testitems.LaunchTwitter"
        },
        {
            "com.ckt.ckttestassistant.testitems.CktTestItem",
            "com.ckt.ckttestassistant.testitems.CktTestItem",
            "com.ckt.ckttestassistant.testitems.CktTestItem",
            "com.ckt.ckttestassistant.testitems.CktTestItem",
            "com.ckt.ckttestassistant.testitems.CktTestItem"
        },
        {
            "com.ckt.ckttestassistant.testitems.CktTestItem",
            "com.ckt.ckttestassistant.testitems.CktTestItem",
            "com.ckt.ckttestassistant.testitems.CktTestItem",
            "com.ckt.ckttestassistant.testitems.CktTestItem",
            "com.ckt.ckttestassistant.testitems.CktTestItem"
        }

    };
    private int mCurrentType = 0;
    private Button mDeleteButton;
    private Button mSaveButton;
    private TextView mTestItemTextView;
    private UseCaseManager mUseCaseManager;
    private Activity mActivity;

    public void setHandler(Handler handler) {
        this.mHandler = handler;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        LogUtils.d(TAG, "onCreate");
        mActivity = getActivity();
        mContext = mActivity.getApplicationContext();
        mUseCaseManager = UseCaseManager.getInstance(mContext);
        mShowPanelInfo.append("test item : ");
        for (int i = 0; i < mTestCategory.length; i++){
            mTestCategoryItems.add(new TestCategory(mTestCategory[i]));
        }
        for (int j = 0; j < mTestItems.length; j++){
            ArrayList<TestItemBase> itemList = new ArrayList<TestItemBase>();
            for(int k = 0; k < mTestItems[j].length; k++){
                try {
                    Class tiClassName = Class.forName(mTestItems[j][k]);
                    // 实例化这个类
                    TestItemBase ti = (TestItemBase) tiClassName.newInstance();
                    ti.setContext(mContext);
                    itemList.add(ti);
                } catch (ClassNotFoundException e) {
                    e.printStackTrace();
                } catch (java.lang.InstantiationException e) {
                    e.printStackTrace();
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                }
            }
            mAllTestItems.put(mTestCategory[j], itemList);
        }
        /*CktTestItem item1 = new CktTestItem(mContext);
        item1.setTitle("ckt test item");
        itemList.add(item1);
        mAllTestItems.put(mTestCategory[0], itemList);

        ArrayList<TestItemBase> itemList1 = new ArrayList<TestItemBase>();

        CktTestItem item1 = new CktTestItem(mContext);
        item1.setTitle("ckt test item");
        itemList1.add(item1);
        mAllTestItems.put(mTestCategory[0], itemList1);

        ArrayList<TestItemBase> itemList2 = new ArrayList<TestItemBase>();
        WifiSwitchOn item2 = new WifiSwitchOn(mContext);
        item2.setTitle("wifi switch on");
        itemList2.clear();
        itemList2.add(item2);
        mAllTestItems.put(mTestCategory[1], itemList2);

        ArrayList<TestItemBase> itemList3 = new ArrayList<TestItemBase>();
        Reboot item3 = new Reboot(mContext);
        item3.setTitle("reboot");
        itemList3.clear();
        itemList3.add(item3);
        mAllTestItems.put(mTestCategory[2], itemList3);*/
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        LogUtils.d(TAG, "onCreateView");
        View rootView = inflater.inflate(R.layout.fragment_defineusecase_layout, container, false);
        mTestItemTextView = (TextView) rootView.findViewById(R.id.usecasetext);
        mTestItemTextView.setText(mShowPanelInfo.toString());
        mDeleteButton = (Button) rootView.findViewById(R.id.delete);
        mDeleteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //do something
                /*int start = mShowPanelInfo.lastIndexOf(">") - 1;
                int end = mShowPanelInfo.length();
                LogUtils.d(TAG, "start = "+start+ "; end = "+end);
                mShowPanelInfo.delete(start, end);*/
                if(mSelectedTestItems != null && !mSelectedTestItems.isEmpty()){
                    TestItemBase ti = mSelectedTestItems.get(mSelectedTestItems.size() - 1);
                    ti.setSN(-1);
                    mSelectedTestItems.remove(ti);
                }
                generateShowPanelString(mSelectedTestItems);
                mTestItemTextView.setText(mShowPanelInfo.toString());
            }
        });
        mSaveButton = (Button) rootView.findViewById(R.id.save);
        mSaveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //do something
                View dialogView = LayoutInflater.from(mContext).inflate(R.layout.usecase_title_setting, null);
                final EditText titleView = (EditText) dialogView.findViewById(R.id.titlesetting);
                titleView.setText("default name");
                AlertDialog.Builder builder = new AlertDialog.Builder(mActivity);
                builder.setTitle("set usecase title:")
                        .setView(dialogView)
                        .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                String title = titleView.getText().toString();
                                LogUtils.d(TAG, "usecase title = "+ title);
                                mUseCaseManager.addUsecaseToAllUseCaseXml(mSelectedTestItems, title);
                                if(mSelectedTestItems != null && !mSelectedTestItems.isEmpty()){
                                    mSelectedTestItems.clear();
                                    generateShowPanelString(mSelectedTestItems);
                                    mTestItemTextView.setText(mShowPanelInfo.toString());
                                }
                            }
                        })
                        .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                //do nothing
                            }
                        }).create().show();


            }
        });
        mTestCategoryList = (RecyclerView) rootView.findViewById(R.id.testcategorylist);
        mTestItemList = (RecyclerView) rootView.findViewById(R.id.testitemlist);
        mAdapter = new TestCategoryListAdapter(mContext, mTestCategoryItems);
        mAdapter.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(int pos) {
                LogUtils.d(TAG, "onItemClick");
                mCurrentType = pos;
                updateTestItemList();
            }
        });

        mTestCategoryList.setHasFixedSize(true);
        mTestCategoryList.setLayoutManager(new LinearLayoutManager(mContext));
        mTestCategoryList.addItemDecoration(new CktItemDecoration(mContext,
                LinearLayoutManager.VERTICAL,
                CktItemDecoration.DECORATION_TYPE_TESTCATEGORY));
        mTestCategoryList.setAdapter(mAdapter);
        initTestItemList();
        return rootView;
    }

    private void updateTestItemList() {
        LogUtils.d(TAG, "updateTestItemList");
        TestItemListAdapter adapter = new TestItemListAdapter(mAllTestItems.get(mTestCategory[mCurrentType]), mSelectedTestItems, true);
        adapter.setUpdateShowPanelListener(new TestItemListAdapter.UpdateShowPanelListener() {
            @Override
            public void updateShowPanel(ArrayList<TestItemBase> info) {
                setShowPanel(info);
            }
        });
        adapter.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(int pos) {
                TestItemBase ti = mAllTestItems.get(mTestCategory[mCurrentType]).get(pos);
                LogUtils.d(TAG, "test item title clicked :"+pos+" : "+ti.getClass().getName());
                ti.showPropertyDialog(mActivity, false);
            }
        });
        mTestItemList.setAdapter(adapter);
    }

    private void initTestItemList() {
        LogUtils.d(TAG, "initTestItemList");
        TestItemListAdapter adapter = new TestItemListAdapter(mAllTestItems.get(mTestCategory[mCurrentType]), mSelectedTestItems, true);
        adapter.setUpdateShowPanelListener(new TestItemListAdapter.UpdateShowPanelListener() {
            @Override
            public void updateShowPanel(ArrayList<TestItemBase> info) {
                setShowPanel(info);
            }
        });
        adapter.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(int pos) {
                TestItemBase ti = mAllTestItems.get(mTestCategory[mCurrentType]).get(pos);
                LogUtils.d(TAG, "test item title clicked :"+pos+" : "+ti.getClass().getName());
                ti.showPropertyDialog(mActivity, false);
            }
        });
        mTestItemList.setHasFixedSize(true);
        mTestItemList.setLayoutManager(new LinearLayoutManager(mContext));
        mTestItemList.addItemDecoration(new CktItemDecoration(mContext,
                LinearLayoutManager.VERTICAL,
                CktItemDecoration.DECORATION_TYPE_TESTCATEGORY_TESTITEM));
        mTestItemList.setAdapter(adapter);
    }

    public void setShowPanel(ArrayList<TestItemBase> selectItems){
        mSelectedTestItems = selectItems;
        if(mSelectedTestItems != null && !mSelectedTestItems.isEmpty()){
            int sn = mSelectedTestItems.size() - 1;
            LogUtils.d(TAG, "set TestItem SN : "+sn);
            mSelectedTestItems.get(mSelectedTestItems.size() - 1).setSN(sn);
        }

        generateShowPanelString(mSelectedTestItems);
        mTestItemTextView.setText(mShowPanelInfo.toString());
    }

    private void generateShowPanelString(ArrayList<TestItemBase> selectItems) {
        if (selectItems != null && !selectItems.isEmpty()){
            mShowPanelInfo.delete(12, mShowPanelInfo.length());
            for (int i = 0; i < selectItems.size(); i++){
                mShowPanelInfo.append(" > " + selectItems.get(i).getTitle());
                mShowPanelInfo.append(" x ");
                mShowPanelInfo.append(selectItems.get(i).getTimes());
            }
        } else {
            //处理最后一个删除不了的问题
            mShowPanelInfo.delete(12, mShowPanelInfo.length());
        }
    }
}
