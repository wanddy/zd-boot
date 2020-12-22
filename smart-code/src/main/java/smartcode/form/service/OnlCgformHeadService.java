package smartcode.form.service;

import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.extension.service.IService;
import commons.api.vo.Result;
import freemarker.template.TemplateException;
import smartcode.config.exception.BusinessException;
import smartcode.config.exception.DBException;
import smartcode.form.entity.OnlCgformButton;
import smartcode.form.entity.OnlCgformEnhanceJs;
import smartcode.form.entity.OnlCgformHead;
import smartcode.form.model.AModel;
import smartcode.form.model.OnlGenerateModel;

import java.io.IOException;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;

/**
 * @Author: LiuHongYan
 * @Date: 2020/8/21 16:06
 * @Description: zdit.zdboot.online.service
 **/
public interface OnlCgformHeadService extends IService<OnlCgformHead> {
    /**
     * online表单开发列表页
     *
     * @param headList
     */
    void initCopyState(List<OnlCgformHead> headList);

    /**
     * 单表代码生成
     *
     * @param model
     * @return
     * @throws Exception
     */
    List<String> generateCode(OnlGenerateModel model) throws Exception;

    /**
     * 多表代码生成
     *
     * @param model
     * @return
     * @throws Exception
     */
    List<String> generateOneToMany(OnlGenerateModel model) throws Exception;

    /**
     * 新增表信息
     *
     * @param aModel
     * @return
     */
    Result addAll(AModel aModel);

    /**
     * 同步数据库
     *
     * @param code
     * @param synMethod
     */
    void doDbSynch(String code, String synMethod) throws DBException, SQLException, IOException, TemplateException;

    /**
     * 表单同步数据库
     *
     * @return
     */
    List<String> queryOnlinetables();


    void saveDbTable2Online(String s);

    OnlCgformEnhanceJs queryEnhanceJs(String code, String form);

    List<OnlCgformButton> queryButtonList(String code, boolean isListButton);

    void deleteRecordAndTable(String id) throws DBException, SQLException;

    void executeEnhanceList(OnlCgformHead head, String buttonCode, List<Map<String, Object>> dataList) throws BusinessException;

    void deleteOneTableInfo(String code, String id) throws BusinessException;

    void saveManyFormData(String var1, JSONObject var2, String var3) throws DBException, BusinessException;

    void editManyFormData(String var1, JSONObject var2) throws DBException, BusinessException;

    Result<?> editAll(AModel model);

    void deleteRecord(String id) throws DBException;

    Map<String, Object> queryManyFormData(String var1, String var2) throws DBException;
}
