package com.ckt.ckttestassistant;

import android.content.Context;

import com.ckt.ckttestassistant.testitems.CktTestItem;
import com.ckt.ckttestassistant.testitems.TestItemBase;
import com.ckt.ckttestassistant.usecases.CktUseCase;
import com.ckt.ckttestassistant.usecases.UseCaseBase;
import com.ckt.ckttestassistant.utils.CktXmlHelper;
import com.ckt.ckttestassistant.utils.CktXmlHelper2;
import com.ckt.ckttestassistant.utils.FileUtils;

import java.util.ArrayList;

/**
 * Created by ckt on 18-1-26.
 */

public class UseCaseManager {
    private static final String FILENAME = "usecases.xml";
    private ArrayList<UseCaseBase> mAllUseCases = new ArrayList<UseCaseBase>();
    private ArrayList<UseCaseBase> mSelectedUseCases = new ArrayList<UseCaseBase>();
    private static Context mContext;
    private volatile static UseCaseManager instance;
    private CktXmlHelper2 mXmlHelper;

    private UseCaseManager(){
        System.out.println("Singleton has loaded");
    }
    public static UseCaseManager getInstance(Context context){
        if(instance==null){
            synchronized (UseCaseManager.class){
                if(context != null){
                    mContext = context;
                }
                if(instance==null){
                    instance=new UseCaseManager();
                }
            }
        }
        return instance;
    }
    boolean importUseCaseConfig(String path){
        return true;
    }

    boolean outputUseCaseConfig(String path){
        return true;
    }

    boolean addUseCaseToConfigureFile(UseCaseBase usecase){
        if(!mSelectedUseCases.contains(usecase)){
            mSelectedUseCases.add(usecase);
        }
        return true;
    }
    public void init(){
        //load from xml
        mXmlHelper = new CktXmlHelper2();
        /*CktXmlHelper.readxml(mContext, FILENAME, mAllUseCases);
        UseCaseBase usecase1 = new CktUseCase("test1 stub");
        usecase1.addTestItem(new CktTestItem());
        usecase1.addTestItem(new CktTestItem());
        usecase1.addTestItem(new CktTestItem());
        mAllUseCases.add(usecase1);
        UseCaseBase usecase2 = new CktUseCase("test2 stub");
        usecase2.addTestItem(new CktTestItem());
        usecase2.addTestItem(new CktTestItem());
        usecase2.addTestItem(new CktTestItem());
        mAllUseCases.add(usecase2);*/
    }

    boolean startExecute(){
        int size = 0;
        if(mSelectedUseCases == null || mSelectedUseCases.isEmpty()){
            return true;
        }
        for (int index = 0; index < mSelectedUseCases.size() - 1; index++){
            UseCaseBase tmp = mSelectedUseCases.get(index);
            tmp.setNextUseCase(mSelectedUseCases.get(index + 1));
        }
        mSelectedUseCases.get(mSelectedUseCases.size() - 1).setNextUseCase(null);
        mSelectedUseCases.get(0).execute();

        return true;
    }

    public void saveUseCaseToXml(Context context, ArrayList<TestItemBase> selectedTestItems, String name) {
        String path = context.getFilesDir()+"/usecases.xml";
        UseCaseBase uc = new CktUseCase(name);
        uc.setTestItems(selectedTestItems);
        mAllUseCases.add(uc);
        try {
            mXmlHelper.addUseCases(path, uc);
        }catch (Exception e){
            e.printStackTrace();
        }
        /*int id = mAllUseCases.size();
        CktUseCase usecase = new CktUseCase(id);
        if(FileUtils.isFileExists(path)){
            CktXmlHelper.updateXML(context, path, usecase);
        }else{
            CktXmlHelper.createXML(context, path, mAllUseCases);
        }*/
    }

    public ArrayList<UseCaseBase> getAllItems() {
        return mAllUseCases != null ? mAllUseCases : null;
    }

    public ArrayList<UseCaseBase> getSelectItems() {
        return mSelectedUseCases != null ? mSelectedUseCases : null;
    }
}
