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
<%@ page import="java.util.List, be.gcroes.thesis.docproc.entity.Job" %>

<!DOCTYPE html">
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
	<h2>Jobs for <%=session.getAttribute("user") %></h2>
	<table class="table table-striped table-bordered table-condensed">
		<tr>
		<td>Job id</td>
		<td>Started on</td>
		<td>Finished on</td>
		<td>Duration</td>
		<td>Number of tasks</td>
		<td>Details</td>
		<td>Download</tr>
		<%
			EntityManagerFactory emf = EntityManagerUtil.getEntityManagerFactory();
	        EntityManager em = emf.createEntityManager();
	        String user = (String)session.getAttribute("user");
	        List<Job> jobs = em.createQuery("SELECT j FROM Job j WHERE j.user = \'" + user + "\'", Job.class).getResultList();
	        ProcessEngine processEngine = ProcessEngines.getDefaultProcessEngine();
	        HistoryService historyService = processEngine.getHistoryService();
	        SimpleDateFormat dt = new SimpleDateFormat("dd-MM-yyyy hh:mm:ss"); 
	        for(Job job : jobs){
	            HistoricProcessInstance hpi = historyService.createHistoricProcessInstanceQuery().processInstanceId(job.getActivitiJobId()).singleResult();
	            String startedOn = "NA", finishedOn = "NA", duration = "NA";
	            if(hpi != null){
	            startedOn = dt.format(hpi.getStartTime());
	            finishedOn = dt.format(hpi.getEndTime());
	            duration = (hpi.getDurationInMillis() / 1000f) + " seconds";
	            }
	            %>
	           <tr>
	           	<td> <a href="${pageContext.request.contextPath}/service/history/historic-process-instances/<%=job.getActivitiJobId()%>"> <%=job.getActivitiJobId() %> </a></td>
	           	<td> <%=startedOn%> </td>
	           	<td> <%=finishedOn%> </td>
	           	<td> <%=duration %> </td>
	           	<td> <%=job.getTasks().size()%></td>
	           	<td> <a href="${pageContext.request.contextPath}/details.jsp?jobId=<%=job.getActivitiJobId()%>">Details</a></td>
	           	<td> <a href="${pageContext.request.contextPath}/download?jobId=<%=job.getActivitiJobId()%>">Download zip</a></td>
	           </tr> 
	        <% }
		%>
	</table>
		<% if(jobs.size() == 0){
		    %> <p>No jobs found</p> 
		<% }%>
</div>
</body>
</html>