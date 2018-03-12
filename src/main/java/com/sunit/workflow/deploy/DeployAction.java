package com.sunit.workflow.deploy;


import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.zip.ZipInputStream;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;

import org.activiti.bpmn.converter.BpmnXMLConverter;
import org.activiti.bpmn.model.BpmnModel;
import org.activiti.editor.constants.ModelDataJsonConstants;
import org.activiti.editor.language.json.converter.BpmnJsonConverter;
import org.activiti.engine.HistoryService;
import org.activiti.engine.RepositoryService;
import org.activiti.engine.RuntimeService;
import org.activiti.engine.TaskService;
import org.activiti.engine.impl.Condition;
import org.activiti.engine.impl.TaskServiceImpl;
import org.activiti.engine.impl.bpmn.parser.BpmnParse;
import org.activiti.engine.impl.identity.Authentication;
import org.activiti.engine.impl.interceptor.Command;
import org.activiti.engine.impl.interceptor.CommandContext;
import org.activiti.engine.impl.interceptor.CommandExecutor;
import org.activiti.engine.impl.persistence.entity.ExecutionEntity;
import org.activiti.engine.impl.persistence.entity.TaskEntity;
import org.activiti.engine.impl.pvm.PvmTransition;
import org.activiti.engine.repository.Deployment;
import org.activiti.engine.repository.Model;
import org.activiti.engine.repository.ProcessDefinition;
import org.activiti.engine.repository.ProcessDefinitionQuery;
import org.activiti.engine.runtime.ProcessInstance;
import org.activiti.engine.task.Task;
import org.activiti.engine.task.TaskQuery;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.node.ObjectNode;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;

import com.sunit.global.base.Global.TaskCompleteBeforeEvent;
import com.sunit.global.base.binder.BaseAction;
import com.sunit.global.base.exception.SLHException;
import com.sunit.global.base.service.BaseService;
import com.sunit.global.flowability.service.FlowabilityManager;
import com.sunit.global.flowvariables.po.Flowvariables;
import com.sunit.global.flowvariables.service.FlowvariablesManager;
import com.sunit.global.util.Paging;
import com.sunit.global.util.SpringContextUtils;
import com.sunit.global.util.SunitStringUtil;
import com.sunit.global.util.jsonlibConvert.DateProcessor;
import com.sunit.sysmanager.po.User;
import com.sunit.workflow.WorkflowTraceService;
import com.sunit.workflow.WorkflowUtils;
import com.sunit.workflow.ability.AbstractFlowAbility;
import com.sunit.workflow.deploy.service.DeployManager;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import net.sf.json.JsonConfig;
import net.sf.json.util.CycleDetectionStrategy;


@Controller
@RequestMapping("/deploy")
public class DeployAction extends BaseAction {

	static Logger logger = Logger.getLogger(DeployAction.class);
	@Autowired
	private RepositoryService repositoryService;

	@Autowired
	TaskService taskService;

	@Autowired
	WorkflowTraceService traceService; 

	@Autowired
	RuntimeService runtimeService;

	@Autowired
	HistoryService historyService;
	
	@Autowired 
	DeployManager deployManager;
	
	
	@Autowired
	FlowvariablesManager  flowvariablesManager ;
	
	@Autowired
	FlowabilityManager flowabilityManager;



	@RequestMapping("/deploySave.action")
	public String deploySave(PrintWriter pw, HttpServletRequest request)
			throws ParseException {
		logger.debug("DeployAction.deploySave()");
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("success", false);

		try {
			MultipartHttpServletRequest mulitRequest = (MultipartHttpServletRequest) request;
			List<MultipartFile> mFiles = mulitRequest.getFiles("deployFile");
			MultipartFile file = mFiles.get(0);
			String fileName = file.getOriginalFilename();
			// String outFileStr =
			// SpringContextUtils.getGlobal().getClusterFileServerDir(request.getSession().getServletContext().getRealPath("/"));
			String outFileStr = request.getSession().getServletContext()
					.getRealPath("/");
			String exportDir = outFileStr + "Diagrams";
			try {
				InputStream fileInputStream = file.getInputStream();
				Deployment deployment = null;

				String extension = FilenameUtils.getExtension(fileName);
				if (extension.equals("zip") || extension.equals("bar")) {
					ZipInputStream zip = new ZipInputStream(fileInputStream);
					deployment = repositoryService.createDeployment()
							.addZipInputStream(zip).deploy();
				} else {
					deployment = repositoryService.createDeployment()
							.addInputStream(fileName, fileInputStream).deploy();
				}

				List<ProcessDefinition> list = repositoryService
						.createProcessDefinitionQuery().deploymentId(
								deployment.getId()).list();

				for (ProcessDefinition processDefinition : list) {
					WorkflowUtils.exportDiagramToFile(repositoryService,
							processDefinition, exportDir);
				}

			} catch (Exception e) {
				logger
						.error(
								"error on deploy process, because of file input stream",
								e);
			}
			map.put("success", true);
		} catch (Exception e) {
			map.put("msg", e.getMessage());
			logger.error("新增部署", e);
		}
		request.setAttribute("success", map.get("success"));
		request.setAttribute("msg", map.get("msg"));
		return "/workflow/deploy/iframeResult";
	}

	@RequestMapping(value = "/getXml.action")
	public void loadByDeployment(String processDefinitionId,
			String resourceType, HttpServletResponse response) throws Exception {
		ProcessDefinition processDefinition = repositoryService
				.createProcessDefinitionQuery().processDefinitionId(
						processDefinitionId).singleResult();
		String resourceName = "";
		if (resourceType.equals("image")) {
			resourceName = processDefinition.getDiagramResourceName();
		} else if (resourceType.equals("xml")) {
			resourceName = processDefinition.getResourceName();
		}
		InputStream resourceAsStream = repositoryService.getResourceAsStream(
				processDefinition.getDeploymentId(), resourceName);
		byte[] b = new byte[1024];
		int len = -1;
		while ((len = resourceAsStream.read(b, 0, 1024)) != -1) {
			response.getOutputStream().write(b, 0, len);
		}
	}

	@RequestMapping("/loadDeployListDataGrid.action")
	public String loaddeployListDataGrid(PrintWriter pw,
			HttpServletRequest request, HttpServletRequest response,
			final int rows, int page, deploySearchPara para) {
		/*
		 * 保存两个对象，一个是ProcessDefinition（流程定义），一个是Deployment（流程部署）
		 */
		ProcessDefinitionQuery processDefinitionQuery = repositoryService
				.createProcessDefinitionQuery().orderByProcessDefinitionName().desc().orderByDeploymentId().desc();
		long count = processDefinitionQuery.count();
		Paging paging = new Paging();
		paging.setRows(rows);
		paging.setTotalRow(count);
		paging
				.setTotalPage(paging.getTotalRow() % paging.getRows() == 0 ? paging
						.getTotalRow()
						/ paging.getRows()
						: paging.getTotalRow() / paging.getRows() + 1);
		paging.setPage((int) (page > paging.getTotalPage() ? paging
				.getTotalPage() : page));
		int beginRow = (paging.getPage() - 1) * rows;
		List<ProcessDefinition> processDefinitionList = processDefinitionQuery
				.listPage(beginRow>0?beginRow:0, beginRow + paging.getRows());
		paging.setList(processDefinitionList);
		Map map = new HashMap();
		map.put("records", paging.getTotalRow());// 记录总数
		map.put("total", paging.getTotalPage());// 总页数
		map.put("page", paging.getPage());// 当前页数
		map.put("rows", paging.getList());
		JsonConfig jc = new JsonConfig();
		jc.setCycleDetectionStrategy(CycleDetectionStrategy.LENIENT);
		jc.setExcludes(new String[] { "eventSupport", "identityLinks" });
		String josnString = JSONObject.fromObject(map, jc).toString();
		pw.print(josnString);
		return null;
	}

	@RequestMapping(value = "/deployDelete.action")
	public String DeployedDelete(PrintWriter pw, String ids,
			HttpServletRequest request) {
		logger.debug("DeployAction.DeployedDelete()");
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

	/**
	 * 转换流程定义 为 model
	 * 
	 * @Title: convertToModelString
	 * @Description:
	 * @param
	 * @param processDefinitionId
	 * @param
	 * @throws UnsupportedEncodingException
	 * @param
	 * @throws XMLStreamException
	 * @return void
	 * @throws
	 * @author joye Jan 23, 2015 9:03:59 AM
	 */
	@RequestMapping("/process-to-model.action")
	public void convertToModelString(String processDefinitionId, PrintWriter pw)
			throws UnsupportedEncodingException, XMLStreamException {
		logger.debug("DeployAction.convertToModelString()");

		Map<String, Object> map = new HashMap<String, Object>();
		map.put("success", false);
		try {
			ProcessDefinition processDefinition = repositoryService
					.createProcessDefinitionQuery().processDefinitionId(
							processDefinitionId).singleResult();
			InputStream bpmnStream = repositoryService.getResourceAsStream(
					processDefinition.getDeploymentId(), processDefinition
							.getResourceName());
			XMLInputFactory xif = XMLInputFactory.newInstance();
			InputStreamReader in = new InputStreamReader(bpmnStream, "UTF-8");
			XMLStreamReader xtr = xif.createXMLStreamReader(in);
			BpmnModel bpmnModel = new BpmnXMLConverter()
					.convertToBpmnModel(xtr);

			BpmnJsonConverter converter = new BpmnJsonConverter();
			ObjectNode modelNode = converter.convertToJson(bpmnModel);
			Model modelData = repositoryService.newModel();
			modelData.setKey(processDefinition.getKey());
			modelData.setName(processDefinition.getResourceName());
			modelData.setCategory(processDefinition.getDeploymentId());

			ObjectNode modelObjectNode = new ObjectMapper().createObjectNode();
			modelObjectNode.put(ModelDataJsonConstants.MODEL_NAME,
					processDefinition.getName());
			modelObjectNode.put(ModelDataJsonConstants.MODEL_REVISION, 1);
			modelObjectNode.put(ModelDataJsonConstants.MODEL_DESCRIPTION,
					processDefinition.getDescription());
			modelData.setMetaInfo(modelObjectNode.toString());
			repositoryService.saveModel(modelData);
			repositoryService.addModelEditorSource(modelData.getId(), modelNode
					.toString().getBytes("utf-8"));
			map.put("success", true);
		} catch (Exception e) {
			map.put("success", false);
			map.put("msg", e.getMessage());
			logger.error("转换流程定义至Model：", e);
		}
		pw.print(JSONObject.fromObject(map).toString());

	}

	@RequestMapping(value = "/todoList.action")
	public void todoList(PrintWriter pw, HttpServletRequest request,
			HttpServletRequest response, final int rows, int page)
			throws Exception {
		User user = (User) request.getSession().getAttribute("user");
		List<Map<String, Object>> result = new ArrayList<Map<String, Object>>();
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd hh:mm");
		
		
		String  flowName = request.getParameter("userPlanName");
		String  taskId = request.getParameter("taskId");
		
		
		List<Task> todoList=new ArrayList();
		List<Task> toClaimList=new ArrayList(); 
		// 已经签收的任务
		 
		TaskQuery  todoListTaskQuery = taskService.createTaskQuery().taskAssignee(
				user.getAccountName());
		
		if(!SunitStringUtil.isBlankOrNull(flowName))
			todoListTaskQuery.processDefinitionNameLike("%"+flowName+"%");

		if(!SunitStringUtil.isBlankOrNull(taskId))
			todoListTaskQuery.taskId(taskId); 
		 
		
		todoList = todoListTaskQuery.list();
		
		// for (Task task : todoList) {
		// String processDefinitionId = task.getProcessDefinitionId();
		// ProcessDefinition processDefinition =
		// getProcessDefinition(processDefinitionId);
		// 
		// Map<String, Object> singleTask = packageTaskInfo(sdf, task,
		// processDefinition);
		// System.out.println("JSONObject.fromObject(singleTask)="
		// + JSONObject.fromObject(singleTask));
		// singleTask.put("status", "todo");
		// // result.add(singleTask);
		// }

		// 等待签收的任务 (基于此帐号的 accountName 与 roles)
		
		TaskQuery  toClaimListTaskQuery =  taskService.createTaskQuery().taskCandidateUser(user.getAccountName());
		
		
		if(!SunitStringUtil.isBlankOrNull(flowName))
			toClaimListTaskQuery.processDefinitionNameLike("%"+flowName+"%");
		
		
		if(!SunitStringUtil.isBlankOrNull(taskId)) 
			toClaimListTaskQuery.taskId(taskId); 
		
		
		toClaimList =toClaimListTaskQuery.active().list();
		
		
		// for (Task task : toClaimList) {
		// String processDefinitionId = task.getProcessDefinitionId();
		// ProcessDefinition processDefinition =
		// getProcessDefinition(processDefinitionId);
		//
		// Map<String, Object> singleTask = packageTaskInfo(sdf, task,
		// processDefinition);
		// System.out.println("JSONObject.fromObject(singleTask)="
		// + JSONObject.fromObject(singleTask));
		// singleTask.put("status", "claim");
		// // result.add(singleTask);
		// }
		List pagingList = new ArrayList();
		todoList.addAll(toClaimList);
		Collections.sort(todoList,new Comparator<Task>(){

			public int compare(Task o1, Task o2) {
				
				return o1.getCreateTime().getTime()< o2.getCreateTime().getTime()?1:-1;
				
			} 
		});
		
		for (Task task : todoList) {
			String processDefinitionId = task.getProcessDefinitionId();
			ProcessDefinition processDefinition = getProcessDefinition(processDefinitionId);
			Map taskMap = new HashMap();
			taskMap.put("id", task.getId());
			taskMap.put("executionID", task.getProcessInstanceId());
			taskMap.put("processID", task.getProcessInstanceId());
			taskMap.put("processDefID", task.getProcessDefinitionId());
			taskMap.put("processDefName", processDefinition.getName()); 
			taskMap.put("createTime", task.getCreateTime()); 
			taskMap.put("trace", task.getName());
			taskMap.put("isSuspend", task.isSuspended());
			taskMap.put("isAssignee", false);
			if (!StringUtils.isBlank(task.getAssignee())) {
				taskMap.put("isAssignee", task.getAssignee().equals(
						user.getAccountName()) ? true : false);
			}
			pagingList.add(taskMap);
			// String poName =processDefinition.getKey();
			// String viewPath =poName+"/"+poName+"View";
			// taskMap.put("viewPath", viewPath);
			taskMap.put("taskName", task.getName());

		}

		Paging paging = new Paging();
		paging.setRows(rows);
		paging.setTotalRow(todoList.size());
		paging
				.setTotalPage(paging.getTotalRow() % paging.getRows() == 0 ? paging
						.getTotalRow()
						/ paging.getRows()
						: paging.getTotalRow() / paging.getRows() + 1);
		paging.setPage((int) (page > paging.getTotalPage() ? paging
				.getTotalPage() : page));
		int beginRow = (paging.getPage() - 1) * rows;
  
		paging.setList(pagingList); 
		Map map = new HashMap();
		map.put("records", paging.getTotalRow());// 记录总数
		map.put("total", paging.getTotalPage());// 总页数
		map.put("page", paging.getPage());// 当前页数 
		map.put("rows", paging.getList());
		JsonConfig jc = getJsonConfig();
		jc.registerJsonValueProcessor(java.sql.Date.class, new DateProcessor());
		jc.registerJsonValueProcessor(java.util.Date.class, new DateProcessor()); 
		jc.registerJsonValueProcessor(java.sql.Timestamp.class, new DateProcessor());
		jc.setExcludes(new String[] { "eventSupport", "identityLinks" });
		String josnString = JSONObject.fromObject(map, jc).toString();
		System.out.println(josnString);
		pw.print(josnString);

	}

	@RequestMapping(value = "/process/trace.action")
	public void traceProcess(@RequestParam("pid")
	String processInstanceId, HttpServletRequest response, PrintWriter pw)
			throws Exception {
		List<Map<String, Object>> activityInfos = traceService
				.traceProcess(processInstanceId);
		String josnString = "";
		// String josnString = JSONObject.fromObject(activityInfos).toString();
		System.out.println(JSONArray.fromObject(activityInfos).toString());
		josnString = JSONArray.fromObject(activityInfos).toString();
		pw.print(josnString);

	}

	/**
	 * 读取资源，通过流程ID
	 * 
	 * @param resourceType
	 *            资源类型(xml|image)
	 * @param processInstanceId
	 *            流程实例ID
	 * @param response
	 * @throws Exception
	 */
	@RequestMapping(value = "/resource/process-instance.action")
	public void loadByProcessInstance(@RequestParam("type")
	String resourceType, @RequestParam("pid")
	String processInstanceId, HttpServletResponse response) throws Exception {
		InputStream resourceAsStream = null;
		ProcessInstance processInstance = runtimeService
				.createProcessInstanceQuery().processInstanceId(
						processInstanceId).singleResult();
		ProcessDefinition processDefinition = repositoryService
				.createProcessDefinitionQuery().processDefinitionId(
						processInstance.getProcessDefinitionId())
				.singleResult();

		String resourceName = "";
		if (resourceType.equals("image")) {
			resourceName = processDefinition.getDiagramResourceName();
		} else if (resourceType.equals("xml")) {
			resourceName = processDefinition.getResourceName();
		}
		resourceAsStream = repositoryService.getResourceAsStream(
				processDefinition.getDeploymentId(), resourceName);
		byte[] b = new byte[1024];
		int len = -1;
		while ((len = resourceAsStream.read(b, 0, 1024)) != -1) {
			response.getOutputStream().write(b, 0, len);
		}
	}

	@RequestMapping(value = "/claim.action")
	public void claim(String taskId, HttpSession session, PrintWriter pw) {
		logger.debug("DeployAction.claim()");
		Map<String, Object> map = new HashMap<String, Object>();
		User user = (User) session.getAttribute("user");
		map.put("success", false);
		try {
			taskService.claim(taskId, user.getAccountName()); // 修改为accountName
			map.put("success", true);
		} catch (Exception e) {
			map.put("success", false);
			map.put("msg", e.getMessage());
			logger.error("签收任务：", e);
		}
		pw.print(JSONObject.fromObject(map).toString());

	}

	@RequestMapping(value = "/businessView.action")
	public String businessView(String taskId, HttpServletRequest request) {
  
		logger.debug("DeployAction.businessView()");
		Map<String, Object> map = new HashMap<String, Object>();
		final Map<String, Object> variables = new HashMap<String, Object>();
		String poName = "";
		map.put("success", false);
		String POId="";
		try {
			final TaskEntity task = (TaskEntity) taskService.createTaskQuery()
					.taskId(taskId).singleResult();
//			ProcessDefinition pdef = repositoryService
//			.getProcessDefinition(task.getProcessDefinitionId());
//			CommandExecutor ce = ((TaskServiceImpl) taskService)
//					.getCommandExecutor(); 
//			ExecutionEntity execEntity = null; 
//			final List<PvmTransition> list = new ArrayList(); 
//			execEntity = (ExecutionEntity) ce.execute(new Command() { 
//				public Object execute(CommandContext commandContext) {
//					ExecutionEntity exec = task.getExecution();
//					list.addAll(exec.getActivity().getOutgoingTransitions());
//					variables.putAll(exec.getVariables());
//					return exec;
//				} 
//			});
//			Condition condition = getValidCondition(list);
//			if (condition instanceof UelExpressionCondition) {
//				org.activiti.engine.delegate.Expression exp = ((UelExpressionCondition) condition)
//						.getExpression();
//				System.out.println(exp.getExpressionText());
//				System.out.println("((JuelExpression)e).getExpressionText()="
//						+ ((JuelExpression) exp).getIdentifier());
//			}
			 
			String className = (String) runtimeService.getVariable(task.getProcessInstanceId(), "class");
			String classManager = (String) runtimeService.getVariable(task.getProcessInstanceId(), "classManager");
			POId=(String) runtimeService.getVariable(task.getProcessInstanceId(), "businessId");
			Class clazz = Class.forName(className); 
			Class<BaseService> clazzManager = (Class<BaseService>) Class
					.forName(classManager);
			Object po = SpringContextUtils.getSpringContext().getBean(
					clazzManager).get(POId);
			 
			poName = java.beans.Introspector
					.decapitalize(clazz.getSimpleName());
 
			request.setAttribute("taskName", task.getName());
			request.setAttribute("processInstanceId", task.getProcessInstanceId());

			request.setAttribute("taskId", taskId);
			request.setAttribute(poName, po);
			request.setAttribute("passVar", task.getTaskDefinitionKey()
					+ "Pass");
			request.setAttribute("TaskDefinitionKey", task.getTaskDefinitionKey());
			
//			List<Flowvariables> list  = ((AbstractFlowAbility)po).getValidFlowability().getVariables();
//			Map<String ,List<Flowvariables>>  varsMap =new HashMap();
//			for (int i = 0; i <list.size(); i++) {
//				Flowvariables fa = list.get(i); 
//				String fvTaskId=fa.getFvTaskId(); 
//				if(varsMap.get(fvTaskId)== null)
//					varsMap.put(fvTaskId, new ArrayList()); 
//				varsMap.get(fvTaskId).add(fa); 
//			}
//			request.setAttribute("AllhistoryAuditInfo", varsMap);
			map.put("success", true);
		} catch (Exception e) {
			map.put("success", false); 
			map.put("msg", e.getMessage());
			logger.error("展示流程任务：", e);   
		}   
		String target ="forward:"+"/"+ poName+ "/" + poName + "View.action?id="+POId ;
		return  target; 
		
		
//		return  "froword:"+SpringContextUtils.getGlobal().getRootName()+"/"+ poName
//		+ "/" + poName + "View"; 
//		return  SpringContextUtils.getGlobal().getRootName()+"/"+ poName
//				+ "/" + poName + "View"; 
	}
	
	
	
	 
	@RequestMapping(value = "/frontBusinessView.action")
	public String frontBusinessView(String taskId, HttpServletRequest request) {

		logger.debug("DeployAction.frontBusinessView()");
		Map<String, Object> map = new HashMap<String, Object>();
		final Map<String, Object> variables = new HashMap<String, Object>();
		String poName = "";
		map.put("success", false);
		String POId="";
		try {
			final TaskEntity task = (TaskEntity) taskService.createTaskQuery()
					.taskId(taskId).singleResult();
//			ProcessDefinition pdef = repositoryService
//					.getProcessDefinition(task.getProcessDefinitionId());
//			CommandExecutor ce = ((TaskServiceImpl) taskService)
//					.getCommandExecutor(); 
//			ExecutionEntity execEntity = null; 
//			final List<PvmTransition> list = new ArrayList(); 
//			execEntity = (ExecutionEntity) ce.execute(new Command() { 
//				public Object execute(CommandContext commandContext) {
//					ExecutionEntity exec = task.getExecution();
//					list.addAll(exec.getActivity().getOutgoingTransitions());
//					variables.putAll(exec.getVariables());
//					return exec;
//				} 
//			});
//			Condition condition = getValidCondition(list);
//			if (condition instanceof UelExpressionCondition) {
//				org.activiti.engine.delegate.Expression exp = ((UelExpressionCondition) condition)
//						.getExpression();
//				System.out.println(exp.getExpressionText());
//				System.out.println("((JuelExpression)e).getExpressionText()="
//						+ ((JuelExpression) exp).getIdentifier());
//			}
			 
			Map hmap =new HashMap();
			String className = (String) runtimeService.getVariable(task.getProcessInstanceId(), "class");
			String classManager = (String) runtimeService.getVariable(task.getProcessInstanceId(), "classManager");
			POId=(String) runtimeService.getVariable(task.getProcessInstanceId(), "businessId");
			Class clazz = Class.forName(className); 
			Class<BaseService> clazzManager = (Class<BaseService>) Class
					.forName(classManager);
			Object po = SpringContextUtils.getSpringContext().getBean(
					clazzManager).get(POId);
			 
			poName = java.beans.Introspector
					.decapitalize(clazz.getSimpleName());
 
			request.setAttribute("taskName", task.getName());
			request.setAttribute("processInstanceId", task.getProcessInstanceId());

			request.setAttribute("taskId", taskId);
			request.setAttribute(poName, po); 
			request.setAttribute("passVar", task.getTaskDefinitionKey()
					+ "Pass");
			request.setAttribute("TaskDefinitionKey", task.getTaskDefinitionKey());
			
//			Flowability f = ((AbstractFlowAbility)po).getValidFlowabilityByExcutionId(task.getExecutionId());
//			f= flowabilityManager.get(f.getId());
//			  List<Flowvariables> list= f.getVariables(); 
			  
			  List<Flowvariables> list = flowvariablesManager.find(" from Flowvariables f where f.fvProcInstId=? and fvName in ('MIPass','auditRemark') " ,  
					  task.getProcessInstanceId());
			Map<String ,Map<String,String>>  varsMap =new HashMap();
			for (int i = 0; i <list.size(); i++) {
				Flowvariables fa = list.get(i); 
				String id=fa.getFvTaskId(); 
				if(varsMap.get(id)== null)
					varsMap.put(id, new HashMap<String, String>());
				if(fa.getFvName().equals("MIPass")){
					varsMap.get(id).put("auditPass", fa.getFvContent()); 
					varsMap.get(id).put("assign", fa.getFvUserId());  
				} 
				if(fa.getFvName().equals("auditRemark")){
					varsMap.get(id).put("auditRemark", fa.getFvContent());
				}
				varsMap.get(id).put("taskName", fa.getFvAction());
				
			}  
			
//			for ( Entry<String ,List<Flowvariables>> v : varsMap.entrySet()) {
////				v.getValue().
//				v.getKey();
//				
//			} 
			
			request.setAttribute("AllhistoryAuditInfo", varsMap);
			map.put("success", true);
		} catch (Exception e) {
			map.put("success", false); 
			map.put("msg", e.getMessage()); 
			logger.error("展示流程任务：", e);   
		}  
		
		
		Map m1  = request.getParameterMap(); 
		
		
		String target ="forward:"+"/"+ poName+ "/frontApprovalView.action?id="+POId ;
		return  target; 
		
		
//		return  "froword:"+SpringContextUtils.getGlobal().getRootName()+"/"+ poName
//		+ "/" + poName + "View"; 
//		return  SpringContextUtils.getGlobal().getRootName()+"/"+ poName
//				+ "/" + poName + "View"; 
	}
	
	
	
	/**
	 * 获取当前 ProcessInstanceId 的历史数据
	* @Title: getAllFlowHistroy 
	* @Description: 
	* @param @param getProcessInstanceId
	* @param @param request
	* @param @param response
	* @param @param pw     
	* @return void  
	* @throws 
	* @author joye 
	* Apr 13, 2016 6:22:14 PM 
	 */
	@RequestMapping(value = "/getAllFlowHistory.action")   
	public void getAllFlowHistory(String processInstanceId, PrintWriter pw,
			HttpServletRequest request, HttpServletResponse response,
			final int rows, int page){
		logger.debug("DeployAction.getAllFlowHistory()");
		StringBuffer sqlBuf =  new StringBuffer();  
//		sqlBuf.append(" select L1.*,L11.fv_content as fv_content_valid  from  ( ");
//		sqlBuf.append(" select fv_action,remark,fv_taskId,fv_proc_inst_id ,max(fv_time) ,fv_userId ,fv_name ,fv_content  from  tb_flowvariables ");
//		sqlBuf.append(" where  fv_proc_inst_id=?  and fv_name = 'passVarName' ");
//		sqlBuf.append(" GROUP BY fv_action,remark,fv_taskId,fv_proc_inst_id ,fv_userId  ,fv_name   ORDER BY fv_time "); 
//		sqlBuf.append(" ) L1  JOIN tb_flowvariables L11 on  L1.fv_content  =  L11.fv_name  and L1.fv_taskId =L11.fv_taskId ");
		
		sqlBuf.append("select *  from  tb_flowvariables fv where fv.fv_proc_inst_id =? and fv_name = CONCAT(remark ,'Pass')  ORDER BY fv_time desc");
		
		 
//		List list =flowvariablesManager.excuteSQLToMap(sqlbuf.toString()); 
		
		Paging paging = flowvariablesManager.pagingSql(Paging.getCountForSQL(sqlBuf).toString(), sqlBuf.toString(),
				rows, page, null, processInstanceId) ;
		Map map = new HashMap();
		map.put("records", paging.getTotalRow());// 记录总数
		map.put("total", paging.getTotalPage());// 总页数
		map.put("page", paging.getPage());// 当前页数
		map.put("rows", paging.getList());// 记录数据 
		JsonConfig jc =getJsonConfig(); 
		jc.registerJsonValueProcessor(java.sql.Date.class, new DateProcessor());
		jc.registerJsonValueProcessor(java.util.Date.class, new DateProcessor()); 
		jc.registerJsonValueProcessor(java.sql.Timestamp.class, new DateProcessor());
		String reString = JSONObject.fromObject(map, jc).toString();
		pw.print(reString);
		
	}
	
	
	

	/**
	 * 
	 * @Title: putProcessPO 设置当前业务PO到参数context中
	 * @Description:
	 * @param
	 * @param id
	 * @param
	 * @param variables
	 * @param
	 * @param task
	 * @param
	 * @param isUpdatePO
	 *            是否更新当前PO状态
	 * @return void
	 * @throws ClassNotFoundException
	 * @throws
	 * @author joye Mar 25, 2015 1:48:34 PM
	 */ 
	public void putProcessPO( Map<String, Object> variables,
			final String executionID, boolean isUpdatePO)
			throws ClassNotFoundException {
 
		String managerClassName = (String) runtimeService.getVariable(
				executionID, "classManager");
		Class<BaseService> clazzManager; 
		clazzManager = (Class<BaseService>) Class.forName(managerClassName);
		final ExecutionEntity execEntity = (ExecutionEntity) runtimeService
				.createExecutionQuery().executionId(executionID).singleResult();
		
		
		CommandExecutor ce = ((TaskServiceImpl) taskService).getCommandExecutor(); 
		final List<PvmTransition> list = new ArrayList();   
		String businessKey  =(String) ce.execute(new  Command() {  
			public Object execute(CommandContext commandContext) { 
				return  execEntity.getProcessBusinessKey();
			}  
		}); 
		Object po = SpringContextUtils.getSpringContext().getBean(clazzManager)
				.get(businessKey);
		variables.put("po", po);
		variables.put("subOrderId",execEntity.getBusinessKey());  // 流程公式变量

		if (isUpdatePO) {
			// 通过反射用reqeust内容 替换PO数据,并从variables 中删除对应变量,最后保存. 达到修改请求的目的

		}

	}

	/**
	 * 
	 * @Title: complete
	 * @Description:
	 * @param
	 * @param poID
	 *            业务ID
	 * @param
	 * @param taskId
	 *            任务ID
	 * @param
	 * @param reqeust
	 * @param
	 * @param
	 * @param pw
	 * @return void
	 * @throws
	 * @author joye Apr 15, 2015 9:10:54 AM
	 */
	@RequestMapping(value = {
			"/complete.action",
			"/completeOfOrderConfirm.action",
			"/completeOfTaxFlowUtTaxAudit.action",
			"/completeOfTaxFlowUtTaxAccount.action",
			"/completeOfTaxFlowUtTaxDeclare.action",
			"/completeOfTaxFlowUtTaxReturn.action",
			"/completeOfTaxFlowUtTaxSignForBuyer.action",
			"/completeOfTaxFlowUtTaxSign.action",
			"/completeOfTaxFlowUtTaxConfirm.action",
			"/completeOfTaxFlowUtTaxCustomService.action",
			
			"/completeOfReportFlowUtAudit.action",
			"/completeOfReportFlowUtStart.action",
			"/completeOfReportFlowUtSucceed.action",
			"/completeOfReportFlowUtArchiving.action",
			"/completeOfReportFlowUtReturn.action",
			"/completeOfReportFlowUtSignForBuyer.action",
			"/completeOfReportFlowUtSign.action",
			"/completeOfReportFlowUtConfirm.action",
			"/completeOfReportFlowUtCustomService.action",
			
			"/completeOfOrderFlowUtAudit.action",
			"/completeOfOrderFlowUtStart.action",
			"/completeOfOrderFlowUtSucceed.action",
			"/completeOfOrderFlowUtArchiving.action",
			"/completeOfOrderFlowUtReturn.action",
			"/completeOfOrderFlowUtSignForBuyer.action",
			"/completeOfOrderFlowUtSign.action",
			"/completeOfOrderFlowUtConfirm.action",
			"/completeOfOrderFlowUtCustomService.action"
			})   
	public void complete(String taskId,
			HttpServletRequest reqeust, PrintWriter pw)  {
		logger.debug("DeployAction.complete()");
		Map<String, Object> map = new HashMap<String, Object>();
		try {
			
			TaskEntity task = (TaskEntity) taskService.createTaskQuery()  
			.taskId(taskId).singleResult();  
			TaskCompleteBeforeEvent fcbe = new TaskCompleteBeforeEvent(task);
			final ExecutionEntity execEntity = (ExecutionEntity) runtimeService 
			.createExecutionQuery().executionId(task.getExecutionId()).singleResult();
			CommandExecutor ce = ((TaskServiceImpl) taskService).getCommandExecutor(); 
			ExecutionEntity executionEntity  =(ExecutionEntity) ce.execute(new  Command() {  
				public Object execute(CommandContext commandContext) { 
					return  execEntity.getProcessInstance(); 
				}  
			});     
			fcbe.setPoId(executionEntity.getBusinessKey());
			String poName = (String) runtimeService.getVariable(
					task.getExecutionId(), "class");
			fcbe.setPOClass(Class.forName(poName));
			SpringContextUtils.getSpringContext().publishEvent(fcbe);
			if(fcbe.isFail()){
				throw new SLHException(fcbe.getMsg());
			} 
			
			
			String taskDef=  task.getTaskDefinitionKey();
			String  passVarName=taskDef+"Pass";
			Map varParts = null;  
			Map<String, Object> flowVariables   = new HashMap();
			Map<String, Object> businessVariables = new HashMap(); 
			businessVariables.putAll(reqeust.getParameterMap()); 
			for (Map.Entry<String, Object> entry : businessVariables.entrySet()) { 
				entry.setValue(SunitStringUtil.getStringbyCommaSeparate((String[]) entry.getValue())); 
				
				if (entry.getKey().equals(passVarName)) {
					entry
							.setValue(Boolean.valueOf(entry.getValue()  
									.toString()));
					flowVariables.put(passVarName, Boolean.valueOf(entry.getValue()
									.toString()));
					continue;
				} 
				
				if(entry.getKey().startsWith("act")){
					String value = entry.getValue().toString();
					//处理boolean类型的参数
					if(value.equals("true")||value.equals("false")){
						entry
						.setValue(Boolean.valueOf(entry.getValue()   
								.toString()));
						flowVariables.put(entry.getKey(), Boolean.valueOf(entry.getValue()
								.toString()));
					}else{ //其余的都做string处理
						entry
						.setValue(String.valueOf(entry.getValue()   
								.toString()));
						flowVariables.put(entry.getKey(), String.valueOf(entry.getValue()
								.toString()));
					}
				}
			}  
			flowVariables.put("businessVars", businessVariables);  
			this.putProcessPO( flowVariables, task.getExecutionId(), false);
			Authentication.setAuthenticatedUserId(task.getAssignee()); 
			deployManager.saveComplete(task.getId(), flowVariables); 
			
			map.put("success", true);
			
//		}catch (PvmException ex1) {
//			map.put("success", false); 
//			map.put("msg", "办理流程任务失败:"+ex1.getMessage()); 
//			logger.error("办理流程任务：", ex1) ; 
		}catch (SLHException ex) {
			map.put("success", false); 
			map.put("msg", "办理流程任务失败:"+ex.getMessage());
			logger.error("办理流程任务：", ex) ;
			
		} catch (Exception e) {
			map.put("success", false); 
			map.put("msg", "办理流程任务失败");
			logger.error("办理流程任务：", e);
		}
		pw.print(JSONObject.fromObject(map).toString());
	}

	public Condition getValidCondition(List<PvmTransition> list) {
		logger.debug("DeployAction.getValidCondition()");
		Condition condition = null;
		if (!list.isEmpty()) {
			for (PvmTransition pvmTransition : list) {
				System.out.println("pvmTransition=" + pvmTransition);
				condition = (Condition) pvmTransition
						.getProperty(BpmnParse.PROPERTYNAME_CONDITION);
				if (condition == null) {
					System.out
							.println("pvmTransition.getDestination().getOutgoingTransitions()="
									+ pvmTransition.getDestination()
											.getOutgoingTransitions());
					condition = getValidCondition(pvmTransition
							.getDestination().getOutgoingTransitions());
				} else {
					System.out.println("condition=" + condition);
					return condition;
				}
			}
		}
		return condition;
	}

	protected static Map<String, ProcessDefinition> PROCESS_DEFINITION_CACHE = new HashMap<String, ProcessDefinition>();

	private ProcessDefinition getProcessDefinition(String processDefinitionId) {
		ProcessDefinition processDefinition = PROCESS_DEFINITION_CACHE
				.get(processDefinitionId);
		if (processDefinition == null) {
			processDefinition = repositoryService
					.createProcessDefinitionQuery().processDefinitionId(
							processDefinitionId).singleResult();
			PROCESS_DEFINITION_CACHE
					.put(processDefinitionId, processDefinition);
		}
		return processDefinition;
	}

	

	public static class Convert {
		public static Map<String, String> map = new HashMap();
		public static Map<String, String> dbTypeMap = new HashMap();
		static {
			map.put("pname", "slhName");
			map.put("pcode", "code");
			map.put("pfromIsUnique", "fromIsUnique");
			map.put("pgridIsHide", "gridIsHide");
			map.put("prequired", "required");
			map.put("psuiselectarg", "suiselect");
			map.put("plistSearch", "listSearch");  
			map.put("pexclude", "exclude");
			map.put("pformValidate", "formValidate"); 
			

			dbTypeMap.put("string", "java.lang.String");
			dbTypeMap.put("date", "java.lang.String");
			dbTypeMap.put("int", "java.lang.Integer");
		}

		/**
		 * 表单名称转换为 slh 注释名
		 * 
		 * @Title: formNameToSLH
		 * @Description:
		 * @param
		 * @param formName
		 * @param
		 * @return
		 * @return String
		 * @throws
		 * @author joye Apr 29, 2015 4:01:19 PM
		 */
		public static String formNameToSLH(String formName) {
			int endIndex = formName.lastIndexOf("]");
			int startIndex = formName.lastIndexOf("[");
			String exactParaName = formName.substring(startIndex + 1, endIndex);
			return map.get(exactParaName) == null ? exactParaName : map
					.get(exactParaName);
		}

		public static String formNameToDBType(String formName) {

			return dbTypeMap.get(formName) == null ? dbTypeMap.get("string")
					: dbTypeMap.get(formName);
		}
		
		public static String  escapeToOracleCommnet(String comment){
			String s = comment.replace("'", "''");
			return s;
		}
		
	}

	public static class deploySearchPara {

		private java.lang.String smodel;

		public void setSmodel(java.lang.String smodel) {
			this.smodel = smodel;
		}

		public java.lang.String getSmodel() {
			return smodel;
		}

		private java.lang.String sdeviceName;

		public void setSdeviceName(java.lang.String sdeviceName) {
			this.sdeviceName = sdeviceName;
		}

		public java.lang.String getSdeviceName() {
			return sdeviceName;
		}

		private java.lang.String scode;

		public void setScode(java.lang.String scode) {
			this.scode = scode;
		}

		public java.lang.String getScode() {
			return scode;
		}

		private java.lang.String smaker;

		public void setSmaker(java.lang.String smaker) {
			this.smaker = smaker;
		}

		public java.lang.String getSmaker() {
			return smaker;
		}

		private java.lang.String svalidDate;

		public void setSvalidDate(java.lang.String svalidDate) {
			this.svalidDate = svalidDate;
		}

		public java.lang.String getSvalidDate() {
			return svalidDate;
		}

		private java.lang.String scerCode;

		public void setScerCode(java.lang.String scerCode) {
			this.scerCode = scerCode;
		}

		public java.lang.String getScerCode() {
			return scerCode;
		}

	}

	public static void main(String[] args) {

		// <property name="owner" type="java.lang.String">
		// <column name="OWNER" length="1000" />
		// </property>
		//   

		SAXReader saxReader = new SAXReader();
		ClassLoader contextClassLoader = Thread.currentThread()
				.getContextClassLoader();
		InputStream rsrc = null;
		if (contextClassLoader != null) {
			rsrc = contextClassLoader.getResourceAsStream("User.hbm.xml");
		}
		try {

			System.out.println(rsrc);
			Document document = saxReader.read(rsrc);
			Element el = (Element) document.selectSingleNode("//class");
			el.setAttributeValue("name", "code11");
			el.setAttributeValue("table", "code11");

			System.out.println(el.getName());
			List<Element> list = el.elements();
			Element newEl = el.addElement("property");
			newEl.addAttribute("name", "codeattr");
			newEl.addAttribute("type", "java.lang.String");
			Element newEl2 = newEl.addElement("column");
			newEl2.addAttribute("name", "codeattr");
			newEl2.addAttribute("length", "1000");

			for (int i = 0; i < list.size(); i++) {
				System.out.println(list.get(i).attribute(0).getName());
			}

			System.out.println(document.asXML());

		} catch (DocumentException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}
}