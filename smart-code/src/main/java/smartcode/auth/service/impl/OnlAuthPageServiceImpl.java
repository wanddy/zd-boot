//
// Source code recreated from OnlCgformButtonServiceImpl .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package smartcode.auth.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import smartcode.auth.entity.OnlAuthPage;
import smartcode.auth.entity.OnlAuthRelation;
import smartcode.auth.mapper.OnlAuthPageMapper;
import smartcode.auth.mapper.OnlAuthRelationMapper;
import smartcode.auth.service.OnlAuthPageService;
import smartcode.auth.vo.AuthColumnVO;
import smartcode.auth.vo.AuthPageVO;

import java.util.ArrayList;
import java.util.List;

@Service("onlAuthPageServiceImpl")
public class OnlAuthPageServiceImpl extends ServiceImpl<OnlAuthPageMapper, OnlAuthPage> implements OnlAuthPageService {
    @Autowired
    private OnlAuthRelationMapper onlAuthRelationMapper;

    public OnlAuthPageServiceImpl() {
    }

    public void disableAuthColumn(AuthColumnVO authColumnVO) {
        LambdaUpdateWrapper lambdaUpdateWrapper = (new UpdateWrapper<OnlAuthPage>()).lambda().eq(OnlAuthPage::getCgformId, authColumnVO.getCgformId()).eq(OnlAuthPage::getCode, authColumnVO.getCode()).eq(OnlAuthPage::getType, 1).set(OnlAuthPage::getStatus, 0);
        this.update(lambdaUpdateWrapper);
    }

    @Transactional
    public void enableAuthColumn(AuthColumnVO authColumnVO) {
        String cgformId = authColumnVO.getCgformId();
        String code = authColumnVO.getCode();
        LambdaQueryWrapper lambdaQueryWrapper = (new LambdaQueryWrapper<OnlAuthPage>()).eq(OnlAuthPage::getCgformId, cgformId).eq(OnlAuthPage::getCode, code).eq(OnlAuthPage::getType, 1);
        List list = this.list(lambdaQueryWrapper);
        if (list != null && list.size() > 0) {
            LambdaUpdateWrapper wrapper= (new UpdateWrapper<OnlAuthPage>()).lambda().eq(OnlAuthPage::getCgformId, cgformId).eq(OnlAuthPage::getCode, code).eq(OnlAuthPage::getType, 1).set(OnlAuthPage::getStatus, 1);
            this.update(wrapper);
        } else {
            ArrayList arrayList = new ArrayList();
            arrayList.add(new OnlAuthPage(cgformId, code, 3, 5));
            arrayList.add(new OnlAuthPage(cgformId, code, 5, 5));
            arrayList.add(new OnlAuthPage(cgformId, code, 5, 3));
            this.saveBatch(arrayList);
        }

    }

    public void switchAuthColumn(AuthColumnVO authColumnVO) {
        String cgformId = authColumnVO.getCgformId();
        String code = authColumnVO.getCode();
        int switchFlag = authColumnVO.getSwitchFlag();
        if (switchFlag == 1) {
            this.switchListShow(cgformId, code, authColumnVO.isListShow());
        } else if (switchFlag == 2) {
            this.switchFormShow(cgformId, code, authColumnVO.isFormShow());
        } else if (switchFlag == 3) {
            this.switchFormEditable(cgformId, code, authColumnVO.isFormEditable());
        }

    }

    @Transactional
    public void switchFormShow(String cgformId, String code, boolean flag) {
        this.updateSwitch(cgformId, code, 5, 5, flag);
    }

    @Transactional
    public void switchFormEditable(String cgformId, String code, boolean flag) {
        this.updateSwitch(cgformId, code, 3, 5, flag);
    }

    @Transactional
    public void switchListShow(String cgformId, String code, boolean flag) {
        this.updateSwitch(cgformId, code, 5, 3, flag);
    }

    public List<AuthPageVO> queryAuthByFormId(String cgformId, int type) {
        return type == 1 ? baseMapper.queryAuthColumnByFormId(cgformId) : baseMapper.queryAuthButtonByFormId(cgformId);
    }

//    public List<String> queryRoleNoAuthCode(String cgformId, Integer control, Integer page) {
//        LoginUser var4 = (LoginUser)SecurityUtils.getSubject().getPrincipal();
//        String var5 = var4.getId();
//        return ((OnlAuthPageMapper)this.baseMapper).queryRoleNoAuthCode(var5, cgformId, control, page, (Integer)null);
//    }
//
//    public List<AuthPageVO> queryRoleAuthByFormId(String roleId, String cgformId, int type) {
//        return ((OnlAuthPageMapper)this.baseMapper).queryRoleAuthByFormId(roleId, cgformId, type);
//    }
//
//    public List<AuthPageVO> queryRoleDataAuth(String roleId, String cgformId) {
//        return ((OnlAuthPageMapper)this.baseMapper).queryRoleDataAuth(roleId, cgformId);
//    }
//
//
//
//    public List<String> queryFormDisabledCode(String cgformId) {
//        return this.queryRoleNoAuthCode(cgformId, 3, 5);
//    }
//
//    public List<String> queryHideCode(String userId, String cgformId, boolean isList) {
//        return ((OnlAuthPageMapper)this.baseMapper).queryRoleNoAuthCode(userId, cgformId, 5, isList ? 3 : 5, (Integer)null);
//    }
//
//    public List<String> queryListHideColumn(String userId, String cgformId) {
//        return ((OnlAuthPageMapper)this.baseMapper).queryRoleNoAuthCode(userId, cgformId, 5, 3, 1);
//    }
//
//    public List<String> queryFormHideColumn(String userId, String cgformId) {
//        return ((OnlAuthPageMapper)this.baseMapper).queryRoleNoAuthCode(userId, cgformId, 5, 5, 1);
//    }
//
//    public List<String> queryFormHideButton(String userId, String cgformId) {
//        return ((OnlAuthPageMapper)this.baseMapper).queryRoleNoAuthCode(userId, cgformId, 5, 5, 2);
//    }
//
//    public List<String> queryHideCode(String cgformId, boolean isList) {
//        LoginUser var3 = (LoginUser)SecurityUtils.getSubject().getPrincipal();
//        String var4 = var3.getId();
//        return ((OnlAuthPageMapper)this.baseMapper).queryRoleNoAuthCode(var4, cgformId, 5, isList ? 3 : 5, (Integer)null);
//    }
//
    private void updateSwitch(String cgformId, String code, int control, int page, boolean flag) {
        LambdaQueryWrapper lambdaQueryWrapper = (new LambdaQueryWrapper<OnlAuthPage>()).eq(OnlAuthPage::getCgformId, cgformId).eq(OnlAuthPage::getCode, code).eq(OnlAuthPage::getControl, control).eq(OnlAuthPage::getPage, page).eq(OnlAuthPage::getType, 1);
        OnlAuthPage onlAuthPage = baseMapper.selectOne(lambdaQueryWrapper);
        if (flag) {
            if (onlAuthPage == null) {
                OnlAuthPage authPage = new OnlAuthPage();
                authPage.setCgformId(cgformId);
                authPage.setCode(code);
                authPage.setControl(control);
                authPage.setPage(page);
                authPage.setType(1);
                authPage.setStatus(1);
                baseMapper.insert(authPage);
            } else if (onlAuthPage.getStatus() == 0) {
                onlAuthPage.setStatus(1);
                baseMapper.updateById(onlAuthPage);
            }
        } else if (!flag && onlAuthPage != null) {
            String id = onlAuthPage.getId();
            baseMapper.deleteById(id);
            this.onlAuthRelationMapper.delete((new LambdaQueryWrapper<OnlAuthRelation>()).eq(OnlAuthRelation::getAuthId, id));
        }
    }
}
