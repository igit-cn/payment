
package com.igomall.entity;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.OrderBy;
import javax.persistence.PreRemove;
import javax.persistence.Transient;

import org.apache.commons.lang.StringUtils;
import org.hibernate.validator.constraints.Length;
import org.hibernate.validator.constraints.NotEmpty;

import com.fasterxml.jackson.annotation.JsonView;

/**
 * Entity - 地区
 * 
 * @author IGOMALL  Team
 * @version 1.0
 */
@Entity
public class Department extends OrderedEntity<Long> {

	public interface EditView extends BaseView {

	}

	public interface listTreeView extends BaseView {

	}

	public interface ListView extends BaseView{

	}

	private static final long serialVersionUID = -2158109459123036967L;

	/**
	 * 树路径分隔符
	 */
	public static final String TREE_PATH_SEPARATOR = ",";

	/**
	 * 名称
	 */
	@NotEmpty
	@Length(max = 200)
	@Column(nullable = false)
	@JsonView({ListView.class,listTreeView.class,EditView.class})
	private String name;

	/**
	 * 全称
	 */
	@Column(nullable = false, length = 4000)
	@JsonView({ListView.class})
	private String fullName;

	/**
	 * 树路径
	 */
	@Column(nullable = false)
	private String treePath;

	/**
	 * 层级
	 */
	@Column(nullable = false)
	@JsonView({ListView.class})
	private Integer grade;

	/**
	 * 上级部门
	 */
	@ManyToOne(fetch = FetchType.LAZY)
	private Department parent;

	/**
	 * 下级部门
	 */
	@OneToMany(mappedBy = "parent", fetch = FetchType.LAZY, cascade = CascadeType.REMOVE)
	@OrderBy("order asc")
	@JsonView({listTreeView.class})
	private Set<Department> children = new HashSet<>();

	/**
	 * 管理员
	 */
	@OneToMany(mappedBy = "department", fetch = FetchType.LAZY)
	private Set<Admin> admins = new HashSet<>();

	/**
	 * 获取名称
	 * 
	 * @return 名称
	 */
	public String getName() {
		return name;
	}

	/**
	 * 设置名称
	 * 
	 * @param name
	 *            名称
	 */
	public void setName(String name) {
		this.name = name;
	}

	/**
	 * 获取全称
	 * 
	 * @return 全称
	 */
	public String getFullName() {
		return fullName;
	}

	/**
	 * 设置全称
	 * 
	 * @param fullName
	 *            全称
	 */
	public void setFullName(String fullName) {
		this.fullName = fullName;
	}

	/**
	 * 获取树路径
	 * 
	 * @return 树路径
	 */
	public String getTreePath() {
		return treePath;
	}

	/**
	 * 设置树路径
	 * 
	 * @param treePath
	 *            树路径
	 */
	public void setTreePath(String treePath) {
		this.treePath = treePath;
	}

	/**
	 * 获取层级
	 * 
	 * @return 层级
	 */
	public Integer getGrade() {
		return grade;
	}

	/**
	 * 设置层级
	 * 
	 * @param grade
	 *            层级
	 */
	public void setGrade(Integer grade) {
		this.grade = grade;
	}

	/**
	 * 获取上级地区
	 * 
	 * @return 上级地区
	 */
	public Department getParent() {
		return parent;
	}

	/**
	 * 设置上级地区
	 * 
	 * @param parent
	 *            上级地区
	 */
	public void setParent(Department parent) {
		this.parent = parent;
	}

	/**
	 * 获取下级地区
	 * 
	 * @return 下级地区
	 */
	public Set<Department> getChildren() {
		return children;
	}

	/**
	 * 设置下级地区
	 * 
	 * @param children
	 *            下级地区
	 */
	public void setChildren(Set<Department> children) {
		this.children = children;
	}

	/**
	 * 获取管理员
	 * 
	 * @return 管理员
	 */
	public Set<Admin> getAdmins() {
		return admins;
	}

	/**
	 * 设置管理员
	 * 
	 * @param admins
	 *            管理员
	 */
	public void setAdmins(Set<Admin> admins) {
		this.admins = admins;
	}

	/**
	 * 获取所有上级部门ID
	 * 
	 * @return 所有上级部门ID
	 */
	@Transient
	public Long[] getParentIds() {
		String[] parentIds = StringUtils.split(getTreePath(), TREE_PATH_SEPARATOR);
		Long[] result = new Long[parentIds.length];
		for (int i = 0; i < parentIds.length; i++) {
			result[i] = Long.valueOf(parentIds[i]);
		}
		return result;
	}

	/**
	 * 获取所有上级部门
	 * 
	 * @return 所有上级部门
	 */
	@Transient
	public List<Department> getParents() {
		List<Department> parents = new ArrayList<>();
		recursiveParents(parents, this);
		return parents;
	}

	/**
	 * 递归上级部门
	 * 
	 * @param parents
	 *            上级部门
	 * @param area
	 *            部门
	 */
	private void recursiveParents(List<Department> parents, Department area) {
		if (area == null) {
			return;
		}
		Department parent = area.getParent();
		if (parent != null) {
			parents.add(0, parent);
			recursiveParents(parents, parent);
		}
	}

	/**
	 * 删除前处理
	 */
	@PreRemove
	public void preRemove() {
		Set<Admin> admins = getAdmins();
		if (admins != null) {
			for (Admin admin : admins) {
				admin.setDepartment(null);
			}
		}
	}
	
	@Transient
	@JsonView({EditView.class})
	public Long getParentId() {
		if(getParent()!=null) {
			return getParent().getId();
		}else {
			return null;
		}
	}

}