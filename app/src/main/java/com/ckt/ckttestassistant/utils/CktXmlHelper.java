package com.ckt.ckttestassistant.utils;

import android.content.Context;
import android.util.Xml;
import android.widget.Toast;

import com.ckt.ckttestassistant.testitems.CktTestItem;
import com.ckt.ckttestassistant.testitems.TestItemBase;
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

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
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

    /**
     * 根据节点code以及对于的属性值删除对应的节点
     *
     * @param code
     * @param property
     * @param value
     */
    private void delete(String path, String code, String property, String value) {
        try {
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            DocumentBuilder bulider = factory.newDocumentBuilder();
            Document doc = bulider.parse(path);
            doc.normalize();
            Element root = doc.getDocumentElement();// 得到根节点

            NodeList listnode = doc.getElementsByTagName(code);
            for (int i = 0; i < listnode.getLength(); i++) {
                Element elink = (Element) listnode.item(i);
                String prop = elink.getAttribute(property);
                if (prop.equals(value)) {
                    if (listnode.getLength() == 1) {
                        //如果只有一条数据，那么这条数据删除之后，大节点也应该被删除
                        root.removeChild(elink.getParentNode());
                    } else {
                        elink.getParentNode().removeChild(elink);
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
    public static void readxml(Context context, String fileName, ArrayList<UseCaseBase> allUseCases) {
        try {

            InputStream is = context.getAssets().open(fileName);// 打开assets下的文件
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
                            usecase = new CktUseCase();//do something
                            usecase.setID(id);
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

    private int getMaxId(String fileName, String code, String idnum) {
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


    private void addUsecasetoxml(String fileName, ArrayList<UseCaseBase> usecases) {
        FileOutputStream out = null;
        XmlSerializer xsl = Xml.newSerializer();
        try {
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            DocumentBuilder bulider = factory.newDocumentBuilder();
            Document doc = bulider.parse(fileName);
            doc.normalize();
            Element root = doc.getDocumentElement();
            for (UseCaseBase usecase : usecases){
                Element usecaseE = doc.createElement("usecase");
                int usecaseID = getMaxId(fileName, "usecase", "0") + 1;
                usecaseE.setAttributeNS("", "id", String.valueOf(usecaseID));
                usecase.setID(usecaseID);
                for (TestItemBase item : usecase.getTestItems()) {
                    Element testitemE = doc.createElement("testitem");
                    testitemE.setAttributeNS("", "id", String.valueOf(getMaxId(fileName, "usecase", "0") + 1));
                    Element titleE = doc.createElement("title");
                    titleE.setNodeValue(item.getTitle());
                    testitemE.appendChild(titleE);
                }
                root.appendChild(usecaseE);
            }

            TransformerFactory tfactory = TransformerFactory.newInstance();
            Transformer trans = tfactory.newTransformer();
            DOMSource source = new DOMSource(doc);
            StreamResult result = new StreamResult(fileName);
            trans.transform(source, result);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    /*private void updateUsecasetoxml(String fileName, ArrayList<UseCaseBase> usecases) {
        try {
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            DocumentBuilder bulider = factory.newDocumentBuilder();
            Document doc = bulider.parse(fileName);
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
                    addUsecasetoxml(fileName, usecases);
                    return;
                }

            }

            TransformerFactory tfactory = TransformerFactory.newInstance();
            Transformer trans = tfactory.newTransformer();
            DOMSource source = new DOMSource(doc);
            StreamResult result = new StreamResult(fileName);
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
     *
     *
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
