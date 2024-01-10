package com.hms.admin.servlet;

import java.io.IOException;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

@WebServlet("/adminLogout")
public class AdminLogoutServlet extends HttpServlet {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		
		//get session means get "adminObj" and remove it, logout done!
		HttpSession session = req.getSession();
		session.removeAttribute("adminObj");
		//show message after logout
		session.setAttribute("successMsg", "Admin Logout Successfully");
		resp.sendRedirect("admin_login.jsp");
		
		
		
	}

	
}
