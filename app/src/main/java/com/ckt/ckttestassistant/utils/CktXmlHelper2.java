package com.ckt.ckttestassistant.utils;

import android.util.Xml;

import com.ckt.ckttestassistant.testitems.CktTestItem;
import com.ckt.ckttestassistant.testitems.TestItemBase;
import com.ckt.ckttestassistant.usecases.CktUseCase;
import com.ckt.ckttestassistant.usecases.UseCaseBase;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlSerializer;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

/**
 * Created by ckt on 18-1-31.
 */

public class CktXmlHelper2 {
    private static final String TAG = "CktXmlHelper2";
    private static int allUseCaseMaxID = -1;
    public int getMaxId(String fileName, String code, String idnum) {
        int num = 0;
        try {
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            DocumentBuilder bulider = factory.newDocumentBuilder();
            Document doc = bulider.parse(fileName);
            doc.normalize();

            NodeList listnode = doc.getElementsByTagName(code);
            for (int i = 0; i < listnode.getLength(); i++) {
                Element elink = (Element) listnode.item(i);
                String id = elink.getAttribute(idnum);
                if (Integer.valueOf(id) > num) {
                    num = Integer.valueOf(id);
                }
            }
            return num;
        } catch (ParserConfigurationException e) {
            e.printStackTrace();
            return num;
        } catch (SAXException e) {
            e.printStackTrace();
            return num;
        } catch (IOException e) {
            e.printStackTrace();
            return num;
        }
    }
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
                        LogUtils.d(TAG,"XmlPullParser.START_TAG name:"+name);

                        if (name.equals("usecase")) { // 判断开始标签元素是否是student
                            int id = Integer.parseInt(parser.getAttributeValue(0));
                            String className = parser.getAttributeValue(1);
                            LogUtils.d(TAG, "usecase id : " + id + "; className = " + className);
                            int whichfile = whichXmlFile(path);
                            if(whichfile == 0){
                                if(allUseCaseMaxID < id){
                                    allUseCaseMaxID = id;
                                }
                            }
                            if (id >= 0) {
                                try {
                                    // 根据给定的类名初始化类
                                    Class catClass = Class.forName(className);
                                    // 实例化这个类
                                    usecase = (UseCaseBase) catClass.newInstance();

                                }catch (IllegalAccessException e) {
                                    e.printStackTrace();
                                } catch (InstantiationException e) {
                                    e.printStackTrace();
                                } catch (ClassNotFoundException e) {
                                    e.printStackTrace();
                                }
                            } else {
                                LogUtils.e(TAG, "error: id < -1 ,from "+path);
                            }
                        }else if(name.equals("title")){
                            String title = parser.nextText();
                            LogUtils.d(TAG, "title : " + title);

                            if(testitem != null){
                                LogUtils.d(TAG, "testitem title : " + title);
                                testitem.setTitle(title);
                            }else if(usecase != null){
                                LogUtils.d(TAG, "usecase title : " + title);
                                usecase.setTitle(title);
                            }
                        }else if(name.equals("times")){
                            int times = Integer.parseInt(parser.nextText());
                            LogUtils.d(TAG, "times : " + times);

                            if(testitem != null){
                                LogUtils.d(TAG, "testitem times : " + times);
                                testitem.setTimes(times);
                            }else if(usecase != null){
                                LogUtils.d(TAG, "usecase times : " + times);
                                usecase.setTimes(times);
                            }

                        }else if(name.equals("selected")){
                            boolean isChecked = Boolean.parseBoolean(parser.nextText());
                            LogUtils.d(TAG, "usecase isChecked : " + isChecked);
                            if(usecase != null){
                                usecase.setIsChecked(isChecked);
                            }
                        }else if(name.equals("testitem")){
                            if (usecase != null) {
                                int id2 = Integer.parseInt(parser.getAttributeValue(0));
                                String className2 = parser.getAttributeValue(1);
                                LogUtils.d(TAG, "testitem id : " + id2+"; className = "+className2);
                                try {
                                    // 根据给定的类名初始化类
                                    Class catClass = Class.forName(className2);
                                    // 实例化这个类
                                    testitem = (TestItemBase) catClass.newInstance();
                                    testitem.setID(id2);
                                }catch (IllegalAccessException e) {
                                    e.printStackTrace();
                                } catch (InstantiationException e) {
                                    e.printStackTrace();
                                } catch (ClassNotFoundException e) {
                                    e.printStackTrace();
                                }

                            }
                        }else if(name.equals("delay")){
                            int delay = Integer.parseInt(parser.nextText());
                            LogUtils.d(TAG, "delay : " + delay);
                            if(testitem != null){
                                LogUtils.d(TAG, "testitem delay : " + delay);
                                testitem.setTimes(delay);
                            }else if(usecase != null){
                                LogUtils.d(TAG, "usecase delay : " + delay);
                                usecase.setTimes(delay);
                            }
                        }else if(name.equals("total")){
                            int total = Integer.parseInt(parser.nextText());
                            LogUtils.d(TAG, "total : " + total);
                            if(testitem != null){
                                LogUtils.d(TAG, "testitem total : " + total);
                                testitem.setTimes(total);
                            }else if(usecase != null){
                                LogUtils.d(TAG, "usecase total : " + total);
                                usecase.setTimes(total);
                            }
                        }else{
                            LogUtils.e(TAG, "error: some new tag has not parser!!! name = " + name);
                        }
                        break;

                    case XmlPullParser.END_TAG:
                        String endName = parser.getName();
                        LogUtils.d(TAG,"XmlPullParser.END_TAG name:"+endName);
                        if (endName.equals("usecase")) {
                            allUseCases.add(usecase);
                            usecase = null;
                        }else if (endName.equals("testitem")) {
                            usecase.addTestItem(testitem);
                            testitem = null;
                        }else{
                            LogUtils.d(TAG, "ignored endName = " + endName);
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

    public int whichXmlFile(String path){
        int result = 0;
        String sub = path.substring(path.lastIndexOf("/") + 1);
        LogUtils.d(TAG, "whichXmlFile sub = "+sub);
        if(sub.equals("selected_usecases.xml")){
            result = 1;
        } else if(sub.equals("usecases.xml")){
            result = 0;
        }
        LogUtils.d(TAG, "whichXmlFile : "+result);
        return result;
    }

    public void NewXML(String path, ArrayList<UseCaseBase> ucs) throws Exception{
        File xmlFile = new File(path);
        FileOutputStream outStream = new FileOutputStream(xmlFile);

        XmlSerializer serializer = Xml.newSerializer();
        serializer.setOutput(outStream, "UTF-8");
        serializer.startDocument("UTF-8", true);
        serializer.startTag(null, "usecases");
        int usecaseID = -1;
        for(UseCaseBase uc : ucs){
            serializer.startTag(null, "usecase");
            usecaseID = uc.getID();
            LogUtils.d(TAG, "usecaseID="+usecaseID);
            if(usecaseID == -1){
                //自定义用例添加时自动生成ID
                int whichfile = whichXmlFile(path);
                if(whichfile == 0){
                    LogUtils.d(TAG, "all maxId="+allUseCaseMaxID);
                    usecaseID = allUseCaseMaxID + 1;
                }else{
                    LogUtils.e(TAG, "error: usecaseID == -1 !!!");
                }
            }
            serializer.attribute(null, "id", String.valueOf(usecaseID));
            serializer.attribute(null, "classname", uc.getClassName());

            serializer.startTag(null, "title");
            serializer.text(uc.getTitle()+"");
            serializer.endTag(null, "title");

            serializer.startTag(null, "times");
            serializer.text(String.valueOf(uc.getTimes()));
            serializer.endTag(null, "times");

            for(TestItemBase ti : uc.getTestItems()){
                serializer.startTag(null, "testitem");
                serializer.attribute(null, "id", ti.getID()+"");
                serializer.attribute(null, "classname", ti.getClassName());

                serializer.startTag(null, "title");
                serializer.text(ti.getTitle()+"");
                serializer.endTag(null, "title");

                ti.saveParametersToXml(serializer);
                /*
                serializer.startTag(null, "times");
                serializer.text(ti.getTimes()+"");
                serializer.endTag(null, "times");

                serializer.startTag(null, "selected");
                serializer.text(ti.isChecked()+"");
                serializer.endTag(null, "selected");
                */

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
