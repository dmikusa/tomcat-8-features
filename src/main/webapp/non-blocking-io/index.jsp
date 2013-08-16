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
                <h3>Random Data</h3>
                <p>Ask the server for some random characters.</p>
                <div class="row collapse">
                    <form>
                        <div class="large-5 columns">
                            <button id="run-demo" class="button prefix">How many characters do you want?</button>
                        </div>
                        <div class="large-7 columns">
                            <input type="text" id="data" name="data" placeholder="???" />
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
	    function postAndGetResult(url, block) {
	        block.find("#run-demo").click(function(e) {
	            e.preventDefault();
	            $.post(url,
	                block.find("#data").val(),
	                function(data) {
	                    block.find("#result").text("You said: " + data);
	                });
	        });
	    }
		var nbio1 = $("#nbio-1");
		nbio1.find("#run-demo").click(function() {
			$.get('<c:url value="/non-blocking-io/HelloNbioServlet" />',
				function(data) {
				  nbio1.find("#result").text("Server Says: " + data);
				});
		});
		postAndGetResult('<c:url value="/non-blocking-io/EchoNbioServlet" />', $("#nbio-2"));
		postAndGetResult('<c:url value="/non-blocking-io/RandomDataNbioServlet" />', $("#nbio-3"));
	</script>

	<script src="<c:url value="/js/foundation.min.js"/>"></script>
	<script>
	    $(document).foundation();
  	</script>
</body>
</html>
