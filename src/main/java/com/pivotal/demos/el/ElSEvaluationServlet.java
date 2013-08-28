package com.pivotal.demos.el;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.lang.reflect.Field;
import java.util.Calendar;
import java.util.Date;
import java.util.Map;

import javax.el.ELException;
import javax.el.ELProcessor;
import javax.el.StandardELContext;
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
		StringBuilder elScript = new StringBuilder();
		while (req.getReader().ready()) {
			elScript.append(req.getReader().readLine());
		}

		// Trim a trailing ';', if it exists
		if (';' == elScript.charAt(elScript.length() - 1)) {
			elScript.deleteCharAt(elScript.length() - 1);
		}

		// Create EL context & evaluate
		ELProcessor processor = new ELProcessor();

		// Defined bean "out", which give a scratch space where EL can write
		ByteArrayOutputStream buf = new ByteArrayOutputStream(1024);
		PrintWriter out = new PrintWriter(new OutputStreamWriter(buf));
		processor.defineBean("out", out);

		// Define bean "person", which allows access to bean methods
		Calendar cal = Calendar.getInstance();
		cal.set(1946, Calendar.JULY, 6, 0, 0, 0);
		Person person = new Person("Sylvester Stallone", "Unknown", 67, cal.getTime());
		processor.defineBean("person", person);

		// Import some common stuff
		processor.getELManager().importClass(Date.class.getName());

		// Execute the EL
		boolean status;
		Object result;
		try {
			result = processor.eval(elScript.toString());
			status = true;
		} catch (ELException ex) {
			result = ex;
			status = false;
		}

		// Clean up
		out.flush();
		out.close();

		// Take EL result, out buffer and context.  Write them as JSON.
		resp.getWriter().print("{" +
			"\"out\": \"" + jsonEncode(buf.toString("utf-8")) + "\", " +
			"\"error\": \"" + (! status) + "\", " +
			"\"result\": \"" + ((result != null) ? jsonEncode(result.toString()) : "") + "\", " +
			"\"context\": " + contextToJsonString(processor.getELManager().getELContext()) +
		"}");
	}

	/**
	 * Utility function to convert StandardELContext's localBeans to JSON
	 *
	 * @param context
	 * @return	local beans as JSON
	 */
	@SuppressWarnings("unchecked")
	private String contextToJsonString(StandardELContext context) {
		StringBuilder sb = new StringBuilder();
		sb.append("{");
		Field f;
		Map<String,Object> beans;
		try {
			f = context.getClass().getDeclaredField("localBeans");
			f.setAccessible(true);
			beans = (Map<String,Object>) f.get(context);
			for (Map.Entry<String, Object> bean : beans.entrySet()) {
				sb.append("\"" + jsonEncode(bean.getKey()) + "\": \""
							   + jsonEncode(bean.getValue().toString()) + "\", ");
			}
		} catch (NoSuchFieldException | IllegalArgumentException | IllegalAccessException e) {
			// Ignore
		}
		sb.delete(sb.length() - 2, sb.length());
		sb.append("}");
		return sb.toString();
	}

	/**
	 * Basic method for escaping a string to be included in JSON.  Does not completely
	 * escape the string as per the JSON RFC.
	 *
	 * @param str	string to escape
	 * @return		escaped string
	 */
	private String jsonEncode(String str) {
		return str.replaceAll("\\\\", "\\\\\\\\")
				  .replaceAll("\\\"", "\\\\\"")
				  .replaceAll("\\n", "\\\\n")
				  .replaceAll("\\r", "\\\\r")
				  .replaceAll("\\f", "\\\\f")
				  .replaceAll("\\t", "\\\\t");
	}
}
