package com.sunit.relations.service.impl;
 
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.httpclient.NameValuePair;
import org.apache.log4j.Logger;
import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Restrictions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.sunit.global.base.AbstractID;
import com.sunit.global.base.dao.BaseDAO;
import com.sunit.global.base.exception.SLHException;
import com.sunit.global.base.service.AbstractBaseServiceImpl;
import com.sunit.global.util.Paging;
import com.sunit.global.util.SunitStringUtil;
import com.sunit.relations.po.RelationTree;
import com.sunit.relations.service.RelationsManager;


@Service
public class RelationsManagerImpl extends AbstractBaseServiceImpl<RelationTree> implements RelationsManager {
 
	static Logger logger = Logger.getLogger(RelationsManagerImpl.class);
	
	public  static  String CLIENTCODE="XWT";  // 客户端代码 ,需要在服务器注册
	
	public  static  String SHAREURL="http://localhost/shareconfig/getAllShares.action";   // share服务的地址
	
	@Autowired 
	BaseDAO dao; 
	
	   
	protected BaseDAO getDao() { 
		return dao; 
	}

	
	public  RelationTree getRelationByClassId(String classId,String className)  {
		RelationTree r =new RelationTree();
		r.setClassId(classId);
		r.setClassName(className);
		try {
			return this.findSingleByExample(r);
		} catch (SLHException e) {
			e.printStackTrace();
			throw new RuntimeException(e.getMessage()); 
		
		} 
	}
	

	public void delete(final String[] ids,String poName) {
		for (int i = 0; i < ids.length; i++) {
			String id= ids[i];
			RelationTree rt =   this.getRelationByClassId(id, poName);
			this.delete(this.getChilds(id, poName));
			this.delete(new String[] {rt.getId()});
		}
	}
	 
	
	
	public boolean addRelation(String classId ,String parentId ,String className,String remark) {
		
		
		RelationTree r =new RelationTree(); 
		r.setClassId(classId);
		r.setClassName(className);
		List<RelationTree> l = this.findByExample(r);
//		if(!l.isEmpty()) {
//			RelationTree rt  =l.get(0);
//			RelationTree parent = this.get(parentId);
//			
//			//计算自身长度
//			int  nodeLength =rt.getRelationsRight() - rt.getRelationsLeft();
//			
//			//在 tree  去掉自身的长度
//			String hql = "update RelationTree  set relationsRight=relationsRight-?  where  relationsRight>? and className=?";  
//			this.executeUpdate(hql, nodeLength,className);
//			
//			hql = "update RelationTree  set relationsRight=relationsRight+?  where  relationsRight>? and className=?";
//			this.executeUpdate(hql, nodeLength,className);
//			
//			hql = "update RelationTree  set relationsRight=relationsRight+2  where  classId = ? and className=?";
//			Integer left =parent.getRelationsRight(); 
//			Integer right = left+1;   
//			r.setRelationsLeft(left);
//			r.setRelationsRight(right); 
//			r.setRemark(remark);
//			r.setRelationsAncestorId(parent.getRelationsAncestorId());
//			r.setRelationsLevel(parent.getRelationsLevel()+1);
//			r.setRelationsParentId(parentId);
//			
//			this.executeUpdate(hql, rt.getClassId(),className);
//			
//			
//			
//		}
		
		
		if(SunitStringUtil.isBlankOrNull(parentId)|| this.get(parentId)==null){ 
			r.setRelationsLeft(1); 
			r.setRelationsRight(2); 
			r.setRelationsLevel(1);
			r.setRemark(remark);
			this.save(r); 
			r.setRelationsAncestorId(r.getId());
		}else { 
			String hql =""; 
			hql = "update RelationTree  set relationsRight=relationsRight+2  where  relationsRight>? and className=?";
			RelationTree parent = this.get(parentId);
			this.executeUpdate(hql, parent.getRelationsRight(),className);
			Integer left =parent.getRelationsRight(); 
			Integer right = left+1;   
			r.setRelationsLeft(left);
			r.setRelationsRight(right); 
			r.setRemark(remark);
			r.setRelationsAncestorId(parent.getRelationsAncestorId());
			r.setRelationsLevel(parent.getRelationsLevel()+1);
			r.setRelationsParentId(parentId);
			parent.setRelationsRight(parent.getRelationsRight()+2);
			this.save(parent);  
		}
		this.save(r); 	
		 
		 return true;
	}
	
	
	
	
	

	


	

	
	
	public List<RelationTree> getDownLevels(String classId) throws Exception {
		RelationTree vo =new RelationTree();
		vo.setRelationsUserId(classId);
		RelationTree self = this.findSingleByExample(vo); 
		if(null==self) return null;
		List downList =  this.find("from Relations s where s.relationsLeft>? and s.relationsRight<? and s.relationsAncestorId=? ",self.getRelationsLeft(),self.getRelationsRight(),self.getRelationsAncestorId() );
		return downList; 
	}

	public List<RelationTree> getFisrtDownLevels(String classId) throws Exception {
		RelationTree vo =new RelationTree();
		vo.setRelationsUserId(classId);
		RelationTree self = this.findSingleByExample(vo); 
		
		if(null==self) return null;
		List downList =  this.find("from Relations s where s.relationsLeft>? and s.relationsRight<? and s.relationsAncestorId=? and s.relationsLevel =?  ",self.getRelationsLeft(),self.getRelationsRight(),self.getRelationsAncestorId(),self.getRelationsLevel()+1 );
		return downList; 
	}

	
	
	
	
	public Paging getDownLevelsForPaging(String classId, Integer rows,Integer page,String shareType,Integer userLevel) throws Exception {
		Map<String,Object> map = new HashMap<String,Object>();
		RelationTree vo =new RelationTree();
		vo.setRelationsUserId(classId);
		RelationTree self = this.findSingleByExample(vo); 
		if(null==self) return null;
//		List downList =  this.find("from Relations s where s.relationsLeft>? and s.relationsRight<?",self.getRelationsLeft(),self.getRelationsRight() );
		RelationsSearchPara para = new RelationsSearchPara();
		para.setSRelationsLeft(self.getRelationsLeft());
		para.setSRelationsRight(self.getRelationsRight());
		para.setSRelationsAncestorId(self.getRelationsAncestorId());
		StringBuffer hqlBuf = new StringBuffer(); 
		//如果页面传的shareType=1 则为直接用户，则只需把直接下线显示
		if( "1".equals(shareType) ){   
			para.setSRelationsLevel(userLevel+1);
			hqlBuf.append("from Relations s where s.relationsLeft >:SRelationsLeft and s.relationsRight<:SRelationsRight and s.relationsAncestorId=:SRelationsAncestorId and s.relationsLevel=:SRelationsLevel ");
		}
		//如果页面传的shareType=2 则为间接用户
		if( "2".equals(shareType) ){   
			para.setSRelationsLevel(userLevel+2);
			hqlBuf.append("from Relations s where s.relationsLeft >:SRelationsLeft and s.relationsRight<:SRelationsRight and s.relationsAncestorId=:SRelationsAncestorId and s.relationsLevel=:SRelationsLevel ");
		}
		//如果为空 则为 我的用户分享
		 if( SunitStringUtil.isBlankOrNull(shareType) ){   
			 para.setSRelationsLevel(userLevel+2);
			hqlBuf.append("from Relations s where s.relationsLeft >:SRelationsLeft and s.relationsRight<:SRelationsRight and s.relationsAncestorId=:SRelationsAncestorId and s.relationsLevel<=:SRelationsLevel ");
		}
				
//		hqlBuf.append("from Relations s where s.relationsLeft >:SRelationsLeft and s.relationsRight<:SRelationsRight");
		Paging paging=null; 
		paging = this.paging(Paging.getCountForHQL(hqlBuf)
				.toString(), hqlBuf.toString(), rows, page, para); 
		return paging; 
	}
	

	public int getLevel(String classId) throws Exception {
		RelationTree vo =new RelationTree();
		vo.setRelationsUserId(classId);
		RelationTree parent = this.findSingleByExample(vo);
		if(null==parent){
			return 0;
		}
		return parent.getRelationsLevel();
	}


	public String getRelationsCode(String UserId) { 
		return UserId;
	}


 

	
	
	
	private NameValuePair createNameValuePair(String name, String value){
		
		return new NameValuePair(name,value); 
	}
	
 
	public List<RelationTree> getUpLevels(String classId) throws Exception {
		RelationTree vo =new RelationTree();
		vo.setRelationsUserId(classId);
		RelationTree self = this.findSingleByExample(vo);
		if(null==self) return null;
		List upList =  this.find("from Relations s where s.relationsLeft<? and s.relationsRight>? and s.relationsAncestorId=? ",self.getRelationsLeft(),self.getRelationsRight() ,self.getRelationsAncestorId());
		return upList; 
	}
	
	public Paging getUpLevelsForPaging(String userId, Integer rows,Integer page,String shareType) throws Exception {
		RelationTree vo =new RelationTree();
		vo.setRelationsUserId(userId);
		RelationTree self = this.findSingleByExample(vo);
		if(null==self) return null;
		
		RelationsSearchPara para = new RelationsSearchPara();
		para.setSRelationsLeft(self.getRelationsLeft());
		para.setSRelationsRight(self.getRelationsRight());
//		para.setSRelationsLevel(userLevel+1);
		para.setSRelationsAncestorId(self.getRelationsAncestorId());
		
		StringBuffer hqlBuf = new StringBuffer(); 
		hqlBuf.append("from Relations s where s.relationsLeft<:SRelationsLeft and s.relationsRight>:SRelationsRight and s.relationsAncestorId=:SRelationsAncestorId");
		Paging paging=null; 
		paging = this.paging(Paging.getCountForHQL(hqlBuf)
				.toString(), hqlBuf.toString(), rows, page, para); 
		return paging;  
	}

	
	
	public static  class RelationsSearchPara { 
	 
		 
		private java.lang.Integer sRelationsLeft;
		
		private java.lang.Integer sRelationsRight;
		
		private java.lang.Integer sRelationsLevel;

		private java.lang.String sRelationsAncestorId;
		
		public java.lang.Integer getSRelationsLeft() {
			return sRelationsLeft;
		}

		public void setSRelationsLeft(java.lang.Integer relationsLeft) {
			sRelationsLeft = relationsLeft;
		}

		public java.lang.Integer getSRelationsRight() {
			return sRelationsRight;
		}

		public void setSRelationsRight(java.lang.Integer relationsRight) {
			sRelationsRight = relationsRight;
		}

		public java.lang.Integer getSRelationsLevel() {
			return sRelationsLevel;
		}

		public void setSRelationsLevel(java.lang.Integer relationsLevel) {
			sRelationsLevel = relationsLevel;
		}

		public java.lang.String getSRelationsAncestorId() {
			return sRelationsAncestorId;
		}

		public void setSRelationsAncestorId(java.lang.String relationsAncestorId) {
			sRelationsAncestorId = relationsAncestorId; 
		}
	}

  
//	public List <?> getRoot(Class<? extends AbstractID> c){  
//		List<?> list = this.find("  from  "+c.getName()+" n where n.id in ( select classId from RelationTree t   where   t.className = ?  and  t.relationsLevel =?)",c.getName(),1 );
//		return list;
//	}
//	
//	public  List  getChilds(Class<? extends AbstractID>  c, String parent) {
//		
//		RelationTree rt =  this.getRelationByClassId(parent, c.getName()); 
//		
//		List<?> list = this.find("  from  "+c.getName()+" n where n.id in ( select classId from RelationTree t   where   t.className = ? and   relationsLevel= ? )",c.getName(), rt.getRelationsLevel()+1 );
//		return list;
//		
//	}
 
	


public  List  getChilds( String parent,String poName) {  
	RelationTree rt =   this.getRelationByClassId(parent, poName);
	List<RelationTree> list = this.find("   from RelationTree t   where   t.relationsLeft>?  and   t.relationsRight <? and t.className = ? and    t.relationsAncestorId=? ",rt.getRelationsLeft(),rt.getRelationsRight(),poName, rt.getRelationsAncestorId() );
	return list;
	
}
	
}
 