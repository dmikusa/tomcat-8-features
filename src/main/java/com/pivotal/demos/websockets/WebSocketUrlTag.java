package com.pivotal.demos.websockets;

import java.io.IOException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.jsp.JspException;
import javax.servlet.jsp.tagext.TagSupport;

/**
 * Utility tag for creating websocket URLs in JSP pages.
 */
public class WebSocketUrlTag extends TagSupport {
	private static final long serialVersionUID = 5870022969801469277L;

	private String value;

	@Override
	public int doEndTag() throws JspException {
		HttpServletRequest req = (HttpServletRequest) pageContext.getRequest();
		StringBuilder sb = new StringBuilder();
		if (req.isSecure() || req.getServerPort() == 4443) {
			sb.append("wss://");
		} else {
			sb.append("ws://");
		}
		sb.append(req.getServerName());
		sb.append(":");
		sb.append(req.getServerPort());
		sb.append(req.getContextPath());
		if (! value.startsWith("/")) {
			sb.append("/");
		}
		sb.append(value);
		try {
			pageContext.getOut().print(sb.toString());
		} catch (IOException e) {
			e.printStackTrace();
		}
		return EVAL_PAGE;
	}

	public void setValue(String value) {
		if (value == null) {
			value = "/";
		}
		this.value = value;
	}
}
