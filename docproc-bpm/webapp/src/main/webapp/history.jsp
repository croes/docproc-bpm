<%@page import="javax.persistence.EntityManager"%>
<%@page import="java.text.SimpleDateFormat"%>
<%@page import="be.gcroes.thesis.docproc.entity.EntityManagerUtil"%>
<%@page import="javax.persistence.EntityManagerFactory"%>
<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
    pageEncoding="ISO-8859-1"%>
<%@ page import="java.util.List, be.gcroes.thesis.docproc.entity.Job" %>
<%@taglib prefix="joda" uri="http://www.joda.org/joda/time/tags" %>

<!DOCTYPE html>
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
				<span class="glyphicon glyphicon-user"></span><%=request.getUserPrincipal().getName()%>
			</p>
		</div>
	</div>
	</nav>
	<div class="container">
	<h2>Jobs for <%=request.getUserPrincipal().getName() %></h2>
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
	        String user = request.getUserPrincipal().getName();
	        List<Job> jobs = em.createQuery("SELECT j FROM Job j WHERE j.user = \'" + user + "\'", Job.class).getResultList();
	        for(Job job : jobs){
	            String duration = job.getDurationInSeconds() != -1 ? job.getDurationInSeconds() + " seconds" : "NA";
	            %>
	           <tr>
	           	<td> <%=job.getId() %></td>
	           	<td> <joda:format value="${job.startTime}" /> </td>
	           	<td> <joda:format value="${job.endTime}" /> </td>
	           	<td> <%=duration %> </td>
	           	<td> <%=job.getTasks().size()%></td>
	           	<td> <a href="${pageContext.request.contextPath}/details.jsp?jobId=<%=job.getId()%>">Details</a></td>
	           	<td> <a href="${pageContext.request.contextPath}/download?jobId=<%=job.getId()%>">Download zip</a></td>
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