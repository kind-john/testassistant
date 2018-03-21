package com.ckt.ckttestassistant.utils;

import android.app.Activity;
import android.content.Context;
import android.util.Xml;
import android.widget.Toast;

import com.ckt.ckttestassistant.TestBase;
import com.ckt.ckttestassistant.testitems.Reboot;
import com.ckt.ckttestassistant.testitems.TestItemBase;
import com.ckt.ckttestassistant.testitems.WifiSwitchOn;
import com.ckt.ckttestassistant.usecases.UseCaseBase;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
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
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;

/**
 * Created by ckt on 18-1-31.
 */

public class CktXmlHelper {
    private static final String TAG = "CktXmlHelper";
    private static volatile int allUseCaseMaxID = -1;
    private static CktXmlHelper instance = null;
    private static int MAX_LEVEL = 2;
    private CktXmlHelper() {

    }

    public static CktXmlHelper getInstance(){
        if (instance == null) {
            synchronized (CktXmlHelper.class){
                if (instance == null) {
                    instance = new CktXmlHelper();
                }
            }
        }
        return instance;
    }

    public void addUsecase(String path, ArrayList<TestBase> usecases, boolean isNeedClean) throws Exception{
        LogUtils.d(TAG, "entry addUsecase!");
        try{
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder = factory.newDocumentBuilder();
            Document doc;
            Element root;
            File file = new File(path);
            if(isNeedClean){
                file.delete();
            }
            if (!file.exists()) {
                LogUtils.d(TAG, path + " not exists, so create it");
                //file.createNewFile();
                doc = builder.newDocument();
                root = doc.createElement(XmlTagConstants.XMLTAG_ROOT);
                doc.appendChild(root);
            } else {
                InputStream is = new FileInputStream(path);
                doc = builder.parse(is);
                doc.normalize();
                root = doc.getDocumentElement();
            }

            for (TestBase tb : usecases) {
                if(tb instanceof UseCaseBase){
                    UseCaseBase uc = (UseCaseBase)tb;
                    createUseCaseElement(doc, root, uc);
                }
            }
            /*Properties properties = new Properties();
            properties.setProperty(OutputKeys.INDENT, "yes");
            properties.setProperty(OutputKeys.MEDIA_TYPE, "xml");
            properties.setProperty(OutputKeys.VERSION, "1.0");
            properties.setProperty(OutputKeys.ENCODING, "utf-8");
            properties.setProperty(OutputKeys.METHOD, "xml");
            properties.setProperty(OutputKeys.OMIT_XML_DECLARATION, "yes");*/

            TransformerFactory tfactory = TransformerFactory.newInstance();
            Transformer transformer = tfactory.newTransformer();
            //transformer.setOutputProperties(properties);

            DOMSource source = new DOMSource(doc);
            StreamResult result = new StreamResult(path);
            transformer.transform(source, result);
        }catch (Exception e){
            throw e;
        }
    }
    private Element createTestItemElement(Document doc, Element root, TestItemBase ti) {
        LogUtils.d(TAG, "entry createTestItemElement!");
        Element testitemE = doc.createElement(XmlTagConstants.XMLTAG_TESTITEM);
        testitemE.setAttribute(XmlTagConstants.XMLTAG_ID, String.valueOf(ti.getID()));
        testitemE.setAttribute(XmlTagConstants.XMLTAG_TESTITEM_CLASSNAME, ti.getClassName());

        createTextElement(doc, testitemE, XmlTagConstants.XMLTAG_TESTITEM_SN, String.valueOf(ti.getSN()));

        createTextElement(doc, testitemE, XmlTagConstants.XMLTAG_TESTITEM_USECASEID, String.valueOf(ti.getUseCaseID()));

        createTextElement(doc, testitemE, XmlTagConstants.XMLTAG_TESTITEM_USECASESN, String.valueOf(ti.getUseCaseSN()));

        createTextElement(doc, testitemE, XmlTagConstants.XMLTAG_TESTITEM_TITLE, ti.getTitle());

        createTextElement(doc, testitemE, XmlTagConstants.XMLTAG_TESTITEM_TIMES, String.valueOf(ti.getTimes()));

        createTextElement(doc, testitemE, XmlTagConstants.XMLTAG_TESTITEM_FAILTIMES, String.valueOf(ti.getFailTimes()));

        createTextElement(doc, testitemE, XmlTagConstants.XMLTAG_TESTITEM_COMPLETEDTIMES, String.valueOf(ti.getCompletedTimes()));

        ti.saveParameters(doc, testitemE);

        root.appendChild(testitemE);

        return testitemE;
    }

    private Element createUseCaseElement(Document doc, Element root, UseCaseBase uc) {
        LogUtils.d(TAG, "entry createUseCaseElement!");
        Element usecaseE = doc.createElement(XmlTagConstants.XMLTAG_USECASE);

        int maxID = getMaxID(doc, XmlTagConstants.XMLTAG_USECASE, XmlTagConstants.XMLTAG_ID);
        LogUtils.d(TAG, "max maxID = "+maxID);
        int usecaseID = uc.getID();
        if(usecaseID == -1){
            usecaseID = maxID + 1;
            uc.setID(usecaseID);
            ArrayList<TestBase> tbs = uc.getChildren();
            if(tbs != null && !tbs.isEmpty()){
                for(TestBase tb : tbs){
                    if(tb instanceof TestItemBase){
                        TestItemBase ti = (TestItemBase)tb;
                        int uc_id = uc.getID(); //可以删除
                        int uc_sn = uc.getSN();
                        LogUtils.d(TAG, "uc_id = "+uc_id+"; uc_sn = "+uc_sn);
                        ti.setUseCaseID(usecaseID);
                        ti.setUseCaseSN(uc_sn);
                    }
                }
            }
        }
        usecaseE.setAttribute(XmlTagConstants.XMLTAG_ID, String.valueOf(uc.getID()));
        usecaseE.setAttribute(XmlTagConstants.XMLTAG_USECASE_CLASSNAME, uc.getClassName());

        createTextElement(doc, usecaseE, XmlTagConstants.XMLTAG_USECASE_SN, String.valueOf(uc.getSN()));

        createTextElement(doc, usecaseE, XmlTagConstants.XMLTAG_USECASE_TITLE, uc.getTitle());

        createTextElement(doc, usecaseE, XmlTagConstants.XMLTAG_USECASE_TIMES, String.valueOf(uc.getTimes()));

        createTextElement(doc, usecaseE, XmlTagConstants.XMLTAG_USECASE_FAILTIMES, String.valueOf(uc.getFailTimes()));

        createTextElement(doc, usecaseE, XmlTagConstants.XMLTAG_USECASE_COMPLETEDTIMES, String.valueOf(uc.getCompletedTimes()));

        uc.saveParameters(doc, usecaseE);
        for (TestBase tb : uc.getChildren()) {
            if(tb instanceof UseCaseBase){
                createUseCaseElement(doc, usecaseE, (UseCaseBase)tb);
            }else if(tb instanceof TestItemBase){
                TestItemBase ti = (TestItemBase)tb;
                createTestItemElement(doc, usecaseE, ti);
            }
        }
        root.appendChild(usecaseE);
        return usecaseE;
    }
    public void updateTestItem(String path, TestItemBase ti, boolean needUCSN) {
        LogUtils.d(TAG, "entry updateTestItem!");
        try {
            File file = new File(path);
            if (!file.exists()) {
                LogUtils.d(TAG, path + " not exists,do nothing");
                return;
            }
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            DocumentBuilder bulider = factory.newDocumentBuilder();
            InputStream is = new FileInputStream(path);
            Document doc = bulider.parse(is);
            doc.normalize();
            Element root = doc.getDocumentElement();

            NodeList uc_listnode = doc.getElementsByTagName(XmlTagConstants.XMLTAG_TESTITEM);
            ArrayList<Element> parents = new ArrayList<>();
            for (int i = 0; i < uc_listnode.getLength(); i++) {
                Element node = (Element) uc_listnode.item(i);
                int level = findParents(node, parents);
                // 为了减少对比次数，先对比level是否相等
                boolean isMatched = false;
                if (level == ti.getLevel()) {
                    isMatched = compareParents(ti, parents);
                } else {
                    continue;
                }
                if (isMatched) {
                    String str_sn = node.getElementsByTagName(XmlTagConstants.XMLTAG_TESTITEM_SN)
                            .item(0)
                            .getTextContent();
                    int sn = Integer.parseInt(str_sn);
                    if (sn == ti.getSN()) {
                        Element newNode = createTestItemElement(doc, parents.get(0), ti);
                        parents.get(0).replaceChild(newNode, node);
                        break;
                    }
                }
            }
            /*NodeList uc_listnode = doc.getElementsByTagName(XmlTagConstants.XMLTAG_USECASE);
            for (int i = 0; i < uc_listnode.getLength(); i++) {
                Element uc_node = (Element) uc_listnode.item(i);
                int id = Integer.parseInt(uc_node.getAttribute(XmlTagConstants.XMLTAG_ID));
                String str_sn = uc_node.getElementsByTagName(XmlTagConstants.XMLTAG_USECASE_SN).item(0).getTextContent();
                int sn = Integer.parseInt(str_sn);
                boolean isMatched = false;
                int level = computeNodeLevel(uc_node);
                //找到父节点
                TestBase parentUC = ti.getParent();
                if(needUCSN){
                    isMatched = (id == parentUC.getID()) &&
                            (sn == parentUC.getSN() &&
                                    (level == parentUC.getLevel()));
                } else {
                    isMatched = id == parentUC.getID() &&
                            (level == parentUC.getLevel());
                }
                if (isMatched) {
                    NodeList ti_listnode = uc_node.getElementsByTagName(XmlTagConstants.XMLTAG_TESTITEM);
                    for (int j = 0; j < ti_listnode.getLength(); j++){
                        Element ti_element = (Element) ti_listnode.item(j);
                        int ti_id = Integer.parseInt(ti_element.getAttribute(XmlTagConstants.XMLTAG_ID));
                        String ti_str_sn = ti_element.getElementsByTagName(XmlTagConstants.XMLTAG_TESTITEM_SN).item(0).getTextContent();
                        int ti_sn = Integer.parseInt(ti_str_sn);
                        if(ti_id == ti.getID() && ti_sn == ti.getSN()){
                            Element newNode = createTestItemElement(doc, uc_node, ti);
                            ti_element.getParentNode().replaceChild(newNode, ti_element);
                            break;
                        }
                    }
                }
            }*/
            TransformerFactory tfactory = TransformerFactory.newInstance();
            Transformer trans = tfactory.newTransformer();
            DOMSource source = new DOMSource(doc);
            StreamResult result = new StreamResult(path);
            trans.transform(source, result);// 将原文件覆盖
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private boolean compareParents(TestBase ti, ArrayList<Element> parents) {
        if (parents != null && !parents.isEmpty()) {
            TestBase tbParent = ti;
            for (int index = parents.size() - 1; index >= 0; index--) {
                tbParent = tbParent.getParent();
                Element parent = parents.get(index);
                String parent_str_sn = parent.getElementsByTagName(XmlTagConstants.XMLTAG_USECASE_SN)
                        .item(0)
                        .getTextContent();
                int parent_sn = Integer.parseInt(parent_str_sn);
                if(tbParent.getSN() != parent_sn){
                    return false;
                }
            }
        }
        return true;
    }

    private int findParents(Element node, ArrayList<Element> parents) {
        Element parent = (Element) node.getParentNode();
        int level = 0;
        while (!"usecases".equals(parent.getTagName())) {
            level++;
            parents.add(parent);
            parent = (Element) parent.getParentNode();
        }
        return level;
    }

    public void updateUseCase(String path, UseCaseBase uc) {
        LogUtils.d(TAG, "entry updateUseCase!");
        try {
            File file = new File(path);
            if (!file.exists()) {
                LogUtils.d(TAG, path + " not exists,do nothing");
                return;
            }
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            DocumentBuilder bulider = factory.newDocumentBuilder();
            InputStream is = new FileInputStream(path);
            Document doc = bulider.parse(is);
            doc.normalize();
            Element root = doc.getDocumentElement();
            NodeList listnode = doc.getElementsByTagName(XmlTagConstants.XMLTAG_USECASE);
            ArrayList<Element> parents = new ArrayList<>();
            for (int i = 0; i < listnode.getLength(); i++) {
                Element node = (Element) listnode.item(i);
                int level = findParents(node, parents);
                // 为了减少对比次数，先对比level是否相等
                boolean isMatched = false;
                if (level == uc.getLevel()) {
                    isMatched = compareParents(uc, parents);
                } else {
                    continue;
                }
                if (isMatched) {
                    String mySNStr = node.getElementsByTagName(XmlTagConstants.XMLTAG_USECASE_SN)
                            .item(0)
                            .getTextContent();
                    int mySn = Integer.parseInt(mySNStr);
                    if (mySn == uc.getSN()) {
                        Element parent = null;
                        if (parents.isEmpty()){
                            parent = root;
                        } else {
                            parent = parents.get(0);
                        }
                        Element newNode = createUseCaseElement(doc, parent, uc);
                        parent.replaceChild(newNode, node);
                        break;
                    }
                }
            }
            /*for (int i = 0; i < listnode.getLength(); i++) {
                Element elink = (Element) listnode.item(i);
                int id = Integer.parseInt(elink.getAttribute(XmlTagConstants.XMLTAG_ID));
                String str_sn = elink.getElementsByTagName(XmlTagConstants.XMLTAG_USECASE_SN).item(0).getTextContent();

                int sn = Integer.parseInt(str_sn);
                int level = computeNodeLevel(elink);
                if ((id == uc.getID()) && (sn == uc.getSN()) && (level == uc.getLevel())) {
                    Element newNode = createUseCaseElement(doc, root, uc);
                    elink.getParentNode().replaceChild(newNode, elink);
                    break;
                }
            }*/
            TransformerFactory tfactory = TransformerFactory.newInstance();
            Transformer trans = tfactory.newTransformer();
            DOMSource source = new DOMSource(doc);
            StreamResult result = new StreamResult(path);
            trans.transform(source, result);// 将原文件覆盖
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void createTextElement(Document doc, Element element, String nodeName, String nodeValue) {
        LogUtils.d(TAG, "entry createTextElement!");
        Element e = doc.createElement(nodeName);
        Node n = doc.createTextNode(nodeValue);
        e.appendChild(n);
        element.appendChild(e);
    }

    public void deleteUseCase(String path, ArrayList<UseCaseBase> ucs) {
        LogUtils.d(TAG, "entry deleteUseCase!");
        try {
            File file = new File(path);
            if (!file.exists()) {
                LogUtils.d(TAG, path + " not exists,do nothing");
                return;
            }
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            DocumentBuilder bulider = factory.newDocumentBuilder();
            InputStream is = new FileInputStream(path);
            Document doc = bulider.parse(is);
            doc.normalize();
            Element root = doc.getDocumentElement();
            for (UseCaseBase uc : ucs) {
                NodeList listnode = doc.getElementsByTagName(XmlTagConstants.XMLTAG_USECASE);
                for (int i = 0; i < listnode.getLength(); i++) {
                    Element elink = (Element) listnode.item(i);
                    elink.getElementsByTagName(XmlTagConstants.XMLTAG_USECASE_SN).item(0).getNodeValue();
                    int id = Integer.parseInt(elink.getAttribute(XmlTagConstants.XMLTAG_ID));
                    //int sn = Integer.parseInt(elink.getAttribute(XmlTagConstants.XMLTAG_USECASE_SN));
                    int sn = Integer.parseInt(elink.getElementsByTagName(XmlTagConstants.XMLTAG_USECASE_SN).item(0).getTextContent());
                    int level = computeNodeLevel(elink);
                    if ((id == uc.getID()) && (sn == uc.getSN()) && (level == uc.getLevel())) {
                        if (listnode.getLength() == 1) {
                            //如果只有一条数据，那么这条数据删除之后，大节点也应该被删除
                            root.removeChild(elink.getParentNode());
                        } else {
                            elink.getParentNode().removeChild(elink);
                        }
                    }
                }
            }
            TransformerFactory tfactory = TransformerFactory.newInstance();
            Transformer trans = tfactory.newTransformer();
            DOMSource source = new DOMSource(doc);
            StreamResult result = new StreamResult(path);
            trans.transform(source, result);// 将原文件覆盖
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private int computeNodeLevel(Element element) {
        Element e = (Element) element.getParentNode();
        int level = 0;
        while(e != null){
            if("usecases".equals(e.getTagName())){
                break;
            }
            level++;
            e = (Element) e.getParentNode();
        }
        LogUtils.d(TAG, "computeNodeLevel = "+level);
        return level;
    }

    /**
     * pull 解析读取xml文件
     */
    public static void readxml(Context context, Activity activity, String path, ArrayList<TestBase> allUseCases) throws XmlPullParserException,IOException{
        LogUtils.d(TAG, "entry readxml!");
        try {
            File file = new File(path);
            if (!file.exists()) {
                LogUtils.d(TAG, path + " not exists, so create it");
                //file.createNewFile();
                return;
            }
            InputStream is = new FileInputStream(path);
            XmlPullParser parser = Xml.newPullParser();
            parser.setInput(is, "utf-8");
            int eventtype = parser.getEventType();// 产生第一个事件
            UseCaseBase usecase = null;
            UseCaseBase usecase2 = null;
            TestItemBase testitem = null;
            allUseCaseMaxID = -1;
            LogUtils.d(TAG, "set allUseCaseMaxID = -1");
            while (eventtype != XmlPullParser.END_DOCUMENT) {
                switch (eventtype) {
                    case XmlPullParser.START_DOCUMENT:// 判断当前事件是否为文档开始事件
                        LogUtils.d(TAG, "START_DOCUMENT");
                        break;
                    case XmlPullParser.START_TAG:// 判断当前事件是否为标签元素开始事件
                        String name = parser.getName();
                        LogUtils.d(TAG, "XmlPullParser.START_TAG name:" + name);

                        if (name.equals(XmlTagConstants.XMLTAG_USECASE)) { // 判断开始标签元素是否是student
                            int id = Integer.parseInt(parser.getAttributeValue(0));
                            String ttt = parser.getAttributeValue(null, XmlTagConstants.XMLTAG_USECASE_CLASSNAME);
                            LogUtils.d(TAG, "ttt = "+ttt);
                            String className = parser.getAttributeValue(1);
                            LogUtils.d(TAG, "usecase id : " + id + "; className = " + className);

                            if (allUseCaseMaxID < id) {
                                allUseCaseMaxID = id;
                                LogUtils.d(TAG, "set allUseCaseMaxID = " + id);
                            }
                            LogUtils.d(TAG, "getUseCases : allUseCaseMaxID = " + allUseCaseMaxID);
                            if (id >= 0) {
                                try {
                                    if(usecase != null){
                                        //嵌套用例
                                        // 根据给定的类名初始化类
                                        Class catClass = Class.forName(className);
                                        // 实例化这个类
                                        usecase2 = (UseCaseBase) catClass.newInstance();
                                        usecase2.setContext(context);
                                        usecase2.setID(id);
                                        usecase2.setExpand(false);
                                        if(usecase != null){
                                            usecase2.setParent(usecase);
                                        }
                                    }else{
                                        // 根据给定的类名初始化类
                                        Class catClass = Class.forName(className);
                                        // 实例化这个类
                                        usecase = (UseCaseBase) catClass.newInstance();
                                        usecase.setContext(context);
                                        usecase.setID(id);
                                        usecase.setExpand(false);
                                        usecase.setParent(null);
                                    }

                                } catch (IllegalAccessException e) {
                                    e.printStackTrace();
                                } catch (InstantiationException e) {
                                    e.printStackTrace();
                                } catch (ClassNotFoundException e) {
                                    e.printStackTrace();
                                }
                            } else {
                                LogUtils.e(TAG, "error: id < -1 ,from " + path);
                            }
                        } else if (name.equals(XmlTagConstants.XMLTAG_USECASE_SN)) {
                            int ucsn = Integer.parseInt(parser.nextText());
                            LogUtils.d(TAG, "ucsn : " + ucsn);
                            if(usecase2 != null){
                                usecase2.setSN(ucsn);
                            }else if (usecase != null) {
                                LogUtils.d(TAG, "usecase ucsn : " + ucsn);
                                usecase.setSN(ucsn);
                            }
                        } else if (name.equals(XmlTagConstants.XMLTAG_USECASE_TITLE)) {
                            String title = parser.nextText();
                            LogUtils.d(TAG, "title : " + title);

                            if(usecase2 != null){
                                usecase2.setTitle(title);
                            }else if (usecase != null) {
                                LogUtils.d(TAG, "usecase title : " + title);
                                usecase.setTitle(title);
                            }
                        } else if (name.equals(XmlTagConstants.XMLTAG_USECASE_TIMES)) {
                            int times = Integer.parseInt(parser.nextText());
                            LogUtils.d(TAG, "times : " + times);

                            if(usecase2 != null){
                                usecase2.setTimes(times);
                            }else if (usecase != null) {
                                LogUtils.d(TAG, "usecase times : " + times);
                                usecase.setTimes(times);
                            }

                        } else if (name.equals(XmlTagConstants.XMLTAG_USECASE_FAILTIMES)) {
                            int failtimes = Integer.parseInt(parser.nextText());
                            LogUtils.d(TAG, "failtimes : " + failtimes);

                            if(usecase2 != null){
                                usecase2.setFailTimes(failtimes);
                            }else if (usecase != null) {
                                LogUtils.d(TAG, "usecase failtimes : " + failtimes);
                                usecase.setFailTimes(failtimes);
                            }

                        } else if (name.equals(XmlTagConstants.XMLTAG_USECASE_COMPLETEDTIMES)) {
                            int completedtimes = Integer.parseInt(parser.nextText());
                            LogUtils.d(TAG, "completedtimes : " + completedtimes);

                            if(usecase2 != null){
                                usecase2.setCompletedTimes(completedtimes);
                            }else if (usecase != null) {
                                LogUtils.d(TAG, "usecase completedtimes : " + completedtimes);
                                usecase.setCompletedTimes(completedtimes);
                            }

                        } else if (name.equals(XmlTagConstants.XMLTAG_USECASE_SELECTED)) {
                            boolean isChecked = Boolean.parseBoolean(parser.nextText());
                            LogUtils.d(TAG, "usecase isChecked : " + isChecked);
                            if(usecase2 != null){
                                usecase2.setChecked(isChecked);
                            }else if (usecase != null) {
                                usecase.setChecked(isChecked);
                            }
                        } else if (name.equals(XmlTagConstants.XMLTAG_USECASE_DELAY)) {
                            int delay = Integer.parseInt(parser.nextText());
                            LogUtils.d(TAG, "delay : " + delay);
                            if(usecase2 != null){
                                usecase2.setDelay(delay);
                            }else if (usecase != null) {
                                LogUtils.d(TAG, "usecase delay : " + delay);
                                usecase.setDelay(delay);
                            }
                        } else if (name.equals(XmlTagConstants.XMLTAG_TESTITEM_SN)) {
                            int tisn = Integer.parseInt(parser.nextText());
                            LogUtils.d(TAG, "tisn : " + tisn);

                            if (testitem != null) {
                                LogUtils.d(TAG, "testitem tisn : " + tisn);
                                testitem.setSN(tisn);
                            }
                        } else if (name.equals(XmlTagConstants.XMLTAG_TESTITEM_USECASEID)) {
                            int ti_ucid = Integer.parseInt(parser.nextText());
                            LogUtils.d(TAG, "ti_ucid : " + ti_ucid);

                            if (testitem != null) {
                                LogUtils.d(TAG, "testitem ti_ucid : " + ti_ucid);
                                testitem.setUseCaseID(ti_ucid);
                            }
                        } else if (name.equals(XmlTagConstants.XMLTAG_TESTITEM_USECASESN)) {
                            int ti_ucsn = Integer.parseInt(parser.nextText());
                            LogUtils.d(TAG, "ti_ucsn : " + ti_ucsn);

                            if (testitem != null) {
                                LogUtils.d(TAG, "testitem ti_ucsn : " + ti_ucsn);
                                testitem.setUseCaseSN(ti_ucsn);
                            }
                        } else if (name.equals(XmlTagConstants.XMLTAG_TESTITEM_TITLE)) {
                            String title = parser.nextText();
                            LogUtils.d(TAG, "title : " + title);

                            if (testitem != null) {
                                LogUtils.d(TAG, "testitem title : " + title);
                                testitem.setTitle(title);
                            }
                        } else if (name.equals(XmlTagConstants.XMLTAG_TESTITEM_TIMES)) {
                            int times = Integer.parseInt(parser.nextText());
                            LogUtils.d(TAG, "times : " + times);

                            if (testitem != null) {
                                LogUtils.d(TAG, "testitem times : " + times);
                                testitem.setTimes(times);
                            }

                        } else if (name.equals(XmlTagConstants.XMLTAG_TESTITEM_FAILTIMES)) {
                            int failtimes = Integer.parseInt(parser.nextText());
                            LogUtils.d(TAG, "failtimes : " + failtimes);

                            if (testitem != null) {
                                LogUtils.d(TAG, "testitem failtimes : " + failtimes);
                                testitem.setFailTimes(failtimes);
                            }

                        } else if (name.equals(XmlTagConstants.XMLTAG_TESTITEM_COMPLETEDTIMES)) {
                            int completedtimes = Integer.parseInt(parser.nextText());
                            LogUtils.d(TAG, "completedtimes : " + completedtimes);

                            if (testitem != null) {
                                LogUtils.d(TAG, "testitem completedtimes : " + completedtimes);
                                testitem.setCompletedTimes(completedtimes);
                            }

                        } else if (name.equals(XmlTagConstants.XMLTAG_TESTITEM_SELECTED)) {
                            boolean isChecked = Boolean.parseBoolean(parser.nextText());
                            LogUtils.d(TAG, "testitem isChecked : " + isChecked);
                            if (testitem != null) {
                                testitem.setChecked(isChecked);
                            }
                        } else if (name.equals(XmlTagConstants.XMLTAG_TESTITEM)) {
                            if (usecase != null) {
                                int id2 = Integer.parseInt(parser.getAttributeValue(0));
                                String className2 = parser.getAttributeValue(1);
                                LogUtils.d(TAG, "testitem id : " + id2 + "; className = " + className2);
                                try {
                                    // 根据给定的类名初始化类
                                    Class catClass = Class.forName(className2);
                                    // 实例化这个类
                                    testitem = (TestItemBase) catClass.newInstance();
                                    testitem.setContext(context);
                                    testitem.setActivity(activity);
                                    testitem.setID(id2);
                                    if(usecase2 != null){
                                        testitem.setParent(usecase2);
                                    }else if(usecase != null){
                                        testitem.setParent(usecase);
                                    }
                                } catch (IllegalAccessException e) {
                                    e.printStackTrace();
                                } catch (InstantiationException e) {
                                    e.printStackTrace();
                                } catch (ClassNotFoundException e) {
                                    e.printStackTrace();
                                }

                            }
                        } else if (name.equals(XmlTagConstants.XMLTAG_TESTITEM_DELAY)) {
                            int delay = Integer.parseInt(parser.nextText());
                            LogUtils.d(TAG, "delay : " + delay);
                            if (testitem != null) {
                                LogUtils.d(TAG, "testitem delay : " + delay);
                                testitem.setDelay(delay);
                            }
                        } else {
                            LogUtils.e(TAG, "error: some new tag has not parser!!! name = " + name);
                        }
                        break;

                    case XmlPullParser.END_TAG:
                        String endName = parser.getName();
                        LogUtils.d(TAG, "XmlPullParser.END_TAG name:" + endName);
                        if (endName.equals(XmlTagConstants.XMLTAG_USECASE)) {
                            if(usecase != null){
                                if(usecase2 != null){
                                    usecase.addTestItem(usecase2);
                                    usecase2 = null;
                                    break;
                                }
                                allUseCases.add(usecase);
                                usecase = null;
                            }
                        } else if (endName.equals(XmlTagConstants.XMLTAG_TESTITEM)) {
                            if(usecase2 != null){
                                usecase2.addTestItem(testitem);
                                testitem = null;
                            }else if(usecase != null){
                                usecase.addTestItem(testitem);
                                testitem = null;
                            }
                        } else {
                            LogUtils.d(TAG, "ignored endName = " + endName);
                        }
                        break;

                    default:
                        break;
                }
                eventtype = parser.next();// 不断的去更新，持续的解析XML文件直到文件的尾部。
            }
        } catch (IOException e) {
            throw e;
        } catch (XmlPullParserException e) {
            throw e;
        }

    }

    private int getMaxID(Document doc, String elementName, String idPropertyName){
        LogUtils.d(TAG, "entry getMaxID!");
        int num = -1;
        NodeList listnode = doc.getElementsByTagName(elementName);
        for (int i = 0; i < listnode.getLength(); i++) {
            Element elink = (Element) listnode.item(i);
            String id = elink.getAttribute(idPropertyName);
            if (Integer.valueOf(id) > num) {
                num = Integer.valueOf(id);
            }
        }
        return num;
    }
}
