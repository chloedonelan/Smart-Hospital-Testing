package com.hms.user.servlet;

import java.io.IOException;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import com.hms.dao.UserDAO;
import com.hms.db.DBConnection;

@WebServlet("/userChangePassword")
public class ChangePasswordServlet extends HttpServlet{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		
		int userId = Integer.parseInt(req.getParameter("userId"));
		String oldPassword = req.getParameter("oldPassword");
		String newPassword = req.getParameter("newPassword");
		
		UserDAO uDAO = new UserDAO(DBConnection.getConn());
		//boolean f = uDAO.checkOldPassword(userId, oldPassword);
		
		
		HttpSession session = req.getSession();
		
		if(uDAO.checkOldPassword(userId, oldPassword)) {
			
			if(uDAO.changePassword(userId, newPassword)) {
				
				session.setAttribute("successMsg", "Password Change Successfully.");
				resp.sendRedirect("change_password.jsp");
				
			}else {
				
				session.setAttribute("errorMsg", "Something wrong on server!");
				resp.sendRedirect("change_password.jsp");
				
			}
			
		}else {
			session.setAttribute("errorMsg", "Old password incorrect");
			resp.sendRedirect("change_password.jsp");
		}
		
		
		
	}
	
	

}
