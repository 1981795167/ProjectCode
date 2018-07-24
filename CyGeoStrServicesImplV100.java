package com.mes.business.service.impl;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;

import com.mes.business.service.IBusinessServices;
import com.mes.business.service.ICyGeoStrServices;
import com.mes.common.ErrorCodeConstant;
import com.mes.common.bean.PurviewUtils;
import com.mes.common.bean.SessionUser;
import com.mes.customexception.AnalysisApiException;
import com.mes.customexception.ApiException;
import com.mes.framework.core.remoting.client.Dispatcher;
import com.mes.framework.core.util.DateUtil;
import com.mes.framework.core.util.Md5;
import com.mes.framework.core.util.MoneyUtil;
import com.mes.util.CacheUtils;
import com.mes.util.ControllerUtils;
import com.mes.util.DateUtils;
import com.mes.util.KzBusinessConfigBean;
import com.mes.util.KzConfigBean;
import com.mes.util.StringUtil;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
/**
 * app 接口业务处理
 * @author Liuxf
 *
 */

@Service("cyGeoStrServicesImplV100")
public class CyGeoStrServicesImplV100 implements ICyGeoStrServices {
	
	@Resource(name = "memberDispacher")
	private Dispatcher memberDispacher;
		
	/**
	 * 获取客户列表
	 * 
	 * @param param
	 * @return
	 * @throws ApiException
	 */
	@SuppressWarnings("unchecked")
	@Override
	public String getCyGeoStrList(Map<String, String> paramJson) throws ApiException {
		Map<Object, Object> resultMap = new HashMap<Object, Object>();
		Map<Object, Object> dataMap = new HashMap<Object, Object>();
		try {
			if (paramJson == null || paramJson.size() == 0) {// 必要参数为空
				throw new ApiException(ErrorCodeConstant.REQUEST_MUST_PARAMS);
			} else {
				String token = StringUtil.trim(paramJson.get("token"));
//				SessionUser su = ControllerUtils.getUserToken(token);
//				if (su == null) {
//					throw new ApiException(ErrorCodeConstant.REQUEST_TOKEN_ERROR);
//				}
				Map<Object, Object> condition = new HashMap<Object, Object>();
				/*String paramStr = StringUtil.trim(paramJson.get("param"));
				int pageIndex = 1;
				int pageSize = 10;
				String _quickSearch = "";
				String filterLevel = "";
				String filterState = "";
				String getHXInfo = "-1";
				List<Map<Object, Object>> businessList = new ArrayList<Map<Object, Object>>();
				condition.put("op", "getKzBusinessAccountList");
				if (StringUtil.hasText(paramStr)) {
					Map<String, String> param = JSONObject.fromObject(paramStr);
					pageIndex = StringUtil.getAsInt(StringUtil.trim(param.get("pageIndex")), 1);
					pageSize = StringUtil.getAsInt(StringUtil.trim(param.get("pageSize")), 10);
					getHXInfo = StringUtil.trim(param.get("getHXInfo"));
					filterLevel = StringUtil.trim(param.get("filterLevel"));
					filterState = StringUtil.trim(param.get("filterState"));
					String filterSupervisorId = StringUtil.trim(param.get("filterSupervisorId"));
					if (StringUtil.hasText(filterState)) {
						condition.put("state", filterState);
					}
					if (StringUtil.hasText(filterLevel)) {
						condition.put("level", filterLevel);
					}
					if (StringUtil.hasText(filterSupervisorId)) {
						condition.put("supervisorId", filterSupervisorId);
					}
					condition.put("topBid", "57");
					// 处理快捷查询
					_quickSearch = StringUtil.trim(param.get("_quickSearch"));
					if (StringUtil.hasText(_quickSearch)) {
						condition.put("_quickSearch", _quickSearch);
					}
				}
				condition.put("pageIndex", pageIndex - 1);
				condition.put("pageSize", pageSize);
				condition.put("rowCount", "1");*/
				condition.put("op", "getCyGeographicalStructureList");
				
				Map<Object,Object> queryFilter = new HashMap<Object,Object>();
				queryFilter.put("isDel", 0);//未删除的
				condition.put("queryFilter", queryFilter);
				List<Map<Object, Object>> cyGeoStrList = new ArrayList<Map<Object, Object>>();
				Map<Object, Object> model = ControllerUtils.excute("cyGeographicalStructureAction", condition, memberDispacher);
				cyGeoStrList = (List<Map<Object, Object>>) model.get("data");
				//int rowCount = StringUtil.getAsInt(StringUtil.trim(model.get("rowCount")));
				/*int pageCount = rowCount % pageSize == 0 ? rowCount / pageSize : rowCount / pageSize + 1;
				dataMap.put("filterLevel", filterLevel);
				dataMap.put("filterState", filterState);
				dataMap.put("pageCount", pageCount);
				dataMap.put("pageIndex", pageIndex);
				dataMap.put("rowCount", rowCount);
				dataMap.put("_quickSearch", _quickSearch);*/
				List<Map<Object,Object>> proList =  new ArrayList<Map<Object,Object>>();
				if (cyGeoStrList == null) {
					cyGeoStrList = new ArrayList<Map<Object, Object>>();
				} else{
					
					List<Map<Object,Object>> cityList =  new ArrayList<Map<Object,Object>>();
					List<Map<Object,Object>> areaList =  new ArrayList<Map<Object,Object>>();
					for (int i = 0, len = cyGeoStrList.size(); i < len; i++) {
						Map<Object, Object> tempMap = cyGeoStrList.get(i);
						//String id = StringUtil.trim(tempMap.get("id"));
						//清空不必要的key 
						Iterator<Object> iterator = tempMap.keySet().iterator();
						while(iterator.hasNext()){
							Object key = iterator.next();
							if( !("parentid".equals(key) || "id".equals(key) || "name".equals(key)) ){
								iterator.remove();
							}
						}
						/*for(Entry<Object,Object> e: ){
							if( !("parentid".equals(e.getKey()) || "id".equals(e.getKey()) || "name".equals(e.getKey())) ){
								tempMap.remove(e.getKey());
							}
						}*/
						String pid = StringUtil.trim(tempMap.get("parentid"));
						if("-1".equals(pid)){//第一层节点的父Id为-1
							proList.add(tempMap);
						}
						/*String name = StringUtil.trim(tempMap.get("name"));
						tempMap.putAll(ControllerUtils.getCustomerIdInCache(uid, aid, "57", busiCompName, "2"));*/
					}
					cyGeoStrList.removeAll(proList);//只剩市，区
					
					for(int i = 0; i < cyGeoStrList.size(); i++){
						Map<Object, Object> tempMap = cyGeoStrList.get(i);
						Map<Object, Object> cityMap = new HashMap<Object,Object>() ;
						String pid = StringUtil.trim(tempMap.get("parentid"));
						for(int j = 0, len = proList.size(); j < len; j++){
							String id = StringUtil.trim(proList.get(j).get("id"));
							if(pid.equals(id)){
								cityList.add(tempMap);
								//cityMap.put("children", cityList);
								//proList.get(j).put("children", cityList);
							}
						}
					}
					cyGeoStrList.removeAll(cityList);//只剩区
					areaList.addAll(cyGeoStrList);
					
					//以上先分别取出省、市、区 放到 proList，cityList,areaList 中
					//然后再 构造 json 形如：
					/*广东{ 深圳{南山，福田} }, 广州{广区} }*/
					
					for(int i = 0; i < proList.size(); i++){
						String proid = StringUtil.trim(proList.get(i).get("id"));
						List<Map<Object,Object>> cList = new ArrayList<Map<Object,Object>>();
						for(int j = 0; j < cityList.size(); j++){
							String cid = StringUtil.trim(cityList.get(j).get("id"));
							String cpid = StringUtil.trim(cityList.get(j).get("parentid"));
							if(proid.equals(cpid)){
								List<Map<Object,Object>> aList = new ArrayList<Map<Object,Object>>();
								for(int k = 0; k < areaList.size(); k++){
									String apid = StringUtil.trim(areaList.get(k).get("parentid"));
									if(cid.equals(apid)){
										areaList.get(k).put("level", 3);
										areaList.get(k).put("hasChild", "-1");
										  aList.add(areaList.get(k));
										  
									}
								}
								cityList.get(j).put("childeList", aList);
								cityList.get(j).put("level", 2);
								if(aList.size() > 0)
									cityList.get(j).put("hasChild", "1");
								else
									cityList.get(j).put("hasChild", "-1");
								cList.add(cityList.get(j));
							}
							
						}
						proList.get(i).put("childeList", cList);
						proList.get(i).put("level", 1);
						if(cList.size() > 0)
							proList.get(i).put("hasChild", "1");
						else
							proList.get(i).put("hasChild", "-1");
					}
					
					//System.out.println(proList.toString());
					
				}
				dataMap.put("cyGeoStrList", proList);
				
			}
		} catch (Exception e) {// 调用数据库接口错误
			resultMap.put("state", "1");
			throw new ApiException(AnalysisApiException.getExceptionStr(e));
		}
		resultMap.put("data", dataMap);
		resultMap.put("state", "0");//数据请求成功
		return JSONObject.fromObject(resultMap).toString();

	}

	

	
}
