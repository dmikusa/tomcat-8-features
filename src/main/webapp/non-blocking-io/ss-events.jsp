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
			<h3>Server Sent Events</h3>
			<p>This is an example of using Tomcat 8's Non-blocking IO support to implement <a href="http://en.wikipedia.org/wiki/Server-sent_events">server sent events</a>.</p>
		</div>
		<div class="large-11 large-centered columns">
			<div id="nbio">
				<div class="row collapse">
					<div class="large-2 columns">
						<button id="run-demo" class="button prefix">Start Listening</button>					
					</div>
					<div class="large-2 columns">
						<button id="stop-demo" class="button prefix">Stop Listening</button>
					</div>
					<div class="large-8 columns">&nbsp;</div>
				</div>
				<div>
					<textarea id="result" style="height: 20em; width: 21em;" placeholder="Demo output will be shown here"></textarea>
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
		var source = null;
		var result = $("#result");
	
		// appends text to the text area
		function appendText(textToAppend) {
			return function(i, val) {
				return ((val) ? val + "\n" : "") + textToAppend;
			};
		}
		
		// move scroll bar to bottom of text area 
		function scrollTextAreaToBottom() {
			result[0].scrollTop = result[0].scrollHeight;
		}
		
		if (!! window.EventSource) {
			$("#run-demo").click(function(event) {
				source = new EventSource('<c:url value="/non-blocking-io/ServerSentEventsServlet" />');
				source.onmessage = function(event) {
					result.val(appendText(event.data));
					scrollTextAreaToBottom();
				};
				$(event.target).attr("disabled", "disabled");
				$("#stop-demo").removeAttr("disabled");
			});
			$("#stop-demo").click(function(event) {
				source.close();
				$(event.target).attr("disabled", "disabled");
				$("#run-demo").removeAttr("disabled");
			}).attr("disabled", "disabled");
		} else {
			$("#run-demo").attr("disabled", "disabled");
			$("#result").val("Sorry, your browser does not support server-sent events.");
		}
	</script>

	<script src="<c:url value="/js/foundation.min.js"/>"></script>
	<script>
	    $(document).foundation();
  	</script>
</body>
</html>
