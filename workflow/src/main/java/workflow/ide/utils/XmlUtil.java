package workflow.ide.utils;

import org.activiti.bpmn.converter.BpmnXMLConverter;
import org.activiti.bpmn.model.BpmnModel;
import org.apache.commons.codec.binary.Base64;
import org.dom4j.Attribute;
import org.dom4j.Element;

import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;
import java.io.*;
import java.util.Iterator;

public class XmlUtil {

    public static String getAttributeValue(Attribute attribute) {
        if (attribute == null) {
            return null;
        }
        return attribute.getValue();
    }

    public static String getAttributeValue(Element ele, String name) {
        Attribute attribute = ele.attribute(name);
        if (attribute == null) {
            return null;
        }
        return attribute.getValue();
    }

    public static String getContent(Element element) {
        StringBuilder builder = new StringBuilder();
//        builder.append(element.asXML());

        for (Iterator<Element> i = element.elementIterator(); i.hasNext();) {
            Element e = i.next();
            builder.append(e.asXML());
        }
        return builder.toString();
    }

    /**
     * JavaScript unescape
     * @param text
     * @return
     */
    public static String unescape(String text) {
        ScriptEngineManager factory = new ScriptEngineManager();
        ScriptEngine engine = factory.getEngineByName("JavaScript");
        try {
            return (String) engine.eval("unescape('" + text + "')");
        } catch (ScriptException e) {
            e.printStackTrace();
            return text;
        }
    }

    /**
     * JavaScript escape
     * @param text
     * @return
     */
    public static String escape(String text) {
        ScriptEngineManager factory = new ScriptEngineManager();
        ScriptEngine engine = factory.getEngineByName("JavaScript");
        try {
            return (String) engine.eval("escape('" + text + "')");
        } catch (ScriptException e) {
            e.printStackTrace();
            return text;
        }
    }

    /**
     * 转换成 BpmnModel
     * @param text
     * @return
     */
    public static BpmnModel convertToBpmnModel(String text) {
        //创建转换对象
        BpmnXMLConverter converter = new BpmnXMLConverter();

        try {
            InputStream inputStream = new ByteArrayInputStream(text.getBytes("utf8"));

            //读取xml文件
            XMLInputFactory factory = XMLInputFactory.newInstance();
            XMLStreamReader reader = factory.createXMLStreamReader(inputStream);
            BpmnModel bpmnModel = converter.convertToBpmnModel(reader);

            return bpmnModel;

        } catch (UnsupportedEncodingException | XMLStreamException e) {
            e.printStackTrace();

            return null;
        }
    }

    public static String getBase64FromInputStream(InputStream in) {
        // 将图片文件转化为字节数组字符串，并对其进行Base64编码处理
        byte[] data = null;
        // 读取图片字节数组
        try {
            ByteArrayOutputStream swapStream = new ByteArrayOutputStream();
            byte[] buff = new byte[100];
            int rc = 0;
            while ((rc = in.read(buff, 0, 100)) > 0) {
                swapStream.write(buff, 0, rc);
            }
            data = swapStream.toByteArray();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (in != null) {
                try {
                    in.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return new String(Base64.encodeBase64(data));
    }

    /**
     * 流程类型（1主、2子）
     * @param processType
     * @return
     */
    public static int parseProcessType(String processType) {

        if ("true".equals(processType)) {
            return 2;
        }

        return 1;
    }

    /**
     * 流程类型（1主、2子）
     * @param processType
     * @return
     */
    public static String unparseProcessType(int processType) {

        if (processType == 2) {
            return "true";
        }

        return "false";
    }


    public static void main(String[] args) {
        String s = escape("<xml>");

        System.out.println(s);

        String t = unescape(s);

        System.out.println(t);
    }
}
