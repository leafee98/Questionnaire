package controller;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.ServletOutputStream;
import java.io.IOException;

public class TestServlet extends HttpServlet {
	private static final long serialVersionUID = 6718759761234978623L;

	@Override
	public void doGet(HttpServletRequest request, HttpServletResponse response) {
		ServletOutputStream out = null;
		try {
			out = response.getOutputStream();
			out.print("<p> Hello! </p>");
			out.print(request.getParameter("str_a"));
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

}
