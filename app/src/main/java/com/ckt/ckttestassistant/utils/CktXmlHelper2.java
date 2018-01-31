package com.ckt.ckttestassistant.utils;

import android.util.Xml;

import com.ckt.ckttestassistant.testitems.CktTestItem;
import com.ckt.ckttestassistant.testitems.TestItemBase;
import com.ckt.ckttestassistant.usecases.CktUseCase;
import com.ckt.ckttestassistant.usecases.UseCaseBase;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlSerializer;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;

/**
 * Created by ckt on 18-1-31.
 */

public class CktXmlHelper2 {
    private static final String TAG = "CktXmlHelper2";

    public void getUseCases(String path, ArrayList<UseCaseBase> allUseCases){
        try {
            InputStream is = new FileInputStream(path);
            XmlPullParser parser = Xml.newPullParser();
            parser.setInput(is, "utf-8");
            int eventtype = parser.getEventType();// 产生第一个事件
            UseCaseBase usecase = null;
            TestItemBase testitem = null;
            StringBuffer str1 = new StringBuffer();
            StringBuffer str2 = new StringBuffer();
            while (eventtype != XmlPullParser.END_DOCUMENT) {
                switch (eventtype) {
                    case XmlPullParser.START_DOCUMENT:// 判断当前事件是否为文档开始事件
                        LogUtils.d(TAG, "START_DOCUMENT");
                        break;
                    case XmlPullParser.START_TAG:// 判断当前事件是否为标签元素开始事件
                        String name = parser.getName();
                        if (name.equals("usecases")) {

                        }
                        if (name.equals("usecase")) { // 判断开始标签元素是否是student
                            int id = Integer.parseInt(parser.getAttributeValue(0));
                            LogUtils.d(TAG, "usecase id : " + id);
                            String title = parser.nextText();
                            String isChecked = parser.nextText();
                            int total = Integer.parseInt(parser.nextText());
                            usecase = new CktUseCase(id);//do something
                        }
                        if (usecase != null) {
                            if (name.equals("testitem")) {
                                testitem = new CktTestItem();
                                int id2 = Integer.parseInt(parser.getAttributeValue(0));
                                testitem.setID(id2);
                                if(name.equals("title")){
                                    testitem.setTitle(parser.nextText());
                                }
                            }
                        }
                        break;

                    case XmlPullParser.END_TAG:
                        if (parser.getName().equals("usecase")) {
                            allUseCases.add(usecase);
                            usecase = null;
                        }
                        if (parser.getName().equals("testitem")) {
                            usecase.addTestItem(testitem);
                            testitem = null;
                        }
                        break;

                    default:
                        break;
                }
                eventtype = parser.next();// 不断的去更新，持续的解析XML文件直到文件的尾部。
            }
        } catch (IOException e) {
            e.printStackTrace();
        } catch (XmlPullParserException e) {
            e.printStackTrace();
        }
    }


    public void addUseCases(String path, UseCaseBase usecase) throws Exception{
        ArrayList<UseCaseBase> usecases = new ArrayList<UseCaseBase>();
        //1、先解析xml数据
        File file=new File(path);
        try {
            getUseCases(path, usecases);
        } catch (Exception e) {
            e.printStackTrace();
        }
        //2、添加入新的数据person
        usecases.add(usecase);
        //3、生成新的XML
        file.delete();
        NewXML(path, usecases);
    }
    /**
     * 删除数据
     * @param id
     * @param path
     * @throws Exception
     */
    public void deletePerson(int id,String path) throws Exception{
        ArrayList<UseCaseBase> usecases = new ArrayList<UseCaseBase>();
        //1、先解析xml数据
        File file=new File(path);
        try {
            getUseCases(path, usecases);
        } catch (Exception e) {
            e.printStackTrace();
        }
        //2、删除person
        UseCaseBase tmp = null;
        for (UseCaseBase usecase:usecases) {
            if(usecase.getID()==id){
                tmp=usecase;
            }
        }
        usecases.remove(tmp);
        //3、生成新的XML
        file.delete();
        NewXML(path, usecases);
    }

    public void updatePerson(UseCaseBase uc,String path) throws Exception{
        ArrayList<UseCaseBase> usecases = new ArrayList<UseCaseBase>();
        //1、先解析xml数据
        File file=new File(path);
        try {
            getUseCases(path, usecases);
        } catch (Exception e) {
            e.printStackTrace();
        }
        //2、删除person
        UseCaseBase tmp = null;
        for (UseCaseBase usecase:usecases) {
            if(usecase.getID()==uc.getID()){
                tmp=usecase;
            }
        }
        usecases.remove(tmp);
        usecases.add(uc);
        //生成新的XML
        file.delete();
        NewXML(path, usecases);
    }

    public void NewXML(String path, ArrayList<UseCaseBase> ucs) throws Exception{

        File xmlFile = new File(path);
        FileOutputStream outStream = new FileOutputStream(xmlFile);

        XmlSerializer serializer = Xml.newSerializer();
        serializer.setOutput(outStream, "UTF-8");
        serializer.startDocument("UTF-8", true);
        serializer.startTag(null, "usecases");
        for(UseCaseBase uc : ucs){
            serializer.startTag(null, "usecase");
            serializer.attribute(null, "id", uc.getID()+"");

            serializer.startTag(null, "title");
            serializer.text(uc.getTitle()+"");
            serializer.endTag(null, "title");

            serializer.startTag(null, "times");
            serializer.text(uc.getTimes()+"");
            serializer.endTag(null, "times");

            for(TestItemBase ti : uc.getTestItems()){
                serializer.startTag(null, "testitem");
                serializer.attribute(null, "id", ti.getID()+"");

                serializer.startTag(null, "title");
                serializer.text(ti.getTitle()+"");
                serializer.endTag(null, "title");

                serializer.startTag(null, "times");
                serializer.text(uc.getTitle()+"");
                serializer.endTag(null, "times");

                serializer.endTag(null, "testitem");
            }

            serializer.endTag(null, "usecase");
        }
        serializer.endTag(null, "usecases");
        serializer.endDocument();
        outStream.flush();
        outStream.close();
    }
}
