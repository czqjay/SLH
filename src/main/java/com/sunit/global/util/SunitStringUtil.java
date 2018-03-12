package com.sunit.global.util;

import java.beans.PropertyDescriptor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.apache.commons.lang.StringUtils;

import com.sunit.global.base.exception.SLHException;


/**
 * 
 * 
 * @class name：StringUtil
 * @desc：字符串处理,根据数组长度生成对应的?
 * @user：shanjizhou
 * @createTime：Jul 22, 2013 2:35:11 PM
 * @update user：shanjizhou
 * @updateTime：Jul 22, 2013 2:35:11 PM
 * @update desc：
 * @version 
 *
 */
public class SunitStringUtil {
	 
	public String getStringUtil(String[] strString){
		if(null==strString || strString.length==0){
			return "";
		}
		if(strString.length==1){
			return "?";
		}
		String str= "";
		while (((str.split(",").length)!=(strString.length))) {
			str+="?,";
		}
		str = str.substring( 0, str.length()-1);
		
		return str.toString();
	}
	
	/**
	 * 根据预编译数据长度得到预编译?号串 
	* @Title: getPrecompileMark 
	* @Description: 
	* @param @param strString
	* @param @return     
	* @return String  
	* @throws 
	* @author joye 
	* Dec 20, 2013 4:14:58 PM
	 */
	public static String getPrecompileMark(String[] strString){
		if(null==strString || strString.length==0){
			return "";
		}
		if(strString.length==1){
			return "?";
		}
		String str= "";
		while (((str.split(",").length)!=(strString.length))) {
			str+="?,";
		}
		str = str.substring( 0, str.length()-1);
		
		return str.toString();
	}
	 
	
	public static String getPrecompileMarkByList(Collection<String> collection){ 
		StringBuffer buf =new StringBuffer();
		for (String string : collection) {
			if(!SunitStringUtil.isBlankOrNull(string)){
				Object o =(SunitStringUtil.isBlankOrNull(buf))?buf.append("?"):buf.append(",?");
			}
		}
		return buf.toString(); 
	}
	
	
	/**
	 * 
	* @Title: getStringbyCommaSeparate 
	* @Description:  从List中获取以逗号分割的字符
	* @param @return     
	* @return String  
	* @throws 
	* @author joye 
	* Jul 2, 2014 11:07:43 AM
	 */
	public static String getStringbyCommaSeparate(List<String>  list){
		StringBuffer sb =new StringBuffer();
		for (int i = 0; i < list.size(); i++) {
			String s =list.get(i);
			if(StringUtils.isBlank(s))
				continue;
			if(!sb.toString().equals(""))
				sb.append(",");	 
			sb.append(s);
		}
		return sb.toString();
	}  
	
	
	public static String getStringbyCommaSeparate(Collection<String>  collection){
		StringBuffer sb =new StringBuffer();
		Iterator<String>  it = collection.iterator();
		while(it.hasNext()){
			String s =it.next();
			if(StringUtils.isBlank(s))
				continue;
			if(!sb.toString().equals(""))
				sb.append(",");	 
			sb.append(s);
		}
		return sb.toString();
	} 
	
	 
	/**
	 * 
	* @Title: getStringbyCommaSeparate 
	* @Description:  从string[] 中获取以逗号分割的字符
	* @param @return     
	* @return String  
	* @throws 
	* @author joye 
	* Jul 2, 2014 11:07:43 AM
	 */
	public static String getStringbyCommaSeparate(String [] arr){
		return getStringbyCommaSeparate (Arrays.asList(arr));
	}

	
	public static String getStringbyCommaSeparate(String [] arr,String defalut){
		if(arr==null){
			return defalut;
		}
		return getStringbyCommaSeparate (Arrays.asList(arr));
	}
	
	
	/**
	 * 
	* @Title: isBlankIfNotNull 
	* @Description: 对象是否为空值或空串 
	* @param @param object
	* @param @return     
	* @return boolean  
	* @throws 
	* @author joye 
	* Jul 9, 2014 2:43:52 PM
	 */
	public static boolean isBlankOrNull(Object object){
		if(object!=null) 
			return StringUtils.isBlank(object.toString()); 
		return true;
	}
	
	 
	public static boolean isBlankOrNulls(Object... object){
		
		if(object==null)
			return true;
		
		for (int i = 0; i < object.length; i++) {
			if(isBlankOrNull(object[i])){
				return true;
			}
		}
		return false;
	}
	
	
	/**
	 * 
	* @Title: getListbyCommaSeparate 
	* @Description: 将带逗号的字符串转 split后转换成List<String>
	* @param @param StringOfContainCommaSeparate
	* @param @return     
	* @return List  
	* @throws 
	* @author joye   
	* Aug 26, 2012 3:23:23 PM
	 */
	public static List<String> getListbyCommaSeparate(String  StringOfContainCommaSeparate,String Separate){
		if(SunitStringUtil.isBlankOrNull(StringOfContainCommaSeparate))
			return new ArrayList<String>(); 
		List<String> list=new ArrayList<String>();
		String []  arr =StringOfContainCommaSeparate.split(Separate);  
		for (int i = 0; i < arr.length; i++) {
			list.add(arr[i]);
		} 
		return list;  
	}

	
	 /** 
	* @Title: spiltString 
	* @Description: (将一个字符串中逗号前的部分加上单引号 str=a,b,c; 处理后 str ='a','b','c';) 
	* @param @param str
	* @param @return     
	* @return String    
	* @author：liangrujian
	* @date : Dec 24, 2015 9:59:03 AM
	*/ 
	public static String spiltString(String str) {
		  StringBuffer sb = new StringBuffer();
		  String[] temp = str.split(",");
		  for (int i = 0; i < temp.length; i++) {
		   if (!"".equals(temp[i]) && temp[i] != null)
		    sb.append("'" + temp[i] + "',");
		  }
		  String result = sb.toString();
		  String tp = result.substring(result.length() - 1, result.length());
		  if (",".equals(tp))
		   return result.substring(0, result.length() - 1);
		  else
		   return result;
		 }  
	
	public static boolean listContainsString2Oject(String source,  List< ? extends Object> targetList,String propertyName) throws SLHException{
		for (Object object : targetList) {
			PropertyDescriptor pd = SunitBeanUtils.getPropertyDescriptor(object.getClass(), propertyName);
			Method readMethod  = pd.getReadMethod();
			if (!Modifier.isPublic(readMethod.getDeclaringClass()
					.getModifiers())) {
				readMethod.setAccessible(true);
			}
			try {
				Object value = readMethod.invoke(source);
				
				if(source.equals(value.toString())){
					return true;
				}
				
			} catch (IllegalArgumentException e) {
				e.printStackTrace();
				throw new SLHException("参数错误");
				
			} catch (IllegalAccessException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				throw new SLHException("访问错误");
			} catch (InvocationTargetException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				throw new SLHException("调用错误");
			}
		}
		
		
		return false;
		
	}
	
	
	public static boolean listContainsString2Oject(String source,List< ? extends Object> targetList,ContainsString2Oject containsString2Oject) {
 
		for (  Object  object : targetList) {  
			
			if(containsString2Oject.Contains(source, object)){
				return true;
			};
		}
		return false;
	} 
	
	public static boolean listContainsString2Oject(String source,Collection< ? extends Object> targetList,ContainsString2Oject containsString2Oject) {
		 
		for (  Object  object : targetList) {  
			
			if(containsString2Oject.Contains(source, object)){
				return true;
			};
		}
		return false;
	}
	

	public interface ContainsString2Oject<T>{ 
		
		public boolean Contains(String source,T target); 
	} 
	 
	public interface  ContainsString2ReturnOject<T> extends ContainsString2Oject<T> {  
		 
		public boolean Contains(String source,T target);
		public T getContainsObj();
		public void setContainsObj(T obj); 
		
	}
	 
	 
	public static boolean  requireAndEquals(String source,String target){
		if(!isBlankOrNull(source)&&source.equals(target))
			return true;
		return false;
		
	}
	
	
	public static void main(String[] args) {

		
	}
	
}
