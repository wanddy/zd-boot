package smartcode.auth.service;

import com.baomidou.mybatisplus.extension.service.IService;
import smartcode.auth.entity.OnlAuthPage;
import smartcode.auth.vo.AuthColumnVO;
import smartcode.auth.vo.AuthPageVO;

import java.util.List;

public interface OnlAuthPageService extends IService<OnlAuthPage> {

    void enableAuthColumn(AuthColumnVO authColumnVO);

    void disableAuthColumn(AuthColumnVO authColumnVO);

    void switchAuthColumn(AuthColumnVO authColumnVO);

    List<AuthPageVO> queryAuthByFormId(String cgformId, int type);
//
//    void switchFormShow(String var1, String var2, boolean var3);
//
//    void switchFormEditable(String var1, String var2, boolean var3);
//
//    void switchListShow(String var1, String var2, boolean var3);
//
//    List<AuthPageVO> queryRoleAuthByFormId(String var1, String var2, int var3);
//
//    List<AuthPageVO> queryRoleDataAuth(String var1, String var2);

//    List<String> queryRoleNoAuthCode(String var1, Integer var2, Integer var3);
//
//    List<String> queryFormDisabledCode(String var1);
//
//    List<String> queryHideCode(String var1, String var2, boolean var3);
//
//    List<String> queryListHideColumn(String var1, String var2);
//
//    List<String> queryFormHideColumn(String var1, String var2);
//
//    List<String> queryFormHideButton(String var1, String var2);
//
//    List<String> queryHideCode(String var1, boolean var2);
}
