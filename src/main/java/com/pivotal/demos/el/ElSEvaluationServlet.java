package com.pivotal.demos.el;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;

import javax.el.ELProcessor;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@WebServlet(urlPatterns="/el/ElEvaluationServlet")
public class ElSEvaluationServlet extends HttpServlet{

	private static final long serialVersionUID = 4440649931493813553L;

	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		// Read in EL as text
		StringBuilder data = new StringBuilder();
		while (req.getReader().ready()) {
			data.append(req.getReader().readLine());
		}
		String el = data.toString();

		// Create EL context & evaluate
		ELProcessor processor = new ELProcessor();

		// Defined bean "out", which give a scratch space where EL can write
		ByteArrayOutputStream out = new ByteArrayOutputStream(1024);
		processor.defineBean("out", new PrintWriter(new OutputStreamWriter(out)));

		// Execute the EL
		Object result = processor.eval(el);

		// Clean up
		out.flush();
		out.close();

		// Take EL result and write it as string to the result
		if (out.size() > 0) {
			resp.getWriter().println(out.toString("utf-8"));
		}
		if (result != null) {
			resp.getWriter().println(result.toString());
		}
	}

}
