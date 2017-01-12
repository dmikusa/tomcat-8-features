<%@ page import="java.util.*" session="false" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<!DOCTYPE html>
<!--[if IE 8]> <html class="no-js lt-ie9" lang="en" > <![endif]-->
<!--[if gt IE 8]><!-->
<html class="no-js" lang="en">
<!--<![endif]-->

<head>
	<meta charset="utf-8">
	<meta name="viewport" content="width=device-width">
	<title>Tomcat 8 Demos</title>
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
            <h3>System Properties</h3>
            <%
            Properties p = System.getProperties();
            Enumeration keys = p.keys();
            while (keys.hasMoreElements()) {
              String key = (String)keys.nextElement();
              String value = (String)p.get(key);
              out.println(key + ": " + value + "<br/>");
            }
            %>
		</div>
	</div>
    <div class="row">
		<div class="large-12 columns">
            <h3>Environment Variables</h3>
            <%
            Map<String, String> envMap = System.getenv();
            SortedMap<String, String> sortedEnvMap = new TreeMap<String, String>(envMap);
            Set<String> keySet = sortedEnvMap.keySet();
            for (String key : keySet) {
                String value = envMap.get(key);
                out.println("[" + key + "] " + value + "<br/>");
            }
            %>
		</div>
	</div>

    <div class="row">
        <div class="large-12 columns">
            <h3>Servlet Request Object</h3>
            <%
                out.println("Auth Type: " + request.getAuthType() + "<br/>");
                out.println("Context Path: " + request.getContextPath() + "<br/>");
                out.println("Method: " + request.getMethod() + "<br/>");
                out.println("Remote User: " + request.getRemoteUser() + "<br/>");
                out.println("Query String: " + request.getQueryString() + "<br/>");
                out.println("Request URI: " + request.getRequestURI() + "<br/>");
                out.println("Servlet Path: " + request.getServletPath() + "<br/>");
                out.println("Remote Address: " + request.getRemoteAddr() + "<br/>");
                out.println("Remote Port: " + request.getRemotePort() + "<br/>");
                out.println("Secure?: " + request.isSecure() + "<br/>");
                out.println("Server Name: " + request.getServerName() + "<br/>");
                out.println("Server Port: " + request.getServerPort() + "<br/>");
            %>
        </div>
    </div>

	<script>
		document.write('<script src='
				+ ('__proto__' in {} ? '<c:url value="/js/vendor/zepto"/>' : '<c:url value="/js/vendor/jquery"/>')
				+ '.js><\/script>')
	</script>

	<script src="<c:url value="/js/foundation.min.js"/>"></script>
	<script>
	    $(document).foundation();
  	</script>
</body>
</html>
