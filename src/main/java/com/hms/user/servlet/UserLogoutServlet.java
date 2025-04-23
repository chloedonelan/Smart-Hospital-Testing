package com.hms.user.servlet;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.io.IOException;

@WebServlet("/userLogout")
public class UserLogoutServlet extends HttpServlet {
  
  /**
   *
   */
  private static final long serialVersionUID = 1L;
  
  @Override
  protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
    
    HttpSession session = req.getSession();
    session.removeAttribute("userObj");
    session.setAttribute("successMsg", "User Logout Successfully.");
    resp.sendRedirect("user_login.jsp");
    
  }
  
  
  
}
