<%@page language="java" contentType="text/html;charset=GB2312"%>
<%@page import="com.afunms.common.util.SessionConstant"%>
<%@page import="com.afunms.topology.dao.ManageXmlDao"%>
<%@page import="com.afunms.topology.model.ManageXml"%>
<%
	String rootPath = request.getContextPath();
	String fileName = (String)session.getAttribute(SessionConstant.CURRENT_SUBMAP_VIEW);
	ManageXmlDao dao = new ManageXmlDao();
	ManageXml vo = (ManageXml)dao.findByXml(fileName);
	String bg = "";
	String Title = "";
	if(vo!=null){
	    bg = vo.getTopoBg();
	    Title = vo.getTopoTitle();
	}
%> 
<html xmlns:v="urn:schemas-microsoft-com:vml">
<head>
<meta http-equiv="content-type" content="text/html; charset=gb2312" />
<meta http-equiv="pragma" content="no-cache" />
<meta http-equiv="cache-control" content="no-cache" />
<meta http-equiv="expires" content="0" />
<title>显示子图</title>
<link href="<%=rootPath%>/resource/css/topo_style.css" rel="stylesheet" type="text/css">
<script type="text/javascript" src="js/profile.js"></script>
<script type="text/javascript" src="js/global.js"></script>
<script type="text/javascript" src="js/disable.js"></script>
<script type="text/javascript" src="js/menu.js"></script>
<script type="text/javascript" src="js/map.js"></script>
<script type="text/javascript" src="js/window.js"></script>
<script type="text/javascript" src="<%=rootPath%>/js/engine.js"></script> 
<script type="text/javascript" src="<%=rootPath%>/js/util.js"></script> 
<script type="text/javascript" src="<%=rootPath%>/dwr/interface/TopoRemoteService.js"></script>
<script type="text/javascript" src="<%=rootPath%>/dwr/interface/LinkRemoteService.js"></script>
<script type="text/javascript" src="<%=rootPath%>/dwr/interface/SubMapRemoteService.js"></script>
<%
	//它控制设备名称显示信息（IP还是名字text）
	//g_viewflag在global.js中定义，默认为0，所以要在其后
	String viewflag = request.getParameter("viewflag");	
	if (viewflag == null) 
		out.print("<script type=\"text/javascript\">g_viewFlag = 0;</script>");
	else 
		out.print("<script type=\"text/javascript\">g_viewFlag = " + viewflag + ";</script>");	
%>

<script type="text/javascript" src="js/topology.js"></script>
<script type="text/javascript">
	window.onerror = new Function('return true;');		// 容错
	openProcDlg();  //显示闪屏
	var fatherXML = "<%=fileName%>";//yangjun add 关联拓扑图时获得父页xml
	function saveFile() {
		resetProcDlg();
		save();  //topoloty.js中的函数,用于保存图数据--->String串
	}
	function doInit()
	{
		loadXML("<%=rootPath%>/resource/xml/<%=fileName%>");
		
		var autoR = setInterval(autoRefresh,1000*60*2);
	}
	
	function autoRefresh()
	{
	   window.location = "showMap.jsp";
	 //  var divlayer = document.all.divLayer; 
	 //   while (divlayer.hasChildNodes()) {
	//		divlayer.removeChild(divlayer.firstChild);
	//	}
	//	loadXML("<%=rootPath%>/resource/xml/<%=fileName%>");
	}
     
    //删除示意链路
    function deleteLine(id){
        window.location = "<%=rootPath%>/submap.do?action=deleteLines&id="+id+"&xml=<%=fileName%>";
        //window.location = "<%=rootPath%>/submap.do?action=deleteDemoLink&id="+id+"&xml=<%=fileName%>";
        //alert("删除成功！");
       // autoRefresh();
    }
    //删除实体链路
    function deleteLink(id) {
        //var xml = "<%=fileName%>";
        if (window.confirm("确定删除该链路吗？")) {
            window.location = "<%=rootPath%>/submap.do?action=deleteLink&lineId="+id;
            alert("删除成功！");
	        autoRefresh();
	    }
    }
    //编辑实体链路   
    function editLink(id) {
	    var url="<%=rootPath%>/submap.do?action=readyEditLink&lineId="+id;
        showModalDialog(url,window,'dialogwidth:500px; dialogheight:430px; status:no; help:no;resizable:0');
    }
    
    //添加实体设备
    function addEquip(nodeid,nodeCategory){
        //window.location="<%=rootPath%>/submap.do?action=addEquipToSubMap&xml=<%=fileName%>&node="+nodeid+"&category="+nodeCategory;
        var xml = "<%=fileName%>";
        var url = "<%=rootPath%>/resource/xml/<%=fileName%>";
        TopoRemoteService.addEquipToMap(xml, nodeid, nodeCategory,{
				callback:function(data){
					if(data){
						addNode(nodeid,url);
					}
				}
			});
    }
    //删除实体设备
    function deleteEquip(nodeid,category){
        if (window.confirm("此操作会将该设备彻底删除,确定删除该设备吗？")) {
            window.location="<%=rootPath%>/submap.do?action=deleteEquipFromSubMap&node="+nodeid+"&xml=<%=fileName%>&category="+category;
            alert("删除成功！");
            autoRefresh();
        }
    }
    //添加示意设备(未用)
    function createGallery(){
        var a = new xWin("1",300,180,200,200,"新增图元","123");
        ShowHide("1",null);
        ShowHide("1","none");
    }
    //删除示意设备
    function deleteHintMeta(id) {
        var xml = "<%=fileName%>";
        if (window.confirm("确定删除该设备吗？")) {
            window.location = "<%=rootPath%>/submap.do?action=deleteHintMeta&nodeId="+id+"&xml="+xml;
            alert("删除成功！");
	        autoRefresh();
	    }
    }
    //只从拓扑图移除实体设备
    function removeEquip(nodeid){
        if (window.confirm("此操作会将该设备从当前拓扑图删除,确定删除该设备吗？")) {
            window.location="<%=rootPath%>/submap.do?action=removeEquipFromSubMap&xml=<%=fileName%>&node="+nodeid;
            alert("删除成功！");
            autoRefresh();
        }
    }
    //服务器设备相关应用添加
    function addApplication(nodeid,ip){
        //alert(nodeid+"_"+ip);
        window.location="<%=rootPath%>/submap.do?action=addApplications&xml=<%=fileName%>&node="+nodeid+"&ip="+ip;
        alert("获取该服务器相关应用成功！");
        autoRefresh();
    }
    //查看设备面板图
    function showpanel(ip,width,height){
        window.open("<%=rootPath%>/submap.do?action=showpanel&ip="+ip,"panelfullScreenWindow", "toolbar=no,height="+height+",width="+width + ",scrollbars=no"+"screenX=0,screenY=0");
    }
    //创建实体链路
    function addLink(direction1,linkName, maxSpeed, maxPer, xml, start_id, start_index, end_id, end_index,linetext,interf){
        var url = "<%=rootPath%>/resource/xml/<%=fileName%>";
        SubMapRemoteService.addLink(direction1,linkName, maxSpeed, maxPer, xml, start_id, start_index, end_id, end_index,linetext,interf, {
				callback:function(data){
					if(data=="error"){
						alert("实体链路创建失败！");
					} else if(data=="error1"){
					    alert("实体链路创建失败:相同端口的链路已经存在!");
					} else if(data=="error2"){
					    alert("实体链路创建失败:已经创建双链路!");
					} else {
					    if(data){
					        var arr=data.split(":");
					        if(arr[1]=="0"){
					            addlink(arr[0],url);
					        } else {
					            addAssLink(arr[0],url)
					        }
					    }
					}
				}
			});
    }
    //创建示意链路
    function addline(direction1,xml,line_name,link_width,start_id,start_x_y,s_alias,end_id,end_x_y,e_alias){
        var url = "<%=rootPath%>/resource/xml/<%=fileName%>";
        LinkRemoteService.addDemoLink(direction1,xml,line_name, link_width, start_id, start_x_y, s_alias, end_id, end_x_y, e_alias, {
				callback:function(data){
					if(data=="error"){
						alert("示意链路创建失败！");
					} else {
					    if(data){
					        addLines(data,url);
					    }
					}
				}
			});
    }
     //添加示意设备
    function addHintMeta(setting){
        var url = "<%=rootPath%>/resource/xml/<%=fileName%>";
        TopoRemoteService.addHintMeta(setting,{
				callback:function(data){
				    if(data=="error"){
						alert("添加示意图元失败！");
					} else {
						addNode(data,url);
					}
				}
			});
    }
    
     function showalert(id) {
		//window.parent.parent.opener.location="/afunms/detail/dispatcher.jsp?id="+id;
		window.parent.parent.opener.parent.window.document.getElementById('mainFrame').src="/afunms/detail/dispatcher.jsp?id="+id+"&fromtopo=true";
	}
</script>
<style>
v\:*{ behavior:url(#default#VML); }
</style>
</head>

<!--画框选择时，用的上下左右四根彩线-->
<img src="<%=rootPath%>/resource/image/topo/line_top.gif" id="imgTop" class="tmpImg" style="width:10; height:10 " />
<img src="<%=rootPath%>/resource/image/topo/line_left.gif" id="imgLeft" class="tmpImg" style="width:10; height:10 "/>
<img src="<%=rootPath%>/resource/image/topo/line_bottom.gif" id="imgBottom" class="tmpImg" style="width:10; height:10 "/>
<img src="<%=rootPath%>/resource/image/topo/line_right.gif" id="imgRight" class="tmpImg" style="width:10; height:10 "/>

<script type="text/javascript">
<!--
document.write('<form name="frmMap" method="post" action="<%=rootPath%>/submap.do?action=save">');
document.write('<body class="main_body" onLoad="hideMenuBar();doInit();" onmousewheel="window.parent.parent.document.body.scrollTop -= event.wheelDelta/2;" onmousedown="bodyDown()" onselectstart="return false" marginheight="0" marginwidth="0" topmargin="0" leftmargin="0">');	
loadMoveController();		// 加载移动控制器
loadSizeController();		// 加载大小控制器
//<div id="divTitle" align="center" style="font:oblique small-caps 900 29pt 黑体;"><%=Title%></div>
document.write('<table height="100%"><tr><td width="100%" align="left" height="100%">');
document.write('<div id="divLayer" style="width:100%;height:100%;background-position: center;background-attachment:fixed;background-repeat: no-repeat;background-image:url(<%=rootPath%>/resource/image/bg/<%=bg%>);color:black;position:absolute;top:0px;left:0px;background-color:#FFFFFF;border:#F0F8FF; 1px solid;" onmousedown="divLayerDown()" onclick="javascript:closeLineFrame();"></div>');//#000066
document.write('</td><td height="100%" width="3px" align="right"><img src="<%=rootPath%>/common/images/arrow_close.jpg" onclick="hideMenu();"></td><td align="right" height="100%">');
document.write('<div id="container-menu-bar" style="height:100%;width:200px;"></div>');
document.write('</td></tr></table>');
document.write('<input type="hidden" name="hidXml"/>');
document.write('</body></form>');
createGallery();
//-->
</script>
<script type="text/javascript">
	function hideMenu(){
		var element = document.getElementById("container-menu-bar").parentElement;
		var display = element.style.display;
		if(display == "inline"){
			hideMenuBar();
		}else{
			showMenuBar();
		}
	}
	
	function showMenuBar(){
		var element = document.getElementById("container-menu-bar").parentElement;
		element.style.display = "inline";
		document.getElementById("container-menu-bar").innerHTML="<iframe src='<%=rootPath%>/topology/network/indicatortree.jsp?treeflag=<%=viewflag%>&fromtopo=true&filename=<%=fileName%>' height='100%' width='200px'/>";
	}
	
	function hideMenuBar(){
		var element = document.getElementById("container-menu-bar").parentElement;
		element.style.display = "none";
	}
</script>
<script type="text/javascript">
<!--
// 调整 divLayer 大小
function resizeTopDiv() {
		//document.all.divLayer.style.width = maxWidth + 1024;
		//document.all.divLayer.style.height = maxHeight + 1024;
		zoomProcDlg("out");
}
	
	setTimeout("resizeTopDiv()", 1000);	
	parent.topFrame.showController(false);
	function showDevice(action) {
		parent.location = action;
	}
	
//-->
</script>
</html>
