package com.hms.user.servlet;

import com.hms.dao.UserDAO;
import com.hms.db.DBConnection;
import com.hms.entity.User;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.io.IOException;

@WebServlet("/user_register")
public class UserRegisterServlet extends HttpServlet {
  
  /**
   *
   */
  private static final long serialVersionUID = 1L;
  
  @Override
  protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
    
    try {
      
      // PrintWriter out = resp.getWriter();
      
      // get all data/value which is coming from signup.jsp page for new User
      // registration
      String fullName = req.getParameter("fullName");
      String email = req.getParameter("email");
      String password = req.getParameter("password");
      
      // Set all data to User Entity
      User user = new User(fullName, email, password);
      
      // Create Connection with DB
      UserDAO userDAO = new UserDAO(DBConnection.getConn());
      
      //get session
      HttpSession session = req.getSession();
      
      
      // call userRegister() and pass user object to insert or save user into DB.
      boolean f = userDAO.userRegister(user); // userRegister() method return boolean type value
      
      if (f == true) {
        
        session.setAttribute("successMsg", "Register Successfully");
        resp.sendRedirect("signup.jsp");//which page you want to show this msg
        //System.out.println("register successfull");
        // out.println("success");
        
      } else {
        
        session.setAttribute("errorMsg", "Something went wrong!");
        resp.sendRedirect("signup.jsp");//which page you want to show this msg
        
        //System.out.println("Error! Something went wrong");
        // out.println("error");
      }
      
    } catch (Exception e) {
      e.printStackTrace();
    }
    
  }
  
}
