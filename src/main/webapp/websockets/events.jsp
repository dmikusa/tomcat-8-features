<!DOCTYPE html>
<%@ page session="false" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="ws" uri="/WEB-INF/tlds/websocket.tld" %>

<!--[if IE 8]> <html class="no-js lt-ie9" lang="en" > <![endif]-->
<!--[if gt IE 8]><!-->
<html class="no-js" lang="en">
<!--<![endif]-->

<head>
	<meta charset="utf-8">
	<meta name="viewport" content="width=device-width">
	<title>Tomcat 8 Demos - WebSockets API</title>
	<link rel="stylesheet" href="<c:url value="/css/foundation.css"/>">
	<style>
		#controls .columns {
			text-align: center;
			padding-left: 0.5em;
			padding-right: 0.5em;
			padding-top: 0.25em;
			background-color: rgb(246, 246, 246);
			border-style: solid;
			border-width: 1px;
			border-color: gainsboro;
			border-radius: 3px;
			margin-right: 0.9375em;
			margin-left: 0.9375em;
			width: 30%
		}
		#log {
			height: 12em; 
			resize: none; 
			readonly: true; 
			background-color: rgb(246, 246, 246);
		}
		
		#controls label {
			display: inline;
		}
	</style>
	<script src="<c:url value="/js/vendor/custom.modernizr.js"/>"></script>
</head>
<body>
	<div class="row">
		<div class="large-12 columns">
			<h2>Tomcat 8 Demos</h2>
			<nav class="breadcrumbs" style="margin-bottom: 1em;">
			  <a href="<c:url value="/" />">Home</a>
			  <a href="<c:url value="/non-blocking-io/" />">WebSockets</a>
			</nav>
		</div>
	</div>
	<div class="row">
		<div class="large-12 columns">
			<h3>Event Viewer</h3>
			<p>This is a simple example that shows the WebSocket events as they happen.</p>
		</div>
		<div id="controls" class="large-11 large-centered columns">
			<div class="row display">
				<div class="large-3 columns">
					<p>Press here to establish a WebSockets connection</p>
					<div>
						<input id="annotation" name="endpoint" type="radio" 
							   checked="checked" value="<ws:url value="/websockets/events/annotation" />" />
						<label for="annotation" style="padding-right: 1em;">Annotation</label> 
						<input id="interface" name="endpoint" type="radio" 
							   value="<ws:url value="/websockets/events/interface" />" />
						<label for="interface">Interface</label>
					</div>
					<button id="connect" class="tiny button">Connect</button>
				</div>
				<div class="large-3 columns">
					<p>Press here to send some data to the server.</p>
					<input id="send_data" type="text" placeholder="Enter something to send..." />
					<button id="send" class="tiny button">Send</button>
				</div> 
				<div class="large-3 columns">
					<p>Press here to disconnect the WebSockets connection</p>
					<button id="disconnect" class="tiny button">Disconnect</button>
				</div>
			</div>
		</div>
		<div class="large-11 large-centered columns">
			<h4>WebSockets Event Log</h4>
			<textarea id="log" readonly="readonly" disabled="disabled"></textarea>
		</div>
	</div> 

	<script>
		document.write('<script src='
				+ ('__proto__' in {} ? '<c:url value="/js/vendor/zepto"/>' : '<c:url value="/js/vendor/jquery"/>')
				+ '.js><\/script>')
	</script>
	
	<script>
		var socket;
		var log = $("#log");
		var connect = $("#connect");
		
		// Open a websockets connection
		connect.click(function() {
			// check the value of our radio group, selects between our annotation based
			//  endpoint and our interface based endpoint
			socket = new WebSocket($("#controls input[name=endpoint]:checked").val());
			
			// executes on open, logs message
			socket.onopen = function(e) {
				log.val("Connection Open");
			};
			
			// executes on close, logs message & updates scroll bar
			socket.onclose = function(e) {
				log.val(appendText("Connection Closed"));
				scrollTextAreaToBottom();
			};
			
			// executes on error, logs message & updates scroll bar
			socket.onerror = function(e) {
				log.val(appendText("Error: " + e.event));
				scrollTextAreaToBottom();
			};
			
			// executes when message received, logs message & updates scroll bar
			socket.onmessage = function(e) {
				log.val(appendText("Message: " + e.data));
				scrollTextAreaToBottom();
			};
			
			// disables the connect button
			connect.attr("disabled", "disabled");
		});
		
		// Close the websocket connection
		$("#disconnect").click(function() {
			socket.close();
			connect.removeAttr("disabled");
		});
		
		// Send data to the server via the websocket
		$("#send").click(function() {
			socket.send($("#send_data").val());
		});
		
		// -- utility functions  --//
		
		// appends text to the text area
		function appendText(textToAppend) {
			return function(i, val) {
				return val + "\n" + textToAppend;
			}
		}
		
		// move scroll bar to bottom of text area 
		function scrollTextAreaToBottom() {
			log[0].scrollTop = log[0].scrollHeight;
		}
	</script>

	<script src="<c:url value="/js/foundation.min.js"/>"></script>
	<script>
	    $(document).foundation();
  	</script>
</body>
</html>