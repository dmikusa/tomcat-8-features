package com.pivotal.demos.upgrade;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@WebServlet(urlPatterns="/protocol-upgrade/KnockKnockServlet")
public class KnockKnockServlet extends HttpServlet {

	private static final long serialVersionUID = -5033164374685708974L;

	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		// did the client properly request an upgrade?
		String upgradeProtocol = req.getHeader("Upgrade");
		if (upgradeProtocol != null) {
			if ("knock-knock".equals(upgradeProtocol)) {
				// Set the response to indicate that the server is upgrading
				resp.setStatus(HttpServletResponse.SC_SWITCHING_PROTOCOLS);
				resp.setHeader("Upgrade", "knock-knock");
				resp.setHeader("Connection", "Upgrade");

				// Configure the upgrade handler
				KnockKnockUpgradeHandler handler = req.upgrade(KnockKnockUpgradeHandler.class);

				// Customize any needed settings
				// handler.setX(..);
			} else {
				resp.sendError(HttpServletResponse.SC_BAD_REQUEST, "Specified protocol not supported");
			}
		} else {
			resp.sendError(426, "Upgrade Required");
		}
		// Exit normally, HttpUpgradeHandler will process the remainder of the request
	}

}
