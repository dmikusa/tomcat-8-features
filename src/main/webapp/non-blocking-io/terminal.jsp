<!DOCTYPE html>
<%@ page session="false" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<!--[if IE 8]> <html class="no-js lt-ie9" lang="en" > <![endif]-->
<!--[if gt IE 8]><!-->
<html class="no-js" lang="en">
<!--<![endif]-->

<head>
	<meta charset="utf-8">
	<meta name="viewport" content="width=device-width">
	<title>Tomcat 8 Demos - Non-Blocking IO API</title>
	<link rel="stylesheet" href="<c:url value="/css/foundation.css"/>">
	<script src="<c:url value="/js/vendor/custom.modernizr.js"/>"></script>
</head>
<body>
	<div class="row">
		<div class="large-12 columns">
			<h2>Tomcat 8 Demos</h2>
			<nav class="breadcrumbs" style="margin-bottom: 1em;">
			  <a href="<c:url value="/" />">Home</a>
			  <a href="<c:url value="/non-blocking-io/" />">Non-Blocking IO</a>
			</nav>
		</div>
	</div>
	<div class="row">
		<div class="large-12 columns">
			<h3>Terminal</h3>
			<p>This is an example that can be run from the terminal.  It sends various character strings to 
				the EchoNbioServlet and prints the responses.  Instructions for running from the terminal
				are listed below.</p>
		</div>
		<div class="large-11 large-centered columns">
			<ol>
				<li>In a terminal, change directory to the location where you have downloaded the demo package.</li>
				<li>Run <code>mvn package</code>.</li>
				<li>Deploy the generated WAR file to your Tomcat 8 server.</li>
				<li>Run <code>mvn -q exec:java -Dexec.mainClass="com.pivotal.demos.nbio.EchoClient"</code>.</li>
				<li>The output should show the information sent to and received from the server.</li>
			</ol>
		</div>
	</div> 

	<script>
		document.write('<script src='
				+ ('__proto__' in {} ? '<c:url value="/js/vendor/zepto"/>' : '<c:url value="/js/vendor/jquery"/>')
				+ '.js><\/script>')
	</script>
	
	<script>
		var nbio = $("#nbio");
		nbio.find("#run-demo").click(function(e) {
            e.preventDefault();
            $.post('<c:url value="/non-blocking-io/EchoNbioServlet" />',
           		nbio.find("#data").val(),
                function(data) {
            		nbio.find("#result").text("Server Says: " + data);
                });
        });
	</script>

	<script src="<c:url value="/js/foundation.min.js"/>"></script>
	<script>
	    $(document).foundation();
  	</script>
</body>
</html>
