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
			<h3>The Repeater</h3>
			<p>Another common example, this demo echoes input from the user, through the server and back asynchronously.</p>
		</div>
		<div class="large-11 large-centered columns">
			<div id="nbio">
				<div class="row collapse">
					<form>
						<div class="large-1 columns">
							<button id="run-demo" class="button prefix">Say</button>					
						</div>
						<div class="large-11 columns">
							<input type="text" id="data" name="data" placeholder="enter something here" />
						</div>
					</form>
				</div>
				<div>
					<textarea id="result" style="height: 5em;" placeholder="Demo output will be shown here"></textarea>
				</div>
			</div>
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
