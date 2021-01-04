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
import java.util.ArrayList;
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
        List<TechActivity> techActivityList = new ArrayList<>();
        List<TechActivity> techActivityList1 = techActivityService.list(
                new QueryWrapper<TechActivity>()
                        .lt("end_time", new Date())
                        .eq("status",2L)
                        .eq("audit",2L));
        if(techActivityList1!=null && techActivityList1.size()>0){
            techActivityList.addAll(techActivityList1);
        }
        List<TechActivity> techActivityList2 = techActivityService.list(
                new QueryWrapper<TechActivity>()
                        .lt("end_time", new Date())
                        .eq("status",2L)
                        .isNull("audit"));
        if(techActivityList2!=null && techActivityList2.size()>0){
            techActivityList.addAll(techActivityList2);
        }
        if(techActivityList.size()>0){
            techActivityList.forEach(techActivity -> {
                techActivity.setStatus(4L);
            });
            techActivityService.updateBatchById(techActivityList);
        }
    }

}
