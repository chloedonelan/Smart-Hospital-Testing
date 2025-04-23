package com.hms.admin.servlet;

import com.hms.entity.User;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.fail;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class AdminLogoutServletTest {
  @Mock
  private HttpServletRequest request;
  @Mock
  private HttpServletResponse response;
  @Mock
  private HttpSession session;
  @InjectMocks
  private AdminLogoutServlet servlet;
  @InjectMocks
  private AdminLoginServlet loginServlet;
  
  @BeforeEach
  public void setup() {
    request = mock(HttpServletRequest.class);
    response = mock(HttpServletResponse.class);
    session = mock(HttpSession.class);
  }
  
  // Successful admin logout (with existing session attributes)
  @Test
  public void testSuccessWithExistingSessionAttributes() {
    when(request.getSession()).thenReturn(session);
    when(request.getParameter("email")).thenReturn("admin@gmail.com");
    when(request.getParameter("password")).thenReturn("admin");
  
    try {
      loginServlet.doPost(request, response);
    
      verify(session).setAttribute(eq("adminObj"), any(User.class));
      verify(response).sendRedirect("admin/index.jsp");
    
      servlet.doGet(request, response);
    
      verify(session).removeAttribute("adminObj");
      verify(session).setAttribute("successMsg", "Admin Logout Successfully");
      verify(response).sendRedirect("admin_login.jsp");
    } catch (ServletException | IOException e) {
      fail();
    }
  }
    
    // Successful admin logout (without existing session attributes)
    @Test
    public void testSuccessWithoutExistingSessionAttributes() {
      when(request.getSession()).thenReturn(session);
    
      try {
        servlet.doGet(request, response);
      
        verify(session).removeAttribute("adminObj");
        verify(session).setAttribute("successMsg", "Admin Logout Successfully");
        verify(response).sendRedirect("admin_login.jsp");
      } catch (ServletException | IOException e) {
        fail();
      }
  }
}
