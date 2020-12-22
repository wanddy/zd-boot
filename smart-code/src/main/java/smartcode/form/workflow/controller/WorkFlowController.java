package smartcode.form.workflow.controller;

import commons.api.vo.Result;
import commons.auth.vo.LoginUser;
import commons.constant.CommonConstant;
import org.apache.commons.beanutils.BeanUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.shiro.SecurityUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;
import smartcode.form.mapper.OnlCgformIndexMapper;
import smartcode.form.workflow.model.WorkFlowModel;

import java.util.Map;

/**
 * @Author: LiuHongYan
 * @Date: 2020/9/2 11:19
 * @Description: zdit.zdboot.smartcode.online.workflow
 **/
@RestController("WorkFlowController")
@RequestMapping({"/online/workflow"})
public class WorkFlowController {
//
//    @Autowired
//    private RestTemplate restTemplate ;
//    @Autowired
//    private OnlCgformIndexMapper onlCgformIndexMapper;

//    @Value("${workflow.host}")
//    private String workHost;
//
//    /**
//     * 获取已发布流程列表
//     * @return
//     */
//    @PostMapping({"/getProcessDefList"})
//    public Result getProcessDefList (){
//        String url = workHost+"api/workflow/console/getProcessDefList?key&name&state=0&onlyLatestVersion=true&pageNum=1&pageSize=1000";
//        return Result.ok(this.restTemplate.getForObject(url,Object.class));
//    }
//
//    /**
//     * 启动流程
//     * @return
//     */
//    @PostMapping({"/startProcessInstanceById"})
//    public Result startProcessInstanceById (@RequestBody WorkFlowModel flowModel){
//        LoginUser user = (LoginUser) SecurityUtils.getSubject().getPrincipal();
//        String url = workHost+"api/workflow/workflow/startProcessInstanceById1?userId="+user.getId()+"&processId="+flowModel.getProcessId()+"&values=&businessKey";
//        Object object = this.restTemplate.getForObject(url, Object.class);
//        try {
//            Map<String, String> map = BeanUtils.describe(object);
//            if(!(StringUtils.isNotBlank(map.get("code")) && map.get("code").equals("500"))){
//                String sql = "update " + flowModel.getFlowCode() + " Set bpm_status = 2 Where id = " +flowModel.getId() ;
//                onlCgformIndexMapper.updateByData(sql);
//            }
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//        return Result.ok(object);
//    }
//
//    /**
//     * 获取流程图
//     * @return
//     */
//    @PostMapping({"/getProcessPreview"})
//    public Result getProcessPreview (@RequestBody WorkFlowModel flowModel){
//        String url = workHost+"api/workflow/console/getProcessPreview?id="+flowModel.getProcessId();
//        Result<Object> r = new Result<>();
//        r.setSuccess(true);
//        r.setCode(CommonConstant.SC_OK_200);
//        r.setResult(this.restTemplate.getForObject(url,String.class));
//        return Result.ok(r);
//    }
}
