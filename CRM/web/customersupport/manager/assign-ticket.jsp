<%@page import="dal.CustomerTicketDAO"%>
<%@page import="util.PermissionChecker"%>
<%@page import="model.UserSession"%>
<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%
    // Check session
    UserSession userSession = (UserSession) session.getAttribute("userSession");
    if (userSession == null) {
        response.sendRedirect(request.getContextPath() + "/login");
        return;
    }
    
    // Check MANAGE permission for ticket assignment
    if (!PermissionChecker.canManage(userSession, "/support/manager/assign-ticket")) {
        request.setAttribute("errorMessage", "Bạn không có quyền phân công tickets");
        request.getRequestDispatcher("/403.jsp").forward(request, response);
        return;
    }
    
    // Only process POST requests
    if (!"POST".equalsIgnoreCase(request.getMethod())) {
        response.sendRedirect(request.getContextPath() + "/support/manager/tickets");
        return;
    }
    
    try {
        int ticketId = Integer.parseInt(request.getParameter("ticketId"));
        int agentId = Integer.parseInt(request.getParameter("agentId"));
        
        CustomerTicketDAO ticketDAO = new CustomerTicketDAO();
        boolean success = ticketDAO.assignTicket(ticketId, agentId);
        
        if (success) {
            session.setAttribute("successMessage", "Phân công ticket thành công!");
        } else {
            session.setAttribute("errorMessage", "Không thể phân công ticket. Vui lòng thử lại.");
        }
        
    } catch (Exception e) {
        e.printStackTrace();
        session.setAttribute("errorMessage", "Có lỗi xảy ra: " + e.getMessage());
    }
    
    response.sendRedirect(request.getContextPath() + "/support/manager/tickets-controller.jsp");
%>
