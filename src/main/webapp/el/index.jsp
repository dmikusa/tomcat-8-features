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
			<h2>Tomcat 8 Demos</h2>
			<nav class="breadcrumbs" style="margin-bottom: 1em;">
			  <a href="<c:url value="/" />">Home</a>
			</nav>
		</div>
	</div>
	<div class="row">
		<div class="large-12 columns">
			<h3>Expression Language Evaluator</h3>
			<p>This pages present an interactive demonstration of the Expression Language 3.0.  Enter 
				valid expression language commands into the box below or select one of the samples from
				the menu on the right.</p>
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
				<option>Assignment</option>
				<option>String Concat</option>
				<option>Static Fields</option>
				<option>List Stream</option>
				<option>Map Stream</option>
				<option>Complex Stream</option>
				<option>Map Operation</option>
				<option>FlatMap Operation</option>
				<option>Distinct Operation</option>
				<option>Sorted Operation</option>
				<option>Sorted with Comparator</option>
				<option>ForEach Operation</option>
				<option>Peek Operation</option>
				<option>Limit Operation</option>
				<option>SubStream Operation</option>
				<option>Reduce Operation</option>
				<option>Max Operation</option>
				<option>Min Operation</option>
				<option>Average Operation</option>
				<option>Sum Operation</option>
				<option>Count Operation</option>
				<option>AnyMatch Operation</option>
				<option>AllMatch Operation</option>
				<option>NoneMatch Operation</option>
				<option>FindFirst Operation</option>
				<option>Lambda Static</option>
				<option>Lambda Basic</option>
				<option>Lambda Two Args</option>
				<option>Lambda Assignment</option>
				<option>Lambda Nested</option>
			</select>
		</div>
	</div>
	<div class="row">
		<div class="large-12 columns">
			<h4>Result of EL:</h4>
			<textarea id="result" style="height: 5em; resize: vertical;" placeholder="Result from evaluation will be displayed here"></textarea>
			<h4>Output Buffer:</h4>
			<textarea id="buffer" style="height: 5em; resize: vertical;" placeholder="Any information written to 'out' will be displayed here"></textarea>
			<h4>Context Beans:</h4>
			<textarea id="beans" style="height: 5em; resize: vertical;" placeholder="Any beans that exist after excuting the EL will be displayed here"></textarea>
		</div>
	</div> 

	<script>
		document.write('<script src='
				+ ('__proto__' in {} ? '<c:url value="/js/vendor/zepto"/>' : '<c:url value="/js/vendor/jquery"/>')
				+ '.js><\/script>')
	</script>
		
	<script>
		$("#templates").click(function(e) {
			$.get('<c:url value="/el/templates/" />' + $(e.target).val().toLowerCase().replace(/ /g, '-') + '.txt', function(text) {
				$("#expression").val(text);
			});
		});
		$("#run-demo").click(function(e) {
			$.post('<c:url value="/el/ElEvaluationServlet" />',$("#expression").val(), function(result) {
				var resp = JSON.parse(result);
				$("#result").val(resp.result);
				$("#buffer").val(resp.out);
				var beans = new Array();
				for (var key in resp.context) {
					if (key == "out") {
						continue;
					}
					beans.push(key + " = " + resp.context[key]);
				}
				$("#beans").val(beans.join("\n"));
			});
		});
	</script>

	<script src="<c:url value="/js/foundation.min.js"/>"></script>
	<script>
	    $(document).foundation();
  	</script>
</body>
</html>
