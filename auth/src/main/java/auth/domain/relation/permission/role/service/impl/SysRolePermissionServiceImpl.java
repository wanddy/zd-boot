package auth.domain.relation.permission.role.service.impl;

import java.util.*;

import lombok.extern.slf4j.Slf4j;
import auth.domain.relation.permission.role.service.ISysRolePermissionService;
import commons.util.IPUtils;
import commons.util.SpringContextUtils;
import commons.util.oConvertUtils;
import auth.entity.RolePermission;
import auth.domain.relation.permission.role.mapper.SysRolePermissionMapper;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;

import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;

/**
 * 角色权限 服务实现类
 */
@Slf4j
@Service
public class SysRolePermissionServiceImpl extends ServiceImpl<SysRolePermissionMapper, RolePermission> implements ISysRolePermissionService {

	@Override
	public void saveRolePermission(String roleId, String permissionIds) {
		String ip = "";
		try {
			//获取request
			HttpServletRequest request = SpringContextUtils.getHttpServletRequest();
			//获取IP地址
			ip = IPUtils.getIpAddr(request);
		} catch (Exception e) {
			ip = "127.0.0.1";
		}
		LambdaQueryWrapper<RolePermission> query = new QueryWrapper<RolePermission>().lambda().eq(RolePermission::getRoleId, roleId);
		this.remove(query);
		List<RolePermission> list = new ArrayList<RolePermission>();
        String[] arr = permissionIds.split(",");
		for (String p : arr) {
			if(oConvertUtils.isNotEmpty(p)) {
				RolePermission rolepms = new RolePermission(roleId, p);
				rolepms.setOperateDate(new Date());
				rolepms.setOperateIp(ip);
				list.add(rolepms);
			}
		}
		this.saveBatch(list);
	}

	@Override
	public void saveRolePermission(String roleId, String permissionIds, String lastPermissionIds) {
		String ip = "";
		try {
			//获取request
			HttpServletRequest request = SpringContextUtils.getHttpServletRequest();
			//获取IP地址
			ip = IPUtils.getIpAddr(request);
		} catch (Exception e) {
			ip = "127.0.0.1";
		}
		List<String> add = getDiff(lastPermissionIds,permissionIds);
		if(add!=null && add.size()>0) {
			List<RolePermission> list = new ArrayList<RolePermission>();
			for (String p : add) {
				if(oConvertUtils.isNotEmpty(p)) {
					RolePermission rolepms = new RolePermission(roleId, p);
					rolepms.setOperateDate(new Date());
					rolepms.setOperateIp(ip);
					list.add(rolepms);
				}
			}
			this.saveBatch(list);
		}

		List<String> delete = getDiff(permissionIds,lastPermissionIds);
		if(delete!=null && delete.size()>0) {
			for (String permissionId : delete) {
				this.remove(new QueryWrapper<RolePermission>().lambda().eq(RolePermission::getRoleId, roleId).eq(RolePermission::getPermissionId, permissionId));
			}
		}
	}

	/**
	 * 从diff中找出main中没有的元素
	 * @param main
	 * @param diff
	 * @return
	 */
	private List<String> getDiff(String main,String diff){
		if(oConvertUtils.isEmpty(diff)) {
			return null;
		}
		if(oConvertUtils.isEmpty(main)) {
			return Arrays.asList(diff.split(","));
		}

		String[] mainArr = main.split(",");
		String[] diffArr = diff.split(",");
		Map<String, Integer> map = new HashMap<>();
		for (String string : mainArr) {
			map.put(string, 1);
		}
		List<String> res = new ArrayList<String>();
		for (String key : diffArr) {
			if(oConvertUtils.isNotEmpty(key) && !map.containsKey(key)) {
				res.add(key);
			}
		}
		return res;
	}

}
