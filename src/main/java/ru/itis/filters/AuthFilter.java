package ru.itis.filters;

import javax.servlet.*;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.util.List;
import java.util.Set;

@WebFilter(filterName = "authFilter", urlPatterns = "/*")
public class AuthFilter implements Filter {

    private static final String HOME = "/myLibrary";
    private static final Set<String> AUTH_PAGES = Set.of("/signIn", "/signUp");
    private static final List<String> STATIC_PREFIXES = List.of("/css/", "/js/", "/images/", "/webjars/");

    @Override
    public void init(FilterConfig filterConfig) {
    }

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain chain)
            throws IOException, ServletException {
        HttpServletRequest request = (HttpServletRequest) servletRequest;
        HttpServletResponse response = (HttpServletResponse) servletResponse;

        String path = request.getServletPath();

        if (STATIC_PREFIXES.stream().anyMatch(path::startsWith)) {
            chain.doFilter(request, response);
            return;
        }

        HttpSession session = request.getSession(false);
        boolean authenticated = session != null && session.getAttribute("userId") != null;
        boolean authPage = AUTH_PAGES.contains(path);

        if (authenticated) {
            if ("admin".equals(session.getAttribute("role")) && path.startsWith("/myLibrary")) {
                response.sendRedirect(request.getContextPath() + "/catalog");
                return;
            }

            if (authPage) {
                response.sendRedirect(request.getContextPath() + HOME);
            } else {
                chain.doFilter(request, response);
            }
        } else {
            if (!authPage) {
                if ("XMLHttpRequest".equals(request.getHeader("X-Requested-With"))) {
                    response.sendError(HttpServletResponse.SC_UNAUTHORIZED);
                } else {
                    response.sendRedirect(request.getContextPath() + "/signIn");
                }
            } else {
                chain.doFilter(request, response);
            }
        }
    }

    @Override
    public void destroy() {
    }
}