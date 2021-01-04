package webapi.quartz.utils;

import org.quartz.SchedulerException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.context.ServletContextAware;
import webapi.quartz.entity.QuartzJob;
import webapi.quartz.service.IQuartzJobService;
import javax.servlet.ServletContext;
import java.util.List;

/**
 * 暂时解决解决定时任务bug
 */
@Component
public class QuartzUtils implements ServletContextAware {
    private static Logger log = LoggerFactory.getLogger(QuartzUtils.class);

    @Autowired
    private IQuartzJobService quartzJobService;

    @Override
    public void setServletContext(ServletContext servletContext) {
        updateQuartz();
    }


    private void updateQuartz(){
        List<QuartzJob> list = quartzJobService.list();
        if(list!=null && list.size()>0){
            list.forEach(quartzJob -> {
                try {
                    quartzJobService.editAndScheduleJob(quartzJob);
                } catch (SchedulerException e) {
                    e.printStackTrace();
                }
            });
        }
    }
    
}
