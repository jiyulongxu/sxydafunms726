package com.afunms.polling.snmp.interfaces;

/*
 * @author hukelei@dhcc.com.cn
 *
 */

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Hashtable;
import java.util.List;
import java.util.Vector;

import com.afunms.common.util.ShareData;
import com.afunms.common.util.SnmpUtils;
import com.afunms.indicators.model.NodeGatherIndicators;
import com.afunms.monitor.executor.base.SnmpMonitor;
import com.afunms.monitor.item.base.MonitoredItem;
import com.afunms.polling.PollingEngine;
import com.afunms.polling.base.Node;
import com.afunms.polling.node.Host;
import com.afunms.polling.om.Diskcollectdata;
import com.afunms.polling.om.IpMac;
import com.afunms.topology.model.HostNode;
import com.gatherResulttosql.NetDatatempFdbRtosql;


/**
 * @author Administrator
 *
 * To change the template for this generated type comment go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
public class FdbSnmp extends SnmpMonitor {
	java.text.SimpleDateFormat sdf = new java.text.SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
	/**
	 * 
	 */
	public FdbSnmp() {
	}

	   public void collectData(Node node,MonitoredItem item){
		   
	   }
	   public void collectData(HostNode node){
		   
	   }
	/* (non-Javadoc)
	 * @see com.dhcc.webnms.host.snmp.AbstractSnmp#collectData()
	 */
	public Hashtable collect_Data(NodeGatherIndicators alarmIndicatorsNode) {
		Hashtable returnHash=new Hashtable();
		Vector fdbVector=new Vector();
		Host node = (Host)PollingEngine.getInstance().getNodeByID(Integer.parseInt(alarmIndicatorsNode.getNodeid()));
		
		try {
			Diskcollectdata diskdata=null;
			Calendar date=Calendar.getInstance();
			Hashtable ipAllData = (Hashtable)ShareData.getSharedata().get(node.getIpAddress());
			if(ipAllData == null)ipAllData = new Hashtable();
			Vector ipmacVector = new Vector();
			
			  try{
				  SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
				  com.afunms.polling.base.Node snmpnode = (com.afunms.polling.base.Node)PollingEngine.getInstance().getNodeByIP(node.getIpAddress());
				  Date cc = date.getTime();
				  String time = sdf.format(cc);
				  snmpnode.setLastTime(time);
				 // if(ipAllData == null)ipAllData = new Hashtable();
				  ipmacVector = (Vector)ipAllData.get("ipmac");
			  }catch(Exception e){
				  
			  }
			  Hashtable MACVSIP = new Hashtable();
			  if(ipmacVector != null && ipmacVector.size()>0){
				  for(int i=0;i<ipmacVector.size();i++){
					  IpMac ipmac = (IpMac)ipmacVector.get(i);
					  if(ipmac != null && ipmac.getMac() != null && ipmac.getIpaddress() != null)
							MACVSIP.put(ipmac.getMac(), ipmac.getIpaddress());
				  }
			  }
			  //---------------------------------------------------得到所有IpNetToMedia,即直接与该设备连接的ip start
//			     try
//			     {
//			        String[] oids = new String[]
//			                    {"1.3.6.1.2.1.4.22.1.1",   //1.ifIndex
//			        		     "1.3.6.1.2.1.4.22.1.2",   //2.mac
//			                     "1.3.6.1.2.1.4.22.1.3",   //3.ip
//			                     "1.3.6.1.2.1.4.22.1.4"};  //4.type
//					String[][] valueArray = null;   	  
//					try {
//						valueArray = snmp.getTableData(node.getIpAddress(),node.getCommunity(),oids);
//					} catch(Exception e){
//						valueArray = null;
//						//e.printStackTrace();
//						//SysLogger.error(node.getIpAddress() + "_CiscoSnmp");
//					}
//				   	  for(int i=0;i<valueArray.length;i++)
//				   	  {
//				   		  IpMac ipmac = new IpMac();
//				   		  for(int j=0;j<4;j++){
//				   			String sValue = valueArray[i][j];
//				   			//SysLogger.info("MAC===="+sValue);
//				   			if(sValue == null)continue;
//							if(j==0){
//								ipmac.setIfindex(sValue);
//							}else if (j==1){
//								ipmac.setMac(sValue);
//							}else if (j==2){
//								ipmac.setIpaddress(sValue);									
//							}
//				   		 }
//				   		ipmac.setIfband("0");
//				   		ipmac.setIfsms("0");
//						ipmac.setCollecttime(new GregorianCalendar());
//						ipmac.setRelateipaddr(node.getIpAddress());
//						//ipmacVector.addElement(ipmac);
//						//SysLogger.info("ARP hostip==>"+node.getIpAddress()+"=="+ipmac.getMac()+"====="+ipmac.getIpaddress());
//						if(ipmac != null && ipmac.getMac() != null && ipmac.getIpaddress() != null)
//							MACVSIP.put(ipmac.getMac(), ipmac.getIpaddress());
//				   	  }	
//			    }
//			    catch (Exception e)
//			    {
//			    	//SysLogger.error("getIpNetToMediaTable(),ip=" + address + ",community=" + community);
//			        //tableValues = null;
//			        //e.printStackTrace();
//			    }
			  
			    //---------------------------------------------------得到所有IpNetToMedia,即直接与该设备连接的ip end	
			  //---------------------------------------------------得到所有FDB,即直接与该设备连接的ip start
			     try
			     {
			    	 List fdbList = SnmpUtils.getFdbTable(node.getIpAddress(), node.getCommunity(), node.getSnmpversion(), 3, 1000*30);
			    	 if(fdbList!=null && fdbList.size()!=0){
			    		 for(int i=0;i<fdbList.size();i++){
			    			 String[] item = new String[2];
			    			 item = (String[])fdbList.get(i);
			    			 IpMac ipmac = new IpMac();
			    			 ipmac.setIfindex(item[0]);
			    			 ipmac.setMac(item[1]);
			    			 ipmac.setIfband("0");
						   	 ipmac.setIfsms("0");
						   	 ipmac.setBak(item[2]);
							 ipmac.setCollecttime(new GregorianCalendar());
							 ipmac.setRelateipaddr(node.getIpAddress());
							 if(MACVSIP != null && MACVSIP.containsKey(ipmac.getMac())){
								 ipmac.setIpaddress((String)MACVSIP.get(ipmac.getMac()));
							 }else{
								 //continue;
								ipmac.setIpaddress("");
							 }
							 //ipmac.setIpaddress("");
							 ipmac.setRelateipaddr(node.getIpAddress());//设置交换机IP
							 //SysLogger.info("FDB hostip : "+node.getIpAddress()+"  ip :"+ipmac.getIpaddress()+" ifIndex : "+ipmac.getIfindex()+" MAC : "+ipmac.getMac()) ;
							 fdbVector.add(ipmac);
			    		 }
			    	 }
			    		 //fdbVector.add(fdbList);

			    }
			    catch (Exception e)
			    {
			        //e.printStackTrace();
			    }
			  
			    //---------------------------------------------------得到所有FDBend	
			}catch(Exception e){
				//returnHash=null;
				//e.printStackTrace();
				//return null;
			}
		
		Hashtable ipAllData = (Hashtable)ShareData.getSharedata().get(node.getIpAddress());
		if(ipAllData == null)ipAllData = new Hashtable();
		ipAllData.put("fdb",fdbVector);
	    ShareData.getSharedata().put(node.getIpAddress(), ipAllData);
	    returnHash.put("fdb", fdbVector);
	    //ShareData.setIprouterdata(node.getIpAddress(), iprouterVector);
	    ipAllData=null;
	    fdbVector=null;
	    
	    
	    //把采集结果生成sql
	    NetDatatempFdbRtosql totempsql=new NetDatatempFdbRtosql();
	    totempsql.CreateResultTosql(returnHash, node);
	    
	    return returnHash;
	}
}





