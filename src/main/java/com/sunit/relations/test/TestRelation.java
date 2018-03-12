package com.sunit.relations.test;

import java.util.List;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.AbstractJUnit4SpringContextTests;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.sunit.global.base.exception.SLHException;
import com.sunit.global.util.SpringContextUtils;
import com.sunit.relations.po.RelationTree;
import com.sunit.relations.service.RelationsManager;
import com.sunit.sysmanager.po.Role;
import com.sunit.sysmanager.po.SysResource;
import com.sunit.sysmanager.service.RoleManager;
import com.sunit.sysmanager.service.SysResourceManager;



@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = "classpath:springTest.xml") 
@Component
public class TestRelation extends AbstractJUnit4SpringContextTests  {
	
	@Autowired
	RelationsManager relationsManager ;
	
	 @Autowired
	 SysResourceManager sysresourceManager; 
	
	 @Autowired
	 RoleManager roleManager; 
	
	 
//	@Test
	public void addRelation() {    
		RelationTree r = new RelationTree(); 
//		relationsManager.addRelation("A2", "402880f75f6b7aa2015f6b7ab0d20001",this.getClass().getName(),"remark");
	}
	
	
	@Test
	public void test() throws SLHException {
		Role r  =roleManager.get("402880f75fc2fdc4015fc301ae270002"); // 供应商的角色id
		System.out.println(r.getClass());
	} 
	
	public void addSysre() throws SLHException {
		
		SysResourceManager	sm = SpringContextUtils.getSpringContext().getBean(SysResourceManager.class);
		SysResource rs =   sm.get("402880f75f280231015f281b1fca0008");
		recursionResource(rs); 
	} 
	
	 
	public  RelationTree getRelation(String poId,String className) throws SLHException {
		
		RelationTree r = new   RelationTree();
		r.setClassId(poId);
		r.setClassName(className); 
		return relationsManager.findSingleByExample(r);
	} 
	
	public void recursionResource(SysResource rs) throws SLHException{
		List<SysResource> list =sysresourceManager.find(" from SysResource where parent=? ", rs.getId()); 
		RelationTree r =getRelation(rs.getParent(),"SysResourceManager")  ;
		String parentId = r==null?"":r.getId();
		if(list==null||list.size()==0) {
			relationsManager.addRelation(rs.getId(), parentId, "SysResourceManager",rs.getModuleCaption());
		}else{
			relationsManager.addRelation(rs.getId(), parentId, "SysResourceManager",rs.getModuleCaption());
			for (SysResource o : list) {
					recursionResource(o); 
				}
		}
	}
	
}
