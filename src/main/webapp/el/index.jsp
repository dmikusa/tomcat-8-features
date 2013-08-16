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
	<title>Tomcat 8 Demos - EL</title>
	<link rel="stylesheet" href="<c:url value="/css/foundation.css"/>">
	<script src="<c:url value="/js/vendor/custom.modernizr.js"/>"></script>
</head>
<body>
	<div class="row">
		<div class="large-12 columns">
			<h2>Tomcat 8 Demos - EL</h2>
			<p>This pages present an interactive demonstration of the Expression Language 3.0.  Enter 
				valid expression language commands into the box below or select one of the samples from
				the menu on the right.</p>
			<hr />
		</div>
	</div>
	<div class="row">
		<div class="large-9 columns">
			<div>
				<textarea id="expression" style="height: 15em; resize: none;" placeholder="Enter expression language code here"></textarea>
			</div>
			<div>
				<button id="run-demo" class="tiny button">Run</button>
			</div>
		</div>
		<div class="large-3 columns">
			<select id="templates" size="20" style="height: 19.1em">
				<option>Set</option>
				<option>List</option>
				<option>Map</option>
			</select>
		</div>
	</div> 

	<script>
		document.write('<script src='
				+ ('__proto__' in {} ? '<c:url value="/js/vendor/zepto"/>' : '<c:url value="/js/vendor/jquery"/>')
				+ '.js><\/script>')
	</script>
		
	<script>
		$("#templates").click(function(e) {
			$.get('<c:url value="/el/templates/" />' + $(e.target).val().toLowerCase() + '.txt', function(text) {
				$("#expression").val(text);
			});
		});
	</script>

	<script src="<c:url value="/js/foundation.min.js"/>"></script>
	<script>
	    $(document).foundation();
  	</script>
</body>
</html>
