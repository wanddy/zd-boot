package workflow.business.model;

public class WorkflowConstant {
	
    /**
     * 待办任务保存失败
     */
    public static final String TASK_SAVE_FAIL = "60510";

    /**
     * 执行任务失败
     */
    public static final String TASK_COMPLATE_FAIL = "60511";

    /**
     * 执行任务成功，保存已办任务、审核日志、新待办任务失败
     */
    public static final String TASK_COMPLATE_SAVE_FAIL = "60512";

    /**
     * 启动任务失败
     */
    public static final String TASK_START_FAIL = "60513";

    /**
     * 启动任务成功，保存已办任务、审核日志、新待办任务失败
     */
    public static final String TASK_START_SAVE_FAIL = "60514";
 
    /**
 	 * 工作流操作参数前缀
 	 */
 	public static final String WORK_FLOW_OPERATE = "judge_";
 	
 	/**
 	 * 工作流用户参数前缀
 	 */
 	public static final String WORK_FLOW_USER = "taskuser_";

 	/**
 	 * 工作流角色参数前缀
 	 */
 	public static final String WORK_FLOW_ROLE = "role_";

 	/**
 	 * 工作流单位/部门参数前缀
 	 */
 	public static final String WORK_FLOW_UNIT = "unit_";
 	
 	/**
 	 * 工作流完成任务扩展参数前缀
 	 */
 	public static final String WORK_FLOW_EXTEND_PARAM = "extend_";

 	/**
 	 * 工作流完成任务扩展参数分隔符
 	 */
 	public static final String WORK_FLOW_EXTEND_REGEX = ":";

 	/** 
 	* @Fields REDIS_MQ_FINISHED_VIEW_KEY : redis MQ完成任务视图key
 	*/ 
 	public static final String REDIS_MQ_FINISHED_VIEW_KEY = "MQFinishedView";

 	/** 
 	* @Fields REDIS_MQ_START_VIEW_KEY : redis MQ启动流程视图key
 	*/ 
 	public static final String REDIS_MQ_START_VIEW_KEY = "MQStartView";
}
