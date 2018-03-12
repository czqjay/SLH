package com.sunit.workflow.action;
 

import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import net.sf.json.JSONObject;
import net.sf.json.JsonConfig;
import net.sf.json.util.CycleDetectionStrategy;

import org.activiti.engine.RepositoryService;
import org.activiti.engine.repository.ProcessDefinition;
import org.activiti.engine.repository.ProcessDefinitionQuery;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import com.sunit.global.base.binder.BaseAction;
import com.sunit.global.util.Paging;


@Controller
@RequestMapping("/workflow")
public class WorkflowAction extends BaseAction { 

	static Logger logger = Logger.getLogger(WorkflowAction.class); 
	
	@Autowired
	private RepositoryService repositoryService;  
	  
    @RequestMapping("/loadDeployedList.action")     
    public String processList(HttpServletRequest request, PrintWriter pw,final int rows, int page) { 
    /*
     * 保存两个对象，一个是ProcessDefinition（流程定义），一个是Deployment（流程部署）
     */
        ProcessDefinitionQuery processDefinitionQuery = repositoryService.createProcessDefinitionQuery().orderByDeploymentId().desc();
        long count =processDefinitionQuery.count(); 
        Paging paging = new Paging();
        paging.setRows(rows);
        paging.setTotalRow(count);
        paging
		.setTotalPage(paging.getTotalRow() % paging.getRows() == 0 ? paging
				.getTotalRow()
				/ paging.getRows() 
				: paging.getTotalRow() / paging.getRows() + 1); 
        paging.setPage( (int) (page>paging.getTotalPage()?paging.getTotalPage():page));  
        int beginRow = ( paging.getPage() - 1) * rows; 
        List<ProcessDefinition> processDefinitionList = processDefinitionQuery.listPage(beginRow,beginRow+paging.getRows()); 
        paging.setList(processDefinitionList);
        
        Map map=new HashMap();
        map.put("records", paging.getTotalRow());// 记录总数
		map.put("total", paging.getTotalPage());// 总页数
		map.put("page", paging.getPage());// 当前页数
        map.put("rows",paging.getList()); 
        JsonConfig jc =new JsonConfig();
        jc.setCycleDetectionStrategy(CycleDetectionStrategy.LENIENT);
        jc.setExcludes(new String[]{"eventSupport","identityLinks"}); 
        String josnString =JSONObject.fromObject(map, jc).toString(); 
        pw.print(josnString);
        return null;
    }
	
    
    @RequestMapping(value = "/DeployedDelete") 
    public String DeployedDelete(PrintWriter pw, String ids,
			HttpServletRequest request) { 
    	logger.debug("WorkflowAction.DeployedDelete()");
    	Map<String, Object> map = new HashMap<String, Object>();
		map.put("success", false);
		try { 
			String[] idArr = ids.split(","); 
			for (int i = 0; i < idArr.length; i++) {
				String id = idArr[i];
				repositoryService.deleteDeployment(id, true);  
			}
			map.put("success", true);
		} catch (Exception e) {
			map.put("success", false);
			map.put("msg", e.getMessage());
			logger.error("删除已部署的流程：", e);
		}
		pw.print(JSONObject.fromObject(map).toString());
		return null;
    }
	public static void main(String[] args) {
		
		System.out.println(java.util.Collection.class
				.isAssignableFrom(new HashMap().getClass()));
		
		System.out.println(  new ArrayList().getClass()
				.isAssignableFrom(java.util.Collection.class));
		
		System.out.println(  new ArrayList() instanceof java.util.Collection);
	}
	
}