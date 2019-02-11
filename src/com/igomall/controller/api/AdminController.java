
package com.igomall.controller.api;

import java.util.HashSet;

import javax.inject.Inject;

import org.apache.commons.lang.BooleanUtils;
import org.apache.commons.lang.StringUtils;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.fasterxml.jackson.annotation.JsonView;
import com.igomall.Message;
import com.igomall.Page;
import com.igomall.Pageable;
import com.igomall.entity.Admin;
import com.igomall.entity.BaseEntity;
import com.igomall.service.AdminService;
import com.igomall.service.RoleService;
import com.igomall.service.UserService;

/**
 * Controller - 管理员
 * 
 * @author IGOMALL  Team
 * @version 1.0
 */
@RestController("apiAdminController")
@RequestMapping("/api/admin")
public class AdminController extends BaseController {

	@Inject
	private AdminService adminService;
	@Inject
	private UserService userService;
	@Inject
	private RoleService roleService;

	/**
	 * 检查用户名是否存在
	 */
	@GetMapping("/check_username")
	public @ResponseBody boolean checkUsername(String username) {
		return StringUtils.isNotEmpty(username) && !adminService.usernameExists(username);
	}

	/**
	 * 检查E-mail是否唯一
	 */
	@GetMapping("/check_email")
	public @ResponseBody boolean checkEmail(Long id, String email) {
		return StringUtils.isNotEmpty(email) && adminService.emailUnique(id, email);
	}

	/**
	 * 保存
	 */
	@PostMapping("/save")
	public Message save(Admin admin,  Boolean unlock,Long[] roleIds) {
		admin.setRoles(new HashSet<>(roleService.findList(roleIds)));
		if (!isValid(admin, BaseEntity.Save.class)) {
			return Message.error("参数错误");
		}
		
		if(admin.isNew()) {
			if (adminService.usernameExists(admin.getUsername())) {
				return Message.error("参数用户名已存在");
			}
			if (adminService.emailExists(admin.getEmail())) {
				return Message.error("邮箱已被占用");
			}
			admin.setIsLocked(false);
			admin.setLockDate(null);
			admin.setLastLoginIp(null);
			admin.setLastLoginDate(null);
			admin.setPaymentTransactions(null);
			adminService.save(admin);
		}else {
			if (!adminService.emailUnique(admin.getId(), admin.getEmail())) {
				return Message.error("邮箱已被占用");
			}
			Admin pAdmin = adminService.find(admin.getId());
			if (pAdmin == null) {
				return Message.error("账号不存在");
			}
			if (BooleanUtils.isTrue(pAdmin.getIsLocked()) && BooleanUtils.isTrue(unlock)) {
				userService.unlock(admin);
				adminService.update(admin, "username", "encodedPassword", "lastLoginIp", "lastLoginDate");
			} else {
				adminService.update(admin, "username", "encodedPassword", "isLocked", "lockDate", "lastLoginIp", "lastLoginDate");
			}
		}
		
		return SUCCESS_MESSAGE;
	}

	/**
	 * 编辑
	 */
	@GetMapping("/edit")
	public Admin edit(Long id, ModelMap model) {
		return adminService.find(id);
	}

	/**
	 * 列表
	 */
	@GetMapping("/list")
	@JsonView(Admin.ListView.class)
	public Page<Admin> list(Pageable pageable, ModelMap model) {
		return adminService.findPage(pageable);
	}

	/**
	 * 删除
	 */
	@PostMapping("/delete")
	public @ResponseBody Message delete(Long[] ids) {
		if (ids.length >= adminService.count()) {
			return Message.error("admin.common.deleteAllNotAllowed");
		}
		adminService.delete(ids);
		return SUCCESS_MESSAGE;
	}

}