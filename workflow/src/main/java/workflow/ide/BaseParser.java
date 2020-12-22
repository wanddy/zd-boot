package workflow.ide;

import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.dom4j.io.OutputFormat;
import org.dom4j.io.SAXReader;
import org.dom4j.io.XMLWriter;
import org.xml.sax.EntityResolver;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import workflow.ide.core.*;
import workflow.ide.core.ext.*;
import workflow.ide.utils.XmlUtil;

import java.io.*;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public abstract class BaseParser {

    /**
     * 解析XML获取流程定义
     *
     * @param file 文件
     * @return 流程定义对象
     */
    protected Definitions getDefinitions(File file) throws DocumentException {
        SAXReader reader = new SAXReader();

        Document document = reader.read(file);

        return getDefinitions(document);
    }

    /**
     * 转成字符串
     * @param definitions
     * @return
     * @throws DocumentException
     * @throws IOException
     */
    protected String toString(Definitions definitions) throws DocumentException, IOException {
        Document document = DocumentHelper.createDocument();// 建立document对象，用来操作xml文件

        //Element bPMNDiagramE = rootE.addElement("BPMNDiagram");

        setDefinitions(document, definitions);


        // 美化格式
        OutputFormat format = OutputFormat.createPrettyPrint();
//            format.setExpandEmptyElements(true);

        StringWriter sw = new StringWriter();
        format.setEncoding("utf-8");

        XMLWriter writer = new XMLWriter(sw, format);

        writer.write(document);

        writer.close();

        return sw.toString();


    }

//    /**
//     * 解析XML获取流程定义
//     *
//     * @param string 文件
//     * @return 流程定义对象
//     */
//    public Definitions getDefinitions(String string) throws DocumentException, UnsupportedEncodingException {
//        return getDefinitions(new ByteArrayInputStream(string.getBytes("UTF8")));
//    }

    class IgnoreDTDEntityResolver implements EntityResolver {

        @Override
        public InputSource resolveEntity(String publicId, String systemId)
                throws SAXException, IOException {
            return new InputSource(new ByteArrayInputStream("<?xml version='1.0' encoding='UTF-8'?>".getBytes()));
        }

    }

    private String saxParserXml(String xmlStr) {
        /*try {
            //1.获取sax解析器的工厂对象
            SAXParserFactory mParserFactory = SAXParserFactory.newInstance();
            // 2.通过工厂对象 mParserFactory创建解析器对象mParser
            SAXParser parser = mParserFactory.newSAXParser();

            XMLReader reader = parser.getXMLReader();
            reader.setContentHandler(new DefaultHandler(){
                @Override
                public void startDocument() throws SAXException {
                    super.startDocument();
                    System.out.println("开始解析整个xml文档");
                }

                @Override
                public void endDocument() throws SAXException {
                    super.endDocument();
                    System.out.println("结束解析整个xml文档");
                }

                @Override
                public void startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException {
                    super.startElement(uri, localName, qName, attributes);

                    System.out.println("开始解析元素："+qName);
                    if("bpmn2:definitions".equals(qName)){
                        System.out.println("开始解析元素AAA："+qName);
                    }
                }

            });

            //发起解析
            StringReader read = new StringReader(xmlStr);
            InputSource source = new InputSource(read);
            reader.parse(source);

            //3.创建事件处理器对象mHanlder
//            SaxHandler dh = new SaxHandler();
//            //4.利用指定处理器解析指定文件
//            mParser.parse(Constant.PARSER_XML_DIR + File.separator + "test.xml", mHanlder);
        } catch (Exception e) {
            e.printStackTrace();
        }*/

        StringBuffer sb = new StringBuffer(xmlStr);
        int iDefinition = xmlStr.indexOf("<bpmn2:definitions ") + "<bpmn2:definitions ".length();
        if (xmlStr.indexOf("xmlns:dc") == -1) {
            sb.insert(iDefinition, "xmlns:dc=\"http://www.omg.org/spec/DD/20100524/DC\" ");
        }

        if (xmlStr.indexOf("xmlns:di") == -1) {
            sb.insert(iDefinition, "xmlns:di=\"http://www.omg.org/spec/DD/20100524/DI\" ");
        }

        return sb.toString();
    }

    /**
     * 解析XML获取流程定义
     *
     * @param string 文件
     * @return 流程定义对象
     */
    protected Definitions getDefinitions(String string, String type) throws DocumentException, SAXException {

        // 解析XML ==>>
        if ("C".equals(type)) {
            string = saxParserXml(string);
        }
        // 解析XML <<==

        SAXReader reader = new SAXReader();
//        reader.setFeature("http://apache.org/xml/features/nonvalidating/load-external-dtd", false);
        //reader.setFeature("http://www.omg.org/spec/DD/20100524/DC", false);

        Document document = null;
        try {
            document = reader.read(new ByteArrayInputStream(string.getBytes("utf8")));
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }

        return getDefinitions(document);

    }

    /**
     * 解析XML获取流程定义
     *
     * @param document Document
     * @return 流程定义对象
     */
    protected Definitions getDefinitions(Document document) throws DocumentException {
        Element rootE = document.getRootElement();
        Definitions definitions = new Definitions();
        definitions.setId(XmlUtil.getAttributeValue(rootE, "id"));

        Element processE = rootE.element("process");

        // 流程定义
        definitions.setProcess(getProcess(processE));

        // 消息
        getMessages(rootE, definitions);

        // 信号
        getSignals(rootE, definitions);

        // 图形节点
        Element bPMNDiagramE = rootE.element("BPMNDiagram");

        definitions.setBPMNDiagram(bPMNDiagramE.asXML());

        //String s = XmlUtil.getContent(bPMNDiagramE);
        // System.out.println(definitions.getBPMNDiagram());
        return definitions;
    }

    /**
     * 解析XML获取流程节点
     *
     * @param processE 流程节点
     * @return 流程对象
     */
    protected BpmnProcess getProcess(Element processE) {
        BpmnProcess process = new BpmnProcess();

//        process.setId(XmlUtil.getAttributeValue(processE, "id"));
//        process.setName(XmlUtil.getAttributeValue(processE, "name"));
//        process.setIsExecutable(XmlUtil.getAttributeValue(processE, "isExecutable"));
        process.setProcessType(XmlUtil.parseProcessType(XmlUtil.getAttributeValue(processE, "processType")));

//        // 默认属性
//        getDefaultAttr(processE, process);
//
//        // 开始事件
//        getStartEvents(processE, process);
//
//        // 结束事件
//        getEndEvents(processE, process);
//
//        // 用户任务
//        getUserTask(processE, process);
//
//        // 顺序流
//        getSequenceFlow(processE, process);
//
//        // 排他网关
//        getGateway(processE, process, "exclusiveGateway");
//
//        // 并行网关
//        getGateway(processE, process, "parallelGateway");
//
//        // 边界事件
//        getBoundaryEvent(processE, process);
//
//        // 捕获事件
//        getCatchEvent(processE, process);
//
//        // 捕获事件
//        getThrowEvent(processE, process);
//
//        // 调用活动
//        getCallActivity(processE, process);
//
//        // 自定义变量
//        getProcessDataObject(processE, process);

        // 流程节点
        getBaseProcess(processE, process);

        return process;
    }

    /**
     * 扩展子查询
     * @param processE
     * @param process
     */
    private void getSubProcess(Element processE, BaseProcess process) {
        List<SubProcess> subProcessList = new ArrayList<SubProcess>();
        List nodes = processE.elements("subProcess");
        for (Iterator it = nodes.iterator(); it.hasNext();) {
            Element elm = (Element) it.next();

            SubProcess subProcess = new SubProcess();

            getBaseProcess(elm, subProcess);

            subProcessList.add(subProcess);
        }

        process.setSubProcessList(subProcessList);
    }

    /**
     * 解析XML获取流程节点
     *
     * @param processE 流程节点
     * @return 流程对象
     */
    protected void getBaseProcess(Element processE, BaseProcess process) {

        process.setId(XmlUtil.getAttributeValue(processE, "id"));
        process.setName(XmlUtil.getAttributeValue(processE, "name"));
        process.setIsExecutable(XmlUtil.getAttributeValue(processE, "isExecutable"));

        // 默认属性
        getDefaultAttr(processE, process);

        // 开始事件
        getStartEvents(processE, process);

        // 结束事件
        getEndEvents(processE, process);

        // 任务
        getTask(processE, process);

        // 用户任务
        getUserTask(processE, process);

        // 顺序流
        getSequenceFlow(processE, process);

        // 排他网关
        getGateway(processE, process, "exclusiveGateway");

        // 并行网关
        getGateway(processE, process, "parallelGateway");

        // 兼容网关
        getGateway(processE, process, "inclusiveGateway");

        // 事件网关
        getGateway(processE, process, "eventBasedGateway");

        // 边界事件
        getBoundaryEvent(processE, process);

        // 捕获事件
        getCatchEvent(processE, process);

        // 捕获事件
        getThrowEvent(processE, process);

        // 调用活动
        getCallActivity(processE, process);

        // 自定义变量
        getProcessDataObject(processE, process);

        // 扩展子流程
        getSubProcess(processE, process);

    }

    /**
     * 用户任务
     * @param processE
     * @param process
     */
    protected void getTask(Element processE, BaseProcess process) {
        List<Task> tasks = new ArrayList<Task>();
        List nodes = processE.elements("task");
        for (Iterator it = nodes.iterator(); it.hasNext();) {
            Element elm = (Element) it.next();

            Task task = new Task();
            task.setId(XmlUtil.getAttributeValue(elm, "id"));
            task.setName(XmlUtil.getAttributeValue(elm, "name"));

            getDefaultAttr(elm, task);
            tasks.add(task);

            // 扩展属性
//            getUserTaskExtensionElements(elm, userTask);
        }

        process.setTasks(tasks);
    }

    /**
     * 用户任务
     * @param processE
     * @param process
     */
    protected void getUserTask(Element processE, BaseProcess process) {
        List<UserTask> userTasks = new ArrayList<UserTask>();
        List nodes = processE.elements("userTask");
        for (Iterator it = nodes.iterator(); it.hasNext();) {
            Element elm = (Element) it.next();

            UserTask userTask = new UserTask();
            userTask.setId(XmlUtil.getAttributeValue(elm, "id"));
            userTask.setName(XmlUtil.getAttributeValue(elm, "name"));
            userTask.setAssignee(XmlUtil.getAttributeValue(elm, "assignee"));
            userTask.setCandidateUsers(XmlUtil.getAttributeValue(elm, "candidateUsers"));
            userTask.setCandidateGroups(XmlUtil.getAttributeValue(elm, "candidateGroups"));

            // 多实例类型
            getMultiInstanceLoopCharacteristics(elm, userTask);

            getDefaultAttr(elm, userTask);
            userTasks.add(userTask);

            // 扩展属性
//            getUserTaskExtensionElements(elm, userTask);
        }

        process.setUserTasks(userTasks);
    }

    /**
     * 多实例类型 > 子节点
     * @param userTaskE
     * @param userTask
     */
    protected void getMultiInstanceLoopCharacteristics(Element userTaskE, IMultiInstanceLoopCharacteristics userTask) {

        Element eleM = userTaskE.element("multiInstanceLoopCharacteristics");

        if (eleM == null) {
            return;
        }
        MultiInstanceLoopCharacteristics multiInstanceLoopCharacteristics = new MultiInstanceLoopCharacteristics();
        userTask.setMultiInstanceLoopCharacteristics(multiInstanceLoopCharacteristics);

        multiInstanceLoopCharacteristics.setIsSequential(XmlUtil.getAttributeValue(eleM, "isSequential"));
        multiInstanceLoopCharacteristics.setCollection(XmlUtil.getAttributeValue(eleM, "collection"));
        multiInstanceLoopCharacteristics.setElementVariable(XmlUtil.getAttributeValue(eleM, "elementVariable"));

        Element eleL = eleM.element("loopCardinality");
        if (eleL != null) {
            multiInstanceLoopCharacteristics.setLoopCardinality(eleL.getText());
        }

        Element eleC = eleM.element("completionCondition");
        if (eleC != null) {
            multiInstanceLoopCharacteristics.setCompletionCondition(eleC.getText());
        }
    }

    /**
     * 开始事件
     * @param processE
     * @param process
     */
    protected void getStartEvents(Element processE, BaseProcess process) {
        List<StartEvent> startEvents = new ArrayList<StartEvent>();
        List nodes = processE.elements("startEvent");
        for (Iterator it = nodes.iterator(); it.hasNext();) {
            Element elm = (Element) it.next();

            StartEvent startEvent = new StartEvent();
            startEvent.setId(XmlUtil.getAttributeValue(elm, "id"));
            startEvent.setName(XmlUtil.getAttributeValue(elm, "name"));

            // 定时开始事件
            getTimerEventDefinition(elm, startEvent);
            // 信号定时事件
            getSignalEventDefinition(elm, startEvent);
            // 消息定时事件
            getMessageEventDefinition(elm, startEvent);

            getDefaultAttr(elm, startEvent);
            startEvents.add(startEvent);
        }

        process.setStartEvents(startEvents);
    }

    /**
     * 定时事件
     * @param eventDefinitionE
     * @param eventDefinition
     */
    protected void getTimerEventDefinition(Element eventDefinitionE, IEventDefinition eventDefinition) {
        Element timerEventDefinitionE = eventDefinitionE.element("timerEventDefinition");

        if (timerEventDefinitionE == null) {
            return;
        }

        TimerEventDefinition timerEventDefinition = new TimerEventDefinition();
        eventDefinition.setTimerEventDefinition(timerEventDefinition);

        Element timeDurationE = timerEventDefinitionE.element("timeDuration");
        if (timeDurationE != null) {
            timerEventDefinition.setTimeDuration(timeDurationE.getText());
        }

        Element timeDateE = timerEventDefinitionE.element("timeDate");
        if (timeDateE != null) {
            timerEventDefinition.setTimeDate(timeDateE.getText());
        }

        Element timeCycleE = timerEventDefinitionE.element("timeCycle");
        if (timeCycleE != null) {
            timerEventDefinition.setTimeCycle(timeCycleE.getText());
        }
    }

    /**
     * 信号事件
     * @param eventDefinitionE
     * @param eventDefinition
     */
    protected void getSignalEventDefinition(Element eventDefinitionE, IEventDefinition eventDefinition) {
        Element signalEventDefinitionE = eventDefinitionE.element("signalEventDefinition");

        if (signalEventDefinitionE == null) {
            return;
        }

        SignalEventDefinition signalEventDefinition = new SignalEventDefinition();
        eventDefinition.setSignalEventDefinition(signalEventDefinition);

        signalEventDefinition.setSignalRef(XmlUtil.getAttributeValue(signalEventDefinitionE, "signalRef"));
    }

    /**
     * 消息事件
     * @param eventDefinitionE
     * @param eventDefinition
     */
    protected void getMessageEventDefinition(Element eventDefinitionE, IEventDefinition eventDefinition) {
        Element messageEventDefinitionE = eventDefinitionE.element("messageEventDefinition");

        if (messageEventDefinitionE == null) {
            return;
        }

        MessageEventDefinition messageEventDefinition = new MessageEventDefinition();
        eventDefinition.setMessageEventDefinition(messageEventDefinition);

        messageEventDefinition.setMessageRef(XmlUtil.getAttributeValue(messageEventDefinitionE, "messageRef"));

    }

    /**
     * 补偿事件
     * @param eventDefinitionE
     * @param eventDefinition
     */
    protected void getCompensateEventDefinition(Element eventDefinitionE, IEventDefinition eventDefinition) {
        Element compensateEventDefinitionE = eventDefinitionE.element("compensateEventDefinition");

        if (compensateEventDefinitionE == null) {
            return;
        }

        CompensateEventDefinition compensateEventDefinition = new CompensateEventDefinition();
        eventDefinition.setCompensateEventDefinition(compensateEventDefinition);

    }

    /**
     * 消息事件
     * @param eventDefinitionE
     * @param eventDefinition
     */
    protected void getErrorEventDefinition(Element eventDefinitionE, IEventDefinition eventDefinition) {
        Element errorEventDefinitionE = eventDefinitionE.element("errorEventDefinition");

        if (errorEventDefinitionE == null) {
            return;
        }

        ErrorEventDefinition errorEventDefinition = new ErrorEventDefinition();
        eventDefinition.setErrorEventDefinition(errorEventDefinition);

        errorEventDefinition.setErrorRef(XmlUtil.getAttributeValue(errorEventDefinitionE, "errorRef"));

    }

    /**
     * 结束事件
     * @param processE
     * @param process
     */
    protected void getEndEvents(Element processE, BaseProcess process) {
        List<EndEvent> endEvents = new ArrayList<EndEvent>();
        List nodes = processE.elements("endEvent");
        for (Iterator it = nodes.iterator(); it.hasNext();) {
            Element elm = (Element) it.next();

            EndEvent endEvent = new EndEvent();
            endEvent.setId(XmlUtil.getAttributeValue(elm, "id"));
            endEvent.setName(XmlUtil.getAttributeValue(elm, "name"));

            // 定时开始事件
            getErrorEventDefinition(elm, endEvent);

            getDefaultAttr(elm, endEvent);
            endEvents.add(endEvent);
        }

        process.setEndEvents(endEvents);
    }

    /**
     * 消息
     * @param processE
     * @param definitions
     */
    protected void getMessages(Element processE, Definitions definitions) {
        List<Message> messages = new ArrayList<Message>();
        List nodes = processE.elements("message");
        for (Iterator it = nodes.iterator(); it.hasNext();) {
            Element elm = (Element) it.next();

            Message message = new Message();
            message.setId(XmlUtil.getAttributeValue(elm, "id"));
            message.setName(XmlUtil.getAttributeValue(elm, "name"));

            messages.add(message);
        }

        definitions.setMessages(messages);
    }

    /**
     * 信号
     * @param processE
     * @param definitions
     */
    protected void getSignals(Element processE, Definitions definitions) {
        List<Signal> signals = new ArrayList<Signal>();
        List nodes = processE.elements("signal");
        for (Iterator it = nodes.iterator(); it.hasNext();) {
            Element elm = (Element) it.next();

            Signal signal = new Signal();
            signal.setId(XmlUtil.getAttributeValue(elm, "id"));
            signal.setName(XmlUtil.getAttributeValue(elm, "name"));
            signal.setScope(XmlUtil.getAttributeValue(elm, "scope"));

            signals.add(signal);
        }

        definitions.setSignals(signals);
    }

    /**
     * 边界事件
     * @param processE
     * @param process
     */
    protected void getBoundaryEvent(Element processE, BaseProcess process) {
        List<BoundaryEvent> boundaryEvents = new ArrayList<BoundaryEvent>();
        List nodes = processE.elements("boundaryEvent");

        for (Iterator it = nodes.iterator(); it.hasNext();) {
            Element elm = (Element) it.next();

            BoundaryEvent boundaryEvent = new BoundaryEvent();
            boundaryEvent.setId(XmlUtil.getAttributeValue(elm, "id"));
            boundaryEvent.setName(XmlUtil.getAttributeValue(elm, "name"));
            boundaryEvent.setAttachedToRef(XmlUtil.getAttributeValue(elm, "attachedToRef"));

            // 定时开始事件
            getTimerEventDefinition(elm, boundaryEvent);
            // 信号定时事件
            getSignalEventDefinition(elm, boundaryEvent);
            // 消息定时事件
            getMessageEventDefinition(elm, boundaryEvent);
            // 补偿事件
            getCompensateEventDefinition(elm, boundaryEvent);
            // 错误事件
            getErrorEventDefinition(elm, boundaryEvent);

            boundaryEvents.add(boundaryEvent);
        }

        process.setBoundaryEvents(boundaryEvents);

    }

    /**
     * 捕获事件
     * @param processE
     * @param process
     */
    protected void getCatchEvent(Element processE, BaseProcess process) {
        List<IntermediateEvent> catchEvents = new ArrayList<IntermediateEvent>();
        List nodes = processE.elements("intermediateCatchEvent");

        for (Iterator it = nodes.iterator(); it.hasNext();) {
            Element elm = (Element) it.next();

            IntermediateEvent catchEvent = new IntermediateEvent();
            catchEvent.setId(XmlUtil.getAttributeValue(elm, "id"));
            catchEvent.setName(XmlUtil.getAttributeValue(elm, "name"));

            // 定时开始事件
            getTimerEventDefinition(elm, catchEvent);
            // 信号定时事件
            getSignalEventDefinition(elm, catchEvent);
            // 消息定时事件
            getMessageEventDefinition(elm, catchEvent);

            catchEvents.add(catchEvent);
        }

        process.setIntermediateCatchEvents(catchEvents);

    }

    /**
     * 捕获事件
     * @param processE
     * @param process
     */
    protected void getThrowEvent(Element processE, BaseProcess process) {
        List<IntermediateEvent> throwEvents = new ArrayList<IntermediateEvent>();
        List nodes = processE.elements("intermediateThrowEvent");

        for (Iterator it = nodes.iterator(); it.hasNext();) {
            Element elm = (Element) it.next();

            IntermediateEvent throwEvent = new IntermediateEvent();
            throwEvent.setId(XmlUtil.getAttributeValue(elm, "id"));
            throwEvent.setName(XmlUtil.getAttributeValue(elm, "name"));

            // 信号定时事件
            getSignalEventDefinition(elm, throwEvent);

            throwEvents.add(throwEvent);
        }

        process.setIntermediateThrowEvent(throwEvents);

    }

    /**
     * 调用活动
     * @param processE
     * @param process
     */
    protected void getCallActivity(Element processE, BaseProcess process) {
        List<CallActivity> callActivitys = new ArrayList<CallActivity>();
        List nodes = processE.elements("callActivity");

        for (Iterator it = nodes.iterator(); it.hasNext();) {
            Element elm = (Element) it.next();

            CallActivity callActivity = new CallActivity();
            callActivity.setId(XmlUtil.getAttributeValue(elm, "id"));
            callActivity.setName(XmlUtil.getAttributeValue(elm, "name"));
            callActivity.setCalledElement(XmlUtil.getAttributeValue(elm, "calledElement"));

            // 多实例类型
            getMultiInstanceLoopCharacteristics(elm, callActivity);

            Element elmX = elm.element("extensionElements");
            if (elmX != null) {

                // 输入参数
                callActivity.setInCallParameters(getCallParameter(elmX,"in"));

                // 输入参数
                callActivity.setOutCallParameters(getCallParameter(elmX,"out"));
            }

            callActivitys.add(callActivity);
        }

        process.setCallActivitys(callActivitys);
    }

    /**
     * 调用活动参数
     * @param callActivityE
     * @param paramType
     * @return
     */
    protected List<CallParameter> getCallParameter(Element callActivityE, String paramType) {
        List<CallParameter> callParameters = new ArrayList<CallParameter>();

        List nodes = callActivityE.elements(paramType);

        for (Iterator it = nodes.iterator(); it.hasNext();) {
            Element inE = (Element) it.next();

            CallParameter callParameter = new CallParameter();
            callParameter.setSource(XmlUtil.getAttributeValue(inE, "source"));
            callParameter.setSourceExpression(XmlUtil.getAttributeValue(inE, "sourceExpression"));
            callParameter.setTarget(XmlUtil.getAttributeValue(inE, "target"));
            callParameter.setTargetExpression(XmlUtil.getAttributeValue(inE, "targetExpression"));

            callParameters.add(callParameter);
        }

        return callParameters;
    }

    /**
     * 顺序流
     * @param processE
     * @param process
     */
    protected void getSequenceFlow(Element processE, BaseProcess process) {
        List<SequenceFlow> sequenceFlows = new ArrayList<SequenceFlow>();
        List nodes = processE.elements("sequenceFlow");
        for (Iterator it = nodes.iterator(); it.hasNext();) {
            Element elm = (Element) it.next();

            SequenceFlow sequenceFlow = new SequenceFlow();
            sequenceFlow.setId(XmlUtil.getAttributeValue(elm, "id"));
            sequenceFlow.setSourceRef(XmlUtil.getAttributeValue(elm, "sourceRef"));
            sequenceFlow.setTargetRef(XmlUtil.getAttributeValue(elm, "targetRef"));
            sequenceFlow.setSkipExpression(XmlUtil.getAttributeValue(elm, "skipExpression"));

            Element condE = elm.element("conditionExpression");
            if (condE != null) {
                ConditionExpression conditionExpression = new ConditionExpression();
                conditionExpression.setType(XmlUtil.getAttributeValue(condE, "xsi:type"));
                conditionExpression.setValue(condE.getText());
                sequenceFlow.setConditionExpression(conditionExpression);
            }

            getDefaultAttr(elm, sequenceFlow);
            sequenceFlows.add(sequenceFlow);
        }

        process.setSequenceFlows(sequenceFlows);
    }

    /**
     * 排他网关
     * @param processE
     * @param process
     */
    protected void getGateway(Element processE, BaseProcess process, String gatewayType) {
        List<Gateway> gateways = new ArrayList<Gateway>();
        List nodes = processE.elements(gatewayType);
        for (Iterator it = nodes.iterator(); it.hasNext();) {
            Element elm = (Element) it.next();

            Gateway gateway = new Gateway();
            gateway.setId(XmlUtil.getAttributeValue(elm, "id"));
            gateway.setName(XmlUtil.getAttributeValue(elm, "name"));
//            gateway.setAsync(XmlUtil.getAttributeValue(elm, "async"));
//            gateway.setExclusive(XmlUtil.getAttributeValue(elm, "exclusive"));

            getDefaultAttr(elm, gateway);
            gateways.add(gateway);
        }

        if ("exclusiveGateway".equals(gatewayType)) {
            process.setExclusiveGateways(gateways);
        } else if ("parallelGateway".equals(gatewayType)) {
            process.setParallelGateways(gateways);
        } else if ("inclusiveGateway".equals(gatewayType)) {
            process.setInclusiveGateways(gateways);
        } else if ("eventBasedGateway".equals(gatewayType)) {
            process.setEventBasedGateways(gateways);
        }
    }

    /**
     * 默认属性（文档，监听器）
     * @param wfE
     * @param wfObject
     */
    protected void getDefaultAttr(Element wfE, BaseWorkflowObject wfObject) {
        wfObject.setAsync(XmlUtil.getAttributeValue(wfE, "async"));
        wfObject.setExclusive(XmlUtil.getAttributeValue(wfE, "exclusive"));

        Element elm = wfE.element("documentation");
        if (elm != null) {
            wfObject.setDocumentation(elm.getText());
        }

        getExtensionElements(wfE, wfObject);
    }

    /**
     * 扩展属性
     * @param wfE
     * @param wfObject
     */
    protected void getExtensionElements(Element wfE, BaseWorkflowObject wfObject) {
        Element extE = wfE.element("extensionElements");

        if (extE == null) {
            return;
        }

        ExtensionElements extensionElements = wfObject.getExtensionElements();
        if (extensionElements == null) {
            extensionElements = new ExtensionElements();
            wfObject.setExtensionElements(extensionElements);
        }

        getExtExecutionListener(extE, extensionElements);

        getExtFormData(extE, extensionElements);
    }

    /**
     * 扩展（监听器）getExtExecutionListener
     * @param extE
     * @param extensionElements
     */
    protected void getExtExecutionListener(Element extE, ExtensionElements extensionElements) {
        List<ExecutionListener> executionListeners = new ArrayList<ExecutionListener>();
        List nodes = extE.elements("executionListener");

        if (nodes == null) {
            return;
        }
        for (Iterator it = nodes.iterator(); it.hasNext();) {
            Element elmEx = (Element) it.next();

            ExecutionListener executionListener = new ExecutionListener();
            executionListener.setEvent(XmlUtil.getAttributeValue(elmEx, "event"));
            executionListener.setExpression(XmlUtil.getAttributeValue(elmEx, "expression"));
            executionListener.setDelegateExpression(XmlUtil.getAttributeValue(elmEx, "delegateExpression"));
            executionListener.setListenerClass(XmlUtil.getAttributeValue(elmEx, "class"));

            getExecutionListenerFields(elmEx, executionListener);
            executionListeners.add(executionListener);
        }

        extensionElements.setExecutionListeners(executionListeners);

    }

    /**
     * 监听属性
     * @param elmEx
     * @param executionListener
     */
    protected void getExecutionListenerFields(Element elmEx, ExecutionListener executionListener) {
        List<Field> fields = new ArrayList<Field>();
        List nodes = elmEx.elements("field");
        for (Iterator it = nodes.iterator(); it.hasNext();) {
            Element elm = (Element) it.next();

            Field field = new Field();
            field.setName(XmlUtil.getAttributeValue(elm, "name"));

            Element exE = elm.element("expression");
            if (exE != null) {
                field.setExpression(exE.getText());
            }

            Element exS = elm.element("string");
            if (exS != null) {
                field.setString(exS.getText());
            }

            fields.add(field);
        }

        executionListener.setFields(fields);
    }

    /**
     * 生成Activiti的Definition
     * @param document
     * @param definitions
     */
    protected abstract void setDefinitions(Document document, Definitions definitions) throws DocumentException;

    /**
     * 扩展（表单）
     * @param extE
     * @param extensionElements
     */
    protected abstract void getExtFormData(Element extE, ExtensionElements extensionElements);

    /**
     * 自定义变量
     * @param wfE
     * @param wfObject
     */
    protected abstract void getProcessDataObject(Element wfE, BaseWorkflowObject wfObject);
}
