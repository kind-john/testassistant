package com.ckt.ckttestassistant.utils;

import android.content.Context;
import android.util.Xml;
import android.widget.Toast;

import com.ckt.ckttestassistant.testitems.CktTestItem;
import com.ckt.ckttestassistant.testitems.Reboot;
import com.ckt.ckttestassistant.testitems.TestItemBase;
import com.ckt.ckttestassistant.testitems.WifiSwitchOn;
import com.ckt.ckttestassistant.usecases.CktUseCase;
import com.ckt.ckttestassistant.usecases.UseCaseBase;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
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
import java.util.Properties;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
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
    private static int allUseCaseMaxID = -1;

    public void addUsecase(String path, ArrayList<UseCaseBase> usecases) throws Exception{
        LogUtils.d(TAG, "entry addUsecase!");
        try{
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder = factory.newDocumentBuilder();
            Document doc;
            Element root;
            File file = new File(path);
            if (!file.exists()) {
                LogUtils.d(TAG, path + " not exists, so create it");
                //file.createNewFile();
                doc = builder.newDocument();
                root = doc.createElement(MyConstants.XMLTAG_ROOT);
                doc.appendChild(root);
            } else {
                InputStream is = new FileInputStream(path);
                doc = builder.parse(is);
                doc.normalize();
                root = doc.getDocumentElement();
            }

            for (UseCaseBase uc : usecases) {
                createUseCaseElement(doc, root, uc);
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
        Element testitemE = doc.createElement(MyConstants.XMLTAG_TESTITEM);
        testitemE.setAttribute(MyConstants.XMLTAG_ID, String.valueOf(ti.getID()));
        testitemE.setAttribute(MyConstants.XMLTAG_TESTITEM_CLASSNAME, ti.getClassName());

        createTextElement(doc, testitemE, MyConstants.XMLTAG_TESTITEM_SN, String.valueOf(ti.getSN()));

        createTextElement(doc, testitemE, MyConstants.XMLTAG_TESTITEM_USECASEID, String.valueOf(ti.getUseCaseID()));

        createTextElement(doc, testitemE, MyConstants.XMLTAG_TESTITEM_USECASESN, String.valueOf(ti.getUseCaseSN()));

        createTextElement(doc, testitemE, MyConstants.XMLTAG_TESTITEM_TITLE, ti.getTitle());

        createTextElement(doc, testitemE, MyConstants.XMLTAG_TESTITEM_TIMES, String.valueOf(ti.getTimes()));

        createTextElement(doc, testitemE, MyConstants.XMLTAG_TESTITEM_FAILTIMES, String.valueOf(ti.getFailTimes()));

        createTextElement(doc, testitemE, MyConstants.XMLTAG_TESTITEM_COMPLETEDTIMES, String.valueOf(ti.getCompletedTimes()));

        ti.saveParameters(doc, testitemE);

        root.appendChild(testitemE);

        return testitemE;
    }

    private Element createUseCaseElement(Document doc, Element root, UseCaseBase uc) {
        LogUtils.d(TAG, "entry createUseCaseElement!");
        Element usecaseE = doc.createElement(MyConstants.XMLTAG_USECASE);

        int maxID = getMaxID(doc, MyConstants.XMLTAG_USECASE, MyConstants.XMLTAG_ID);
        LogUtils.d(TAG, "max maxID = "+maxID);
        int usecaseID = uc.getID();
        if(usecaseID == -1){
            usecaseID = maxID + 1;
            ArrayList<TestItemBase> tis = uc.getTestItems();
            if(tis != null && !tis.isEmpty()){
                for(TestItemBase ti : tis){
                    int uc_id = uc.getID(); //可以删除
                    int uc_sn = uc.getSN();
                    LogUtils.d(TAG, "uc_id = "+uc_id+"; uc_sn = "+uc_sn);
                    ti.setUseCaseID(uc_id);
                    ti.setUseCaseSN(uc_sn);
                }
            }
        }
        usecaseE.setAttribute(MyConstants.XMLTAG_ID, String.valueOf(usecaseID));
        usecaseE.setAttribute(MyConstants.XMLTAG_USECASE_CLASSNAME, uc.getClassName());

        createTextElement(doc, usecaseE, MyConstants.XMLTAG_USECASE_SN, String.valueOf(uc.getSN()));

        createTextElement(doc, usecaseE, MyConstants.XMLTAG_USECASE_TITLE, uc.getTitle());

        createTextElement(doc, usecaseE, MyConstants.XMLTAG_USECASE_TIMES, String.valueOf(uc.getTimes()));

        createTextElement(doc, usecaseE, MyConstants.XMLTAG_USECASE_FAILTIMES, String.valueOf(uc.getFailTimes()));

        createTextElement(doc, usecaseE, MyConstants.XMLTAG_USECASE_COMPLETEDTIMES, String.valueOf(uc.getCompletedTimes()));

        for (TestItemBase ti : uc.getTestItems()) {
            createTestItemElement(doc, usecaseE, ti);
        }
        root.appendChild(usecaseE);
        return usecaseE;
    }
    public void updateTestItem(String path, TestItemBase ti) {
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
            NodeList uc_listnode = doc.getElementsByTagName(MyConstants.XMLTAG_USECASE);
            for (int i = 0; i < uc_listnode.getLength(); i++) {
                Element uc_node = (Element) uc_listnode.item(i);
                int id = Integer.parseInt(uc_node.getAttribute(MyConstants.XMLTAG_ID));
                String str_sn = uc_node.getElementsByTagName(MyConstants.XMLTAG_USECASE_SN).item(0).getTextContent();
                int sn = Integer.parseInt(str_sn);
                if ((id == ti.getUseCaseID()) && (sn == ti.getUseCaseSN())) {
                    NodeList ti_listnode = uc_node.getElementsByTagName(MyConstants.XMLTAG_TESTITEM);
                    for (int j = 0; j < ti_listnode.getLength(); j++){
                        Element ti_element = (Element) ti_listnode.item(j);
                        int ti_id = Integer.parseInt(ti_element.getAttribute(MyConstants.XMLTAG_ID));
                        String ti_str_sn = ti_element.getElementsByTagName(MyConstants.XMLTAG_TESTITEM_SN).item(0).getTextContent();
                        int ti_sn = Integer.parseInt(ti_str_sn);
                        if(ti_id == ti.getID() && ti_sn == ti.getSN()){
                            Element newNode = createTestItemElement(doc, uc_node, ti);
                            ti_element.getParentNode().replaceChild(newNode, ti_element);
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
            NodeList listnode = doc.getElementsByTagName(MyConstants.XMLTAG_USECASE);
            for (int i = 0; i < listnode.getLength(); i++) {
                Element elink = (Element) listnode.item(i);
                int id = Integer.parseInt(elink.getAttribute(MyConstants.XMLTAG_ID));
                String str_sn = elink.getElementsByTagName(MyConstants.XMLTAG_USECASE_SN).item(0).getTextContent();

                int sn = Integer.parseInt(str_sn);
                if ((id == uc.getID()) && (sn == uc.getSN())) {
                    Element newNode = createUseCaseElement(doc, root, uc);
                    elink.getParentNode().replaceChild(newNode, elink);
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

    private void createTextElement(Document doc, Element element, String nodeName, String nodeValue) {
        LogUtils.d(TAG, "entry createTextElement!");
        Element e = doc.createElement(nodeName);
        Node n = doc.createTextNode(nodeValue);
        e.appendChild(n);
        element.appendChild(e);
    }

    private void deleteUseCase(String path, ArrayList<UseCaseBase> ucs) {
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
                NodeList listnode = doc.getElementsByTagName(MyConstants.XMLTAG_USECASE);
                for (int i = 0; i < listnode.getLength(); i++) {
                    Element elink = (Element) listnode.item(i);
                    elink.getElementsByTagName(MyConstants.XMLTAG_USECASE_SN).item(0).getNodeValue();
                    int id = Integer.parseInt(elink.getAttribute(MyConstants.XMLTAG_ID));
                    //int sn = Integer.parseInt(elink.getAttribute(MyConstants.XMLTAG_USECASE_SN));
                    int sn = Integer.parseInt(elink.getElementsByTagName(MyConstants.XMLTAG_USECASE_SN).item(0).getTextContent());
                    if ((id == uc.getID()) && (sn == uc.getSN())) {
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

    /**
     * pull 解析读取xml文件
     */
    public static void readxml(Context context, String path, ArrayList<UseCaseBase> allUseCases) throws XmlPullParserException,IOException{
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

                        if (name.equals(MyConstants.XMLTAG_USECASE)) { // 判断开始标签元素是否是student
                            int id = Integer.parseInt(parser.getAttributeValue(0));
                            String ttt = parser.getAttributeValue(null, MyConstants.XMLTAG_USECASE_CLASSNAME);
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
                                    // 根据给定的类名初始化类
                                    Class catClass = Class.forName(className);
                                    // 实例化这个类
                                    usecase = (UseCaseBase) catClass.newInstance();
                                    usecase.setContext(context);
                                    usecase.setID(id);

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
                        } else if (name.equals(MyConstants.XMLTAG_USECASE_SN)) {
                            int ucsn = Integer.parseInt(parser.nextText());
                            LogUtils.d(TAG, "ucsn : " + ucsn);

                            if (usecase != null) {
                                LogUtils.d(TAG, "usecase ucsn : " + ucsn);
                                usecase.setSN(ucsn);
                            }
                        } else if (name.equals(MyConstants.XMLTAG_USECASE_TITLE)) {
                            String title = parser.nextText();
                            LogUtils.d(TAG, "title : " + title);

                            if (usecase != null) {
                                LogUtils.d(TAG, "usecase title : " + title);
                                usecase.setTitle(title);
                            }
                        } else if (name.equals(MyConstants.XMLTAG_USECASE_TIMES)) {
                            int times = Integer.parseInt(parser.nextText());
                            LogUtils.d(TAG, "times : " + times);

                            if (usecase != null) {
                                LogUtils.d(TAG, "usecase times : " + times);
                                usecase.setTimes(times);
                            }

                        } else if (name.equals(MyConstants.XMLTAG_USECASE_FAILTIMES)) {
                            int failtimes = Integer.parseInt(parser.nextText());
                            LogUtils.d(TAG, "failtimes : " + failtimes);

                            if (usecase != null) {
                                LogUtils.d(TAG, "usecase failtimes : " + failtimes);
                                usecase.setFailTimes(failtimes);
                            }

                        } else if (name.equals(MyConstants.XMLTAG_USECASE_COMPLETEDTIMES)) {
                            int completedtimes = Integer.parseInt(parser.nextText());
                            LogUtils.d(TAG, "completedtimes : " + completedtimes);

                            if (usecase != null) {
                                LogUtils.d(TAG, "usecase completedtimes : " + completedtimes);
                                usecase.setCompletedTimes(completedtimes);
                            }

                        } else if (name.equals(MyConstants.XMLTAG_USECASE_SELECTED)) {
                            boolean isChecked = Boolean.parseBoolean(parser.nextText());
                            LogUtils.d(TAG, "usecase isChecked : " + isChecked);
                            if (usecase != null) {
                                usecase.setIsChecked(isChecked);
                            }
                        } else if (name.equals(MyConstants.XMLTAG_USECASE_DELAY)) {
                            int delay = Integer.parseInt(parser.nextText());
                            LogUtils.d(TAG, "delay : " + delay);
                            if (usecase != null) {
                                LogUtils.d(TAG, "usecase delay : " + delay);
                                usecase.setDelay(delay);
                            }
                        } else if (name.equals(MyConstants.XMLTAG_TESTITEM_SN)) {
                            int tisn = Integer.parseInt(parser.nextText());
                            LogUtils.d(TAG, "tisn : " + tisn);

                            if (testitem != null) {
                                LogUtils.d(TAG, "testitem tisn : " + tisn);
                                testitem.setSN(tisn);
                            }
                        } else if (name.equals(MyConstants.XMLTAG_TESTITEM_USECASEID)) {
                            int ti_ucid = Integer.parseInt(parser.nextText());
                            LogUtils.d(TAG, "ti_ucid : " + ti_ucid);

                            if (testitem != null) {
                                LogUtils.d(TAG, "testitem ti_ucid : " + ti_ucid);
                                testitem.setUseCaseID(ti_ucid);
                            }
                        } else if (name.equals(MyConstants.XMLTAG_TESTITEM_USECASESN)) {
                            int ti_ucsn = Integer.parseInt(parser.nextText());
                            LogUtils.d(TAG, "ti_ucsn : " + ti_ucsn);

                            if (testitem != null) {
                                LogUtils.d(TAG, "testitem ti_ucsn : " + ti_ucsn);
                                testitem.setUseCaseSN(ti_ucsn);
                            }
                        } else if (name.equals(MyConstants.XMLTAG_TESTITEM_TITLE)) {
                            String title = parser.nextText();
                            LogUtils.d(TAG, "title : " + title);

                            if (testitem != null) {
                                LogUtils.d(TAG, "testitem title : " + title);
                                testitem.setTitle(title);
                            }
                        } else if (name.equals(MyConstants.XMLTAG_TESTITEM_TIMES)) {
                            int times = Integer.parseInt(parser.nextText());
                            LogUtils.d(TAG, "times : " + times);

                            if (testitem != null) {
                                LogUtils.d(TAG, "testitem times : " + times);
                                testitem.setTimes(times);
                            }

                        } else if (name.equals(MyConstants.XMLTAG_TESTITEM_FAILTIMES)) {
                            int failtimes = Integer.parseInt(parser.nextText());
                            LogUtils.d(TAG, "failtimes : " + failtimes);

                            if (testitem != null) {
                                LogUtils.d(TAG, "testitem failtimes : " + failtimes);
                                testitem.setFailTimes(failtimes);
                            }

                        } else if (name.equals(MyConstants.XMLTAG_TESTITEM_COMPLETEDTIMES)) {
                            int completedtimes = Integer.parseInt(parser.nextText());
                            LogUtils.d(TAG, "completedtimes : " + completedtimes);

                            if (testitem != null) {
                                LogUtils.d(TAG, "testitem completedtimes : " + completedtimes);
                                testitem.setCompletedTimes(completedtimes);
                            }

                        } else if (name.equals(MyConstants.XMLTAG_TESTITEM_SELECTED)) {
                            boolean isChecked = Boolean.parseBoolean(parser.nextText());
                            LogUtils.d(TAG, "testitem isChecked : " + isChecked);
                            if (testitem != null) {
                                testitem.setIsChecked(isChecked);
                            }
                        } else if (name.equals(MyConstants.XMLTAG_TESTITEM)) {
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
                                    testitem.setID(id2);
                                    if(usecase != null){
                                        testitem.setParentUseCase(usecase);
                                    }
                                } catch (IllegalAccessException e) {
                                    e.printStackTrace();
                                } catch (InstantiationException e) {
                                    e.printStackTrace();
                                } catch (ClassNotFoundException e) {
                                    e.printStackTrace();
                                }

                            }
                        } else if (name.equals(MyConstants.XMLTAG_TESTITEM_DELAY)) {
                            int delay = Integer.parseInt(parser.nextText());
                            LogUtils.d(TAG, "delay : " + delay);
                            if (testitem != null) {
                                LogUtils.d(TAG, "testitem delay : " + delay);
                                if (testitem instanceof WifiSwitchOn) {
                                    ((WifiSwitchOn) testitem).setDelay(delay);
                                } else if (testitem instanceof Reboot) {
                                    ((Reboot) testitem).setDelay(delay);
                                } else {
                                    LogUtils.d(TAG, "there is no " + MyConstants.XMLTAG_TESTITEM_DELAY);
                                }
                            }
                        } else {
                            LogUtils.e(TAG, "error: some new tag has not parser!!! name = " + name);
                        }
                        break;

                    case XmlPullParser.END_TAG:
                        String endName = parser.getName();
                        LogUtils.d(TAG, "XmlPullParser.END_TAG name:" + endName);
                        if (endName.equals(MyConstants.XMLTAG_USECASE)) {
                            allUseCases.add(usecase);
                            usecase = null;
                        } else if (endName.equals(MyConstants.XMLTAG_TESTITEM)) {
                            usecase.addTestItem(testitem);
                            testitem = null;
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

    /*private int getMaxId(String fileName, String code, String idnum) {
        int num = -1;
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
    }*/

    /*private void updateUsecasetoxml(String path, ArrayList<UseCaseBase> usecases) {
        try {
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            DocumentBuilder bulider = factory.newDocumentBuilder();
            Document doc = bulider.parse(path);
            doc.normalize();
            Element root = doc.getDocumentElement();
            for (UseCaseBase usecase : usecases){
                NodeList usecaseNodes = root.getChildNodes();

                if (usecases != null) {
                    int flag = 0;
                    for (int i = 0; i < usecaseNodes.getLength(); i++) {
                        Node mycode = usecaseNodes.item(i);
                        if (mycode.getNodeName().equals("usecase")) {
                            flag++;
                            if(mycode.getAttributes().getNamedItem("id").equals(usecase.getID()))
                            Element per = (Element) selectSingleNode("/Object/" + bigCode, root);
                            Element codeNode = doc.createElement(code);
                            codeNode.setAttributeNS("", "id", String.valueOf(getMaxId(code, "id") + 1));
                            codeNode.setAttributeNS("", "type", type);
                            for (String key : map.keySet()) {
                                Node node = codeNode.appendChild(doc.createElement(key)).appendChild(doc.createTextNode(map.get(key)));
                            }
                            per.appendChild(codeNode);
                        }
                    }

                }

                if (flag == 0) {
                    addUsecasetoxml(path, usecases);
                    return;
                }

            }

            TransformerFactory tfactory = TransformerFactory.newInstance();
            Transformer trans = tfactory.newTransformer();
            DOMSource source = new DOMSource(doc);
            StreamResult result = new StreamResult(path);
            trans.transform(source, result);
        } catch (ParserConfigurationException e) {
            LogUtils.d(TAG, "ParserConfigurationException");
            e.printStackTrace();
        } catch (SAXException e) {
            LogUtils.d(TAG,"SAXException");
            e.printStackTrace();
        } catch (IOException e) {
            LogUtils.d(TAG, "IOException");
            e.printStackTrace();
        } catch (TransformerConfigurationException e) {
            LogUtils.d(TAG, "TransformerConfigurationException");
            e.printStackTrace();
        } catch (TransformerException e) {
            LogUtils.d(TAG, "TransformerException");
            e.printStackTrace();
        }
    }*/

    /**
     * 选择具体某一结点
     */
    public static Node selectSingleNode(String express, Element source) {
        Node result = null;
        XPathFactory xpathFactory = XPathFactory.newInstance();
        XPath xpath = xpathFactory.newXPath();
        try {
            result = (Node) xpath.evaluate(express, source, XPathConstants.NODE);
        } catch (XPathExpressionException e) {
            e.printStackTrace();
        }
        return result;
    }

    public static void createXML(Context context, String path, ArrayList<UseCaseBase> list) {
        XmlSerializer serializer = Xml.newSerializer();
        File file = new File(path);
        FileOutputStream fos = null;
        try {
            fos = new FileOutputStream(file);
            serializer.setOutput(fos, "utf-8");
            serializer.startDocument("utf-8", true);
            serializer.startTag(null, "usecases");

            if (list != null) {
                for (UseCaseBase item : list) {
                    item.saveParametersToXml(serializer);
                }
            }
            serializer.endTag(null, "usecases");// 结束标签
            serializer.endDocument();// 结束xml文档
            Toast.makeText(context, "生成成功。", Toast.LENGTH_SHORT);
        } catch (Exception e) {
            Toast.makeText(context, "生成失败！ ", Toast.LENGTH_SHORT);
            e.printStackTrace();
        } finally {
            try {
                fos.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public static void updateXML(Context context, String path, UseCaseBase usecase) {

    }
}
