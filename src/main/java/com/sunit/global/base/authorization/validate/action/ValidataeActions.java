package com.sunit.global.base.authorization.validate.action;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;

import com.sunit.global.base.authorization.validate.AbstractValidatorProcess;
import com.sunit.global.base.authorization.validate.ValidateInterface;
import com.sunit.global.base.authorization.validate.constraints.Length;
import com.sunit.global.base.authorization.validate.constraints.Pattern;
import com.sunit.global.base.authorization.validate.constraints.Range;
import com.sunit.global.base.authorization.validate.constraints.Remote;
import com.sunit.global.base.authorization.validate.constraints.Script;
import com.sunit.global.base.authorization.validate.constraints.Script.AbstractScriptExecute;
import com.sunit.global.base.authorization.validate.impl.DateISOValidator;
import com.sunit.global.base.authorization.validate.impl.DigitLetterValidator;
import com.sunit.global.base.authorization.validate.impl.DigitsValidator;
import com.sunit.global.base.authorization.validate.impl.EmailValidator;
import com.sunit.global.base.authorization.validate.impl.IdCardValidator;
import com.sunit.global.base.authorization.validate.impl.JavaValidator;
import com.sunit.global.base.authorization.validate.impl.LengthValidator;
import com.sunit.global.base.authorization.validate.impl.LetterNumberChineseValidator;
import com.sunit.global.base.authorization.validate.impl.MobileAndMailValidator;
import com.sunit.global.base.authorization.validate.impl.MobileValidator;
import com.sunit.global.base.authorization.validate.impl.NotBlankValidator;
import com.sunit.global.base.authorization.validate.impl.NotSupportValidator;
import com.sunit.global.base.authorization.validate.impl.NumberRangeValidator;
import com.sunit.global.base.authorization.validate.impl.PartnerValidator;
import com.sunit.global.base.authorization.validate.impl.PatternValidator;
import com.sunit.global.base.authorization.validate.impl.PhoneValidator;
import com.sunit.global.base.authorization.validate.impl.RemoteValidator;
import com.sunit.global.base.authorization.validate.impl.StringCHValidator;
import com.sunit.global.base.authorization.validate.impl.StringValidator;
import com.sunit.global.util.SessionContext;
import com.sunit.global.util.SpringContextUtils;
import com.sunit.global.util.SunitStringUtil;

import net.sf.json.JSONObject;
public class ValidataeActions {
	
	 
	
	static Logger logger = Logger.getLogger(ValidataeActions.class); 
	
static{

	//所有申请页面的上传图片name为userInfo
	SpringContextUtils.getGlobal().setUploadArgs("userInfo_size",5000);
	SpringContextUtils.getGlobal().setUploadArgs("userInfo_subDir","userInfo");
	
	//所有商家中的我的文件上传name为myFile       购买服务时的提交资料也是这个
	SpringContextUtils.getGlobal().setUploadArgs("myFile_size",3000);
	SpringContextUtils.getGlobal().setUploadArgs("myFile_subDir","myFile");
	
	//我的投诉name为complain
	SpringContextUtils.getGlobal().setUploadArgs("complain_size",3000);				//能购买的用户就有(我的文件上传),和(投诉上传),(账户设置)，三个能上传图片的地方
	SpringContextUtils.getGlobal().setUploadArgs("complain_subDir","complain");		//服务商就还有(商品管理)和(店铺装修)
																					//政府和市场和服务商就都有(添加客户)
	//店铺装修logo的name为agencyDivs,轮播图为agencyDiv									//政府用户，暂时只有账户设置的图片上传和添加客户的图片上传
	SpringContextUtils.getGlobal().setUploadArgs("agencyDiv_size",3000);
	SpringContextUtils.getGlobal().setUploadArgs("agencyDiv_subDir","agencyDiv");
	SpringContextUtils.getGlobal().setUploadArgs("agencyDivs_size",200);
	SpringContextUtils.getGlobal().setUploadArgs("agencyDivs_subDir","agencyDivs");
	
	
	//服务商->商品管理
	SpringContextUtils.getGlobal().setUploadArgs("ageServerImg_size",3000);
	SpringContextUtils.getGlobal().setUploadArgs("ageServerImg_subDir","ageServerImg");	
	 
	SpringContextUtils.getGlobal().setUploadArgs("imgByWYSIWYG_size",3000); 
	SpringContextUtils.getGlobal().setUploadArgs("imgByWYSIWYG_subDir","imgByWYSIWYG");	 
	
	//记账凭证
	SpringContextUtils.getGlobal().setUploadArgs("billVoucher_size",3000);
	SpringContextUtils.getGlobal().setUploadArgs("billVoucher_subDir","billVoucher");

	
	//每个商家的账户设置
	SpringContextUtils.getGlobal().setUploadArgs("updataUserInfo_size",3000);
	SpringContextUtils.getGlobal().setUploadArgs("updataUserInfo_subDir","updataUserInfo");

	
}
	
	private static Map<String, List<ValidateObject>> actionsMap = new HashMap();
	private static Map<Class, AbstractValidatorProcess> processsMap = new HashMap();
	private static Map<String, Class<? extends ValidateInterface>> validateMethods = new HashMap();
	
	
	
	
	
	public static void reconfig(){ 
		actionsMap.clear();
		processsMap.clear();
		validateMethods.clear();
		
		
		
		
		
		
		
		//登录页面
		ValidataeActions.addValidate("/front/j_spring_security_check","j_username","requiredValidator");
		ValidataeActions.addValidate("/front/j_spring_security_check","j_username","mobileAndMailValidator");//mobileValidator到时换成只能输入手机号码
		ValidataeActions.addValidate("/front/j_spring_security_check","j_password","requiredValidator");
		ValidataeActions.addValidate("/front/j_spring_security_check","j_password","rangelengthValidator","{min:4,max:20}");
		ValidataeActions.addValidate("/front/j_spring_security_check","j_password","digitLetterValidator");

		
		
		
		ValidataeActions.addValidate("/agencycustom/frontAgencycustomSaveOfSellerAccount.action","javaValidator","javaValidator", new  Script.AbstractScriptExecute(){
			public boolean execute(Script script) {
				try { 
					String  identifyingCode_disable = script.getRequest().getParameter("identifyingCode_disable");
					
					String account =(String) SessionContext.getAttribute(script.getRequest(), "userAccount");
					
					String identifyingCode =(String) SessionContext.getAttribute(script.getRequest(), account);
					 
					if(!SunitStringUtil.requireAndEquals(identifyingCode_disable,identifyingCode)){
						script.setMsg("验证码错误"); 
						return false;
					} 
					script.getRequest().getSession().removeAttribute(account);
					
				} catch (Exception e) { 
					script.setMsg("验证码错误"); 
					return false;
				}
				 
				
				
				return true;
			}
 		} ); 
		
	
		ValidataeActions.addValidate("/xwyuser/frontXwyuserDelete.action","javaValidator","javaValidator",new AbstractScriptExecute(){
			
			@Override 
			public boolean execute(Script script) {
				HttpServletRequest request = script.getRequest(); 
				String mid =   (String) request.getSession().getAttribute("orgManageId");
				String delIds = (String) request.getParameter("ids");
				if(delIds.indexOf(mid)>-1){
					script.setMsg("管理帐号不能删除");
					return false;
				}
				return true;
			}
		}); 
	}
	
	static {
		reconfig();
		
		validateMethods.put("notBlankValidator", NotBlankValidator.class);
		validateMethods.put("requiredValidator", NotBlankValidator.class);

		validateMethods.put("lengthValidator", LengthValidator.class);
		// validateMethods.put("equalsValidator", EqualsValidator.class);
		validateMethods.put("patternValidator", PatternValidator.class);

		validateMethods.put("digitLetterValidator", DigitLetterValidator.class);
		validateMethods.put("rangelengthValidator", LengthValidator.class);

		validateMethods.put("digitsValidator", DigitsValidator.class);

		validateMethods.put("stringCHValidator", StringCHValidator.class);
		validateMethods.put("idCardValidator", IdCardValidator.class);
		validateMethods.put("dateISOValidator", DateISOValidator.class);
		validateMethods.put("emailValidator", EmailValidator.class);
		validateMethods.put("letterNumberChineseValidator",
				LetterNumberChineseValidator.class);				
		validateMethods.put("phoneValidator", PhoneValidator.class);
		validateMethods.put("stringValidator", StringValidator.class);
		validateMethods.put("partnerValidator", PartnerValidator.class);
		validateMethods.put("mobileAndMailValidator",
				MobileAndMailValidator.class);
		validateMethods.put("mobileValidator", MobileValidator.class);
		 
		validateMethods.put("remoteValidator", RemoteValidator.class);
		validateMethods.put("notSupportValidator", NotSupportValidator.class);
		validateMethods.put("javaValidator", JavaValidator.class);   
		validateMethods.put("numberRangeValidator", NumberRangeValidator.class);


		processsMap.put(NotSupportValidator.class,
				new AbstractValidatorProcess() {
					public boolean process(HttpServletRequest reqeust,HttpServletResponse response,ValidateObject validate,
							String[]  filedParamenter, Map resultMap, Map args) {
						NotSupportValidator va = new NotSupportValidator();
						for (int i = 0; i < filedParamenter.length; i++) {
							if (!va.isValid(filedParamenter[i])) {
								resultMap.put("msg", getMsg(args, "不允许的字段"));
								return false;
							}
						} 
						return true;
					}
				});
		 
		processsMap.put(JavaValidator.class,
				new AbstractValidatorProcess() {
					public boolean process(HttpServletRequest reqeust,HttpServletResponse response,ValidateObject validate,
							String[]  filedParamenter, Map resultMap, Map args) {
						JavaValidator va = new  JavaValidator();
						Script s = new Script();
						s.setRequest(reqeust);
						s.setFieldName(validate.getFieldName());
						s.setValue(SunitStringUtil.getStringbyCommaSeparate(filedParamenter,""));
						s.setScriptEx((Script.AbstractScriptExecute)args.get("scriptEx")); 
						s.setResponse(response); 
						va.initialize(s); 
						if (!va.isValid(s.getValue())) {
							resultMap.put("msg", SunitStringUtil.isBlankOrNull(s.getMsg())?"自定义验证错误":s.getMsg());
							return false;
						}
						return true;
					}
				});
		
		processsMap.put(NotBlankValidator.class,
				new AbstractValidatorProcess() {
					public boolean process(HttpServletRequest reqeust,HttpServletResponse response,ValidateObject validate,
							String[]  filedParamenter, Map resultMap, Map args) {
						NotBlankValidator va = new NotBlankValidator();
						for (int i = 0; i < filedParamenter.length; i++) {
							if (!va.isValid(filedParamenter[i])) {
								resultMap.put("msg", "not blank");
								return false;
							}
						}
						return true;
					}
				});

		processsMap.put(LengthValidator.class, new AbstractValidatorProcess() {
			public boolean process(HttpServletRequest reqeust,HttpServletResponse response,ValidateObject validate,
					String[]  filedParamenter, Map resultMap, Map args) {
				LengthValidator va = new LengthValidator();

				Length length = new Length();
				length.setMin((Integer) (args.get("min")));
				length.setMax((Integer) args.get("max"));
				length.setMessage(getMsg(args, ""));
				va.initialize(length);
				for (int i = 0; i < filedParamenter.length; i++) {
					if (!va.isValid(filedParamenter[i])) {
					resultMap.put("msg", SunitStringUtil.isBlankOrNull(length 
							.getMessage()) == true ? "长度错误" : length
							.getMessage());
					return false; 
				}}
				return true;
			}
		}); 
		
		
		processsMap.put(PatternValidator.class, new AbstractValidatorProcess() {
			public boolean process(HttpServletRequest reqeust,HttpServletResponse response,ValidateObject validate,
					String[]  filedParamenter, Map resultMap, Map args) {
				PatternValidator va = new PatternValidator();
				Pattern pattern = new Pattern();
				pattern.setRegpx((String) args.get("regpx"));
				pattern.setMessage(getMsg(args, ""));
				va.initialize(pattern);
				for (int i = 0; i < filedParamenter.length; i++) {
					if (!va.isValid(filedParamenter[i])) {
					resultMap.put("msg", SunitStringUtil.isBlankOrNull(pattern
							.getMessage()) == true ? "效验错误" : pattern
							.getMessage());
					// response.getWriter().print(
					// JSONObject.fromObject(map).toString());
					return false;
					}
				}
				return true;
			}
		});
		
		processsMap.put(DigitLetterValidator.class, new AbstractValidatorProcess() {
			public boolean process(HttpServletRequest reqeust,HttpServletResponse response,ValidateObject validate,
					String[]  filedParamenter, Map resultMap, Map args) {
				DigitLetterValidator va = new DigitLetterValidator();

				Pattern pattern  =new  Pattern();   
				pattern.setRegpx("^[A-Za-z0-9]+$");   
				pattern.setMessage(getMsg(args, "只能输入字母与数字")); 
				va.initialize(pattern); 
				for (int i = 0; i < filedParamenter.length; i++) {
					if (!va.isValid(filedParamenter[i])) {
					resultMap.put("msg", SunitStringUtil.isBlankOrNull(pattern.getMessage())==true?"效验错误":pattern.getMessage());
					return false;
				}}
				return true;
			}
		});
		
		 
		processsMap.put(MobileValidator.class, new AbstractValidatorProcess() {
			public boolean process(HttpServletRequest reqeust,HttpServletResponse response,ValidateObject validate,
					String[]  filedParamenter, Map resultMap, Map args) {
				MobileValidator va = new MobileValidator();

				Pattern pattern  =new  Pattern();   
				pattern.setRegpx("^1[3|5|7|8][0-9]{9}$");   
				pattern.setMessage(getMsg(args, "请输入正确的手机号码")); 
				va.initialize(pattern); 
				for (int i = 0; i < filedParamenter.length; i++) {
					if (!va.isValid(filedParamenter[i])) {
					resultMap.put("msg", SunitStringUtil.isBlankOrNull(pattern.getMessage())==true?"效验错误":pattern.getMessage());
					return false;
				}}
				return true;
			}
		});
		
		processsMap.put(DigitsValidator.class, new AbstractValidatorProcess() {
			public boolean process(HttpServletRequest reqeust,HttpServletResponse response,ValidateObject validate,
					String[]  filedParamenter, Map resultMap, Map args) {
				DigitsValidator va = new DigitsValidator();

				Pattern pattern  =new  Pattern();   
				pattern.setRegpx("^\\d+$");   
				pattern.setMessage(getMsg(args, "只能输入数字"));  
				va.initialize(pattern); 
				for (int i = 0; i < filedParamenter.length; i++) {
					if (!va.isValid(filedParamenter[i])) {
					resultMap.put("msg", SunitStringUtil.isBlankOrNull(pattern.getMessage())==true?"效验错误":pattern.getMessage());
					return false;
				}}
				return true;
			}
		});
		
		
		processsMap.put(StringCHValidator.class, new AbstractValidatorProcess() {
			public boolean process(HttpServletRequest reqeust,HttpServletResponse response,ValidateObject validate,
					String[]  filedParamenter, Map resultMap, Map args) {
				StringCHValidator va = new StringCHValidator();

				Pattern pattern  =new  Pattern();    
				pattern.setRegpx("^[\\u4e00-\\u9fa5]+$");    
				pattern.setMessage(getMsg(args, "只能输入汉字"));  
				va.initialize(pattern); 
				for (int i = 0; i < filedParamenter.length; i++) {
					if (!va.isValid(filedParamenter[i])) {
					resultMap.put("msg", SunitStringUtil.isBlankOrNull(pattern.getMessage())==true?"效验错误":pattern.getMessage());
					return false;
				}}
				return true;
			}
		});
		
	
		processsMap.put(IdCardValidator.class, new AbstractValidatorProcess() {
			public boolean process(HttpServletRequest reqeust,HttpServletResponse response,ValidateObject validate,
					String[]  filedParamenter, Map resultMap, Map args) {
				IdCardValidator va = new IdCardValidator();

				Pattern pattern  =new  Pattern();    
				pattern.setRegpx("(^\\d{15}$)|(^\\d{18}$)|(^\\d{17}(\\d|X|x)$)");     
				pattern.setMessage(getMsg(args, "身份证不合法"));  
				va.initialize(pattern); 
				for (int i = 0; i < filedParamenter.length; i++) {
					if (!va.isValid(filedParamenter[i])) {
					resultMap.put("msg", SunitStringUtil.isBlankOrNull(pattern.getMessage())==true?"效验错误":pattern.getMessage());
					return false;
				}}
				return true;
			}
		});
		
		processsMap.put(DateISOValidator.class, new AbstractValidatorProcess() {
			public boolean process(HttpServletRequest reqeust,HttpServletResponse response,ValidateObject validate,
					String[]  filedParamenter, Map resultMap, Map args) {
				DateISOValidator va = new DateISOValidator();
 
				Pattern pattern  =new  Pattern();    
				pattern.setRegpx("^\\d{4}[\\/-]\\d{1,2}[\\/-]\\d{1,2}$");    
				pattern.setMessage(getMsg(args, "日期不合法")); 
				va.initialize(pattern); 
				for (int i = 0; i < filedParamenter.length; i++) {
					if (!va.isValid(filedParamenter[i])) {
					resultMap.put("msg", SunitStringUtil.isBlankOrNull(pattern.getMessage())==true?"效验错误":pattern.getMessage());
					return false;
				}}
				return true;
			}
		}); 
		
/*		processsMap.put(EmailValidator.class, new AbstractValidatorProcess() {
			public boolean process(HttpServletRequest reqeust,HttpServletResponse response,ValidateObject validate,
					String filedParamenter, Map resultMap, Map args) {
				EmailValidator va = new EmailValidator();

				Pattern pattern  =new  Pattern();    
				pattern.setRegpx("^([a-zA-Z0-9_\\\\.\\\\-])+\\\\@(([a-zA-Z0-9\\-])+\\\\.)+([a-zA-Z0-9]{2,4})+$");   
				pattern.setMessage(getMsg(args, "邮件不合法")); 
				va.initialize(pattern); 
				boolean kx =va.isValid(filedParamenter);
				
				Pattern patternEmail  =new  Pattern();    
				patternEmail.setRegpx("^([A-Za-z0-9\\u4e00-\\u9fa5]|[\\x00-\\xff])+$");   
				patternEmail.setMessage(getMsg(args, "邮件不合法")); 
				va.initialize(patternEmail); 
				boolean ks =va.isValid(filedParamenter);
				
				if (!(kx || ks)) { 
					resultMap.put("msg", SunitStringUtil.isBlankOrNull(pattern.getMessage())==true?"效验错误":pattern.getMessage());
					return false;
				}
				return true;
			}
		});*/
		processsMap.put(EmailValidator.class, new AbstractValidatorProcess() {
			public boolean process(HttpServletRequest reqeust,HttpServletResponse response,ValidateObject validate,
					String[]  filedParamenter, Map resultMap, Map args) {
				PhoneValidator va = new PhoneValidator();

				Pattern pattern  =new  Pattern();     
				pattern.setRegpx("^([a-zA-Z0-9_\\.\\-])+\\@(([a-zA-Z0-9\\-])+\\.)+([a-zA-Z0-9]{2,4})+$");    
				pattern.setMessage(getMsg(args, "请输入正确的邮箱")); 
				va.initialize(pattern); 
				for (int i = 0; i < filedParamenter.length; i++) {
					if (!va.isValid(filedParamenter[i])) {
					resultMap.put("msg", SunitStringUtil.isBlankOrNull(pattern.getMessage())==true?"效验错误":pattern.getMessage());
					return false;
				}}
				return true;
			}
		});
		

		
		processsMap.put(LetterNumberChineseValidator.class, new AbstractValidatorProcess() {
			public boolean process(HttpServletRequest reqeust,HttpServletResponse response,ValidateObject validate,
					String[]  filedParamenter, Map resultMap, Map args) {
				LetterNumberChineseValidator va = new LetterNumberChineseValidator();

				Pattern pattern  =new  Pattern();    
				pattern.setRegpx("^([A-Za-z0-9\\u4e00-\\u9fa5]|[^\\x00-\\xff])+$");    
				pattern.setMessage(getMsg(args, "只能输入字母,数字和中文")); 
				va.initialize(pattern); 
				for (int i = 0; i < filedParamenter.length; i++) {
					if (!va.isValid(filedParamenter[i])) {
					resultMap.put("msg", SunitStringUtil.isBlankOrNull(pattern.getMessage())==true?"效验错误":pattern.getMessage());
					return false;
				}}
				return true;
			}
		});
		
		processsMap.put(PhoneValidator.class, new AbstractValidatorProcess() {
			public boolean process(HttpServletRequest reqeust,HttpServletResponse response,ValidateObject validate,
					String[]  filedParamenter, Map resultMap, Map args) {
				PhoneValidator va = new PhoneValidator();

				Pattern pattern  =new  Pattern();     
				pattern.setRegpx("^0\\d{2,3}-?\\d{7,8}$");    
				pattern.setMessage(getMsg(args, "请输入正确的电话号码 如 010-33333333")); 
				va.initialize(pattern); 
				for (int i = 0; i < filedParamenter.length; i++) {
					if (!va.isValid(filedParamenter[i])) {
					resultMap.put("msg", SunitStringUtil.isBlankOrNull(pattern.getMessage())==true?"效验错误":pattern.getMessage());
					return false;
				}}
				return true;
			}
		});
		
		processsMap.put(StringValidator.class, new AbstractValidatorProcess() {
			public boolean process(HttpServletRequest reqeust,HttpServletResponse response,ValidateObject validate,
					String[]  filedParamenter, Map resultMap, Map args) {
				StringValidator va = new StringValidator();
				Pattern pattern  =new  Pattern();      
				pattern.setRegpx("^([\\u0391-\\uFFE5\\w]|-|\\s)+$");    
				pattern.setMessage(getMsg(args, "不允许包含特殊符号!")); 
				va.initialize(pattern); 
				for (int i = 0; i < filedParamenter.length; i++) {
					if (!va.isValid(filedParamenter[i])) {
					resultMap.put("msg", SunitStringUtil.isBlankOrNull(pattern.getMessage())==true?"效验错误":pattern.getMessage());
					return false;
				}}
				return true;
			}
		});
		
		
		processsMap.put(PartnerValidator.class, new AbstractValidatorProcess() {
			public boolean process(HttpServletRequest reqeust,HttpServletResponse response,ValidateObject validate,
					String[]  filedParamenter, Map resultMap, Map args) {
				PartnerValidator va = new PartnerValidator();

				Pattern pattern  =new  Pattern();      
				pattern.setRegpx("^(2088)\\d{12}$");     
				pattern.setMessage(getMsg(args, "必须以2088开头由16位纯数字组成的字符串!")); 
				va.initialize(pattern); 
				for (int i = 0; i < filedParamenter.length; i++) {
					if (!va.isValid(filedParamenter[i])) {
					resultMap.put("msg", SunitStringUtil.isBlankOrNull(pattern.getMessage())==true?"效验错误":pattern.getMessage());
					return false;
				}}
				return true;
			}
		});
		
		
		processsMap.put(MobileAndMailValidator.class, new AbstractValidatorProcess() {
			public boolean process(HttpServletRequest reqeust,HttpServletResponse response,ValidateObject validate,
					String[]  filedParamenter, Map resultMap, Map args) {
				MobileAndMailValidator va = new MobileAndMailValidator(); 

				Pattern pattern  =new  Pattern();    
				pattern.setRegpx("^([a-zA-Z0-9_-])+@([a-zA-Z0-9_-])+(.[a-zA-Z0-9_-])+");    
				pattern.setMessage(getMsg(args, "请输入正确的手机号或邮箱")); 
				va.initialize(pattern);
				
				
				boolean b =va.isValid(SunitStringUtil.getStringbyCommaSeparate(filedParamenter,""));
				
				 
				Pattern patternMobile  =new  Pattern();     
				patternMobile.setRegpx("^1[3|5|7|8][0-9]{9}$");   
				patternMobile.setMessage(getMsg(args, "请输入正确的手机号或邮箱")); 
				va.initialize(patternMobile);  
				boolean mb =va.isValid(SunitStringUtil.getStringbyCommaSeparate(filedParamenter,"")); 
				
				if(! (b || mb)){
					resultMap.put("msg", SunitStringUtil.isBlankOrNull(pattern.getMessage())==true?"效验错误":pattern.getMessage());
					return false;
				}
				return true;
			}
		});
		
		 
		 
		processsMap.put(RemoteValidator.class, new AbstractValidatorProcess() {
			public boolean process(HttpServletRequest reqeust,HttpServletResponse response,  ValidateObject validate,
					String[]  filedParamenter, Map resultMap, Map args){
				RemoteValidator va = new RemoteValidator(); 
				
				Remote remote  =new  Remote();
				remote.setWebPath((String)args.get("xwt_web_app_name"));
				remote.setAction((String)args.get("action")); 
				remote.setMessage(getMsg(args, "远程校验错误!"));
				remote.setIsReverse(SunitStringUtil.isBlankOrNull(args.get("isReverse"))?"false":"true");
				remote.setFieldName(validate.getFieldName());
				remote.setReqeust(reqeust);
				remote.setResponse(response);
				va.initialize(remote); 
				if (!va.isValid(SunitStringUtil.getStringbyCommaSeparate(filedParamenter,""))) { 
					resultMap.put("msg", SunitStringUtil.isBlankOrNull(remote.getMessage())==true?"效验错误":remote.getMessage());
					return false;
				}
				return true;
			}
		});
		
		 
		 
		processsMap.put(NumberRangeValidator.class, new AbstractValidatorProcess() {
			public boolean process(HttpServletRequest reqeust,HttpServletResponse response,  ValidateObject validate,
					String[]  filedParamenter, Map resultMap, Map args){
				NumberRangeValidator va = new NumberRangeValidator();

				Range range = new Range();
				range.setMin( args.get("min").toString());
				range.setMax( args.get("max").toString()); 
				range.setMessage(getMsg(args, "不在范围内"));
				va.initialize(range);
				for (int i = 0; i < filedParamenter.length; i++) {
					if (!va.isValid(filedParamenter[i])) {
					resultMap.put("msg", SunitStringUtil.isBlankOrNull(range
							.getMessage()) == true ? "不在范围内" : range
							.getMessage());
					return false;
				}} 
				return true;
			}
		});
		
		
		
	}

	public static void addValidate(String actionName, String fieldName,
			String methodName) {
		if (actionsMap.get(actionName) == null) {
			actionsMap.put(actionName, new ArrayList());
		}

		ValidateObject vo = new ValidateObject();
		vo.setFieldName(fieldName);
		vo.setMethodName(methodName);

		add(actionsMap.get(actionName), vo);
	}

	public static void addValidate(String actionName, String fieldName,
			String methodName, String otherFieldName, Object methodNameArgs) {
		if (actionsMap.get(actionName) == null) {
			actionsMap.put(actionName, new ArrayList());
		}
		ValidateObject vo = new ValidateObject();
		vo.setFieldName(fieldName);
		vo.setMethodName(methodName);
		vo.setOtherFieldName(otherFieldName);
		vo.setMethodNameArgs(methodNameArgs);

		add(actionsMap.get(actionName), vo);
	}

	public static void addValidate(String actionName, String fieldName,
			String methodName, Object methodNameArgs) {
		if (actionsMap.get(actionName) == null) {
			actionsMap.put(actionName, new ArrayList());
		}
		ValidateObject vo = new ValidateObject();
		vo.setFieldName(fieldName);
		vo.setMethodName(methodName);
		vo.setMethodNameArgs(methodNameArgs);

		// actionsMap.get(actionName).add(vo);
		add(actionsMap.get(actionName), vo);
	}
	
	

	private static void add(List<ValidateObject> list, ValidateObject vo) {

//		for (int i = 0; i < list.size(); i++) {
//			ValidateObject vao = list.get(i);
//			if (vao != null) {
//				// 已存在相同的验证，则不添加,忽略此次添加 
//				if (vao.getFieldName().equals(vo.getFieldName())
//						&& vao.getMethodName().equals(vo.getMethodName())) {
//					return;
//				}
//			} 
//		}
		list.add(vo);
	}

	public static List<ValidateObject> getActionValidateObj(String actionName) {
		return actionsMap.get(actionName);

	}

	public static Class getValidateMethod(String methodName) {
		Class<? extends ValidateInterface> cv = validateMethods.get(methodName);
		return cv;

	}

	public static AbstractValidatorProcess getValidateProcess(Class clazz) {
		AbstractValidatorProcess process = processsMap.get(clazz);
		return process;

	}

	public static Map<String, List<ValidateObject>> getActionsMap() {

		return actionsMap;
	}

	public static void setActionsMap(
			Map<String, List<ValidateObject>> actionsMap) {
		ValidataeActions.actionsMap = actionsMap;
	}
  
	 
	public static void main(String[] args) { 
		
		String s ="{action:\"/user/isExists.action\"}"; 
		
		Map map =JSONObject.fromObject(s);
		System.out.println(map);
		java.util.regex.Pattern pattern = java.util.regex.Pattern.compile("^([a-zA-Z0-9_-])+@([a-zA-Z0-9_-])+(.[a-zA-Z0-9_-])+"); 
		System.out.println(pattern.matcher("187187S1111@163.com").matches());
	}
}
