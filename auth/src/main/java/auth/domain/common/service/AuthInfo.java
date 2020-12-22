package auth.domain.common.service;

import auth.discard.model.SysDepartTreeModel;
import auth.domain.common.dto.MdmDto;
import auth.domain.common.dto.UserDepartDto;

import java.util.List;

public interface AuthInfo {

    UserDepartDto getUserInfo();

    List<MdmDto> getUserDepartSub(String userId);

    List<SysDepartTreeModel> getDeparts();

    List<MdmDto> getUsers();

    List<UserDepartDto> getUserById(String id);

    List<UserDepartDto> getUserByName(String name);

}
