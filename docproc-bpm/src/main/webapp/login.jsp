<!DOCTYPE html>
<html>
<head>
<meta charset="ISO-8859-1" />
<title>Docproc document processing</title>
<meta name="viewport" content="width=device-width, initial-scale=1.0" />
<link rel="stylesheet" href="${pageContext.request.contextPath}/css/bootstrap.css" />
<script src="${pageContext.request.contextPath}/js/jquery-1.10.2.js"></script>
<script src="${pageContext.request.contextPath}/js/bootstrap.js"></script>
</head>
<body>
	<div class="container">
		<h1>Docproc</h1>
		<form id="loginForm" method="post" action="${pageContext.request.contextPath}/j_security_check">
			<label for="username" >Username:</label>
			<input id="username" class="form-control" type="text" name="j_username" />
			
			<label for="password" >Password:</label>
			<input id="password" class="form-control" type="password" name="j_password" />
			
			<div style="text-align:center;margin-top:10px;">
				<input type="submit"  id="loginbtn" value="Login" />
			</div>
		</form>
	</div>
</body>
</html>