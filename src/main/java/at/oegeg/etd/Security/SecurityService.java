package at.oegeg.etd.Security;

import com.vaadin.flow.component.UI;
import com.vaadin.flow.server.VaadinServletRequest;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.logout.SecurityContextLogoutHandler;
import org.springframework.stereotype.Component;

import java.security.Security;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class SecurityService
{
    public void Logout()
    {
        UI.getCurrent().getPage().setLocation("/");

        SecurityContextLogoutHandler logoutHandler = new SecurityContextLogoutHandler();

        logoutHandler.logout(VaadinServletRequest.getCurrent().getHttpServletRequest(), null, null);
    }

    public static List<String> GetAuthorities()
    {
        SecurityContext context = SecurityContextHolder.getContext();
        Object principal = context.getAuthentication().getPrincipal();
        if(principal instanceof UserDetails)
        {
            UserDetails userDetails = (UserDetails) context.getAuthentication().getPrincipal();
            Collection<? extends GrantedAuthority> authorities = userDetails.getAuthorities();
            List<String> auths = authorities.stream().map(e -> e.getAuthority()).collect(Collectors.toList());
            return auths;
        }
        return null;
    }
}

























