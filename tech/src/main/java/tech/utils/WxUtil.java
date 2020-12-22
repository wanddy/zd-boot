package tech.utils;

import com.alibaba.fastjson.JSON;
import org.dom4j.Document;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;

import javax.servlet.http.HttpServletRequest;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.*;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.*;

/**
 * 微信工具类
 */
public class WxUtil {
 
    private static final String CHAR_SET = "UTF-8";

    public static String sendGet(String url, Map<String,Object> params){
        StringBuilder responseStr = null;
        StringBuilder paramsStr = new StringBuilder();
        if(params != null && params.size() > 0){
            for(Map.Entry<String,Object> entry : params.entrySet()){
                paramsStr.append(entry.getKey());
                paramsStr.append("=");
                try {
                    paramsStr.append(URLEncoder.encode(String.valueOf(entry.getValue()),CHAR_SET));
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                }
                paramsStr.append("&");
            }
        }
        URL URLstr = null;
        BufferedReader bufr = null;
        HttpURLConnection httpURLConnection = null;
        try {
            if(paramsStr != null && paramsStr.length() > 0){
                url = url + "?" + paramsStr.substring(0,paramsStr.length() - 1);
            }
            URLstr = new URL(url);
            httpURLConnection = (HttpURLConnection) URLstr.openConnection();
            httpURLConnection.setRequestProperty("Content-Type","application/x-www-form-urlencoded");
            httpURLConnection.connect();
            responseStr = new StringBuilder();
            bufr = new BufferedReader(new InputStreamReader(httpURLConnection.getInputStream(),CHAR_SET));
            String str = null;
            while((str = bufr.readLine()) != null){
                responseStr.append(str);
            }
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }finally {
            httpURLConnection.disconnect();
            if (bufr != null){
                try {
                    bufr.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return  responseStr.toString();
    }
 
 
    public static String sha1(String data) throws NoSuchAlgorithmException {
        //信息摘要器                                算法名称
        MessageDigest md = MessageDigest.getInstance("SHA1");
        //把字符串转为字节数组
        byte[] b = data.getBytes();
        //使用指定的字节来更新我们的摘要
        md.update(b);
        //获取密文  （完成摘要计算）
        byte[] b2 = md.digest();
        //获取计算的长度
        int len = b2.length;
        //16进制字符串
        String str = "0123456789abcdef";
        //把字符串转为字符串数组
        char[] ch = str.toCharArray();
 
        //创建一个40位长度的字节数组
        char[] chs = new char[len*2];
        //循环20次
        for(int i=0,k=0;i<len;i++) {
            byte b3 = b2[i];//获取摘要计算后的字节数组中的每个字节
            // >>>:无符号右移
            // &:按位与
            //0xf:0-15的数字
            chs[k++] = ch[b3 >>> 4 & 0xf];
            chs[k++] = ch[b3 & 0xf];
        }
 
        //字符数组转为字符串
        return new String(chs);
    }
 
    public  static String sendPost(String urlString, Map<String, Object> params){
        String rs = "";
        String encode = "UTF-8";
        String json = JSON.toJSONString(params);
        try {
            // 建立连接
            URL url = new URL(urlString);
            HttpURLConnection httpConn = (HttpURLConnection) url
                    .openConnection();
            // //设置连接属性
            // 使用 URL 连接进行输出
            httpConn.setDoOutput(true);
            // 使用 URL 连接进行输入
            httpConn.setDoInput(true);
            // 忽略缓存
            httpConn.setUseCaches(false);
            httpConn.setConnectTimeout(50*60*1000);
            httpConn.setReadTimeout(50*60*1000);
            // 设置URL请求方法
            httpConn.setRequestMethod("POST");
            String requestString = "客服端要以以流方式发送到服务端的数据...";
            // 设置请求属性
            // 获得数据字节数据，请求数据流的编码，必须和下面服务器端处理请求流的编码一致
            byte[] requestStringBytes = requestString.getBytes(encode);
 			/*httpConn.setRequestProperty("Content-length", ""
 					+ requestStringBytes.length);
 			httpConn.setRequestProperty("Content-Type",
 					"application/octet-stream");*/
            // 维持长连接
            httpConn.setRequestProperty("Connection", "Keep-Alive");
            httpConn.setRequestProperty("Charset", encode);
            // 设置文件类型:
            httpConn.setRequestProperty("Content-Type","application/json; charset=UTF-8");
            // 设置接收类型否则返回415错误
            //conn.setRequestProperty("accept","*/*")此处为暴力方法设置接受所有类型，以此来防范返回415;
            httpConn.setRequestProperty("accept","application/json");
            // 往服务器里面发送数据
            if (json != null) {
                byte[] writebytes = json.getBytes(encode);
                // 设置文件长度
                httpConn.setRequestProperty("Content-Length", String.valueOf(writebytes.length));
                OutputStream outwritestream = httpConn.getOutputStream();
                outwritestream.write(json.getBytes(encode));
                outwritestream.flush();
                outwritestream.close();
            }
 
            // 建立输出流，并写入数据
            OutputStream outputStream = httpConn.getOutputStream();
            outputStream.write(requestStringBytes);
            outputStream.close();
            // 获得响应状态
            int responseCode = httpConn.getResponseCode();
            // 连接成功
            if (HttpURLConnection.HTTP_OK == responseCode) {
                // 当正确响应时处理数据
                StringBuffer sb = new StringBuffer();
                String readLine;
                BufferedReader responseReader;
                // 处理响应流，必须与服务器响应流输出的编码一致
                responseReader = new BufferedReader(new InputStreamReader(
                        httpConn.getInputStream(), encode));
                while ((readLine = responseReader.readLine()) != null) {
                    sb.append(readLine).append("\n");
                }
                responseReader.close();
                rs = sb.toString();
            }
        } catch (Exception ex) {
            System.out.println("httpConnection异常"+ex.toString());
            ex.printStackTrace();
        }
        return rs;
    }

//    /**
//     * XML格式字符串转换为Map
//     *
//     * @param strXML XML字符串
//     * @return XML数据转换后的Map
//     * @throws Exception
//     */
//    public static Map<String, String> xmlToMap(String strXML) throws Exception {
//        try {
//            Map<String, String> data = new HashMap<>();
//            DocumentBuilderFactory documentBuilderFactory = DocumentBuilderFactory.newInstance();
//            DocumentBuilder documentBuilder = documentBuilderFactory.newDocumentBuilder();
//            InputStream stream = new ByteArrayInputStream(strXML.getBytes("UTF-8"));
//            org.w3c.dom.Document doc = documentBuilder.parse(stream);
//            doc.getDocumentElement().normalize();
//            NodeList nodeList = doc.getDocumentElement().getChildNodes();
//            for (int idx = 0; idx < nodeList.getLength(); ++idx) {
//                Node node = nodeList.item(idx);
//                if (node.getNodeType() == Node.ELEMENT_NODE) {
//                    org.w3c.dom.Element element = (org.w3c.dom.Element) node;
//                    data.put(element.getNodeName(), element.getTextContent());
//                }
//            }
//            try {
//                stream.close();
//            } catch (Exception ex) {
//                // do nothing
//            }
//            return data;
//        } catch (Exception ex) {
//            throw ex;
//        }
//    }

    /**
     * xml转换为map集合
     *
     * @param request
     * @return
     * @throws Exception
     */
    public static Map<String, String> xmlToMap(HttpServletRequest request) throws Exception {
        Map<String, String> map = new HashMap<>();
        SAXReader reader = new SAXReader();

        InputStream ins = request.getInputStream();
        Document doc = reader.read(ins);
        Element root = doc.getRootElement();

        List<Element> list = root.elements();
        for (Element element : list) {
            map.put(element.getName(), element.getText());
        }
        ins.close();
        return map;
    }


    /**
     * 将Map转换为XML格式的字符串
     *
     * @param data Map类型数据
     * @return XML格式的字符串
     * @throws Exception
     */
    public static String mapToXml(Map<String, String> data) throws Exception {
        DocumentBuilderFactory documentBuilderFactory = DocumentBuilderFactory.newInstance();
        DocumentBuilder documentBuilder= documentBuilderFactory.newDocumentBuilder();
        org.w3c.dom.Document document = documentBuilder.newDocument();
        org.w3c.dom.Element root = document.createElement("xml");
        document.appendChild(root);

//        xml(data,document,root);
        for (String key: data.keySet()) {
            String value = data.get(key);
            if (value == null) {
                value = "";
            }
            value = value.trim();
            org.w3c.dom.Element filed = document.createElement(key);
            filed.appendChild(document.createTextNode(value));
            root.appendChild(filed);
        }
        TransformerFactory tf = TransformerFactory.newInstance();
        Transformer transformer = tf.newTransformer();
        DOMSource source = new DOMSource(document);
        transformer.setOutputProperty(OutputKeys.ENCODING, "UTF-8");
        transformer.setOutputProperty(OutputKeys.INDENT, "yes");
        StringWriter writer = new StringWriter();
        StreamResult result = new StreamResult(writer);
        transformer.transform(source, result);
        String output = writer.getBuffer().toString(); //.replaceAll("\n|\r", "");
        try {
            writer.close();
        } catch (Exception ex) {

        }
        return output;
    }

    private void xml(Map map,org.w3c.dom.Element root,org.w3c.dom.Document document){
        Set set = map.keySet();
        for (Iterator it = set.iterator(); it.hasNext();) {
            String key = (String) it.next();
            Object value = map.get(key);
            if (null == value) {
                value = "";
            }
            if (value.getClass().getName().equals("java.util.ArrayList")) {
                ArrayList list = (ArrayList) map.get(key);

                for (int i = 0; i < list.size(); i++) {
                    HashMap hm = (HashMap) list.get(i);
                    xml(hm, root,document);
                }
                value = value.toString().trim();
                org.w3c.dom.Element filed = document.createElement(key);
                filed.appendChild(document.createTextNode(value.toString()));
                root.appendChild(filed);
            } else {
                if (value instanceof HashMap) {
                    xml((HashMap)value, root,document);
                } else {
                    value = value.toString().trim();
                    org.w3c.dom.Element filed = document.createElement(key);
                    filed.appendChild(document.createTextNode(value.toString()));
                    root.appendChild(filed);
                }

            }
        }
    }
}