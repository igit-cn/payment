
package com.igomall.controller.admin;

import java.util.ArrayList;

import javax.inject.Inject;

import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.igomall.Message;
import com.igomall.entity.Department;
import com.igomall.service.DepartmentService;

/**
 * Controller - 部门
 * 
 * @author IGOMALL  Team
 * @version 1.0
 */
@Controller("adminDepartmentController")
@RequestMapping("/admin/department")
public class DepartmentController2 extends BaseController {

	@Inject
	private DepartmentService departmentService;

	/**
	 * 添加
	 */
	@GetMapping("/add")
	public String add(Long parentId, ModelMap model) {
		model.addAttribute("parent", departmentService.find(parentId));
		return "admin/department/add";
	}

	/**
	 * 保存
	 */
	@PostMapping("/save")
	public String save(Department department, Long parentId, RedirectAttributes redirectAttributes) {
		department.setParent(departmentService.find(parentId));
		if (!isValid(department)) {
			return ERROR_VIEW;
		}
		department.setFullName(null);
		department.setTreePath(null);
		department.setGrade(null);
		department.setChildren(null);
		department.setAdmins(null);
		departmentService.save(department);
		addFlashMessage(redirectAttributes, SUCCESS_MESSAGE);
		return "redirect:list";
	}

	/**
	 * 编辑
	 */
	@GetMapping("/edit")
	public String edit(Long id, ModelMap model) {
		model.addAttribute("department", departmentService.find(id));
		return "admin/department/edit";
	}

	/**
	 * 更新
	 */
	@PostMapping("/update")
	public String update(Department department, RedirectAttributes redirectAttributes) {
		if (!isValid(department)) {
			return ERROR_VIEW;
		}
		departmentService.update(department, "fullName", "treePath", "grade", "parent", "children", "admins");
		addFlashMessage(redirectAttributes, SUCCESS_MESSAGE);
		return "redirect:list";
	}

	/**
	 * 列表
	 */
	@GetMapping("/list")
	public String list(Long parentId, ModelMap model) {
		Department parent = departmentService.find(parentId);
		if (parent != null) {
			model.addAttribute("parent", parent);
			model.addAttribute("departments", new ArrayList<>(parent.getChildren()));
		} else {
			model.addAttribute("departments", departmentService.findRoots());
		}
		return "admin/department/list";
	}

	/**
	 * 删除
	 */
	@PostMapping("/delete")
	public @ResponseBody Message delete(Long id) {
		departmentService.delete(id);
		return SUCCESS_MESSAGE;
	}

}