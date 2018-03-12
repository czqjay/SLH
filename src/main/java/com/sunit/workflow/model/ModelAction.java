package com.sunit.workflow.model;

import java.io.PrintWriter;
import java.text.ParseException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import net.sf.json.JSONObject;
import net.sf.json.JsonConfig;
import net.sf.json.util.CycleDetectionStrategy;

import org.activiti.bpmn.converter.BpmnXMLConverter;
import org.activiti.bpmn.model.BpmnModel;
import org.activiti.editor.constants.ModelDataJsonConstants;
import org.activiti.editor.language.json.converter.BpmnJsonConverter;
import org.activiti.engine.RepositoryService;
import org.activiti.engine.repository.Deployment;
import org.activiti.engine.repository.Model;
import org.activiti.engine.repository.ModelQuery;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.node.ObjectNode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import com.sunit.global.base.binder.BaseAction;
import com.sunit.global.util.Paging;

@Controller
@RequestMapping("/model")
public class ModelAction extends BaseAction {

	static Logger logger = Logger.getLogger(ModelAction.class);
	@Autowired
	private RepositoryService repositoryService;

	@RequestMapping("/modelSave.action")
	public void modelSave(PrintWriter pw, HttpServletRequest request,
			String name, 
			String key,
			String description, 
			HttpServletResponse response) throws ParseException {
		logger.debug("ModelAction.modelSave()");
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("success", false);
		try {
				Map metaMap=new HashMap();
				metaMap.put(ModelDataJsonConstants.MODEL_NAME, name); 
				metaMap.put(ModelDataJsonConstants.MODEL_REVISION, 1); 
				metaMap.put(ModelDataJsonConstants.MODEL_DESCRIPTION, description); 
 				Model modelData = repositoryService.newModel();
				modelData.setMetaInfo(JSONObject.fromObject(metaMap).toString());
				modelData.setName(name);  
				modelData.setKey(StringUtils.defaultString(key));  
				repositoryService.saveModel(modelData);
				metaMap.clear(); 
				metaMap.put("id", "canvas"); 
				metaMap.put("resourceId", "canvas"); 
				Map meatMapNamespace = new HashMap();
				meatMapNamespace.put("namespace", "http://b3mn.org/stencilset/bpmn2.0#");
				metaMap.put("stencilset", meatMapNamespace); 
				repositoryService.addModelEditorSource(modelData.getId(),JSONObject.fromObject(metaMap).toString().getBytes("utf-8"));
				map.put("success", true); 
			//	response.sendRedirect(request.getContextPath() + "/service/editor?id="+ modelData.getId()); 
		} catch (Exception e) { 
			map.put("msg", e.getMessage());
			logger.error("新增模型", e);
		} 
	}
 
	@RequestMapping(value = "/getXml.action")
	public void loadByModelment(String processDefinitionId,
			String resourceType, HttpServletResponse response) throws Exception {
	}

	@RequestMapping("/loadModelListDataGrid.action")
	public String loadmodelListDataGrid(PrintWriter pw,
			HttpServletRequest request, HttpServletRequest response,
			final int rows, int page, modelSearchPara para) {

		ModelQuery modelQuery = repositoryService.createModelQuery()
				.orderByModelId().desc();
		long count = modelQuery.count();
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
		List<Model> modelList = modelQuery.listPage(beginRow>0?beginRow:0, beginRow
				+ paging.getRows());
		paging.setList(modelList);
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

	@RequestMapping(value = "/modelDelete")
	public String ModeledDelete(PrintWriter pw, String ids,
			HttpServletRequest request) {
		logger.debug("ModelAction.ModeledDelete()");
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("success", false);
		try {
			String[] idArr = ids.split(",");
			for (int i = 0; i < idArr.length; i++) {
				String id = idArr[i];
				 repositoryService.deleteModel(id); 
			}
			map.put("success", true);
		} catch (Exception e) {
			map.put("success", false);
			map.put("msg", e.getMessage());
			logger.error("删除模型：", e);
		}
		pw.print(JSONObject.fromObject(map).toString());
		return null;
	}
	
	
   /**
    * 根据Model部署流程
   * @Title: deploy 
   * @Description: 
   * @param @param modelId
   * @param @param redirectAttributes
   * @param @return     
   * @return String  
   * @throws 
   * @author joye 
   * Jan 15, 2015 11:35:52 AM
    */   
		   
    @RequestMapping(value = "/deploy") 
    public String deploy(String modelID,PrintWriter pw,HttpServletRequest request) {
    	logger.debug("ModelAction.deploy()");
    	Map<String, Object> map = new HashMap<String, Object>();
		map.put("success", false);  
        try {
            Model modelData = repositoryService.getModel(modelID);
            ObjectNode modelNode = (ObjectNode) new ObjectMapper().readTree(repositoryService.getModelEditorSource(modelData.getId()));
            byte[] bpmnBytes = null;

            BpmnModel model = new BpmnJsonConverter().convertToBpmnModel(modelNode); 
            bpmnBytes = new BpmnXMLConverter().convertToXML(model);
            String deployText=new String(bpmnBytes,"utf-8");
  
            String processName = modelData.getName() + ".bpmn20.xml";
            Deployment deployment = repositoryService.createDeployment().name(modelData.getName()).addString(processName,deployText).deploy();
//            redirectAttributes.addFlashAttribute("message", "部署成功，部署ID=" + deployment.getId());
            map.put("success", true);
        } catch (Exception e) {
        	map.put("success", false);
			map.put("msg", e.getMessage());
            logger.error("根据模型部署流程失败", e); 
        }
        pw.print(JSONObject.fromObject(map).toString());
		return null;
    }
	
	

	public static class modelSearchPara {

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

//	public static void main(String[] args) {
////		double d = 1000;
////		System.out.println((d * 25 * 12 + (d * 12)) * 2);
////		Map metaMap = new HashMap();
////		metaMap.put("a", "1");
////		metaMap.put("b", "2");
////		metaMap.clear();
////		metaMap.put("c", "1"); 
////		metaMap.put("d", "2");
//		
////		System.out.println(JSONObject.fromObject(metaMap).toString());
//		
//		
//		
//		
//	}
}