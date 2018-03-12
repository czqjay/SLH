package com.sunit.navimenu.service.impl;

import java.util.List;

import org.apache.log4j.Logger;
import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Restrictions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.sunit.global.base.dao.BaseDAO;
import com.sunit.global.base.service.AbstractBaseServiceImpl;
import com.sunit.navimenu.po.Navimenu;
import com.sunit.navimenu.service.NavimenuManager;
import com.sunit.relations.po.RelationTree;
import com.sunit.relations.service.RelationsManager;

@Service
public class NavimenuManagerImpl extends AbstractBaseServiceImpl<Navimenu> implements NavimenuManager {

	static Logger logger = Logger.getLogger(NavimenuManagerImpl.class);

	@Autowired
	BaseDAO dao;

	@Autowired
	RelationsManager relationsManager;

	protected BaseDAO getDao() {
		return dao;
	}

	public void save(Navimenu entity) {
		super.save(entity);
		RelationTree parentRelation;
		if (entity.getParent() != null) {
			parentRelation = relationsManager.getRelationByClassId(entity.getParent().getId(), relationName); 
			relationsManager.addRelation(entity.getId(), parentRelation.getId(), relationName, "");
		} else {
			relationsManager.addRelation(entity.getId(), null, relationName, "");
		}
	}
	
	public void delete(final String[] ids) {
		for (int i = 0; i < ids.length; i++) {
			String id =ids[i];
			super.delete(this.get(id).getChilds());
		} 
		relationsManager.delete(ids,relationName);
		super.delete(ids);
	}

	
}
