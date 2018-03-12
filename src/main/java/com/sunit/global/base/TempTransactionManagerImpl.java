package com.sunit.global.base;

import org.apache.poi.util.IntegerField;
import org.hibernate.Session;
import org.springframework.stereotype.Component;

/**
 *  当一些非常简单的事务操作时,  需要实现 manager 接口的新方法很麻烦, 可以直接实现此类,来达到事务的效果
 * 
 * 类名称：TempTransactionManagerImpl
 * 类描述：
 * 创建人：Administrator
 * 创建时间：2017年11月18日 下午8:56:47
 * 修改人：joye
 * 修改时间：2017年11月18日 下午8:56:47
 * 修改备注：
 * @version 
 *
 */
@Component
public  class TempTransactionManagerImpl { 

	 	public void saveOrUpdate(TempTX tx) throws Exception {
	 		tx.doAction();
	 	}
 
	 	public  interface  TempTX  { 
	 		
	 		public void   doAction() throws Exception;
	 		
	 	}
	 	
	
	 	
	 	
}
