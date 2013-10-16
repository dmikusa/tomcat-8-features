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
			  <a href="<c:url value="/websockets/" />">WebSockets</a>
			</nav>
		</div>
	</div>
	<div class="row">
		<div class="large-12 columns">
			<h3>Event Viewer</h3>
			<p>This demo shows sub-protocol negotiation in action.  Select one of the 
				protocols:  stomp, wamp or smtp.  Note that while none of the protocols are 
				technically supported, the back-end end-point is setup to negotiate support 
				for both stomp and wamp.  Selecting either of those should result in a socket 
				being created, while selecting smtp should result in an error.</p>
		</div>
		<div id="controls" class="large-11 large-centered columns">
			<div class="row display">
				<div class="large-3 columns">
					<p>Press here to establish a WebSockets connection</p>
					<button id="connect" class="tiny button">Connect</button>
				</div>
				<div class="large-3 columns">
					<p>Select a sub-protocol to use.</p>
					<input type="radio" name="protocol" id="stomp" value="stomp" />
					<label for="stomp">Stomp</label> | 
					<input type="radio" name="protocol" id="wamp" value="wamp" />
					<label for="wamp">Wamp</label> | 
					<input type="radio" name="protocol" id="smtp" value="smtp" />
					<label for="smtp">SMTP</label>
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
			var url = "<ws:url value="/websockets/protocol" />";
			var subProtocol = $("input[type='radio'][name='protocol']:checked").val();
			
			if (subProtocol) {
				socket = new WebSocket(url, subProtocol);
			} else {
				socket = new WebSocket(url);
			}
			
			// disables the connect button
			connect.attr("disabled", "disabled");
			log.val();
			
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
				connect.removeAttr("disabled");
			};
			
			// executes when message received, logs message & updates scroll bar
			socket.onmessage = function(e) {
				log.val(appendText("Message: " + e.data));
				scrollTextAreaToBottom();
			};
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