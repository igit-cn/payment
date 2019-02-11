
package com.igomall.controller.api;

import java.util.List;

import javax.inject.Inject;

import org.apache.commons.collections.CollectionUtils;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.fasterxml.jackson.annotation.JsonView;
import com.igomall.Message;
import com.igomall.Page;
import com.igomall.Pageable;
import com.igomall.entity.Role;
import com.igomall.service.RoleService;

/**
 * Controller - 角色
 * 
 * @author IGOMALL  Team
 * @version 1.0
 */
@RestController("apiRoleController")
@RequestMapping("/api/role")
public class RoleController extends BaseController {

	@Inject
	private RoleService roleService;

	/**
	 * 保存
	 */
	@PostMapping("/save")
	public Message save(Role role) {
		if (!isValid(role)) {
			return Message.error("参数错误");
		}
		
		if(role.isNew()) {
			role.setIsSystem(false);
			role.setAdmins(null);
			roleService.save(role);
		}else {
			Role pRole = roleService.find(role.getId());
			if (pRole == null || pRole.getIsSystem()) {
				return Message.error("参数错误");
			}
			roleService.update(role, "isSystem", "admins");
		}
		
		return SUCCESS_MESSAGE;
	}

	/**
	 * 编辑
	 */
	@PostMapping("/edit")
	public Role edit(Long id) {
		return roleService.find(id);
	}

	/**
	 * 列表
	 */
	@PostMapping("/page")
	public Page<Role> page(Pageable pageable) {
		return roleService.findPage(pageable);
	}
	
	/**
	 * 列表
	 */
	@PostMapping("/list")
	@JsonView(Role.ListView.class)
	public List<Role> list() {
		return roleService.findAll();
	}

	/**
	 * 删除
	 */
	@PostMapping("/delete")
	public @ResponseBody Message delete(Long[] ids) {
		if (ids != null) {
			for (Long id : ids) {
				Role role = roleService.find(id);
				if (role != null && (role.getIsSystem() || CollectionUtils.isNotEmpty(role.getAdmins()))) {
					return Message.error("admin.role.deleteExistNotAllowed", role.getName());
				}
			}
			roleService.delete(ids);
		}
		return SUCCESS_MESSAGE;
	}

}