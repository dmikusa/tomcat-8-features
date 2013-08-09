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
			<h2>Tomcat 8 Demos - Non-Blocking IO API</h2>
			<p>This section demonstrates the Non-Blocking IO API which is now available in Tomcat 8 / Servlet 3.1.</p>
			<hr />
		</div>
	</div>
	<div class="row">
		<div class="large-10 columns">
			<div id="nbio-1">
				<h3>Hello World!</h3>
				<p>The typical "Hello World!" demo.  Uses an WriteListener to print 'Hello World!'.</p>
				<div>
					<button id="run-demo" class="tiny button">Run Demo</button>
				</div>
				<div>
					<textarea id="result" style="height: 5em;" placeholder="Demo output will be shown here"></textarea>
				</div>
			</div>
			<div id="nbio-2">
				<h3>The Repeater</h3>
				<p>Another common example, this demo echoes input from the user, through the server and back asynchronously.</p>
				<div class="row collapse">
					<form>
						<div class="large-1 columns">
							<button id="run-demo" class="button prefix">Say</button>					
						</div>
						<div class="large-11 columns">
							<input type="text" id="data" name="data" placeholder="something" />
						</div>
					</form>
				</div>
				<div>
					<textarea id="result" style="height: 5em;" placeholder="Demo output will be shown here"></textarea>
				</div>
			</div>
			<div id="nbio-3">
				<h3>Long Running Task</h3>
				<p>This demo shows how to execute a long running task and show the results.</p>
				<div>
					<button id="run-demo" class="tiny button">Run Demo</button>
					<progress id="progress" value="0" max="100"></progress>
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
		var nbio1 = $("#nbio-1");
		nbio1.find("#run-demo").click(function() {
			$.get('<c:url value="/non-blocking-io/HelloNbioServlet" />',
				function(data) {
				  nbio1.find("#result").text("Server Says: " + data);
				});
		});
		var nbio2 = $("#nbio-2");
		nbio2.find("#run-demo").click(function(e) {
			e.preventDefault();
			$.post('<c:url value="/non-blocking-io/EchoNbioServlet" />',
				nbio2.find("#data").val(),
    			function(data) {
					nbio2.find("#result").text("You said: " + data);	
				});
		});
		// Look at how to stream chunked response
		//  https://github.com/englercj/jquery-ajax-progress/blob/master/js/jquery.ajax-progress.js
		//  http://stackoverflow.com/questions/6789703/how-to-write-javascript-in-client-side-to-receive-and-parse-chunked-response-i
		// Response Flow [START.........(100 times)......END]
		// Update progress bar with task run time
	</script>

	<script src="<c:url value="/js/foundation.min.js"/>"></script>
	<script src="<c:url value="/js/foundation/foundation.alerts.js"/>"></script>
	<!--
	  <script src="js/foundation/foundation.js"></script>
	  <script src="js/foundation/foundation.clearing.js"></script>
	  <script src="js/foundation/foundation.cookie.js"></script>
	  <script src="js/foundation/foundation.dropdown.js"></script>
	  <script src="js/foundation/foundation.forms.js"></script>
	  <script src="js/foundation/foundation.joyride.js"></script>
	  <script src="js/foundation/foundation.magellan.js"></script>
	  <script src="js/foundation/foundation.orbit.js"></script>
	  <script src="js/foundation/foundation.reveal.js"></script>
	  <script src="js/foundation/foundation.section.js"></script>
	  <script src="js/foundation/foundation.tooltips.js"></script>
	  <script src="js/foundation/foundation.topbar.js"></script>
	  <script src="js/foundation/foundation.interchange.js"></script>
	  <script src="js/foundation/foundation.placeholder.js"></script>
	  <script src="js/foundation/foundation.abide.js"></script>
	  -->
	<script>
	    $(document).foundation();
  	</script>
</body>
</html>
