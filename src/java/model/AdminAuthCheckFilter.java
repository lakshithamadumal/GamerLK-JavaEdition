package model;

import java.io.IOException;
import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

/**
 *
 * @author Laky
 */
@WebFilter(urlPatterns = {
    "/admin/General-Add-Game.html",
    "/admin/General-Category.html",
    "/admin/General-Developers.html",
    "/admin/General-Games.html",
    "/admin/General-Offers.html",
    "/admin/index.html",
    "/admin/Infomation-Customer.html",
    "/admin/Infomation-Sales.html",
    "/admin/Infomation-Subscribers.html",
    "/admin/order-invoice.html",
    "/admin/Support-Community.html",
    "/admin/Support-Inquiry.html",
    "/admin/General-Update-Game.html",
    "/admin/Support-Send-Email.html"
})

public class AdminAuthCheckFilter implements Filter {

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
    }

    @Override
    public void doFilter(ServletRequest req, ServletResponse res, FilterChain chain) throws IOException, ServletException {
        HttpServletRequest request = (HttpServletRequest) req;
        HttpServletResponse response = (HttpServletResponse) res;

        HttpSession session = request.getSession(false);

        if (session != null && session.getAttribute("admin") != null) {
            chain.doFilter(req, res);
        } else {
            response.sendRedirect(request.getContextPath() + "/admin/auth-signin.html");
        }
    }

    @Override
    public void destroy() {
    }

}
