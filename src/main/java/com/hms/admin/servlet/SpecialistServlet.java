package com.hms.admin.servlet;

import java.io.IOException;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import com.hms.dao.SpecialistDAO;
import com.hms.db.DBConnection;

@WebServlet("/addSpecialist")
public class SpecialistServlet extends HttpServlet{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		
		String specialistName = req.getParameter("specialistName");
		
		SpecialistDAO specialistDAO = new SpecialistDAO(DBConnection.getConn());
		boolean f = specialistDAO.addSpecialist(specialistName);
		
		HttpSession session = req.getSession();
		
		if (f==true) {
			session.setAttribute("successMsg", "Specialist added Successfully.");
			resp.sendRedirect("admin/index.jsp");
			
		} else {
			session.setAttribute("errorMsg", "Something went wrong on server");
			resp.sendRedirect("admin/index.jsp");
		}
	}
	
	

}
