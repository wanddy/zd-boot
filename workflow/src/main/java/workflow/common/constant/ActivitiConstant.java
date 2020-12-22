package workflow.common.constant;


/**
 * @author KaminanGTO
 * @ClassName: ActivitiConstant
 * @Description: 工作流静态常量
 * @date 2018年10月10日 上午10:40:54
 */
public class ActivitiConstant {

    /**
     * @author KaminanGTO
     * @ClassName: EOrderType
     * @Description: 排序类型
     * @date 2018年11月8日 下午3:12:58
     */
    public enum EOrderType {
        /**
         * @Fields None : 不排序
         */
        None,
        /**
         * @Fields Desc : 倒序
         */
        Desc,
        /**
         * @Fields Asc : 正序
         */
        Asc
    }

    /**
     * @Fields BUSINESS_KEY : 表单变量，任务完成后，存放到本地自定义变量，业务key
     */
    public static final String BUSINESS_KEY = "workflow";

    /**
     * @Fields BUSINESS_KEY : 表单变量，任务完成后，存放到本地自定义变量，业务过期时间
     */
    public static final String EXPIRED_KEY = "expired";

    /**
     * @Fields RETRIEVE_KEY : 表单变量，存放任务定义ID和取回判断变量信息，配置在流程任务中，用于判断任务是否可取回
     */
    public static final String RETRIEVE_KEY = "retrieve";

    /**
     * @Fields STARTER_KEY : 自定义变量，流程启动者key
     */
    public static final String STARTER_KEY = "starter";

    /**
     * @Fields DEF_BUSINESS_KEY : 默认业务key，启动流程时如果未传入业务key，则使用默认的。
     */
    public static final String DEF_PROCESS_INST_BUSINESS_KEY = "default";

    /**
     * @Fields BUSINESS_TYPENAME_KEY : 自定义变量，业务类型名字
     */
    public static final String BUSINESS_TYPENAME_KEY = "businessTypeName";

    /**
     * @Fields BUSINESS_TYPE_KEY : 自定义变量，业务类型
     */
    public static final String BUSINESS_TYPE_KEY = "businessType";

    /**
     * @Fields SIGN_USERS_HEAD : 会签用户数据ID头
     */
    public static final String SIGN_USERS_ID_HEAD = "sign";

    /**
     * @Fields DEF_SIGN_USER_HEAD : 会签用户定义数据ID头
     */
    public static final String DEF_SIGN_USER_ID_HEAD = "defsign";

    /**
     * @Fields CLAIM_USERS_ID_HEAD : 候选用户数据ID头
     */
    public static final String CLAIM_USERS_ID_HEAD = "claim";

    /**
     * @Fields DEF_CLAIM_USERS_ID_HEAD : 候选用户定义数据ID头
     */
    public static final String DEF_CLAIM_USERS_ID_HEAD = "defclaim";

    /**
     * @Fields CLAIM_GROUP_JOBS_HEAD : 候选角色数据id头
     */
    public static final String CLAIM_GROUP_JOBS_HEAD = "job";

    /**
     * @Fields CLAIM_GROUP_JOBS_HEAD : 候选角色定义数据id头
     */
    public static final String DEF_CLAIM_GROUP_JOBS_HEAD = "defjob";

    /**
     * @Fields USERTASK_HEAD : 用户任务头
     */
    public static final String USERTASK_HEAD = "usertask";

    /**
     * @Fields CALLTASK_HEAD : 调用任务头
     */
    public static final String CALLTASK_HEAD = "calltask";

    /**
     * @Fields TEMPTASK_HEAD : 临时任务，目前用在循环任务开始任务
     */
    public static final String TEMPTASK_HEAD = "temptask";

    /**
     * @Fields SIGN_USERS_FORM_HEAD : 会签用户表单数据ID头
     */
    public static final String SIGN_USERS_FORM_ID_HEAD = "us";

    /**
     * @Fields CLAIM_USERS_FORM_ID : 当前任务候选用户列表信息表单数据ID
     */
    public static final String CURRENT_TASK_CLAIM_USERS_FORM_ID = "currentus";

    /**
     * @Fields SIGN_USERS_FORM_HEAD : 候选用户表单数据ID头
     */
    public static final String CLAIM_USERS_FORM_ID_HEAD = "claimus";

    /**
     * @Fields CLAIM_GROUPS_FORM_ID_HEAD : 候选用户组表单数据ID头
     */
    public static final String CLAIM_GROUPS_FORM_ID_HEAD = "claimgp";

    /**
     * @Fields CLAIM_GROUPS_UNIT_FORM_ID_HEAD : 候选用户组表单数据ID头--用于使用外部传递的数据进行赋值任务候选组信息
     */
    public static final String CLAIM_GROUPS_VALUE_FORM_ID_HEAD = "claimugp";

    /**
     * @Fields CLAIM_GROUPS_DATA_FORM_ID_HEAD : 候选组信息表单数据ID头--用于外部控制候选组信息使用
     */
    public static final String CLAIM_GROUPS_DATA_FORM_ID_HEAD = "claimdgp";

    /**
     * @Fields CLAIM_LOOP_TASK_FORM_ID_HEAD : 循环任务表单数据ID头
     */
    public static final String CLAIM_LOOP_TASK_FORM_ID_HEAD = "loopus";

    /**
     * @Fields CLAIM_GROUPS_FORM_TYPE_UNIT : 候选用户组部门标示-用于部门保持任务
     */
    public static final String CLAIM_GROUPS_FORM_TYPE_UNIT = "unit";

    /**
     * @Fields CLAIM_GROUPS_FORM_TYPE_REGION : 候选用户组区县标示-用于区域保持任务
     */
    public static final String CLAIM_GROUPS_FORM_TYPE_REGION = "region";

    /**
     * @Fields CLAIM_GROUPS_FORM_TYPE_ADMIN_UNIT : 候选用户组管理员部门标示-用于管理员同部门保持任务
     */
    public static final String CLAIM_GROUPS_FORM_TYPE_ADMIN_UNIT = "adminunit";

    /**
     * @Fields CLAIM_GROUPS_FORM_TYPE_ITEMS_UNIT : 候选用户组事项部门标示-用于事项同部门保持任务
     */
    public static final String CLAIM_GROUPS_FORM_TYPE_ITEMS_UNIT = "itemsunit";

    /**
     * @Fields BUSINESS_PROPERTY_KEY : 业务自定义变量数据ID头
     */
    public static final String BUSINESS_PROPERTY_KEY = "bp";

    /**
     * @Fields TASK_USER_ID_HEAD : 任务操作者变量头
     */
    public static final String TASK_USER_ID_HEAD = "taskuser";

    /**
     * @Fields TASK_JUDGE_HEAD : 任务判断头
     */
    public static final String TASK_JUDGE_HEAD = "judge";

    /**
     * @Fields TASK_ROLE_HEAD : 任务角色数据头，直接给业务处理
     */
    public static final String TASK_ROLE_HEAD = "role";

    /**
     * @Fields FORM_FORM_HEAD : 表单数据表单数据ID头
     */
    public static final String SMARTFORM_FORM_ID_HEAD = "form";

    /**
     * @Fields LOOP_COUNT_HEAD : 循环任务计数表单数据头
     */
    public static final String LOOP_COUNT_HEAD = "loop";

    /**
     * @Fields FORM_PROPERTY_KEY_SPAN : 工作流表单变量分割符
     */
    public static final String FORM_PROPERTY_KEY_SPAN = "_";

    /**
     * @Fields FORM_PROPERTY_ARRAY_KEY_SPAN : 工作流表单变量数组分割符
     */
    public static final String FORM_PROPERTY_ARRAY_KEY_SPAN = ",";

    /**
     * @Fields LOCAL_PROPERTY_KEY_SPAN : 流程任务本地变量分隔符
     */
    public static final String LOCAL_PROPERTY_KEY_SPAN = "~";

    /**
     * @Fields RETRIEVE_PROPERTY_VALUE_SPAN : 取回任务数据分隔符
     */
    public static final String RETRIEVE_PROPERTY_VALUE_SPAN = "__";

    /**
     * @Fields CLAIM_GROUPS_VALUE_SPAN : 候选组信息分割符。候选组信息规则为 角色+部门+区域
     */
    public static final String CLAIM_GROUPS_VALUE_SPAN = "_";

    /**
     * @Fields PROCESS_KEY_SPAN : 流程key分割符号，流程key规则 流程类型+流程业务类型+流程key
     */
    public static final String PROCESS_KEY_SPAN = "_";

    /**
     * @Fields PROCESS_TASK_ID_SPAN : 流程任务定义id分隔符，任务定义id规则 任务类型+任务业务类型
     */
    public static final String PROCESS_TASK_ID_SPAN = "_";

    /**
     * @Fields RETRIEVE_TASK_SPAN : 取回任务信息任务id分割符
     */
    public static final String RETRIEVE_TASK_SPAN = ",";

    /**
     * @Fields MAIN_PROCESS_HEAD : 主流程key头
     */
    public static final String MAIN_PROCESS_KEY_HEAD = "main_";

    /**
     * @Fields SUB_PROCESS_HEAD : 子流程key头
     */
    public static final String SUB_PROCESS_KEY_HEAD = "sub_";

    /**
     * @Fields LOOP_END_VALUE : 循环任务结束判断
     */
    public static final String LOOP_END_VALUE = "yes";

    /**
     * @Fields LOOP_CONTINUE_VALUE : 循环任务继续判断
     */
    public static final String LOOP_CONTINUE_VALUE = "no";

    /**
     * @Fields REDIS_USER_FINISHED_VIEW_KEY : redis用户已完成任务视图key
     */
    public static final String REDIS_USER_FINISHED_VIEW_KEY = "UserFinishedView";

    /**
     * @Fields REDIS_USER_FINISHED_LOADING_KEY : redis已完成数据加载中key
     */
    public static final String REDIS_FINISHED_LOADING_KEY = "FinishedLoading";

    /**
     * @Fields REDIS_UNIT_FINISHED_VIEW_KEY : redis部门已完成任务视图key
     */
    public static final String REDIS_UNIT_FINISHED_VIEW_KEY = "UnitFinishedView";

    /**
     * @Fields REDIS_HASHKEY_SPAN : redis hashkey通用分割符
     */
    public static final String REDIS_HASHKEY_SPAN = "_";

    /**
     * @Fields REDIS_FINISHED_INIT_TIME : redis已完成数据初始化锁时间
     */
    public static final long REDIS_FINISHED_INIT_TIME = 5 * 60;
}