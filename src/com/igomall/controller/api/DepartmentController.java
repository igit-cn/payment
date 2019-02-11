
package com.igomall.controller.api;

import java.util.List;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.fasterxml.jackson.annotation.JsonView;
import com.igomall.Message;
import com.igomall.Pageable;
import com.igomall.entity.Admin;
import com.igomall.entity.Department;
import com.igomall.service.DepartmentService;

/**
 * Controller - 管理员
 * 
 * @author IGOMALL  Team
 * @version 1.0
 */
@RestController("apiDepartmentController")
@RequestMapping("/api/department")
public class DepartmentController extends BaseController {

	@Autowired
	private DepartmentService departmentService;


	/**
	 * 列表
	 */
	@PostMapping("/list")
	@JsonView(Department.ListView.class)
	public List<Department> list(Pageable pageable) {
		return departmentService.findTree();
	}

	/**
	 * 列表
	 */
	@PostMapping("/listTree")
	@JsonView(Department.listTreeView.class)
	public List<Department> listTree() {
		return departmentService.findRoots();
	}
	
	/**
	 * 保存
	 */
	@PostMapping("/save")
	public Message save(Department department, Long parentId, RedirectAttributes redirectAttributes) {
		department.setParent(departmentService.find(parentId));
		if (!isValid(department)) {
			return Message.error("参数错误");
		}
		
		if(department.isNew()) {
			department.setFullName(null);
			department.setTreePath(null);
			department.setGrade(null);
			department.setChildren(null);
			department.setAdmins(null);
			departmentService.save(department);
		}else {
			if (department.getParent() != null) {
				Department parent = department.getParent();
				if (parent.equals(department)) {
					return Message.error("上级部门不能是自己");
				}
				List<Department> children = departmentService.findChildren(parent, true, null);
				if (children != null && children.contains(parent)) {
					return Message.error("上级部门选择错误");
				}
			}
			departmentService.update(department,"adminis","children");
		}
		return SUCCESS_MESSAGE;
	}
	
	/**
	 * 列表
	 */
	@PostMapping("/edit")
	@JsonView(Department.EditView.class)
	public Department edit(Long id) {
		return departmentService.find(id);
	}
	
	/**
	 * 删除
	 */
	@PostMapping("/delete")
	public @ResponseBody Message delete(Long id) {
		Department department = departmentService.find(id);
		if (department == null) {
			return ERROR_MESSAGE;
		}
		Set<Department> children = department.getChildren();
		if (children != null && !children.isEmpty()) {
			return Message.error("存在下级部门，删除失败");
		}
		Set<Admin> admins = department.getAdmins();
		if (admins != null && !admins.isEmpty()) {
			return Message.error("该部门下存在用户，请先移除用户再删除！");
		}
		departmentService.delete(id);
		return SUCCESS_MESSAGE;
	}
}