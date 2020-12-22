package workflow.olddata.constant;

/**
 * 系统级静态变量
 *
 */
public class SystemConstant {

	/**
	 * 日志交换机
	 */
	public static final String USER_LOG_EXCHANGE = "user_log_exchange";

	/**
	 * 日志队列
	 */
	public static final String USER_LOG_QUEUE = "user_log_queue";

	/**
	 * 日志队列路由键
	 */
	public static final String USER_LOG_ROUTE_KEY = "stcsm.user.log";

	/**
	 * 正常返回
	 */
	public static final Integer NORMAL_CODE = 200;

	/**
	 * 未登录
	 */
	public static final Integer NOT_LOGIN = 400;

	/**
	 * 没有权限
	 */
	public static final Integer UNAUTHORIZED = 401;

	/**
	 * token超时
	 */
	public static final Integer TOKEN_EXPRIED = 402;

	/**
	 * token非法
	 */
	public static final Integer TOKEN_SIGNATURE = 403;

	/**
	 * 服务器错误
	 */
	public static final Integer INTERNAL_SERVER_ERROR = 500;

	/**
	 * 异常类大的状态码：操作失败。
	 */
	public static final Integer BUSSINESS_ERROR = 600;

	/**
	 * topic交换机
	 */
	public static final String STCSM_COMMON_EXCHANGE = "exchange.stcsm.common";

	/**
	 * 工作流任务队列
	 */
	public static final String STCSM_WORKFLOW_TASK_QUEUE = "queue.workflow.task";

	/**
	 * 工作流任务队列路由键-通配符
	 */
	public static final String STCSM_WORKFLOW_TASK_ROUTE_KEY = "route.workflow.task.#";

	/**
	 * 工作流启动任务队列
	 */
	public static final String STCSM_WORKFLOW_START_TASK_QUEUE = "queue.workflow.starttask";

	/**
	 * 工作流启动任务队列路由键-通配符
	 */
	public static final String STCSM_WORKFLOW_START_TASK_ROUTE_KEY = "route.workflow.starttask.#";
}
