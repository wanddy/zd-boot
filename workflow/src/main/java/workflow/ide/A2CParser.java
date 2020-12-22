package workflow.ide;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.io.FileUtils;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.xml.sax.SAXException;
import workflow.ide.core.*;
import workflow.ide.core.ext.*;
import workflow.ide.utils.XmlUtil;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class A2CParser extends BaseParser {

    public static void main(String[] args) {

        String bpmnFile = "D:\\02.Projects\\KW_科技计划流程再造项目\\doc\\camuda\\diagram (47).bpmn.xml";

        try {
            String cXmlString = FileUtils.readFileToString(new File(bpmnFile), "utf8");
//            System.out.println(cXmlString);
            String aXmlString = convert(cXmlString, 1,"", true);

            FileUtils.writeStringToFile(new File(bpmnFile + ".xml"), aXmlString, "utf8");
//            System.out.println(aXmlString);
        } catch (IOException e) {
            e.printStackTrace();
        }
/*
//        String bpmnFile = "D:\\02.Projects\\KW_科技计划流程再造项目\\doc\\camuda\\创新券流程（新）.bpmn20.xml";
//        String bpmnFile = "D:\\02.Projects\\KW_科技计划流程再造项目\\bpmn\\example2.bpmn.xml";
        String bpmnFile = "D:\\02.Projects\\KW_科技计划流程再造项目\\doc\\camuda\\diagram (47).bpmn";
//        Process parser = new Process();
//        parser.setId();
//        File file = new File("D:\\02.Projects\\KW_科技计划流程再造项目\\bpmn\\example2.bpmn");
        File file = new File(bpmnFile);

        A2CParser parser = new A2CParser();
        Definitions definitions = null;
        try {
            definitions = parser.getDefinitions(file);

            // 生成新XML
            System.out.println("----------------");
            parser.toBpmn(definitions, bpmnFile);
        } catch (DocumentException e) {
            e.printStackTrace();
        }
//        for(Book book : bookList){
//            System.out.println(book);
//        }
*/
    }

    /**
     * 转换
     * @param xml
     * @return
     */
    public static String convert(String xml, int processType, String subProcessStr, boolean isNew) {
        A2CParser parser = new A2CParser();
        Definitions definitions = null;
        try {
            definitions = parser.getDefinitions(xml, "A");

            // 是否子流程
            definitions.getProcess().setProcessType(processType);

            // 附加属性
            definitions.getProcess().setIsNew(String.valueOf(isNew));
            definitions.getProcess().setSubActivityStr(subProcessStr);

            // 生成新XML
            return parser.toString(definitions);

        } catch (DocumentException | SAXException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return null;
    }

    /**
     * 扩展（表单）
     * @param extE
     * @param extensionElements
     */
    @Override
    protected void getExtFormData(Element extE, ExtensionElements extensionElements) {
        List nodes = extE.elements("formProperty");

        if (CollectionUtils.isEmpty(nodes)) {
            return;
        }
        FormData formData = new FormData();
        extensionElements.setFormData(formData);

        List<FormField> formFields = new ArrayList<FormField>();
        for (Iterator it = nodes.iterator(); it.hasNext();) {
            Element elm = (Element) it.next();

            FormField formField = new FormField();
            formField.setId(XmlUtil.getAttributeValue(elm, "id"));
            formField.setLabel(XmlUtil.getAttributeValue(elm, "name")); // 不一样的地方
//                    formField.setLabel(XmlUtil.getAttributeValue(elm, "label"));
            formField.setType(XmlUtil.getAttributeValue(elm, "type")); // 类型
            formField.setDefaultValue(XmlUtil.getAttributeValue(elm, "default")); // 默认
            formField.setExpression(XmlUtil.getAttributeValue(elm, "expression")); // 表达式
            formField.setVariable(XmlUtil.getAttributeValue(elm, "variable")); // 变量
            formField.setRequired(XmlUtil.getAttributeValue(elm, "required")); // 必须
            formField.setDatePattern(XmlUtil.getAttributeValue(elm, "datePattern")); //
            formField.setReadable(XmlUtil.getAttributeValue(elm, "readable")); // 可读
            formField.setWritable(XmlUtil.getAttributeValue(elm, "writable")); // 可写

            // 表单的值对象
            getExtFormDataValues(elm, formField);

            formFields.add(formField);
        }

        formData.setFormFields(formFields);
    }


    /**
     * 表单的值对象
     * @param formFieldE
     * @param formField
     */
    protected void getExtFormDataValues(Element formFieldE, FormField formField) {

        List nodes = formFieldE.elements("value");
        if (CollectionUtils.isEmpty(nodes)) {
            return;
        }

        List<Property> properts = new ArrayList<Property>();

        Properties properties = new Properties();
        formField.setProperties(properties);

        for (Iterator it = nodes.iterator(); it.hasNext();) {
            Element elm = (Element) it.next();

            Property property = new Property();
            property.setId(XmlUtil.getAttributeValue(elm, "id"));
            property.setName(XmlUtil.getAttributeValue(elm, "name"));
//                property.setType(XmlUtil.getAttributeValue(elm, "type"));
//                property.setValue(XmlUtil.getAttributeValue(elm, "value"));
            properts.add(property);
        }

        properties.setProperties(properts);
    }

    /**
     * 自定义变量
     * @param wfE
     * @param wfObject
     */
    @Override
    protected void getProcessDataObject(Element wfE, BaseWorkflowObject wfObject) {

        ExtensionElements extensionElements = wfObject.getExtensionElements();
        if (extensionElements == null) {
            extensionElements = new ExtensionElements();
            wfObject.setExtensionElements(extensionElements);
        }

        Properties properties = new Properties();
        extensionElements.setProperties(properties);

        List<Property> propertys = new ArrayList<Property>();
        List nodes = wfE.elements("dataObject");
        for (Iterator it = nodes.iterator(); it.hasNext();) {
            Element elm = (Element) it.next();

            Property property = new Property();
            property.setId(XmlUtil.getAttributeValue(elm, "id"));
            property.setName(XmlUtil.getAttributeValue(elm, "name"));
            property.setType(XmlUtil.getAttributeValue(elm, "itemSubjectRef"));
            //property.setValue(XmlUtil.getAttributeValue(elm, "value"));

            Element extE = elm.element("extensionElements");
            if (extE != null) {
                Element extV = extE.element("value");
                property.setValue(extV.getText());
            }

            propertys.add(property);
        }
        properties.setProperties(propertys);
    }

    /*
    public void toBpmn(Definitions definitions, String bpmnFile) throws DocumentException {
        Document document = DocumentHelper.createDocument();// 建立document对象，用来操作xml文件

        //Element bPMNDiagramE = rootE.addElement("BPMNDiagram");

        setDefinitions(document, definitions);

        try {

            // 美化格式
            OutputFormat format = OutputFormat.createPrettyPrint();
//            format.setExpandEmptyElements(true);

            XMLWriter writer = new XMLWriter(new FileWriter(new File(bpmnFile + ".xml")), format);

            writer.write(document);

            writer.close();

        } catch (Exception e) {

            e.printStackTrace();

        }
    }*/

    /**
     * 生成Activiti的Definition
     * @param document
     * @param definitions
     */
    @Override
    protected void setDefinitions(Document document, Definitions definitions) throws DocumentException {

        Element rootE = document.addElement("bpmn2:definitions", "http://www.omg.org/spec/BPMN/20100524/MODEL");// 建立根节点
        rootE.addAttribute("xmlns:xsi", "http://www.w3.org/2001/XMLSchema-instance");
        rootE.addAttribute("xmlns:bpmn2", "http://www.omg.org/spec/BPMN/20100524/MODEL");
        rootE.addAttribute("xmlns:bpmndi", "http://www.omg.org/spec/BPMN/20100524/DI");
        rootE.addAttribute("xmlns:dc", "http://www.omg.org/spec/DD/20100524/DC");
        rootE.addAttribute("xmlns:camunda", "http://camunda.org/schema/1.0/bpmn");
        rootE.addAttribute("xmlns:di", "http://www.omg.org/spec/DD/20100524/DI");
        rootE.addAttribute("id", "sample-diagram");
        rootE.addAttribute("targetNamespace", "http://bpmn.io/schema/bpmn");
        rootE.addAttribute("xsi:schemaLocation", "http://www.omg.org/spec/BPMN/20100524/MODEL BPMN20.xsd");

        rootE.addNamespace("camunda", "http://camunda.org/schema/1.0/bpmn");
        // Process 节点
        setProcess(rootE, definitions);

        // 信号
        setSignals(rootE, definitions);

        // 消息
        setMessages(rootE, definitions);

        // 图形节点
        Document diagramDoc = DocumentHelper.parseText(definitions.getBPMNDiagram());

        rootE.add(diagramDoc.getRootElement());

    }

    /**
     * 消息
     * @param processE
     * @param definitions
     */
    protected void setMessages(Element processE, Definitions definitions) {
        for (Message message : definitions.getMessages()) {
            Element messageE = processE.addElement("bpmn2:message");
            messageE.addAttribute("id", message.getId());
            messageE.addAttribute("name", message.getName());
        }
    }

    /**
     * 信号
     * @param processE
     * @param definitions
     */
    protected void setSignals(Element processE, Definitions definitions) {
        for (Signal signal : definitions.getSignals()) {
            Element signalE = processE.addElement("bpmn2:signal");
            signalE.addAttribute("id", signal.getId());
            signalE.addAttribute("name", signal.getName());
//            signalE.addAttribute("activiti:scope", StringUtils.defaultString(signal.getScope(), "processInstance"));

        }
    }

    /**
     * Process 节点
     * @param rootE
     * @param definitions
     * @throws DocumentException
     */
    protected void setProcess(Element rootE, Definitions definitions) {
        BpmnProcess process = definitions.getProcess();
        Element processE = rootE.addElement("bpmn2:process");

        processE.addAttribute("isNew", process.getIsNew());
        String processType = XmlUtil.unparseProcessType(process.getProcessType());
        if ("true".equals(processType)) {
            processE.addAttribute("processType", processType);
        }
        processE.addAttribute("subProcessList", process.getSubActivityStr());

        setBaseProcess(processE, process);

        /*processE.addAttribute("id", process.getId());
        processE.addAttribute("name", process.getName());
        processE.addAttribute("isExecutable", process.getIsExecutable());
        processE.addAttribute("isNew", process.getIsNew());

        String processType = XmlUtil.unparseProcessType(process.getProcessType());
        if ("true".equals(processType)) {
            processE.addAttribute("processType", processType);
        }
        processE.addAttribute("subProcessList", process.getSubActivityStr());

        setProcessDefaultAttr(processE, process);

        // 开始事件
        setStartEvents(processE, process);

        // 结束事件
        setEndEvents(processE, process);

        // 用户任务
        setUserTasks(processE, process);

        // 顺序流
        setSequenceFlow(processE, process);

        // 排他网关
        setGateway(processE, process, "exclusiveGateway");

        // 并行网关
        setGateway(processE, process, "parallelGateway");

        // 边界事件
        setBoundaryEvent(processE, process);

        // 捕获事件
        setCatchEvent(processE, process);

        // 捕获事件
        setThrowEvent(processE, process);

        // 调用活动
        setCallActivity(processE, process);*/

        // 扩展（DataObject)
//        setProcessExtensionElements(processE, process);
    }

    protected void setBaseProcess(Element processE, BaseProcess process) {

        processE.addAttribute("id", process.getId());
        processE.addAttribute("name", process.getName());
        processE.addAttribute("isExecutable", process.getIsExecutable());

        setProcessDefaultAttr(processE, process);

        // 开始事件
        setStartEvents(processE, process);

        // 结束事件
        setEndEvents(processE, process);

        // 任务
        setTasks(processE, process);

        // 用户任务
        setUserTasks(processE, process);

        // 顺序流
        setSequenceFlow(processE, process);

        // 排他网关
        setGateway(processE, process, "exclusiveGateway");

        // 并行网关
        setGateway(processE, process, "parallelGateway");

        // 排他网关
        setGateway(processE, process, "inclusiveGateway");

        // 并行网关
        setGateway(processE, process, "eventBasedGateway");

        // 边界事件
        setBoundaryEvent(processE, process);

        // 捕获事件
        setCatchEvent(processE, process);

        // 捕获事件
        setThrowEvent(processE, process);

        // 调用活动
        setCallActivity(processE, process);

        // 扩展子流程
        setSubProcess(processE, process);
    }

    /**
     * 扩展子流程
     * @param processE
     * @param process
     */
    protected void setSubProcess(Element processE, BaseProcess process) {
        for (SubProcess subProcess : process.getSubProcessList()) {
            Element subProcessE = processE.addElement("bpmn2:subProcess");

            setBaseProcess(subProcessE, subProcess);
        }
    }

    /**
     * 开始事件
     * @param processE
     * @param process
     */
    protected void setStartEvents(Element processE, BaseProcess process) {
        for (StartEvent startEvent : process.getStartEvents()) {
            Element startEventE = processE.addElement("bpmn2:startEvent");
            startEventE.addAttribute("id", startEvent.getId());
            startEventE.addAttribute("name", startEvent.getName());

            // 定时开始事件
            setTimerEventDefinition(startEventE, startEvent);
            // 信号开始事件
            setSignalEventDefinition(startEventE, startEvent);
            // 消息开始事件
            setMessageEventDefinition(startEventE, startEvent);

            setDefaultAttr(startEventE, startEvent);
        }
    }

    /**
     * 结束事件
     * @param processE
     * @param process
     */
    protected void setEndEvents(Element processE, BaseProcess process) {
        for (EndEvent endEvent : process.getEndEvents()) {
            Element endEventE = processE.addElement("bpmn2:endEvent");
            endEventE.addAttribute("id", endEvent.getId());
            endEventE.addAttribute("name", endEvent.getName());

            // 出错事件
            setErrorEventDefinition(endEventE, endEvent);

            setDefaultAttr(endEventE, endEvent);
        }
    }

    /**
     * 定时事件
     * @param eventDefinitionE
     * @param eventDefinition
     */
    protected void setTimerEventDefinition(Element eventDefinitionE, IEventDefinition eventDefinition) {
        TimerEventDefinition timerEventDefinition = eventDefinition.getTimerEventDefinition();

        if (timerEventDefinition == null) {
            return;
        }
        Element timeE = eventDefinitionE.addElement("bpmn2:timerEventDefinition");
        if (timerEventDefinition.getTimeDuration() != null) {
            timeE.addElement("bpmn2:timeDuration").addAttribute("xsi:type", "bpmn2:tFormalExpression")
                    .setText(timerEventDefinition.getTimeDuration());
        }

        if (timerEventDefinition.getTimeDate() != null) {
            timeE.addElement("bpmn2:timeDate").setText(timerEventDefinition.getTimeDate());
        }

        if (timerEventDefinition.getTimeCycle() != null) {
            timeE.addElement("bpmn2:timeCycle").setText(timerEventDefinition.getTimeCycle());
        }
    }

    /**
     * 信号事件
     * @param eventDefinitionE
     * @param eventDefinition
     */
    protected void setSignalEventDefinition(Element eventDefinitionE, IEventDefinition eventDefinition) {
        SignalEventDefinition signalEventDefinition = eventDefinition.getSignalEventDefinition();

        if (signalEventDefinition == null) {
            return;
        }
        Element signalE = eventDefinitionE.addElement("bpmn2:signalEventDefinition");
        signalE.addAttribute("signalRef", signalEventDefinition.getSignalRef());

    }

    /**
     * 消息事件
     * @param eventDefinitionE
     * @param eventDefinition
     */
    protected void setMessageEventDefinition(Element eventDefinitionE, IEventDefinition eventDefinition) {
        MessageEventDefinition messageEventDefinition = eventDefinition.getMessageEventDefinition();

        if (messageEventDefinition == null) {
            return;
        }
        Element messageE = eventDefinitionE.addElement("bpmn2:messageEventDefinition");
        messageE.addAttribute("messageRef", messageEventDefinition.getMessageRef());

    }

    /**
     * 错误事件
     * @param eventDefinitionE
     * @param eventDefinition
     */
    protected void setErrorEventDefinition(Element eventDefinitionE, IEventDefinition eventDefinition) {
        ErrorEventDefinition errorEventDefinition = eventDefinition.getErrorEventDefinition();

        if (errorEventDefinition == null) {
            return;
        }
        Element errorE = eventDefinitionE.addElement("bpmn2:errorEventDefinition");
        errorE.addAttribute("camunda:errorRef", errorEventDefinition.getErrorRef());

    }

    /**
     * 补偿事件
     * @param eventDefinitionE
     * @param eventDefinition
     */
    protected void setCompensateEventDefinition(Element eventDefinitionE, IEventDefinition eventDefinition) {
        CompensateEventDefinition compensateEventDefinition = eventDefinition.getCompensateEventDefinition();

        if (compensateEventDefinition == null) {
            return;
        }
        Element messageE = eventDefinitionE.addElement("bpmn2:compensateEventDefinition");

    }

    /**
     * 顺序流
     * @param processE
     * @param process
     */
    protected void setSequenceFlow(Element processE, BaseProcess process) {
        for (SequenceFlow sequenceFlow : process.getSequenceFlows()) {
            Element sequenceFlowE = processE.addElement("bpmn2:sequenceFlow");
            sequenceFlowE.addAttribute("id", sequenceFlow.getId());
            sequenceFlowE.addAttribute("sourceRef", sequenceFlow.getSourceRef());
            sequenceFlowE.addAttribute("targetRef", sequenceFlow.getTargetRef());
            sequenceFlowE.addAttribute("skipExpression", sequenceFlow.getSkipExpression());

            ConditionExpression conditionExpression = sequenceFlow.getConditionExpression();

            if (conditionExpression != null) {
                Element condE = sequenceFlowE.addElement("bpmn2:conditionExpression");
                condE.addAttribute("xsi:type", "bpmn2:tFormalExpression");
                condE.setText(conditionExpression.getValue());
            }

            setDefaultAttr(sequenceFlowE, sequenceFlow);
        }
    }

    /**
     * 网关
     * @param processE
     * @param process
     */
    protected void setGateway(Element processE, BaseProcess process, String gatewayType) {

        List<Gateway> gateways = null;

        if ("exclusiveGateway".equals(gatewayType)) {
            gateways = process.getExclusiveGateways();
        } else if ("parallelGateway".equals(gatewayType)) {
            gateways = process.getParallelGateways();
        } else if ("inclusiveGateway".equals(gatewayType)) {
            gateways = process.getInclusiveGateways();
        } else if ("eventBasedGateway".equals(gatewayType)) {
            gateways = process.getEventBasedGateways();
        }

        if (gateways == null) {
            return;
        }
        for (Gateway gateway : gateways) {
            Element gatewayE = processE.addElement("bpmn2:" + gatewayType);
            gatewayE.addAttribute("id", gateway.getId());
            gatewayE.addAttribute("name", gateway.getName());
//            gatewayE.addAttribute("activiti:async", gateway.getAsync());
//            gatewayE.addAttribute("activiti:exclusive", gateway.getExclusive());

            setDefaultAttr(gatewayE, gateway);
        }
    }

    /**
     * 边界事件
     * @param processE
     * @param process
     */
    protected void setBoundaryEvent(Element processE, BaseProcess process) {
        for (BoundaryEvent boundaryEvent : process.getBoundaryEvents()) {
            Element boundaryEventE = processE.addElement("bpmn2:boundaryEvent");
            boundaryEventE.addAttribute("id", boundaryEvent.getId());
            boundaryEventE.addAttribute("name", boundaryEvent.getName());
            boundaryEventE.addAttribute("attachedToRef", boundaryEvent.getAttachedToRef());

            // 定时开始事件
            setTimerEventDefinition(boundaryEventE, boundaryEvent);
            // 信号开始事件
            setSignalEventDefinition(boundaryEventE, boundaryEvent);
            // 消息开始事件
            setMessageEventDefinition(boundaryEventE, boundaryEvent);
            // 补偿事件
            setCompensateEventDefinition(boundaryEventE, boundaryEvent);
            // 错误事件
            setErrorEventDefinition(boundaryEventE, boundaryEvent);
        }
    }

    /**
     * 捕获事件
     * @param processE
     * @param process
     */
    protected void setCatchEvent(Element processE, BaseProcess process) {
        for (IntermediateEvent catchEvent : process.getIntermediateCatchEvents()) {
            Element catchEventE = processE.addElement("bpmn2:intermediateCatchEvent");
            catchEventE.addAttribute("id", catchEvent.getId());
            catchEventE.addAttribute("name", catchEvent.getName());

            // 定时开始事件
            setTimerEventDefinition(catchEventE, catchEvent);
            // 信号开始事件
            setSignalEventDefinition(catchEventE, catchEvent);
            // 消息开始事件
            setMessageEventDefinition(catchEventE, catchEvent);

        }
    }


    /**
     * 捕获事件
     * @param processE
     * @param process
     */
    protected void setThrowEvent(Element processE, BaseProcess process) {
        for (IntermediateEvent throwEvent : process.getIntermediateThrowEvent()) {
            Element throwEventE = processE.addElement("bpmn2:intermediateThrowEvent");
            throwEventE.addAttribute("id", throwEvent.getId());
            throwEventE.addAttribute("name", throwEvent.getName());

            // 定时开始事件
            setTimerEventDefinition(throwEventE, throwEvent);
            // 信号开始事件
            setSignalEventDefinition(throwEventE, throwEvent);
            // 消息开始事件
            setMessageEventDefinition(throwEventE, throwEvent);

        }
    }

    /**
     * 用户任务
     * @param processE
     * @param process
     */
    protected void setUserTasks(Element processE, BaseProcess process) {
        for (UserTask userTask : process.getUserTasks()) {
            Element userTaskE = processE.addElement("bpmn2:userTask");
            userTaskE.addAttribute("id", userTask.getId());
            userTaskE.addAttribute("name", userTask.getName());
            userTaskE.addAttribute("camunda:assignee", userTask.getAssignee());
            userTaskE.addAttribute("camunda:candidateUsers", userTask.getCandidateUsers());
            userTaskE.addAttribute("camunda:candidateGroups", userTask.getCandidateGroups());

            // 多实例
            setMultiInstanceLoopCharacteristics(userTaskE, userTask);

            setDefaultAttr(userTaskE, userTask);
//            setUserTaskExtensionElements(userTaskE, userTask);
        }
    }

    /**
     * 用户任务
     * @param processE
     * @param process
     */
    protected void setTasks(Element processE, BaseProcess process) {
        for (Task task : process.getTasks()) {
            Element taskE = processE.addElement("bpmn2:task");
            taskE.addAttribute("id", task.getId());
            taskE.addAttribute("name", task.getName());

            setDefaultAttr(taskE, task);
//            setUserTaskExtensionElements(userTaskE, userTask);
        }
    }

    /**
     * 多实例
     * @param userTaskE
     * @param userTask
     */
    protected void setMultiInstanceLoopCharacteristics(Element userTaskE, IMultiInstanceLoopCharacteristics userTask) {
        MultiInstanceLoopCharacteristics multiInstanceLoop = userTask.getMultiInstanceLoopCharacteristics();

        if (multiInstanceLoop == null) {
            return;
        }

        Element multiInstanceE = userTaskE.addElement("bpmn2:multiInstanceLoopCharacteristics");

        multiInstanceE.addAttribute("isSequential", multiInstanceLoop.getIsSequential());
        multiInstanceE.addAttribute("camunda:collection", multiInstanceLoop.getCollection());
        multiInstanceE.addAttribute("camunda:elementVariable", multiInstanceLoop.getElementVariable());

        if (multiInstanceLoop.getLoopCardinality() != null) {
            Element loopCardinalityE = multiInstanceE.addElement("bpmn2:loopCardinality");
            loopCardinalityE.addAttribute("xsi:type", "bpmn2:tFormalExpression");
            loopCardinalityE.setText(multiInstanceLoop.getLoopCardinality());
        }

        if (multiInstanceLoop.getCompletionCondition() != null) {
            Element completionConditionE = multiInstanceE.addElement("bpmn2:completionCondition");
            completionConditionE.addAttribute("xsi:type", "bpmn2:tFormalExpression");
            completionConditionE.setText(multiInstanceLoop.getCompletionCondition());
        }
    }


    /**
     * 调用活动
     * @param processE
     * @param process
     */
    protected void setCallActivity(Element processE, BaseProcess process) {
        for (CallActivity callActivity : process.getCallActivitys()) {
            Element callActivityE = processE.addElement("bpmn2:callActivity");
            callActivityE.addAttribute("id", callActivity.getId());
            callActivityE.addAttribute("name", callActivity.getName());
            callActivityE.addAttribute("calledElement", callActivity.getCalledElement());

            // 多实例
            setMultiInstanceLoopCharacteristics(callActivityE, callActivity);

            if (CollectionUtils.isNotEmpty(callActivity.getInCallParameters()) ||
                    CollectionUtils.isNotEmpty(callActivity.getOutCallParameters())) {
                Element extE = callActivityE.addElement("bpmn2:extensionElements");

                // 输入参数
                setCallParameter(extE, callActivity.getInCallParameters(), "in");

                // 输出参数
                setCallParameter(extE, callActivity.getOutCallParameters(), "out");
            }
        }
    }

    /**
     * 调用活动参数
     * @param extE
     * @param callParameters
     * @param paramType
     * @return
     */
    protected void setCallParameter(Element extE, List<CallParameter> callParameters, String paramType) {

        for (CallParameter callParameter : callParameters) {
            Element paramE = extE.addElement("camunda:" + paramType);
            paramE.addAttribute("source", callParameter.getSource());
            paramE.addAttribute("sourceExpression", callParameter.getSourceExpression());
            paramE.addAttribute("target", callParameter.getTarget());
            paramE.addAttribute("targetExpression", callParameter.getTargetExpression());

        }

    }

    /**
     * 默认属性
     * @param wfE
     * @param wfObject
     */
    protected void setProcessDefaultAttr(Element wfE, BaseWorkflowObject wfObject) {
        // 文档
        if (wfObject.getDocumentation() != null) {
            Element documentation = wfE.addElement("bpmn2:documentation");
            documentation.setText(wfObject.getDocumentation());
        }

        // 扩展属性
        if (wfObject.getExtensionElements() == null) {
            return;
        }

        ExtensionElements extensionElements = wfObject.getExtensionElements();

        Element wfe = wfE.addElement("bpmn2:extensionElements");

        setProcessExtensionElements(wfe, extensionElements);

        if (CollectionUtils.isNotEmpty(extensionElements.getExecutionListeners())) {

            setExtExecutionListener(wfe, extensionElements);
        }
    }

    /**
     * 扩展（监听器）
     * @param wfe
     * @param extensionElements
     */
    protected void setExtExecutionListener(Element wfe, ExtensionElements extensionElements) {

        List<ExecutionListener> executionListeners = extensionElements.getExecutionListeners();

        if (executionListeners == null) {
            return;
        }
        for (ExecutionListener executionListener : executionListeners) {
            Element executionListenerE = wfe.addElement("camunda:executionListener");
            executionListenerE.addAttribute("event", executionListener.getEvent());
            executionListenerE.addAttribute("expression", executionListener.getExpression());
            executionListenerE.addAttribute("delegateExpression", executionListener.getDelegateExpression());
            executionListenerE.addAttribute("class", executionListener.getListenerClass());

            for (Field field : executionListener.getFields()) {
                Element fieldE = executionListenerE.addElement("camunda:field");
                fieldE.addAttribute("name", field.getName());

                if (field.getString() != null) {
                    Element stringE = fieldE.addElement("camunda:string");
                    stringE.setText(field.getString());
                }

                if (field.getExpression() != null) {
                    Element expressionE = fieldE.addElement("camunda:expression");
                    expressionE.setText(field.getExpression());
                }
            }

        }
    }

    /**
     * 默认属性
     * @param wfE
     * @param wfObject
     */
    protected void setDefaultAttr(Element wfE, BaseWorkflowObject wfObject) {
        wfE.addAttribute("activiti:async", wfObject.getAsync());
        wfE.addAttribute("activiti:exclusive", wfObject.getExclusive());

        // 文档
        if (wfObject.getDocumentation() != null) {
            Element documentation = wfE.addElement("bpmn2:documentation");
            documentation.setText(wfObject.getDocumentation());
        }

        // 扩展属性
        if (wfObject.getExtensionElements() == null) {
            return;
        }

        ExtensionElements extensionElements = wfObject.getExtensionElements();
        //if (extensionElements.getExecutionListeners() == null && extensionElements.getProperties() == null)

        Element wfe = wfE.addElement("bpmn2:extensionElements");

        setExtFormData(wfe, extensionElements);

        setExtExecutionListener(wfe, extensionElements);
    }


    /**
     * 流程（扩展属性）
     * @param processE
     * @param extensionElements
     */
    protected void setProcessExtensionElements(Element processE, ExtensionElements extensionElements) {

        Properties properties = extensionElements.getProperties();
        if (properties == null) {
            return;
        }

        Element propertiesE = processE.addElement("camunda:properties");

        for (Property property : properties.getProperties()) {
            Element dataObjectE = propertiesE.addElement("camunda:property");
            dataObjectE.addAttribute("id", property.getId());
            dataObjectE.addAttribute("name", property.getName());
            dataObjectE.addAttribute("type", property.getType());
            dataObjectE.addAttribute("value", property.getValue());
        }
    }

    /**
     * FormData
     * @param wfe
     * @param extensionElements
     */
    protected void setExtFormData(Element wfe, ExtensionElements extensionElements) {

        FormData formData = extensionElements.getFormData();

        if (formData == null) {
            return;
        }

        Element formDataE = wfe.addElement("camunda:formData");

        for (FormField formField : formData.getFormFields()) {
            Element formFieldE = formDataE.addElement("camunda:formField");
            formFieldE.addAttribute("id", formField.getId());
            formFieldE.addAttribute("label", formField.getLabel());
            formFieldE.addAttribute("type", formField.getType());
            formFieldE.addAttribute("defaultValue", formField.getDefaultValue());
            formFieldE.addAttribute("expression", formField.getExpression());
            formFieldE.addAttribute("variable", formField.getVariable());
            formFieldE.addAttribute("required", formField.getRequired());
            formFieldE.addAttribute("datePattern", formField.getDatePattern());
            formFieldE.addAttribute("readable", formField.getReadable());
            formFieldE.addAttribute("writeable", formField.getWritable());

            setExtFormDataValues(formFieldE, formField);
        }
    }

    /**
     * 表单的值对象
     * @param formFieldE
     * @param formField
     */
    protected void setExtFormDataValues(Element formFieldE, FormField formField) {

        Properties properties = formField.getProperties();
        if (properties == null) {
            return;
        }

        Element propertiesE = formFieldE.addElement("camunda:properties");

        for (Property property : properties.getProperties()) {
            Element dataObjectE = propertiesE.addElement("camunda:property");
            dataObjectE.addAttribute("id", property.getId());
            dataObjectE.addAttribute("value", property.getName());

        }
    }
}
