package workflow.ide;


import org.activiti.bpmn.model.BpmnModel;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang.StringUtils;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.dom4j.io.OutputFormat;
import org.dom4j.io.XMLWriter;
import org.xml.sax.SAXException;
import workflow.ide.core.*;
import workflow.ide.core.ext.*;
import workflow.ide.utils.XmlUtil;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class C2AParser extends BaseParser {

    /**
     * 解析XML获取流程定义
     *
     * @param file 文件
     * @return 流程定义对象
     */
//    public Definitions getDefinitions(File file) throws DocumentException {
//
//        SAXReader reader = new SAXReader();
//
//        Document document = reader.read(file);
//
//        Element rootE = document.getRootElement();
//        Definitions definitions = new Definitions();
//        definitions.setId(XmlUtil.getAttributeValue(rootE, "id"));
//
//        Element processE = rootE.element("process");
//        definitions.setProcess(getProcess(processE));
//
//        // 图形节点
//        Element bPMNDiagramE = rootE.element("BPMNDiagram");
//
//        definitions.setBPMNDiagram(bPMNDiagramE.asXML());
//
//        //String s = XmlUtil.getContent(bPMNDiagramE);
//        System.out.println(definitions.getBPMNDiagram());
//        return definitions;
//
//    }

//    /**
//     * 解析XML获取流程节点
//     *
//     * @param processE 流程节点
//     * @return 流程对象
//     */
//    public BpmnProcess getProcess(Element processE) {
//        BpmnProcess process = new BpmnProcess();
//
//        process.setId(XmlUtil.getAttributeValue(processE, "id"));
//        process.setName(XmlUtil.getAttributeValue(processE, "name"));
//        process.setIsExecutable(XmlUtil.getAttributeValue(processE, "isExecutable"));
//
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
//        // 消息
//        getMessages(processE, process);
//
//        // 信号
//        getSignals(processE, process);
//
//        // 边界事件
//        getBoundaryEvent(processE, process);
//
//        // 扩展属性
////        getProcessExtensionElements(processE, process);
//
//        return process;
//    }

    /**
     * 扩展属性(Process)
     * @param processE
     * @param process
     */
    /*
    public void getProcessExtensionElements(Element processE, BpmnProcess process) {
        Element extE = processE.element("extensionElements");

        if (extE != null) {
            ExtensionElements extensionElements = process.getExtensionElements();
            if (extensionElements == null) {
                extensionElements = new ExtensionElements();
                process.setExtensionElements(extensionElements);
            }

            Element propertiesE = extE.element("properties");

            if (propertiesE != null) {
                Properties properties = new Properties();
                extensionElements.setProperties(properties);

                List<Property> properts = new ArrayList<Property>();
                List nodes = propertiesE.elements("property");

                for (Iterator it = nodes.iterator(); it.hasNext();) {
                    Element elm = (Element) it.next();

                    Property property = new Property();
                    property.setName(elm.attribute("name").getValue());

                    properts.add(property);
                }

                properties.setProperties(properts);
            }
        }
    }*/

    /**
     * 扩展属性(UserTask)
//     * @param userTaskE
//     * @param userTask
     */
    /*
    public void getUserTaskExtensionElements(Element userTaskE, UserTask userTask) {
        Element extE = userTaskE.element("extensionElements");

        if (extE != null) {
            ExtensionElements extensionElements = userTask.getExtensionElements();
            if (extensionElements == null) {
                extensionElements = new ExtensionElements();
                userTask.setExtensionElements(extensionElements);
            }

            Element formDataE = extE.element("formData");

            if (formDataE != null) {
                FormData formData = new FormData();
                extensionElements.setFormData(formData);

                List<FormField> formFields = new ArrayList<FormField>();
                List nodes = formDataE.elements("formField");

                for (Iterator it = nodes.iterator(); it.hasNext();) {
                    Element elm = (Element) it.next();

                    FormField formField = new FormField();
                    formField.setId(XmlUtil.getAttributeValue(elm, "id"));
                    formField.setName(XmlUtil.getAttributeValue(elm, "name"));
//                    formField.setLabel(XmlUtil.getAttributeValue(elm, "label"));
                    formField.setType(XmlUtil.getAttributeValue(elm, "type")); // 类型
                    formField.setDefaultValue(XmlUtil.getAttributeValue(elm, "defaultValue")); // 默认
                    formField.setExpression(XmlUtil.getAttributeValue(elm, "expression")); // 表达式
                    formField.setVariable(XmlUtil.getAttributeValue(elm, "variable")); // 变量
                    formField.setRequired(XmlUtil.getAttributeValue(elm, "required")); // 必须
                    formField.setDatePattern(XmlUtil.getAttributeValue(elm, "datePattern")); //
                    formField.setReadable(XmlUtil.getAttributeValue(elm, "readable")); // 可读
                    formField.setWritable(XmlUtil.getAttributeValue(elm, "writable")); // 可写

                    formFields.add(formField);
                }

                formData.setFormFields(formFields);
            }
        }
    }*/

//    /**
//     * 用户任务
//     * @param processE
//     * @param process
//     */
//    public void getUserTask(Element processE, BpmnProcess process) {
//        List<UserTask> userTasks = new ArrayList<UserTask>();
//        List nodes = processE.elements("userTask");
//        for (Iterator it = nodes.iterator(); it.hasNext();) {
//            Element elm = (Element) it.next();
//
//            UserTask userTask = new UserTask();
//            userTask.setId(XmlUtil.getAttributeValue(elm, "id"));
//            userTask.setName(XmlUtil.getAttributeValue(elm, "name"));
//            userTask.setAssignee(XmlUtil.getAttributeValue(elm, "assignee"));
//            userTask.setCandidateUsers(XmlUtil.getAttributeValue(elm, "candidateUsers"));
//            userTask.setCandidateGroups(XmlUtil.getAttributeValue(elm, "candidateGroups"));
//
//            // 多实例类型
//            getMultiInstanceLoopCharacteristics(elm, userTask);
//
//            getDefaultAttr(elm, userTask);
//            userTasks.add(userTask);
//
//            // 扩展属性
////            getUserTaskExtensionElements(elm, userTask);
//        }
//
//        process.setUserTasks(userTasks);
//    }

//    /**
//     * 多实例类型 > 子节点
//     * @param userTaskE
//     * @param userTask
//     */
//    public void getMultiInstanceLoopCharacteristics(Element userTaskE, UserTask userTask) {
//
//        Element eleM = userTaskE.element("multiInstanceLoopCharacteristics");
//
//        if (eleM == null) {
//            return;
//        }
//        MultiInstanceLoopCharacteristics multiInstanceLoopCharacteristics = new MultiInstanceLoopCharacteristics();
//        userTask.setMultiInstanceLoopCharacteristics(multiInstanceLoopCharacteristics);
//
//        multiInstanceLoopCharacteristics.setIsSequential(XmlUtil.getAttributeValue(eleM, "isSequential"));
//        multiInstanceLoopCharacteristics.setCollection(XmlUtil.getAttributeValue(eleM, "collection"));
//        multiInstanceLoopCharacteristics.setElementVariable(XmlUtil.getAttributeValue(eleM, "elementVariable"));
//
//        Element eleL = eleM.element("loopCardinality");
//        if (eleL != null) {
//            multiInstanceLoopCharacteristics.setLoopCardinality(eleL.getText());
//        }
//
//        Element eleC = eleM.element("completionCondition");
//        if (eleL != null) {
//            multiInstanceLoopCharacteristics.setCompletionCondition(eleC.getText());
//        }
//    }
//
//    /**
//     * 开始事件
//     * @param processE
//     * @param process
//     */
//    public void getStartEvents(Element processE, BpmnProcess process) {
//        List<StartEvent> startEvents = new ArrayList<StartEvent>();
//        List nodes = processE.elements("startEvent");
//        for (Iterator it = nodes.iterator(); it.hasNext();) {
//            Element elm = (Element) it.next();
//
//            StartEvent startEvent = new StartEvent();
//            startEvent.setId(XmlUtil.getAttributeValue(elm, "id"));
//            startEvent.setName(XmlUtil.getAttributeValue(elm, "name"));
//
//            // 定时开始事件
//            getTimerEventDefinition(processE, startEvent);
//            // 信号定时事件
//            getSignalEventDefinition(processE, startEvent);
//            // 消息定时事件
//            getMessageEventDefinition(processE, startEvent);
//
//            getDefaultAttr(elm, startEvent);
//            startEvents.add(startEvent);
//        }
//
//        process.setStartEvents(startEvents);
//    }
//
//    /**
//     * 定时事件
//     * @param eventDefinitionE
//     * @param eventDefinition
//     */
//    public void getTimerEventDefinition(Element eventDefinitionE, IEventDefinition eventDefinition) {
//        Element timerEventDefinitionE = eventDefinitionE.element("timerEventDefinition");
//
//        if (timerEventDefinitionE == null) {
//            return;
//        }
//
//        TimerEventDefinition timerEventDefinition = new TimerEventDefinition();
//        eventDefinition.setTimerEventDefinition(timerEventDefinition);
//
//        Element timeDurationE = timerEventDefinitionE.element("timeDuration");
//        if (timeDurationE != null) {
//            timerEventDefinition.setTimeDuration(timeDurationE.getText());
//        }
//
//        Element timeDateE = timerEventDefinitionE.element("timeDate");
//        if (timeDateE != null) {
//            timerEventDefinition.setTimeDate(timeDateE.getText());
//        }
//
//        Element timeCycleE = timerEventDefinitionE.element("timeCycle");
//        if (timeDateE != null) {
//            timerEventDefinition.setTimeCycle(timeCycleE.getText());
//        }
//    }
//
//    /**
//     * 信号事件
//     * @param eventDefinitionE
//     * @param eventDefinition
//     */
//    public void getSignalEventDefinition(Element eventDefinitionE, IEventDefinition eventDefinition) {
//        Element signalEventDefinitionE = eventDefinitionE.element("signalEventDefinition");
//
//        if (signalEventDefinitionE == null) {
//            return;
//        }
//
//        SignalEventDefinition signalEventDefinition = new SignalEventDefinition();
//        eventDefinition.setSignalEventDefinition(signalEventDefinition);
//
//        signalEventDefinition.setSignalRef(XmlUtil.getAttributeValue(signalEventDefinitionE, "signalRef"));
//    }
//
//    /**
//     * 消息事件
//     * @param eventDefinitionE
//     * @param eventDefinition
//     */
//    public void getMessageEventDefinition(Element eventDefinitionE, IEventDefinition eventDefinition) {
//        Element messageEventDefinitionE = eventDefinitionE.element("messageEventDefinition");
//
//        if (messageEventDefinitionE == null) {
//            return;
//        }
//
//        MessageEventDefinition messageEventDefinition = new MessageEventDefinition();
//        eventDefinition.setMessageEventDefinition(messageEventDefinition);
//
//        messageEventDefinition.setMessageRef(XmlUtil.getAttributeValue(messageEventDefinitionE, "messageRef"));
//
//    }
//
//    /**
//     * 结束事件
//     * @param processE
//     * @param process
//     */
//    public void getEndEvents(Element processE, BpmnProcess process) {
//        List<EndEvent> endEvents = new ArrayList<EndEvent>();
//        List nodes = processE.elements("endEvent");
//        for (Iterator it = nodes.iterator(); it.hasNext();) {
//            Element elm = (Element) it.next();
//
//            EndEvent endEvent = new EndEvent();
//            endEvent.setId(XmlUtil.getAttributeValue(elm, "id"));
//            endEvent.setName(XmlUtil.getAttributeValue(elm, "name"));
//
//            getDefaultAttr(elm, endEvent);
//            endEvents.add(endEvent);
//        }
//
//        process.setEndEvents(endEvents);
//    }
//
//    /**
//     * 消息
//     * @param processE
//     * @param process
//     */
//    public void getMessages(Element processE, BpmnProcess process) {
//        List<Message> messages = new ArrayList<Message>();
//        List nodes = processE.elements("message");
//        for (Iterator it = nodes.iterator(); it.hasNext();) {
//            Element elm = (Element) it.next();
//
//            Message message = new Message();
//            message.setId(XmlUtil.getAttributeValue(elm, "id"));
//            message.setName(XmlUtil.getAttributeValue(elm, "name"));
//
//            messages.add(message);
//        }
//
//        process.setMessages(messages);
//    }
//
//    /**
//     * 信号
//     * @param processE
//     * @param process
//     */
//    public void getSignals(Element processE, BpmnProcess process) {
//        List<Signal> signals = new ArrayList<Signal>();
//        List nodes = processE.elements("signal");
//        for (Iterator it = nodes.iterator(); it.hasNext();) {
//            Element elm = (Element) it.next();
//
//            Signal signal = new Signal();
//            signal.setId(XmlUtil.getAttributeValue(elm, "id"));
//            signal.setName(XmlUtil.getAttributeValue(elm, "name"));
//            signal.setScope(XmlUtil.getAttributeValue(elm, "scope"));
//
//            signals.add(signal);
//        }
//
//        process.setSignals(signals);
//    }
//
//    /**
//     * 边界事件
//     * @param processE
//     * @param process
//     */
//    public void getBoundaryEvent(Element processE, BpmnProcess process) {
//        List<BoundaryEvent> boundaryEvents = new ArrayList<BoundaryEvent>();
//        List nodes = processE.elements("boundaryEvent");
//
//        for (Iterator it = nodes.iterator(); it.hasNext();) {
//            Element elm = (Element) it.next();
//
//            BoundaryEvent boundaryEvent = new BoundaryEvent();
//            boundaryEvent.setId(XmlUtil.getAttributeValue(elm, "id"));
//            boundaryEvent.setName(XmlUtil.getAttributeValue(elm, "name"));
//            boundaryEvent.setAttachedToRef(XmlUtil.getAttributeValue(elm, "attachedToRef"));
//
//            // 定时开始事件
//            getTimerEventDefinition(elm, boundaryEvent);
//            // 信号定时事件
//            getSignalEventDefinition(elm, boundaryEvent);
//            // 消息定时事件
//            getMessageEventDefinition(elm, boundaryEvent);
//
//            boundaryEvents.add(boundaryEvent);
//        }
//
//        process.setBoundaryEvents(boundaryEvents);
//
//    }
//
//    /**
//     * 顺序流
//     * @param processE
//     * @param process
//     */
//    public void getSequenceFlow(Element processE, BpmnProcess process) {
//        List<SequenceFlow> sequenceFlows = new ArrayList<SequenceFlow>();
//        List nodes = processE.elements("sequenceFlow");
//        for (Iterator it = nodes.iterator(); it.hasNext();) {
//            Element elm = (Element) it.next();
//
//            SequenceFlow sequenceFlow = new SequenceFlow();
//            sequenceFlow.setId(XmlUtil.getAttributeValue(elm, "id"));
//            sequenceFlow.setSourceRef(XmlUtil.getAttributeValue(elm, "sourceRef"));
//            sequenceFlow.setTargetRef(XmlUtil.getAttributeValue(elm, "targetRef"));
//            sequenceFlow.setSkipExpression(XmlUtil.getAttributeValue(elm, "skipExpression"));
//
//            getDefaultAttr(elm, sequenceFlow);
//            sequenceFlows.add(sequenceFlow);
//        }
//
//        process.setSequenceFlows(sequenceFlows);
//    }
//
//    /**
//     * 排他网关
//     * @param processE
//     * @param process
//     */
//    public void getGateway(Element processE, BpmnProcess process, String gatewayType) {
//        List<Gateway> gateways = new ArrayList<Gateway>();
//        List nodes = processE.elements(gatewayType);
//        for (Iterator it = nodes.iterator(); it.hasNext();) {
//            Element elm = (Element) it.next();
//
//            Gateway gateway = new Gateway();
//            gateway.setId(XmlUtil.getAttributeValue(elm, "id"));
//            gateway.setName(XmlUtil.getAttributeValue(elm, "name"));
//            gateway.setAsync(XmlUtil.getAttributeValue(elm, "async"));
//            gateway.setExclusive(XmlUtil.getAttributeValue(elm, "exclusive"));
//
//            getDefaultAttr(elm, gateway);
//            gateways.add(gateway);
//        }
//
//        if ("exclusiveGateway".equals(gatewayType)) {
//            process.setExclusiveGateways(gateways);
//        } else if ("parallelGateway".equals(gatewayType)) {
//            process.setParallelGateways(gateways);
//        }
//    }
//
//    // 默认属性（文档，监听器）
//    public void getDefaultAttr(Element wfE, BaseWorkflowObject wfObject) {
//        Element elm = wfE.element("documentation");
//        if (elm != null) {
//            wfObject.setDocumentation(elm.getText());
//        }
//
//        getExtensionElements(wfE, wfObject);
//        /*
//        Element extE = wfE.element("extensionElements");
//
//        if (extE == null) {
//            return;
//        }
//
//        ExtensionElements extensionElements = wfObject.getExtensionElements();
//        if (extensionElements == null) {
//            extensionElements = new ExtensionElements();
//            wfObject.setExtensionElements(extensionElements);
//        }
////
////        Element executionE = extE.element("executionListener");
////        if (executionE == null) {
////            return;
////        }
//
//        List<ExecutionListener> executionListeners = new ArrayList<ExecutionListener>();
//        List nodes = extE.elements("executionListener");
//        for (Iterator it = nodes.iterator(); it.hasNext();) {
//            Element elmEx = (Element) it.next();
//
//            ExecutionListener executionListener = new ExecutionListener();
//            executionListener.setEvent(XmlUtil.getAttributeValue(elmEx, "event"));
//            executionListener.setExpression(XmlUtil.getAttributeValue(elmEx, "expression"));
//            executionListener.setDelegateExpression(XmlUtil.getAttributeValue(elmEx, "delegateExpression"));
//
//            getExecutionListenerFields(elmEx, executionListener);
//            executionListeners.add(executionListener);
//        }
//
//        extensionElements.setExecutionListeners(executionListeners);
////
////        ExecutionListener executionListener = new ExecutionListener();
////
////        extensionElements.setId(XmlUtil.getAttributeValue(elm, "id"));
////
////        List nodes = executionE.elements("field");
//        */
//    }

    /**
     * 自定义变量
     * @param wfE
     * @param wfObject
     */
    protected void getProcessDataObject(Element wfE, BaseWorkflowObject wfObject) {
        Element extE = wfE.element("extensionElements");

        if (extE == null) {
            return;
        }
        ExtensionElements extensionElements = wfObject.getExtensionElements();
        if (extensionElements == null) {
            extensionElements = new ExtensionElements();
            wfObject.setExtensionElements(extensionElements);
        }

        getExtProperties(extE, extensionElements);

    }

//    /**
//     * 扩展属性
//     * @param wfE
//     * @param wfObject
//     */
//    public void getExtensionElements(Element wfE, BaseWorkflowObject wfObject) {
//        Element extE = wfE.element("extensionElements");
//
//        if (extE == null) {
//            return;
//        }
//
//        ExtensionElements extensionElements = wfObject.getExtensionElements();
//        if (extensionElements == null) {
//            extensionElements = new ExtensionElements();
//            wfObject.setExtensionElements(extensionElements);
//        }
//
//        getExtExecutionListener(extE, extensionElements);
//
//        getExtFormData(extE, extensionElements);
//    }

//    /**
//     * 扩展（监听器）
//     * @param extE
//     * @param extensionElements
//     */
//    public void getExtExecutionListener(Element extE, ExtensionElements extensionElements) {
//        List<ExecutionListener> executionListeners = new ArrayList<ExecutionListener>();
//        List nodes = extE.elements("executionListener");
//
//        if (nodes == null) {
//            return;
//        }
//        for (Iterator it = nodes.iterator(); it.hasNext();) {
//            Element elmEx = (Element) it.next();
//
//            ExecutionListener executionListener = new ExecutionListener();
//            executionListener.setEvent(XmlUtil.getAttributeValue(elmEx, "event"));
//            executionListener.setExpression(XmlUtil.getAttributeValue(elmEx, "expression"));
//            executionListener.setDelegateExpression(XmlUtil.getAttributeValue(elmEx, "delegateExpression"));
//
//            getExecutionListenerFields(elmEx, executionListener);
//            executionListeners.add(executionListener);
//        }
//
//        extensionElements.setExecutionListeners(executionListeners);
//
//    }

    /**
     * 扩展（属性）
     * @param extE
     * @param extensionElements
     */
    protected void getExtProperties(Element extE, ExtensionElements extensionElements) {

        Element propertiesE = extE.element("properties");

        if (propertiesE != null) {
            Properties properties = new Properties();
            extensionElements.setProperties(properties);

            List<Property> propertys = new ArrayList<Property>();
            List nodes = propertiesE.elements("property");

            for (Iterator it = nodes.iterator(); it.hasNext();) {
                Element elm = (Element) it.next();

                Property property = new Property();
                property.setId(XmlUtil.getAttributeValue(elm, "id"));
                property.setName(XmlUtil.getAttributeValue(elm, "name"));
                property.setType(XmlUtil.getAttributeValue(elm, "type"));
                property.setValue(XmlUtil.getAttributeValue(elm, "value"));
                propertys.add(property);
            }

            properties.setProperties(propertys);
        }
    }

    /**
     * 扩展（表单）
     * @param extE
     * @param extensionElements
     */
    protected void getExtFormData(Element extE, ExtensionElements extensionElements) {

        Element formDataE = extE.element("formData");

        if (formDataE == null) {
            return;
        }
        FormData formData = new FormData();
        extensionElements.setFormData(formData);

        List<FormField> formFields = new ArrayList<FormField>();
        List nodes = formDataE.elements("formField");

        for (Iterator it = nodes.iterator(); it.hasNext();) {
            Element elm = (Element) it.next();

            FormField formField = new FormField();
            formField.setId(XmlUtil.getAttributeValue(elm, "id"));
            formField.setName(XmlUtil.getAttributeValue(elm, "label"));
//                    formField.setLabel(XmlUtil.getAttributeValue(elm, "label"));
            formField.setType(XmlUtil.getAttributeValue(elm, "type")); // 类型
            formField.setDefaultValue(XmlUtil.getAttributeValue(elm, "defaultValue")); // 默认
            formField.setExpression(XmlUtil.getAttributeValue(elm, "expression")); // 表达式
            formField.setVariable(XmlUtil.getAttributeValue(elm, "variable")); // 变量
            formField.setRequired(XmlUtil.getAttributeValue(elm, "required")); // 必须
            formField.setDatePattern(XmlUtil.getAttributeValue(elm, "datePattern")); //
            formField.setReadable(XmlUtil.getAttributeValue(elm, "readable")); // 可读
            formField.setWritable(XmlUtil.getAttributeValue(elm, "writeable")); // 可写

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

        Element propertiesE = formFieldE.element("properties");

        if (propertiesE != null) {
            Properties properties = new Properties();
            formField.setProperties(properties);

            List<Property> properts = new ArrayList<Property>();
            List nodes = propertiesE.elements("property");

            for (Iterator it = nodes.iterator(); it.hasNext();) {
                Element elm = (Element) it.next();

                Property property = new Property();
                property.setId(XmlUtil.getAttributeValue(elm, "id"));
                property.setName(XmlUtil.getAttributeValue(elm, "value"));
//                property.setType(XmlUtil.getAttributeValue(elm, "type"));
//                property.setValue(XmlUtil.getAttributeValue(elm, "value"));
                properts.add(property);
            }

            properties.setProperties(properts);
        }
    }

//    /**
//     * 监听属性
//     * @param elmEx
//     * @param executionListener
//     */
//    public void getExecutionListenerFields(Element elmEx, ExecutionListener executionListener) {
//        List<Field> fields = new ArrayList<Field>();
//        List nodes = elmEx.elements("field");
//        for (Iterator it = nodes.iterator(); it.hasNext();) {
//            Element elm = (Element) it.next();
//
//            Field field = new Field();
//            field.setName(XmlUtil.getAttributeValue(elm, "name"));
//
//            Element exE = elm.element("expression");
//            if (exE != null) {
//                field.setExpression(exE.getText());
//            }
//
//            Element exS = elm.element("string");
//            if (exS != null) {
//                field.setString(exS.getText());
//            }
//
//            fields.add(field);
//        }
//
//        executionListener.setFields(fields);
//    }

    /**
     * 转换
     * @param xml
     * @return
     */
    public static BpmnModel convertBpmnModel(String xml) {

        String bpmnString = convert(xml).getActivitiXml();

        if (bpmnString == null) { // 解析错误
            return null;
        }

        BpmnModel bpmnModel = XmlUtil.convertToBpmnModel(bpmnString);

        return bpmnModel;
    }

    /**
     * 转换
     * @param xml
     * @return
     */
    public static Definitions convert(String xml) {
        C2AParser parser = new C2AParser();
        Definitions definitions = null;
        try {
            definitions = parser.getDefinitions(xml, "C");

            String bpmnString = parser.toString(definitions);
            // 生成新XML
            definitions.setActivitiXml(bpmnString);

            BpmnModel bpmnModel = XmlUtil.convertToBpmnModel(bpmnString);
            definitions.setBpmnModel(bpmnModel);

            return definitions;
        } catch (DocumentException | SAXException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return null;
    }
    
    public static String toXml(Definitions definitions)
    {
    	C2AParser parser = new C2AParser();
    	try {
			return parser.toString(definitions);
		} catch (DocumentException | IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    	return null;
    }

    public static void main(String[] args) {

//        String bpmnFile = "D:\\02.Projects\\KW_科技计划流程再造项目\\doc\\camuda\\diagram (47).bpmn";
        String bpmnFile = "D:\\02.Projects\\KW_科技计划流程再造项目\\bpmn\\create.bpmn";

        try {
            String cXmlString = FileUtils.readFileToString(new File(bpmnFile), "utf8");
//            System.out.println(cXmlString);
            String aXmlString = convert(cXmlString).getActivitiXml();

            FileUtils.writeStringToFile(new File(bpmnFile + ".xml"), aXmlString, "utf8");
//            System.out.println(aXmlString);
        } catch (IOException e) {
            e.printStackTrace();
        }
/*
//        String bpmnFile = "D:\\02.Projects\\KW_科技计划流程再造项目\\bpmn\\example2.bpmn";
//        Process parser = new Process();
//        parser.setId();
//        File file = new File("D:\\02.Projects\\KW_科技计划流程再造项目\\bpmn\\example2.bpmn");
        File file = new File(bpmnFile);

        C2AParser parser = new C2AParser();
        Definitions definitions = null;
        try {
            definitions = parser.getDefinitions(file);

            // 生成新XML
            parser.toActiviti(definitions, bpmnFile);
        } catch (DocumentException e) {
            e.printStackTrace();
        }
//        for(Book book : bookList){
//            System.out.println(book);
//        }*/
    }

    /**
     * 转成文件
     * @param definitions
     * @param bpmnFile
     * @throws DocumentException
     */
    protected void toActiviti(Definitions definitions, String bpmnFile) throws DocumentException {
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
    }



    /**
     * 生成Activiti的Definition
     * @param document
     * @param definitions
     */
    protected void setDefinitions(Document document, Definitions definitions) throws DocumentException {

        Element rootE = document.addElement("definitions", "http://www.omg.org/spec/BPMN/20100524/MODEL");// 建立根节点
        rootE.addAttribute("xmlns:xsi", "http://www.w3.org/2001/XMLSchema-instance");
        rootE.addAttribute("xmlns:xsd", "http://www.w3.org/2001/XMLSchema");
        rootE.addAttribute("xmlns:activiti", "http://activiti.org/bpmn");
        rootE.addAttribute("xmlns:bpmndi", "http://www.omg.org/spec/BPMN/20100524/DI");
        rootE.addAttribute("xmlns:omgdc", "http://www.omg.org/spec/DD/20100524/DC");
        rootE.addAttribute("xmlns:omgdi", "http://www.omg.org/spec/DD/20100524/DI");
        rootE.addAttribute("typeLanguage", "http://www.w3.org/2001/XMLSchema");
        rootE.addAttribute("expressionLanguage", "http://www.w3.org/1999/XPath");
        rootE.addAttribute("targetNamespace", "http://bpmn.io/schema/bpmn");

        rootE.addNamespace("activiti", "http://activiti.org/bpmn");

        // Process 节点
        setProcess(rootE, definitions);

        // 消息
        setMessages(rootE, definitions);

        // 信号
        setSignals(rootE, definitions);

        // 图形节点
        Document diagramDoc = DocumentHelper.parseText(definitions.getBPMNDiagram());

        rootE.add(diagramDoc.getRootElement());

    }

    /**
     * Process 节点
     * @param rootE
     * @param definitions
     * @throws DocumentException
     */
    protected void setProcess(Element rootE, Definitions definitions) {
        BpmnProcess process = definitions.getProcess();
        Element processE = rootE.addElement("process");

        setBaseProcess(processE, process);
        /*processE.addAttribute("id", process.getId());
        processE.addAttribute("name", process.getName());
        processE.addAttribute("isExecutable", process.getIsExecutable());

        // 是否子流程保存到数据库里
//        processE.addAttribute("processType", XmlUtil.unparseProcessType(process.getProcessType()));

        setProcessDefaultAttr(processE, process);

        // 开始事件
        setStartEvents(processE, process);

        // 开始事件
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
        setCallActivity(processE, process);

        // 扩展（DataObject)
        setProcessExtensionElements(processE, process);*/
    }

    /**
     * Process 节点
     * @param processE
     * @param process
     * @throws DocumentException
     */
    protected void setBaseProcess(Element processE, BaseProcess process) {
        processE.addAttribute("id", process.getId());
        processE.addAttribute("name", process.getName());
        processE.addAttribute("isExecutable", process.getIsExecutable());

        // 是否子流程保存到数据库里
//        processE.addAttribute("processType", XmlUtil.unparseProcessType(process.getProcessType()));

        setProcessDefaultAttr(processE, process);

        // 开始事件
        setStartEvents(processE, process);

        // 开始事件
        setEndEvents(processE, process);

        // 用户任务
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

        // 扩展（DataObject)
        setProcessExtensionElements(processE, process);

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
            Element subProcessE = processE.addElement("subProcess");

            setBaseProcess(subProcessE, subProcess);
        }
    }

    /**
     * 默认属性
     * @param wfE
     * @param wfObject
     */
    protected void setDefaultAttr(Element wfE, BaseWorkflowObject wfObject) {
        // 文档
        if (wfObject.getDocumentation() != null) {
            Element documentation = wfE.addElement("documentation");
            documentation.setText(wfObject.getDocumentation());
        }

        // 扩展属性
        if (wfObject.getExtensionElements() == null) {
            return;
        }

        ExtensionElements extensionElements = wfObject.getExtensionElements();
        //if (extensionElements.getExecutionListeners() == null && extensionElements.getProperties() == null)

        Element wfe = wfE.addElement("extensionElements");

        setExtFormData(wfe, extensionElements);

        setExtExecutionListener(wfe, extensionElements);
    }

    /**
     * 默认属性
     * @param wfE
     * @param wfObject
     */
    protected void setProcessDefaultAttr(Element wfE, BaseWorkflowObject wfObject) {
        // 文档
        if (wfObject.getDocumentation() != null) {
            Element documentation = wfE.addElement("documentation");
            documentation.setText(wfObject.getDocumentation());
        }

        // 扩展属性
        if (wfObject.getExtensionElements() == null) {
            return;
        }

        ExtensionElements extensionElements = wfObject.getExtensionElements();
        if (CollectionUtils.isEmpty(extensionElements.getExecutionListeners())) {
            return;
        }

        Element wfe = wfE.addElement("extensionElements");

        setExtExecutionListener(wfe, extensionElements);
    }


    /**
     * 开始事件
     * @param processE
     * @param process
     */
    protected void setStartEvents(Element processE, BaseProcess process) {
        for (StartEvent startEvent : process.getStartEvents()) {
            Element startEventE = processE.addElement("startEvent");
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
     * 边界事件
     * @param processE
     * @param process
     */
    protected void setBoundaryEvent(Element processE, BaseProcess process) {
        for (BoundaryEvent boundaryEvent : process.getBoundaryEvents()) {
            Element boundaryEventE = processE.addElement("boundaryEvent");
            boundaryEventE.addAttribute("id", boundaryEvent.getId());
            boundaryEventE.addAttribute("name", boundaryEvent.getName());
            boundaryEventE.addAttribute("attachedToRef", boundaryEvent.getAttachedToRef());

            // 定时事件
            setTimerEventDefinition(boundaryEventE, boundaryEvent);
            // 信号事件
            setSignalEventDefinition(boundaryEventE, boundaryEvent);
            // 消息事件
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
            Element catchEventE = processE.addElement("intermediateCatchEvent");
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
            Element throwEventE = processE.addElement("intermediateThrowEvent");
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
     * 定时事件
     * @param eventDefinitionE
     * @param eventDefinition
     */
    protected void setTimerEventDefinition(Element eventDefinitionE, IEventDefinition eventDefinition) {
        TimerEventDefinition timerEventDefinition = eventDefinition.getTimerEventDefinition();

        if (timerEventDefinition == null) {
            return;
        }
        Element timeE = eventDefinitionE.addElement("timerEventDefinition");
        if (timerEventDefinition.getTimeDuration() != null) {
            timeE.addElement("timeDuration").setText(timerEventDefinition.getTimeDuration());
        }

        if (timerEventDefinition.getTimeDate() != null) {
            timeE.addElement("timeDate").setText(timerEventDefinition.getTimeDate());
        }

        if (timerEventDefinition.getTimeCycle() != null) {
           timeE.addElement("timeCycle").setText(timerEventDefinition.getTimeCycle());
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
        Element signalE = eventDefinitionE.addElement("signalEventDefinition");
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
        Element messageE = eventDefinitionE.addElement("messageEventDefinition");
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
        Element errorE = eventDefinitionE.addElement("errorEventDefinition");
        errorE.addAttribute("errorRef", errorEventDefinition.getErrorRef());

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
        Element messageE = eventDefinitionE.addElement("compensateEventDefinition");

    }

    /**
     * 结束事件
     * @param processE
     * @param process
     */
    protected void setEndEvents(Element processE, BaseProcess process) {
        for (EndEvent endEvent : process.getEndEvents()) {
            Element endEventE = processE.addElement("endEvent");
            endEventE.addAttribute("id", endEvent.getId());
            endEventE.addAttribute("name", endEvent.getName());

            // 出错事件
            setErrorEventDefinition(endEventE, endEvent);

            setDefaultAttr(endEventE, endEvent);
        }
    }

    /**
     * 消息
     * @param processE
     * @param definitions
     */
    protected void setMessages(Element processE, Definitions definitions) {
        for (Message message : definitions.getMessages()) {
            Element messageE = processE.addElement("message");
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
            Element signalE = processE.addElement("signal");
            signalE.addAttribute("id", signal.getId());
            signalE.addAttribute("name", signal.getName());
            signalE.addAttribute("activiti:scope", StringUtils.defaultString(signal.getScope(), "processInstance"));

        }
    }

    /**
     * 顺序流
     * @param processE
     * @param process
     */
    protected void setSequenceFlow(Element processE, BaseProcess process) {
        for (SequenceFlow sequenceFlow : process.getSequenceFlows()) {
            Element sequenceFlowE = processE.addElement("sequenceFlow");
            sequenceFlowE.addAttribute("id", sequenceFlow.getId());
            sequenceFlowE.addAttribute("sourceRef", sequenceFlow.getSourceRef());
            sequenceFlowE.addAttribute("targetRef", sequenceFlow.getTargetRef());
            sequenceFlowE.addAttribute("skipExpression", sequenceFlow.getSkipExpression());

            ConditionExpression conditionExpression = sequenceFlow.getConditionExpression();

            if (conditionExpression != null) {
                Element condE = sequenceFlowE.addElement("conditionExpression");
                condE.addAttribute("xsi:type", "tFormalExpression");
                condE.addCDATA(conditionExpression.getValue());
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
            Element gatewayE = processE.addElement(gatewayType);
            gatewayE.addAttribute("id", gateway.getId());
            gatewayE.addAttribute("name", gateway.getName());
            gatewayE.addAttribute("activiti:async", gateway.getAsync());
            gatewayE.addAttribute("activiti:exclusive", gateway.getExclusive());

            setDefaultAttr(gatewayE, gateway);
        }
    }

    /**
     * 用户任务
     * @param processE
     * @param process
     */
    protected void setUserTasks(Element processE, BaseProcess process) {
        for (UserTask userTask : process.getUserTasks()) {
            Element userTaskE = processE.addElement("userTask");
            userTaskE.addAttribute("id", userTask.getId());
            userTaskE.addAttribute("name", userTask.getName());
            userTaskE.addAttribute("activiti:assignee", userTask.getAssignee());
            userTaskE.addAttribute("activiti:candidateUsers", userTask.getCandidateUsers());
            userTaskE.addAttribute("activiti:candidateGroups", userTask.getCandidateGroups());

            setDefaultAttr(userTaskE, userTask);
//            setUserTaskExtensionElements(userTaskE, userTask);

            // 多实例
            setMultiInstanceLoopCharacteristics(userTaskE, userTask);
        }
    }

    /**
     * 任务
     * @param processE
     * @param process
     */
    protected void setTasks(Element processE, BaseProcess process) {
        for (Task task : process.getTasks()) {
            Element taskE = processE.addElement("task");
            taskE.addAttribute("id", task.getId());
            taskE.addAttribute("name", task.getName());

            setDefaultAttr(taskE, task);
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

        Element multiInstanceE = userTaskE.addElement("multiInstanceLoopCharacteristics");

        multiInstanceE.addAttribute("isSequential", multiInstanceLoop.getIsSequential());
        multiInstanceE.addAttribute("activiti:collection", multiInstanceLoop.getCollection());
        multiInstanceE.addAttribute("activiti:elementVariable", multiInstanceLoop.getElementVariable());

        if (multiInstanceLoop.getLoopCardinality() != null) {
            Element loopCardinalityE = multiInstanceE.addElement("loopCardinality");
            loopCardinalityE.setText(multiInstanceLoop.getLoopCardinality());
        }

        if (multiInstanceLoop.getCompletionCondition() != null) {
            Element completionConditionE = multiInstanceE.addElement("completionCondition");
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
            Element callActivityE = processE.addElement("callActivity");
            callActivityE.addAttribute("id", callActivity.getId());
            callActivityE.addAttribute("name", callActivity.getName());
            callActivityE.addAttribute("calledElement", callActivity.getCalledElement());

            // 多实例
            setMultiInstanceLoopCharacteristics(callActivityE, callActivity);

            if (CollectionUtils.isNotEmpty(callActivity.getInCallParameters()) ||
                    CollectionUtils.isNotEmpty(callActivity.getOutCallParameters())) {
                Element extE = callActivityE.addElement("extensionElements");

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
            Element paramE = extE.addElement("activiti:" + paramType);
            paramE.addAttribute("source", callParameter.getSource());
            paramE.addAttribute("sourceExpression", callParameter.getSourceExpression());
            paramE.addAttribute("target", callParameter.getTarget());
            paramE.addAttribute("targetExpression", callParameter.getTargetExpression());

        }

    }

    /**
     * 用户任务（扩展属性）
//     * @param userTaskE
//     * @param userTask
     */
    /*
    public void setUserTaskExtensionElements(Element userTaskE, UserTask userTask) {

        if (userTask.getExtensionElements() != null) {
            Element extensionElements = userTaskE.addElement("extensionElements");

            FormData formData = userTask.getExtensionElements().getFormData();

            if (formData == null) {
                return;
            }
            for (FormField formField : formData.getFormFields()) {
                Element formFieldE = extensionElements.addElement("activiti:formProperty");
                formFieldE.addAttribute("id", formField.getId());
                formFieldE.addAttribute("name", formField.getName());
                formFieldE.addAttribute("type", formField.getType());
                formFieldE.addAttribute("default", formField.getDefaultValue());
                formFieldE.addAttribute("expression", formField.getExpression());
                formFieldE.addAttribute("variable", formField.getVariable());
                formFieldE.addAttribute("required", formField.getRequired());
                formFieldE.addAttribute("datePattern", formField.getDatePattern());
                formFieldE.addAttribute("readable", formField.getReadable());
                formFieldE.addAttribute("writable", formField.getWritable());
            }
        }
    }*/
    protected void setExtFormData(Element wfe, ExtensionElements extensionElements) {

        FormData formData = extensionElements.getFormData();

        if (formData == null) {
            return;
        }
        for (FormField formField : formData.getFormFields()) {
            Element formFieldE = wfe.addElement("activiti:formProperty");
            formFieldE.addAttribute("id", formField.getId());
            formFieldE.addAttribute("name", formField.getName());
            formFieldE.addAttribute("type", formField.getType());
            formFieldE.addAttribute("default", formField.getDefaultValue());
            formFieldE.addAttribute("expression", formField.getExpression());
            formFieldE.addAttribute("variable", formField.getVariable());
            formFieldE.addAttribute("required", formField.getRequired());
            formFieldE.addAttribute("datePattern", formField.getDatePattern());
            formFieldE.addAttribute("readable", formField.getReadable());
            formFieldE.addAttribute("writable", formField.getWritable());

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

        for (Property property : properties.getProperties()) {
            Element dataObjectE = formFieldE.addElement("activiti:value");
            dataObjectE.addAttribute("id", property.getId());
            dataObjectE.addAttribute("name", property.getName());

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
            Element executionListenerE = wfe.addElement("activiti:executionListener");
            executionListenerE.addAttribute("event", executionListener.getEvent());
            executionListenerE.addAttribute("expression", executionListener.getExpression());
            executionListenerE.addAttribute("delegateExpression", executionListener.getDelegateExpression());
            executionListenerE.addAttribute("class", executionListener.getListenerClass());

            for (Field field : executionListener.getFields()) {
                Element fieldE = executionListenerE.addElement("activiti:field");
                fieldE.addAttribute("name", field.getName());

                if (field.getString() != null) {
                    Element stringE = fieldE.addElement("activiti:string");
                    stringE.addCDATA(field.getString());
                }

                if (field.getExpression() != null) {
                    Element expressionE = fieldE.addElement("activiti:expression");
                    expressionE.addCDATA(field.getExpression());
                }
            }

        }
    }

    /**
     * 流程（扩展属性）
     * @param processE
     * @param process
     */
    protected void setProcessExtensionElements(Element processE, BaseProcess process) {
        if (process.getExtensionElements() != null) {

            Properties properties = process.getExtensionElements().getProperties();
            if (properties == null) {
                return;
            }

            for (Property property : properties.getProperties()) {
                Element dataObjectE = processE.addElement("dataObject");
                dataObjectE.addAttribute("id", property.getId());
                dataObjectE.addAttribute("name", property.getName());
                dataObjectE.addAttribute("itemSubjectRef", property.getType());

                Element extensionElementsE = dataObjectE.addElement("extensionElements");

                Element valueE = extensionElementsE.addElement("activiti:value");

                valueE.addText(property.getValue());
            }
        }
    }
    /*public void setProcessExtensionElements(Element processE, BpmnProcess process) {
        if (process.getExtensionElements() != null) {

            FormData formData = process.getExtensionElements().getFormData();
            if (formData == null) {
                return;
            }

            for (FormField formField : formData.getFormFields()) {
                Element dataObjectE = processE.addElement("dataObject");
                dataObjectE.addAttribute("id", formField.getId());
                dataObjectE.addAttribute("name", formField.getName());
                dataObjectE.addAttribute("itemSubjectRef", formField.getType());

                Element extensionElementsE = dataObjectE.addElement("extensionElements");

                Element valueE = extensionElementsE.addElement("activiti:value");

                valueE.addText(formField.getValue());
            }
        }
    }*/
    /*
    public void setExtProperties(Element processE, ExtensionElements extensionElements) {

        Properties properties = extensionElements.getProperties();
        if (properties == null) {
            return;
        }

        for (Property formField : properties.getProperties()) {
            Element dataObjectE = processE.addElement("dataObject");
//            dataObjectE.addAttribute("id", formField.getId());
            dataObjectE.addAttribute("name", formField.getName());
//            dataObjectE.addAttribute("itemSubjectRef", formField.getType());

            Element extensionElementsE = dataObjectE.addElement("extensionElements");

            Element valueE = extensionElementsE.addElement("activiti:value");

            valueE.addText(formField.getValue());
        }
    }*/
}
