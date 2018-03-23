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
import android.text.Editable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.ckt.ckttestassistant.R;
import com.ckt.ckttestassistant.TestBase;
import com.ckt.ckttestassistant.TestCategory;
import com.ckt.ckttestassistant.UseCaseManager;
import com.ckt.ckttestassistant.adapter.CktItemDecoration;
import com.ckt.ckttestassistant.adapter.TestCategoryListAdapter;
import com.ckt.ckttestassistant.adapter.TestItemListAdapter;
import com.ckt.ckttestassistant.interfaces.OnItemClickListener;
import com.ckt.ckttestassistant.testitems.EndTagItem;
import com.ckt.ckttestassistant.testitems.StartTagItem;
import com.ckt.ckttestassistant.testitems.TestItemBase;
import com.ckt.ckttestassistant.utils.CktXmlHelper;
import com.ckt.ckttestassistant.utils.LogUtils;
import com.ckt.ckttestassistant.utils.MyConstants;
import com.ckt.ckttestassistant.utils.ToastHelper;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by ckt on 18-1-30.
 */

public class DefineUseCaseFragment extends Fragment implements View.OnClickListener{
    private static final String TAG = "DefineUseCaseFragment";
    private Context mContext;
    private ArrayList<TestCategory> mTestCategoryItems = new ArrayList<TestCategory>();
    private RecyclerView mTestItemList;
    private HashMap<String, ArrayList<TestBase>> mAllTestItems = new HashMap<String,ArrayList<TestBase>>();
    private ArrayList<TestBase> mSelectedTestItems = new ArrayList<TestBase>();
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
            //"com.ckt.ckttestassistant.testitems.CameraContinuousCapture",
            //"com.ckt.ckttestassistant.testitems.CameraEnterPhotoMode",
            //"com.ckt.ckttestassistant.testitems.CameraEnterPreview",
            //"com.ckt.ckttestassistant.testitems.CameraEnterVideoMode",
            "com.ckt.ckttestassistant.testitems.CameraFlashlight",
            "com.ckt.ckttestassistant.testitems.CameraFocus",
            "com.ckt.ckttestassistant.testitems.CameraHDR",
            //"com.ckt.ckttestassistant.testitems.CameraPanorama",
            "com.ckt.ckttestassistant.testitems.CameraRecordVideo",
            //"com.ckt.ckttestassistant.testitems.CameraSetBackground",
            //"com.ckt.ckttestassistant.testitems.CameraSetFront",
            "com.ckt.ckttestassistant.testitems.CameraSettings",
            "com.ckt.ckttestassistant.testitems.CameraSwitchFrontBack",
            "com.ckt.ckttestassistant.testitems.CameraViewPhotos",
            //"com.ckt.ckttestassistant.testitems.CameraZoomIn",
            //"com.ckt.ckttestassistant.testitems.CameraZoomMax",
            //"com.ckt.ckttestassistant.testitems.CameraZoomOut"
        },
        {
            "com.ckt.ckttestassistant.testitems.AirPlaneSwitchOff",
            "com.ckt.ckttestassistant.testitems.AirPlaneSwitchOn",
            "com.ckt.ckttestassistant.testitems.BTSwitchOff",
            "com.ckt.ckttestassistant.testitems.BTSwitchOn",
            "com.ckt.ckttestassistant.testitems.DataUsageSwitchOff",
            "com.ckt.ckttestassistant.testitems.DataUsageSwitchOn",
            //"com.ckt.ckttestassistant.testitems.FlashlightSwitchOff",
            //"com.ckt.ckttestassistant.testitems.FlashlightSwitchOn",
            "com.ckt.ckttestassistant.testitems.GpsSwitchOff",
            "com.ckt.ckttestassistant.testitems.GpsSwitchOn",
            //"com.ckt.ckttestassistant.testitems.NFCSwitchOff",
            //"com.ckt.ckttestassistant.testitems.NFCSwitchOn",
            //"com.ckt.ckttestassistant.testitems.ScreenSaverSwitchOff",
            //"com.ckt.ckttestassistant.testitems.ScreenSaverSwitchOn",
            //"com.ckt.ckttestassistant.testitems.SharedNetSwitchOff",
            //"com.ckt.ckttestassistant.testitems.SharedNetSwitchOn",
            //"com.ckt.ckttestassistant.testitems.TorchSwitchOff",
            //"com.ckt.ckttestassistant.testitems.TorchSwitchOn",
            "com.ckt.ckttestassistant.testitems.WifiSwitchOff",
            "com.ckt.ckttestassistant.testitems.WifiSwitchOn"
        },
        {
            //"com.ckt.ckttestassistant.testitems.AutoScreenOff",
            //"com.ckt.ckttestassistant.testitems.AutoScreenOn",
            "com.ckt.ckttestassistant.testitems.AutoUnlockScreen",
            //"com.ckt.ckttestassistant.testitems.BrightnessSetting",
            "com.ckt.ckttestassistant.testitems.GetNetworkStatus",
            //"com.ckt.ckttestassistant.testitems.GotoSleep",
            //"com.ckt.ckttestassistant.testitems.RemoveRecentApp",
            "com.ckt.ckttestassistant.testitems.SimulateBackKey",
            "com.ckt.ckttestassistant.testitems.SimulateHomeKey",
            "com.ckt.ckttestassistant.testitems.SimulateMenuKey",
            "com.ckt.ckttestassistant.testitems.SimulatePowerKey",
            "com.ckt.ckttestassistant.testitems.SimulateVolumeDownKey",
            "com.ckt.ckttestassistant.testitems.SimulateVolumeUpKey",
            //"com.ckt.ckttestassistant.testitems.WakeUp",
            "com.ckt.ckttestassistant.testitems.Reboot",
            //"com.ckt.ckttestassistant.testitems.CheckMMIInformation"
        },
        {
            //"com.ckt.ckttestassistant.testitems.AutoGenerateContacts",
            //"com.ckt.ckttestassistant.testitems.AutoGenerateSMS",
            //"com.ckt.ckttestassistant.testitems.ClickCallLog",
            //"com.ckt.ckttestassistant.testitems.MMSAutoReceive",
            //"com.ckt.ckttestassistant.testitems.MMSAutoSend",
            //"com.ckt.ckttestassistant.testitems.ReadSms",
            //"com.ckt.ckttestassistant.testitems.SMSAutoReceive",
            //"com.ckt.ckttestassistant.testitems.SMSAutosend",
            //"com.ckt.ckttestassistant.testitems.VoicecallAutoAnswer",
            //"com.ckt.ckttestassistant.testitems.VoicecallAutoDial",
            //"com.ckt.ckttestassistant.testitems.VoicecallAutoHangup"
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

        },
        {

        }

    };
    private int mCurrentType = 0;
    private TextView mTestItemTextView;
    private UseCaseManager mUseCaseManager;
    private Activity mActivity;
    private RecyclerView mTestCategoryList;
    private TextView mDivider;
    private TextView mEmptyView;
    private static volatile int mTagLevel = 1;

    public void setHandler(Handler handler) {
        Handler mHandler = handler;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        LogUtils.d(TAG, "onCreate");
        mActivity = getActivity();
        mContext = mActivity.getApplicationContext();
        mUseCaseManager = UseCaseManager.getInstance(mContext, mActivity);
        for (int i = 0; i < mTestCategory.length; i++){
            mTestCategoryItems.add(new TestCategory(mTestCategory[i]));
        }
        for (int j = 0; j < mTestItems.length; j++){
            ArrayList<TestBase> itemList = new ArrayList<TestBase>();
            for(int k = 0; k < mTestItems[j].length; k++){
                try {
                    Class tiClassName = Class.forName(mTestItems[j][k]);
                    // 实例化这个类
                    TestItemBase ti = (TestItemBase) tiClassName.newInstance();
                    ti.setContext(mContext);
                    ti.setActivity(mActivity);
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
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.delete:
                if(mSelectedTestItems != null && !mSelectedTestItems.isEmpty()){
                    TestBase ti = mSelectedTestItems.get(mSelectedTestItems.size() - 1);
                    if (ti instanceof StartTagItem) {
                        mTagLevel--;
                    } else if (ti instanceof EndTagItem) {
                        mTagLevel++;
                    }
                    ti.setSN(-1);
                    mSelectedTestItems.remove(ti);
                }
                generateShowPanelString(mSelectedTestItems);
                break;
            case R.id.save:
                if (mSelectedTestItems == null || mSelectedTestItems.isEmpty()) {
                    ToastHelper.showToast(mContext, R.string.item_is_null, Toast.LENGTH_SHORT);
                }
                View dialogView = LayoutInflater.from(mContext).inflate(R.layout.usecase_title_setting, null);
                final EditText titleView = (EditText) dialogView.findViewById(R.id.titlesetting);
                titleView.setText(R.string.default_uc_name);
                titleView.setSelection(titleView.getText().length());
                AlertDialog.Builder builder = new AlertDialog.Builder(mActivity);
                builder.setTitle(R.string.set_uc_name)
                        .setView(dialogView)
                        .setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                if (checkSelectedItemValide()) {
                                    String title = titleView.getText().toString();
                                    LogUtils.d(TAG, "usecase title = "+ title);
                                    mUseCaseManager.addUsecaseToAllUseCaseXml(mSelectedTestItems, title);
                                    if(mSelectedTestItems != null && !mSelectedTestItems.isEmpty()){
                                        mSelectedTestItems.clear();
                                        generateShowPanelString(mSelectedTestItems);
                                    }
                                } else {
                                    // show toast
                                    ToastHelper.showToast(mContext, R.string.item_invalide, Toast.LENGTH_SHORT);
                                }
                            }
                        })
                        .setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                //do nothing
                            }
                        }).create().show();
                break;
            case R.id.insertStart:
                if (mTagLevel < CktXmlHelper.MAX_LEVEL) {
                    mTagLevel++;
                    mSelectedTestItems.add(new StartTagItem());
                    generateShowPanelString(mSelectedTestItems);
                } else {
                    ToastHelper.showToast(mContext, R.string.tag_insert_error, Toast.LENGTH_LONG);
                }
                break;
            case R.id.insertEnd:
                if (mTagLevel > 1) {
                    AlertDialog.Builder tagBuilder = new AlertDialog.Builder(mActivity);
                    View tagView = LayoutInflater.from(mActivity).inflate(R.layout.settings_usecase_times_layout, null);
                    final EditText timesEditText = (EditText) tagView.findViewById(R.id.times);
                    timesEditText.setText("1");
                    timesEditText.setSelection(timesEditText.getText().length());
                    tagBuilder.setTitle(R.string.set_tag_times)
                            .setView(tagView)
                            .setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    Editable ea = timesEditText.getText();
                                    if (ea == null){
                                        // do nothing
                                    } else {
                                        if (mSelectedTestItems != null &&
                                                mSelectedTestItems.size() > 1 &&
                                                !(mSelectedTestItems.get(mSelectedTestItems.size() - 1) instanceof StartTagItem) &&
                                                !(mSelectedTestItems.get(mSelectedTestItems.size() - 1) instanceof EndTagItem)) {
                                            mTagLevel--;
                                            EndTagItem ett = new EndTagItem();
                                            ett.setTimes(Integer.parseInt(timesEditText.getText().toString()));
                                            mSelectedTestItems.add(ett);
                                            generateShowPanelString(mSelectedTestItems);
                                        } else {
                                            ToastHelper.showToast(mContext, R.string.uc_must_has_chilren, Toast.LENGTH_LONG);
                                        }
                                    }
                                }
                            })
                            .setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {

                                }
                            }).create().show();

                } else {
                    ToastHelper.showToast(mContext, R.string.tag_insert_error, Toast.LENGTH_LONG);
                }
                break;
            default:
                break;
        }
    }

    private boolean checkSelectedItemValide() {
        if (mSelectedTestItems != null && !mSelectedTestItems.isEmpty()) {
            int startTagCount = 0;
            int endTagCount = 0;
            for (TestBase tb : mSelectedTestItems) {
                if (tb instanceof StartTagItem) {
                    startTagCount++;
                } else if (tb instanceof EndTagItem) {
                    endTagCount++;
                }
            }
            if (startTagCount == endTagCount) {
                return true;
            }
        }
        return false;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        LogUtils.d(TAG, "onCreateView");
        View rootView = inflater.inflate(R.layout.fragment_defineusecase_layout, container, false);
        initViews(rootView);

        generateShowPanelString(mSelectedTestItems);
        initTestCategoryListFocus(mTestCategoryItems);
        TestCategoryListAdapter adapter = new TestCategoryListAdapter(mContext, mTestCategoryItems);
        adapter.setOnItemClickListener(new OnItemClickListener() {
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
        mTestCategoryList.setAdapter(adapter);
        if (mTestCategoryItems == null || mTestCategoryItems.isEmpty()) {
            showEmptyView();
        } else {
            hideEmptyView();
        }
        initTestItemList();
        return rootView;
    }

    private void hideEmptyView() {
        mEmptyView.setVisibility(View.GONE);
        mTestCategoryList.setVisibility(View.VISIBLE);
        mTestItemList.setVisibility(View.VISIBLE);
        mDivider.setVisibility(View.VISIBLE);
    }

    private void showEmptyView() {
        mEmptyView.setVisibility(View.VISIBLE);
        mTestCategoryList.setVisibility(View.GONE);
        mTestItemList.setVisibility(View.GONE);
        mDivider.setVisibility(View.GONE);
    }

    private void initViews(View rootView) {
        mTestItemTextView = (TextView) rootView.findViewById(R.id.testitemtext);
        Button deleteButton = (Button) rootView.findViewById(R.id.delete);
        Button saveButton = (Button) rootView.findViewById(R.id.save);
        Button insertStartButton = (Button) rootView.findViewById(R.id.insertStart);
        Button insertEndButton = (Button) rootView.findViewById(R.id.insertEnd);
        deleteButton.setOnClickListener(this);
        saveButton.setOnClickListener(this);
        insertStartButton.setOnClickListener(this);
        insertEndButton.setOnClickListener(this);
        mTestCategoryList = (RecyclerView) rootView.findViewById(R.id.categorylist);
        mTestItemList = (RecyclerView) rootView.findViewById(R.id.testitemlist);
        mDivider = (TextView) rootView.findViewById(R.id.divider);
        mEmptyView = (TextView) rootView.findViewById(R.id.emptyview);
    }

    private void initTestCategoryListFocus(ArrayList<TestCategory> tcs) {
        if(tcs != null && !tcs.isEmpty()){
            TestCategory tc;
            for (int i = 0; i< tcs.size(); i++){
                tc = tcs.get(i);
                if(i == 0){
                    LogUtils.d(TAG, "initTestCategoryListFocus : 0");
                    tc.setChecked(true);
                }else{
                    LogUtils.d(TAG, "initTestCategoryListFocus : "+i);
                    tc.setChecked(false);
                }
            }
        }
    }
    private void updateTestItemList() {
        LogUtils.d(TAG, "updateTestItemList");
        TestItemListAdapter adapter = new TestItemListAdapter(mAllTestItems.get(mTestCategory[mCurrentType]), mSelectedTestItems, true);
        adapter.setUpdateShowPanelListener(new TestItemListAdapter.UpdateShowPanelListener() {
            @Override
            public void updateShowPanel(ArrayList<TestBase> info) {
                setShowPanel(info);
            }
        });
        adapter.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(int pos) {
                TestItemBase ti = (TestItemBase)mAllTestItems.get(mTestCategory[mCurrentType]).get(pos);
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
            public void updateShowPanel(ArrayList<TestBase> info) {
                setShowPanel(info);
            }
        });
        adapter.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(int pos) {
                TestItemBase ti = (TestItemBase)mAllTestItems.get(mTestCategory[mCurrentType]).get(pos);
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

    public void setShowPanel(ArrayList<TestBase> selectItems){
        mSelectedTestItems = selectItems;
        if(mSelectedTestItems != null && !mSelectedTestItems.isEmpty()){
            int sn = mSelectedTestItems.size() - 1;
            //LogUtils.d(TAG, "set TestItem SN : "+sn);
            //mSelectedTestItems.get(mSelectedTestItems.size() - 1).setSN(sn);
        }

        generateShowPanelString(mSelectedTestItems);
    }

    private void generateShowPanelString(ArrayList<TestBase> selectItems) {
        mShowPanelInfo.delete(0, mShowPanelInfo.length());
        String StartTag = getResources().getString(R.string.testItemStartTag);
        mShowPanelInfo.append(StartTag);
        if (selectItems != null && !selectItems.isEmpty()){
            mShowPanelInfo.delete(StartTag.length(), mShowPanelInfo.length());
            for (int i = 0; i < selectItems.size(); i++){
                TestBase ti = selectItems.get(i);
                if (ti instanceof StartTagItem) {
                    mShowPanelInfo.append(" (");
                } else if (ti instanceof EndTagItem) {
                    mShowPanelInfo.delete(mShowPanelInfo.length() - MyConstants.ITEM_DIVIDER_STRING.length(), mShowPanelInfo.length());
                    mShowPanelInfo.append(")");
                    mShowPanelInfo.append(" x ");
                    mShowPanelInfo.append(ti.getTimes());
                } else {
                    mShowPanelInfo.append(ti.getTitle());
                    mShowPanelInfo.append(" x ");
                    mShowPanelInfo.append(ti.getTimes());
                    if (i < selectItems.size() - 1) {
                        mShowPanelInfo.append(MyConstants.ITEM_DIVIDER_STRING);
                    }
                }
            }
        } else {
            //处理最后一个删除不了的问题
            mShowPanelInfo.delete(StartTag.length(), mShowPanelInfo.length());
        }
        mTestItemTextView.setText(mShowPanelInfo.toString());
    }
}
