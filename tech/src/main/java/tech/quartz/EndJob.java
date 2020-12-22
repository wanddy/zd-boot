package tech.quartz;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import groovy.util.logging.Slf4j;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.springframework.beans.factory.annotation.Autowired;
import tech.techActivity.entity.TechActivity;
import tech.techActivity.service.ITechActivityService;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

/**
 * 定时任务--活动结束
 */
@Slf4j
public class EndJob implements Job{


    @Autowired
    private ITechActivityService techActivityService;

    @Override
    public void execute(JobExecutionContext jobExecutionContext) throws JobExecutionException {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
        List<TechActivity> techActivityList = techActivityService.list(
                new QueryWrapper<TechActivity>()
                        .gt("end_time", simpleDateFormat.format(new Date()))
                        .eq("status",2L).eq("audit",2L));
        if(techActivityList!=null && techActivityList.size()>0){
            techActivityList.forEach(techActivity -> {
                techActivity.setStatus(4L);
            });
            techActivityService.updateBatchById(techActivityList);
        }
    }

}
