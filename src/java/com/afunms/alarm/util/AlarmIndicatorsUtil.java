package com.afunms.alarm.util;

import java.util.ArrayList;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.List;
import java.util.Vector;

import com.afunms.alarm.dao.AlarmIndicatorsDao;
import com.afunms.alarm.dao.AlarmIndicatorsNodeDao;
import com.afunms.alarm.dao.IndicatorsTopoRelationDao;
import com.afunms.alarm.model.AlarmIndicators;
import com.afunms.alarm.model.AlarmIndicatorsNode;
import com.afunms.alarm.model.IndicatorsTopoRelation;
import com.afunms.common.base.BaseVo;
import com.afunms.common.util.SysLogger;
import com.afunms.indicators.model.NodeDTO;
import com.afunms.indicators.util.NodeUtil;
import com.afunms.initialize.ResourceCenter;
import com.afunms.polling.om.ProcessInfo;
import com.afunms.common.util.ShareData;




public class AlarmIndicatorsUtil {
	
	

	/**
	 * 告警阀值初始化加载
	 * @author makewen
	 * @date   Apr 1, 2011
	 */
    public static void loadAlarmIndicatorsNode(){
    	AlarmIndicatorsUtil alarmIndicatorsUtil = new AlarmIndicatorsUtil();
    	Hashtable alarmHashtable=new Hashtable();
    	try{
	    	List alarmList= getAllAlarmInicatorsNodes();
	    	for(int index=0;index<alarmList.size();index++){
	    		AlarmIndicatorsNode alarmNode=(AlarmIndicatorsNode)alarmList.get(index);
	    		String key=alarmNode.getNodeid()+":"+alarmNode.getType()+":"+alarmNode.getSubtype(); 
	    		if(alarmHashtable.containsKey(key)){ 
	    			((ArrayList)alarmHashtable.get(key)).add(alarmNode); 
	    		}
	    		else{
	    			List list=new ArrayList();
	    			list.add(alarmNode);
	    			alarmHashtable.put(key,list);
	    			
	    		}
	    	}
	    	//ResourceCenter.getInstance().setAlarmHashtable(alarmHashtable);  
	    	ShareData.setAlarmHashtable(alarmHashtable);//把告警阀值放入到数据中
		 }
    	catch(Exception e){
    		 SysLogger.error("SysInitializtion.loadAlarmIndicatorsNode()",e);
    	} 
//    	Enumeration e = alarmHashtable.keys(); 
//		while( e. hasMoreElements() ){ 
//			System.out.println( e.nextElement() ); 
//		} 
    }
	public void setAllAlarmInicatorsNodes(AlarmIndicatorsNode alarmIndicatorsNode)
	{ 
		try{
			String key=alarmIndicatorsNode.getNodeid()+":"+alarmIndicatorsNode.getType()+":"+alarmIndicatorsNode.getSubtype();
 	
			Hashtable hs=ResourceCenter.getInstance().getAlarmHashtable();
			if(hs.containsKey(key)){
				List alarmIndicatorsNodeList=(ArrayList)hs.get(key); 
				for(int index=0;index<alarmIndicatorsNodeList.size();index++){
					System.out.println(alarmIndicatorsNodeList.get(index));
				}
				for(int index=0;index<alarmIndicatorsNodeList.size();index++){
					AlarmIndicatorsNode oldvalue=(AlarmIndicatorsNode)alarmIndicatorsNodeList.get(index); 
					//判断主键如果主键一致那么直接修改内容
					if(oldvalue.getId()==alarmIndicatorsNode.getId()){ 
						alarmIndicatorsNodeList.set(index, alarmIndicatorsNode); 
						System.out.println("修改成功"); 
					} 
				} 
				for(int index=0;index<alarmIndicatorsNodeList.size();index++){
					System.out.println(alarmIndicatorsNodeList.get(index));
				}
			}
		}
		catch(RuntimeException e){
			e.printStackTrace();
		}
	}
	
	public void saveAlarmInicatorsThresholdForNode(String nodeid , String type , String subtype){
		AlarmIndicatorsDao alarmIndicatorsDao = new AlarmIndicatorsDao();
		
		List list = null;
		try {
			list = alarmIndicatorsDao.getByTypeAndSubType(type, subtype);
		} catch (RuntimeException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			alarmIndicatorsDao.close();
		}
		
		if(list !=null && list.size() > 0){
			List list2 = new ArrayList();
			for(int i = 0 ; i < list.size(); i++){
				AlarmIndicators alarmIndicators = (AlarmIndicators)list.get(i);
				AlarmIndicatorsNode alarmIndicatorsNode = createAlarmIndicatorsNodeByAlarmIndicators(nodeid, alarmIndicators);
				list2.add(alarmIndicatorsNode);
			}
			
			AlarmIndicatorsNodeDao alarmIndicatorsNodeDao = new AlarmIndicatorsNodeDao();
			try {
				alarmIndicatorsNodeDao.saveBatch(list2);
			} catch (RuntimeException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} finally{
				alarmIndicatorsNodeDao.close();
			}
		}
		
	}
	
	public void saveAlarmInicatorsThresholdForNode(String nodeid , String type , String subtype,String indiname){
		AlarmIndicatorsDao alarmIndicatorsDao = new AlarmIndicatorsDao();
		
		List list = null;
		try {
			list = alarmIndicatorsDao.getByTypeAndSubType(type, subtype,indiname);
		} catch (RuntimeException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			alarmIndicatorsDao.close();
		}
		
		if(list !=null && list.size() > 0){
			List list2 = new ArrayList();
			for(int i = 0 ; i < list.size(); i++){
				AlarmIndicators alarmIndicators = (AlarmIndicators)list.get(i);
				AlarmIndicatorsNode alarmIndicatorsNode = createAlarmIndicatorsNodeByAlarmIndicators(nodeid, alarmIndicators);
				list2.add(alarmIndicatorsNode);
			}
			
			AlarmIndicatorsNodeDao alarmIndicatorsNodeDao = new AlarmIndicatorsNodeDao();
			try {
				alarmIndicatorsNodeDao.saveBatch(list2);
			} catch (RuntimeException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} finally{
				alarmIndicatorsNodeDao.close();
			}
		}
		
	}
	
	public void saveAlarmInicatorsThresholdForNode(String ind,String nodeid){
		AlarmIndicatorsDao alarmIndicatorsDao = new AlarmIndicatorsDao();
		
		AlarmIndicators alarmIndicators = null;
		try {
			alarmIndicators = (AlarmIndicators)alarmIndicatorsDao.findByID(ind);
		} catch (RuntimeException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			alarmIndicatorsDao.close();
		}
		List list2 = new ArrayList();
		AlarmIndicatorsNode alarmIndicatorsNode = createAlarmIndicatorsNodeByAlarmIndicators(nodeid, alarmIndicators);
		list2.add(alarmIndicatorsNode);
		
		AlarmIndicatorsNodeDao alarmIndicatorsNodeDao = new AlarmIndicatorsNodeDao();
		try {
			alarmIndicatorsNodeDao.saveBatch(list2);
		} catch (RuntimeException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally{
			alarmIndicatorsNodeDao.close();
		}
	}
//	public void saveAlarmInicatorsThresholdForNodeAndOID(String nodeid , String type , String oid){
//		AlarmIndicatorsDao alarmIndicatorsDao = new AlarmIndicatorsDao();
//		
//		List list = null;
//		try {
//			list = alarmIndicatorsDao.getByTypeAndSubType(type, subtype);
//		} catch (RuntimeException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		} finally {
//			alarmIndicatorsDao.close();
//		}
//		
//		if(list !=null && list.size() > 0){
//			List list2 = new ArrayList();
//			for(int i = 0 ; i < list.size(); i++){
//				AlarmIndicators alarmIndicators = (AlarmIndicators)list.get(i);
//				AlarmIndicatorsNode alarmIndicatorsNode = createAlarmIndicatorsNodeByAlarmIndicators(nodeid, alarmIndicators);
//				list2.add(alarmIndicatorsNode);
//			}
//			
//			AlarmIndicatorsNodeDao alarmIndicatorsNodeDao = new AlarmIndicatorsNodeDao();
//			try {
//				alarmIndicatorsNodeDao.saveBatch(list2);
//			} catch (RuntimeException e) {
//				// TODO Auto-generated catch block
//				e.printStackTrace();
//			} finally{
//				alarmIndicatorsNodeDao.close();
//			}
//		}
//		
//	}
	
	
	
	public List getAlarmInicatorsThresholdForNode( String type , String subtype){
		
		List list = null;
		AlarmIndicatorsDao alarmIndicatorsNodeDao = new AlarmIndicatorsDao();
		try {
			list = alarmIndicatorsNodeDao.getByTypeAndSubType(type, subtype);
		} catch (RuntimeException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally{
			alarmIndicatorsNodeDao.close();
		}
		
		return list; 
	}
	
	
//	public List getAlarmInicatorsThresholdForNode(String nodeid , String type , String subtype){
//	
//		List list = null;
//		AlarmIndicatorsNodeDao alarmIndicatorsNodeDao = new AlarmIndicatorsNodeDao();
//		try {
//			list = alarmIndicatorsNodeDao.getByNodeIdAndTypeAndSubType(nodeid, type , subtype);
//		} catch (RuntimeException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		} finally{
//			alarmIndicatorsNodeDao.close();
//		}
//		
//		return list;
//		
//	}
	
//	public List getAlarmInicatorsThresholdForNode(String nodeid , String type , String subtype,String alarmname){ 
//		List list = null;
//		AlarmIndicatorsNodeDao alarmIndicatorsNodeDao = new AlarmIndicatorsNodeDao();
//		try {
//			list = alarmIndicatorsNodeDao.getByNodeIdAndTypeAndSubType(nodeid, type , subtype,alarmname);
//		} catch (RuntimeException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		} finally{
//			alarmIndicatorsNodeDao.close();
//		}
//		
//		return list;
//	
//	}
	 /**
	  * 从ResourceCenter得到指标阀值列表
	  * @param nodeId
	  * @param type
	  * @param subtype
	  * @param alarmname
	  * @return
	  * @author makewen
	  * @date   Apr 1, 2011
	  */
	public List getAlarmInicatorsThresholdForNode(String nodeId , String type , String subtype,String alarmname){
		List resultList=new ArrayList();
		try{
			//getAlarmInicatorsNodeFromResourceCenter 
			List list=getAlarmInicatorsThresholdForNode(nodeId,type,subtype);
			if(list!=null){
				for(int index=0;index<list.size();index++){
					AlarmIndicatorsNode alarmNode=	(AlarmIndicatorsNode)list.get(index);
					if(alarmNode.getName().equals(alarmname))
					{ 
						resultList.add(alarmNode);
					}
				}
			}
		}catch(Exception e){
			e.printStackTrace();
		}
		return resultList;  
	} 
	/**
	 * 从ResourceCenter得到指标阀值列表
	 * @param nodeId
	 * @param type
	 * @param subtype
	 * @return
	 * @author makewen
	 * @date   Apr 1, 2011
	 */
	public List getAlarmInicatorsThresholdForNode(String nodeId , String type , String subtype){ 
		String key= nodeId +":"+ type+":"+ subtype; 
		List resultList=new ArrayList();//根据Key查询得到的结果集合需要调整的 
		try{
			ShareData.getAlarmHashtable();
			//if(ShareData.getAlarmHashtable() == null) hs = new Hashtable();
			if(subtype != null && subtype.trim().length()>0){				
				if(ShareData.getAlarmHashtable().containsKey(key))
				{
					resultList=(ArrayList)ShareData.getAlarmHashtable().get(key);
				}
			}else{
				if(ShareData.getAlarmHashtable().size()>0){
					Enumeration newProEnu = ShareData.getAlarmHashtable().keys();
					while(newProEnu.hasMoreElements())
					{
						String alarmName = (String)newProEnu.nextElement();
						if(alarmName.startsWith(nodeId +":"+ type+":")){
							resultList=(ArrayList)ShareData.getAlarmHashtable().get(alarmName);
						}
					}
				}
			}

		}catch(Exception e){
			e.printStackTrace();
		}
		return resultList;  
	} 
	 /**
	  *  得到指标阀值列表nms_alarm_indicators_node中所有记录
	  * @return
	  * @author makewen
	  * @date   Apr 1, 2011
	  */
	public static List getAllAlarmInicatorsNodes(){ 
		List list = null;
		AlarmIndicatorsNodeDao alarmIndicatorsNodeDao = new AlarmIndicatorsNodeDao();
		try {
			list = alarmIndicatorsNodeDao.getAllAlarmInicatorsNodes();
		} catch (RuntimeException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally{
			alarmIndicatorsNodeDao.close();
		}
		
		return list;
		
	}  
	
	public AlarmIndicatorsNode getAlarmInicatorsNodes(String ind,String id){
		AlarmIndicatorsNodeDao alarmIndicatorsNodeDao = new AlarmIndicatorsNodeDao();
		AlarmIndicatorsNode alarmIndicatorsNode = null;
		try {
			alarmIndicatorsNode = (AlarmIndicatorsNode) alarmIndicatorsNodeDao.findByIdAndNode(ind,id);
		} catch (Exception e1) {
			e1.printStackTrace();
		} finally {
			alarmIndicatorsNodeDao.close();
		}
		return alarmIndicatorsNode;
	}

	public List<NodeDTO> getNodeListByTypeAndSubtype(String type , String subtype){
		NodeUtil nodeUtil = new NodeUtil();
		List<BaseVo> nodelist = nodeUtil.getNodeByTyeAndSubtype(type, subtype);
		List<NodeDTO> list = nodeUtil.conversionToNodeDTO(nodelist);	
		return list;
	}
	
	/**
	 * 通过 设备类型 ， 子类型和设备id 获取此设备的所有指标
	 * @param nodeid
	 * @param type
	 * @param subtype
	 */
	public List<AlarmIndicatorsNode> getAlarmIndicatorsForNode(String nodeid , String type , String subtype){
		List<AlarmIndicatorsNode> list = null;
		AlarmIndicatorsNodeDao alarmIndicatorsNodeDao = new AlarmIndicatorsNodeDao();
		try {
			list = alarmIndicatorsNodeDao.getByNodeIdAndTypeAndSubType(nodeid, type, subtype);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			alarmIndicatorsNodeDao.close();
		}
		return list;
	}
	
	public void deleteAlarmInicatorsThresholdForNode(String nodeid , String type , String subtype){
		AlarmIndicatorsNodeDao alarmIndicatorsNodeDao = new AlarmIndicatorsNodeDao();
		try {
			alarmIndicatorsNodeDao.deleteByNodeId(nodeid,  type , subtype);
		} catch (RuntimeException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally{
			alarmIndicatorsNodeDao.close();
		}
		
	}
	
	public void deleteAlarmInicatorsThresholdForNode(String nodeid){
			
		AlarmIndicatorsNodeDao alarmIndicatorsNodeDao = new AlarmIndicatorsNodeDao();
		try {
			alarmIndicatorsNodeDao.deleteByNodeId(nodeid);
		} catch (RuntimeException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally{
			alarmIndicatorsNodeDao.close();
		}
		
	}
	
	public AlarmIndicatorsNode createAlarmIndicatorsNodeByAlarmIndicators(String nodeid , AlarmIndicators alarmIndicators){
		AlarmIndicatorsNode alarmIndicatorsNode = new AlarmIndicatorsNode();
		//SysLogger.info("########################");
		String name = alarmIndicators.getName();
		String subentity = alarmIndicators.getSubentity();
		String type = alarmIndicators.getType();
		String subtype = alarmIndicators.getSubtype();
		String datatype = alarmIndicators.getDatatype();
		String moid = alarmIndicators.getMoid();
		int threshold = alarmIndicators.getThreshlod();
		String threshold_unit = alarmIndicators.getThreshlod_unit();
		int compare = alarmIndicators.getCompare();
		int compare_type = alarmIndicators.getCompare_type();
		String alarm_times = alarmIndicators.getAlarm_times();
		String alarm_info = alarmIndicators.getAlarm_info();
		String alarm_level = alarmIndicators.getAlarm_level();
		String enabled = alarmIndicators.getEnabled();
		String poll_interval = alarmIndicators.getPoll_interval();
		String interval_unit = alarmIndicators.getInterval_unit();
		String limenvalue0 = alarmIndicators.getLimenvalue0();
		String limenvalue1 = alarmIndicators.getLimenvalue1();
		String limenvalue2 = alarmIndicators.getLimenvalue2();
		String time0 = alarmIndicators.getTime0();
		String time1 = alarmIndicators.getTime1();
		String time2 = alarmIndicators.getTime2();
		String sms0 = alarmIndicators.getSms0();
		String sms1 = alarmIndicators.getSms1();
		String sms2 = alarmIndicators.getSms2();
		String way0 = alarmIndicators.getWay0();
		String way1 = alarmIndicators.getWay1();
		String way2 = alarmIndicators.getWay2();
		String category = alarmIndicators.getCategory();
		String descr = alarmIndicators.getDescr();
		String unit = alarmIndicators.getUnit();
		
		alarmIndicatorsNode.setNodeid(nodeid);
		alarmIndicatorsNode.setSubentity(subentity);
		alarmIndicatorsNode.setName(name);
		alarmIndicatorsNode.setType(type);
		alarmIndicatorsNode.setSubtype(subtype);
		alarmIndicatorsNode.setDatatype(datatype);
		alarmIndicatorsNode.setMoid(moid);
		alarmIndicatorsNode.setThreshlod(threshold);
		alarmIndicatorsNode.setThreshlod_unit(threshold_unit);
		alarmIndicatorsNode.setCompare(compare);
		alarmIndicatorsNode.setCompare_type(compare_type);
		alarmIndicatorsNode.setAlarm_times(alarm_times);
		alarmIndicatorsNode.setAlarm_info(alarm_info);
		alarmIndicatorsNode.setAlarm_level(alarm_level);
		alarmIndicatorsNode.setEnabled(enabled);
		alarmIndicatorsNode.setPoll_interval(poll_interval);
		alarmIndicatorsNode.setInterval_unit(interval_unit);
		alarmIndicatorsNode.setLimenvalue0(limenvalue0);
		alarmIndicatorsNode.setLimenvalue1(limenvalue1);
		alarmIndicatorsNode.setLimenvalue2(limenvalue2);
		alarmIndicatorsNode.setTime0(time0);
		alarmIndicatorsNode.setTime1(time1);
		alarmIndicatorsNode.setTime2(time2);
		alarmIndicatorsNode.setSms0(sms0);
		alarmIndicatorsNode.setSms1(sms1);
		alarmIndicatorsNode.setSms2(sms2);
		alarmIndicatorsNode.setWay0(way0);
		alarmIndicatorsNode.setWay1(way1);
		alarmIndicatorsNode.setWay2(way2);
		alarmIndicatorsNode.setCategory(category);
		alarmIndicatorsNode.setDescr(descr);
		alarmIndicatorsNode.setUnit(unit);
		
		return alarmIndicatorsNode;
	}
	
	public String getShowIndicators(String topoId,String indicatorsId,String sindex){
		String result = "";
		List<IndicatorsTopoRelation> relationList = null;
		IndicatorsTopoRelationDao relationDao = new IndicatorsTopoRelationDao(); 
		try {
			if(sindex!=null && sindex.length()>0){
				relationList = relationDao.findByTopoIndicatorsIdAndSindex(topoId,indicatorsId, sindex);
			}else {
				relationList = relationDao.findByTopoAndIndicatorsId(topoId,indicatorsId);
				sindex = "";
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			relationDao.close();
		}
		if(relationList!=null&&relationList.size()>0){
			result = "checked";
		}
		return result;
	}
	
	public AlarmIndicatorsNode createAlarmIndicatorsNodeByAlarmIndicators(AlarmIndicators alarmIndicators){
		AlarmIndicatorsNode alarmIndicatorsNode = new AlarmIndicatorsNode();
		//SysLogger.info("########################");
		String name = alarmIndicators.getName();
		String subentity = alarmIndicators.getSubentity();
		String type = alarmIndicators.getType();
		String subtype = alarmIndicators.getSubtype();
		String datatype = alarmIndicators.getDatatype();
		String moid = alarmIndicators.getMoid();
		int threshold = alarmIndicators.getThreshlod();
		String threshold_unit = alarmIndicators.getThreshlod_unit();
		int compare = alarmIndicators.getCompare();
		int compare_type = alarmIndicators.getCompare_type();
		String alarm_times = alarmIndicators.getAlarm_times();
		String alarm_info = alarmIndicators.getAlarm_info();
		String alarm_level = alarmIndicators.getAlarm_level();
		String enabled = alarmIndicators.getEnabled();
		String poll_interval = alarmIndicators.getPoll_interval();
		String interval_unit = alarmIndicators.getInterval_unit();
		String limenvalue0 = alarmIndicators.getLimenvalue0();
		String limenvalue1 = alarmIndicators.getLimenvalue1();
		String limenvalue2 = alarmIndicators.getLimenvalue2();
		String time0 = alarmIndicators.getTime0();
		String time1 = alarmIndicators.getTime1();
		String time2 = alarmIndicators.getTime2();
		String sms0 = alarmIndicators.getSms0();
		String sms1 = alarmIndicators.getSms1();
		String sms2 = alarmIndicators.getSms2();
		String way0 = alarmIndicators.getWay0();
		String way1 = alarmIndicators.getWay1();
		String way2 = alarmIndicators.getWay2();
		String category = alarmIndicators.getCategory();
		String descr = alarmIndicators.getDescr();
		String unit = alarmIndicators.getUnit();
		
		alarmIndicatorsNode.setSubentity(subentity);
		alarmIndicatorsNode.setName(name);
		alarmIndicatorsNode.setType(type);
		alarmIndicatorsNode.setSubtype(subtype);
		alarmIndicatorsNode.setDatatype(datatype);
		alarmIndicatorsNode.setMoid(moid);
		alarmIndicatorsNode.setThreshlod(threshold);
		alarmIndicatorsNode.setThreshlod_unit(threshold_unit);
		alarmIndicatorsNode.setCompare(compare);
		alarmIndicatorsNode.setCompare_type(compare_type);
		alarmIndicatorsNode.setAlarm_times(alarm_times);
		alarmIndicatorsNode.setAlarm_info(alarm_info);
		alarmIndicatorsNode.setAlarm_level(alarm_level);
		alarmIndicatorsNode.setEnabled(enabled);
		alarmIndicatorsNode.setPoll_interval(poll_interval);
		alarmIndicatorsNode.setInterval_unit(interval_unit);
		alarmIndicatorsNode.setLimenvalue0(limenvalue0);
		alarmIndicatorsNode.setLimenvalue1(limenvalue1);
		alarmIndicatorsNode.setLimenvalue2(limenvalue2);
		alarmIndicatorsNode.setTime0(time0);
		alarmIndicatorsNode.setTime1(time1);
		alarmIndicatorsNode.setTime2(time2);
		alarmIndicatorsNode.setSms0(sms0);
		alarmIndicatorsNode.setSms1(sms1);
		alarmIndicatorsNode.setSms2(sms2);
		alarmIndicatorsNode.setWay0(way0);
		alarmIndicatorsNode.setWay1(way1);
		alarmIndicatorsNode.setWay2(way2);
		alarmIndicatorsNode.setCategory(category);
		alarmIndicatorsNode.setDescr(descr);
		alarmIndicatorsNode.setUnit(unit);
		
		return alarmIndicatorsNode;
	}
	
}
