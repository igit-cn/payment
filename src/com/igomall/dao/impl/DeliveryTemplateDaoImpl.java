
package com.igomall.dao.impl;

import java.util.List;

import javax.persistence.NoResultException;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import org.springframework.stereotype.Repository;
import org.springframework.util.Assert;

import com.igomall.Page;
import com.igomall.Pageable;
import com.igomall.dao.DeliveryTemplateDao;
import com.igomall.entity.DeliveryTemplate;
import com.igomall.entity.Store;

/**
 * Dao - 快递单模板
 * 
 * @author IGOMALL  Team
 * @version 1.0
 */
@Repository
public class DeliveryTemplateDaoImpl extends BaseDaoImpl<DeliveryTemplate, Long> implements DeliveryTemplateDao {

	public DeliveryTemplate findDefault(Store store) {
		Assert.notNull(store);

		try {
			String jpql = "select deliveryTemplate from DeliveryTemplate deliveryTemplate where deliveryTemplate.isDefault = true and lower(deliveryTemplate.store) = lower(:store)";
			return entityManager.createQuery(jpql, DeliveryTemplate.class).setParameter("store", store).getSingleResult();
		} catch (NoResultException e) {
			return null;
		}
	}

	public List<DeliveryTemplate> findList(Store store) {
		CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
		CriteriaQuery<DeliveryTemplate> criteriaQuery = criteriaBuilder.createQuery(DeliveryTemplate.class);
		Root<DeliveryTemplate> root = criteriaQuery.from(DeliveryTemplate.class);
		criteriaQuery.select(root);
		Predicate restrictions = criteriaBuilder.conjunction();
		if (store != null) {
			restrictions = criteriaBuilder.and(restrictions, criteriaBuilder.equal(root.get("store"), store));
		}
		criteriaQuery.where(restrictions);
		return super.findList(criteriaQuery);
	}

	public Page<DeliveryTemplate> findPage(Store store, Pageable pageable) {
		CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
		CriteriaQuery<DeliveryTemplate> criteriaQuery = criteriaBuilder.createQuery(DeliveryTemplate.class);
		Root<DeliveryTemplate> root = criteriaQuery.from(DeliveryTemplate.class);
		criteriaQuery.select(root);
		Predicate restrictions = criteriaBuilder.conjunction();
		if (store != null) {
			restrictions = criteriaBuilder.and(restrictions, criteriaBuilder.equal(root.get("store"), store));
		}
		criteriaQuery.where(restrictions);
		return super.findPage(criteriaQuery, pageable);
	}

	public void clearDefault(Store store) {
		Assert.notNull(store);

		String jpql = "update DeliveryTemplate deliveryTemplate set deliveryTemplate.isDefault = false where deliveryTemplate.isDefault = true and lower(deliveryTemplate.store) = lower(:store)";
		entityManager.createQuery(jpql).setParameter("store", store).executeUpdate();
	}

	public void clearDefault(DeliveryTemplate exclude) {
		Assert.notNull(exclude);

		String jpql = "update DeliveryTemplate deliveryTemplate set deliveryTemplate.isDefault = false where deliveryTemplate.isDefault = true and deliveryTemplate != :exclude and lower(deliveryTemplate.store) = lower(:store)";
		entityManager.createQuery(jpql).setParameter("exclude", exclude).setParameter("store", exclude.getStore()).executeUpdate();
	}

}