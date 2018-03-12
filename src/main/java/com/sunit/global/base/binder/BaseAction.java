package com.sunit.global.base.binder;

import java.text.SimpleDateFormat;
import java.util.Date;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.propertyeditors.CustomDateEditor;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.InitBinder;

import com.sunit.global.util.date.DateStyle;
import com.sunit.global.util.date.DateUtil;
import com.sunit.sysmanager.po.User;
import com.sunit.sysmanager.service.UserManager;

import net.sf.json.JsonConfig;
import net.sf.json.processors.JsonValueProcessor;
import net.sf.json.util.CycleDetectionStrategy;
import net.sf.json.util.PropertyFilter;

public class BaseAction {

	static Logger logger = Logger.getLogger(BaseAction.class);

	@Autowired
	UserManager userManager;


	public JsonValueProcessor maskUserNameJsonValueProcessor = new JsonValueProcessor() {

		public Object processArrayValue(Object value, JsonConfig jsonConfig) {
			return value;
		}

		public Object processObjectValue(String key, Object value, JsonConfig jsonConfig) {
			User user = userManager.get(String.valueOf(value));
			String name = (String) (user == null ? value : user.getAccountName());
			name = name.replace(name.substring(3, 9), "***");
			return name;
		}
	};
	

	
	

	SimpleDateFormat sf = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");

	@InitBinder
	public void baseBinder(WebDataBinder binder) {
		binder.registerCustomEditor(String.class, new Html2StringBinder());
		binder.registerCustomEditor(Date.class, new CustomDateEditor(sf, true));
	}

	/**
	 * @Title: getJsonConfig
	 * @Description: (获取JsonConfig)
	 * @param @return
	 * @return JsonConfig
	 * @author：liangrujian
	 * @date : Feb 1, 2016 5:27:16 PM
	 */
	public static JsonConfig getJsonConfig() {
		JsonConfig jsonConfig = new JsonConfig();
		jsonConfig.setCycleDetectionStrategy(CycleDetectionStrategy.LENIENT);

		jsonConfig.setJsonPropertyFilter(new PropertyFilter() {
			public boolean apply(Object arg0, String arg1, Object arg2) {
				if (arg1.equals("handler") || arg1.equals("hibernateLazyInitializer") || arg1.equals("flowvariables")|| arg1.equals("roots"))
					return true;
				else
					return false;
			}
		});
		return jsonConfig;
	}

	/**
	 * @Title: getStringDateProcessor
	 * @Description: (获取字符串时间处理器)
	 * @param @return
	 * @return JsonValueProcessor
	 * @author：liangrujian
	 * @date : Mar 2, 2016 8:57:56 PM
	 */
	public JsonValueProcessor getStringDateProcessor() {
		JsonValueProcessor jvp = new JsonValueProcessor() {
			public Object processArrayValue(Object arg0, JsonConfig arg1) {
				return arg0;
			}

			public Object processObjectValue(String arg0, Object value, JsonConfig arg2) {
				String valueDate = "";
				// Calendar ca = DateUtil.parseCalendar(value);
				// if(DateUtil.isValid(value.toString())){
				// valueDate =DateUtil.LONG_FORMAT.format(ca.getTime());
				//
				// if(valueDate.indexOf("00:00:00")>1)
				// valueDate = valueDate.substring(0,
				// valueDate.indexOf("00:00:00"));
				//
				// }
				Date date = DateUtil.ifParse(value.toString());
				if (date != null) {
					valueDate = DateUtil.getDateFormat(DateStyle.YYYY_MM_DD_HH_MM_SS.getValue()).format(date);
					if (valueDate.indexOf("00:00:00") > 1)
						valueDate = valueDate.substring(0, valueDate.indexOf("00:00:00"));
				}
				return valueDate.trim();
			}
		};
		return jvp;
	}

	private static final String[] exlude = new String[] { "taskId", "assigneeType", "assignee", "candidate",
			"activityId", "poId", "proccessInstanceId" };

	public String[] getFlowAbilityExclude() {
		return exlude;
	}

	public JsonValueProcessor getMaskUserNameJsonValueProcessor() {
		return maskUserNameJsonValueProcessor;
	}

	public void setMaskUserNameJsonValueProcessor(JsonValueProcessor maskUserNameJsonValueProcessor) {
		this.maskUserNameJsonValueProcessor = maskUserNameJsonValueProcessor;
	}


}
