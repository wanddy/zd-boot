package smartcode.form.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import commons.constant.CommonConstant;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import smartcode.form.entity.OnlCgformIndex;
import smartcode.form.mapper.OnlCgformHeadMapper;
import smartcode.form.mapper.OnlCgformIndexMapper;
import smartcode.form.service.OnlCgformIndexService;

import java.util.Iterator;
import java.util.List;

/**
 * @Author: LiuHongYan
 * @Date: 2020/8/24 10:33
 * @Description: zdit.zdboot.auth.online.service.impl
 **/
@Service
public class OnlCgformIndexServiceImpl extends ServiceImpl<OnlCgformIndexMapper, OnlCgformIndex> implements OnlCgformIndexService {

    private static final Logger log = LoggerFactory.getLogger(OnlCgformIndexServiceImpl.class);

    @Autowired
    private OnlCgformHeadMapper onlCgformHeadMapper;

    public boolean isExistIndex(String countSql) {
        if (countSql == null) {
            return true;
        } else {
            Integer var2 = this.baseMapper.queryIndexCount(countSql);
            return var2 != null && var2 > 0;
        }
    }

    @Override
    public void createIndex(String code, String databaseType, String tableName) {
        QueryWrapper queryWrapper = new QueryWrapper<OnlCgformIndex>();
        queryWrapper.eq("cgform_head_id" , code);
        List list = this.list(queryWrapper);
        if (list != null && list.size() > 0) {
            Iterator iterator = list.iterator();

            while(iterator.hasNext()) {
                OnlCgformIndex onlCgformIndex = (OnlCgformIndex)iterator.next();
                if (!CommonConstant.DEL_FLAG_1.equals(onlCgformIndex.getDelFlag()) && "N".equals(onlCgformIndex.getIsDbSynch())) {
                    String str = "";
                    String indexName = onlCgformIndex.getIndexName();
                    String indexField = onlCgformIndex.getIndexField();
                    String indexType = "normal".equals(onlCgformIndex.getIndexType()) ? " index " : onlCgformIndex.getIndexType() + " index ";
                    byte by = -1;
                    switch(databaseType.hashCode()) {
                        case -1955532418:
                            if (databaseType.equals("ORACLE")) {
                                by = 1;
                            }
                            break;
                        case -1620389036:
                            if (databaseType.equals("POSTGRESQL")) {
                                by = 3;
                            }
                            break;
                        case 73844866:
                            if (databaseType.equals("MYSQL")) {
                                by = 0;
                            }
                            break;
                        case 912124529:
                            if (databaseType.equals("SQLSERVER")) {
                                by = 2;
                            }
                    }

                    switch(by) {
                        case 0:
                            str = "create " + indexType + indexName + " on " + tableName + "(" + indexField + ")";
                            break;
                        case 1:
                            str = "create " + indexType + indexName + " on " + tableName + "(" + indexField + ")";
                            break;
                        case 2:
                            str = "create " + indexType + indexName + " on " + tableName + "(" + indexField + ")";
                            break;
                        case 3:
                            str = "create " + indexType + indexName + " on " + tableName + "(" + indexField + ")";
                            break;
                        default:
                            str = "create " + indexType + indexName + " on " + tableName + "(" + indexField + ")";
                    }

                    log.info(" 创建索引 executeDDL ：" + str);
                    this.onlCgformHeadMapper.executeDDL(str);
                    onlCgformIndex.setIsDbSynch("Y");
                    this.updateById(onlCgformIndex);
                }
            }
        }
    }

    @Override
    public List<OnlCgformIndex> getCgformIndexsByCgformId(String cgformId) {
        return this.baseMapper.selectList(new QueryWrapper<OnlCgformIndex>().in("cgform_head_id",new Object[]{cgformId}));

    }


}
