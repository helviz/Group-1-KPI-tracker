package org.pahappa.systems.kpiTracker.security;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Custom authentication filter that uses email instead of username for
 * authentication.
 * This filter intercepts the login form submission and converts the email field
 * to
 * a username field that Spring Security can process.
 * 
 * Note: This filter works with the existing authentication provider and just
 * changes
 * the input field from username to email.
 */
public class EmailPasswordAuthenticationFilter extends UsernamePasswordAuthenticationFilter {

    private static final String EMAIL_PARAMETER = "j_username";
    private static final String PASSWORD_PARAMETER = "j_password";

    @Override
    public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response)
            throws AuthenticationException {

        String email = obtainEmail(request);
        String password = obtainPassword(request);

        if (email == null) {
            email = "";
        }

        if (password == null) {
            password = "";
        }

        email = email.trim();

        // Create authentication token with email as the principal
        // The existing authentication provider will handle the actual authentication
        UsernamePasswordAuthenticationToken authRequest = new UsernamePasswordAuthenticationToken(email, password);

        // Allow subclasses to set the "details" property
        setDetails(request, authRequest);

        return this.getAuthenticationManager().authenticate(authRequest);
    }

    /**
     * Extract the email from the request parameters.
     */
    protected String obtainEmail(HttpServletRequest request) {
        return request.getParameter(EMAIL_PARAMETER);
    }

    /**
     * Extract the password from the request parameters.
     */
    protected String obtainPassword(HttpServletRequest request) {
        return request.getParameter(PASSWORD_PARAMETER);
    }
}
