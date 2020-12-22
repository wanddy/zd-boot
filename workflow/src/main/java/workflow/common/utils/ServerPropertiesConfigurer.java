package workflow.common.utils;

import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.beans.factory.config.PropertyPlaceholderConfigurer;

import java.util.Properties;

/**
 * 
 * @功能描述：加载服务器配置信息
 * @创建时间：2013-11-21
 * @作者： 侯广强
 *
 */
public class ServerPropertiesConfigurer extends PropertyPlaceholderConfigurer {

	private Properties props;
	
	@Override
	protected void processProperties(ConfigurableListableBeanFactory beanFactoryToProcess,
                                     Properties props){
		super.processProperties(beanFactoryToProcess, props);
		this.props = props;
	}
	
	public Object getProperty(String key){
		return props.get(key);
	}
}
