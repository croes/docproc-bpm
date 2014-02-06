<%@page import="org.activiti.engine.history.HistoricProcessInstance"%>
<%@page import="org.activiti.engine.ProcessEngines"%>
<%@page import="org.activiti.engine.ProcessEngine"%>
<%@page import="org.activiti.engine.HistoryService"%>
<%@page import="javax.persistence.EntityManager"%>
<%@page import="java.text.SimpleDateFormat"%>
<%@page import="be.gcroes.thesis.docproc.entity.EntityManagerUtil"%>
<%@page import="javax.persistence.EntityManagerFactory"%>
<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
    pageEncoding="ISO-8859-1"%>
<%@ page import="java.util.List, be.gcroes.thesis.docproc.entity.Job, be.gcroes.thesis.docproc.entity.Task, java.util.Map" %>

<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
<meta name="viewport" content="width=device-width, initial-scale=1.0" />
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/bootstrap.css" />
    <script src="${pageContext.request.contextPath}/js/jquery-1.10.2.js"></script>
    <script src="${pageContext.request.contextPath}/js/bootstrap.js"></script>
<title>Docproc</title>
</head>
<body style="padding-top: 50px;">

	<nav class="navbar navbar-default navbar-fixed-top navbar-inverse"
		role="navigation">
	<div class="container">
		<div class="navbar-header">
			<button type="button" class="navbar-toggle" data-toggle="collapse"
				data-target="#bs-example-navbar-collapse-1">
				<span class="sr-only">Toggle navigation</span>
				<span class="icon-bar"></span>
				<span class="icon-bar"></span>
				<span class="icon-bar"></span>
			</button>
			<a class="navbar-brand" href="${pageContext.request.contextPath}/index.jsp">Docproc</a>
		</div>
		<div class="collapse navbar-collapse"
			id="bs-example-navbar-collapse-1">
			<ul class="nav navbar-nav">
				<li><a href="${pageContext.request.contextPath}/addjob.jsp">Add job</a></li>
				<li class="active"><a href="${pageContext.request.contextPath}/history.jsp">History</a></li>
			</ul>
			<ul class="nav navbar-nav navbar-right">
				<li><a href="${pageContext.request.contextPath}/logout.jsp">Logout</a></li>
			</ul>
			<p class="navbar-text navbar-right">
				<span class="glyphicon glyphicon-user"></span><%=session.getAttribute("user")%>
			</p>
		</div>
	</div>
	</nav>
	<div class="container">
	<h2>Details for job <%=request.getParameter("jobId") %></h2>
		<%
			EntityManagerFactory emf = EntityManagerUtil.getEntityManagerFactory();
	        EntityManager em = emf.createEntityManager();
	        String user = (String)session.getAttribute("user");
	        String jobid = (String) request.getParameter("jobId");
	        Job job = em.createQuery("SELECT j FROM Job j WHERE j.activitiJobId = " + jobid + " AND j.user = \'" + user + "\'", Job.class).getSingleResult();
	        ProcessEngine processEngine = ProcessEngines.getDefaultProcessEngine();
	        HistoryService historyService = processEngine.getHistoryService();
	        SimpleDateFormat dt = new SimpleDateFormat("dd-mm-yyyy hh:mm:ss"); 
	        HistoricProcessInstance hpi = historyService.createHistoricProcessInstanceQuery().processInstanceId(job.getActivitiJobId()).singleResult();
	        String startedOn = dt.format(hpi.getStartTime());
	        String finishedOn = dt.format(hpi.getEndTime());
	        String duration = (hpi.getDurationInMillis() / 1000f) + " seconds";
	     %>
	     <h3>Job info</h3>
	     <table class="table table-striped table-bordered table-condensed">
	     	<tr><td>ID</td><td><%=job.getActivitiJobId() %></td></tr>
	     	<tr><td>Started on</td><td><%=startedOn %></td></tr>
	     	<tr><td>Started on</td><td><%=finishedOn %></td></tr>
	     	<tr><td>Duration</td><td><%=duration %></td></tr>
	     	<tr><td>Number of tasks</td><td><%=job.getTasks().size() %></td></tr>
	     	<tr><td>Template</td><td><%=job.getTemplate() %></td></tr>
	     	<tr><td>Input data</td><td><%=job.getInputdata() %></td></tr>
	     	<tr><td>Job result</td><td><a href="${pageContext.request.contextPath}/download?jobId=<%=job.getActivitiJobId()%>">Download zip</a></td></tr>
	     </table>
	     <h3>Tasks</h3>
	     <table class="table table-striped table-bordered table-condensed">
	     <tr>
	     	<td>ID</td>
	     	<td>Param values</td>
	     	<td>Result</td>
	     </tr>
	     <%
	     for(Task task : job.getTasks()){
	         %>
	           <tr>
	           	<td> <%=task.getId() %></td>
	           	<td>
	           		<ul>
	           			<%
	           			for(Map.Entry<String, String> e : task.getParams().entrySet()){ %>
	           			    <li><%=e.getKey()%>: <%=e.getValue()%></li>
	           			<%
	           			}
	           			%>
	           		</ul>
	           	<td> <a href="${pageContext.request.contextPath}/download?jobId=<%=job.getActivitiJobId()%>&taskId=<%=task.getId()%>">Download</a></td>
	           </tr> 
	        <% }
		%>
		</table>
		<% if(job.getTasks().size() == 0){
		    %> <p>No tasks found</p> 
		<% }%>
	</div>
</body>
</html>