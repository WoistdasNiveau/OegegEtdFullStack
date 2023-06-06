package at.oegeg.etd.Security;

import at.oegeg.etd.Repositories.ITokenBlackListRepository;
import at.oegeg.etd.Security.Services.JwtService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

import static at.oegeg.etd.Constants.AUTHORIZATIONHEADER;


@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter
{
    private final JwtService jwtService;
    private final UserDetailsService userDetailsService;
    private final ITokenBlackListRepository _tokenblackListRepository;
    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException
    {
        final String authHeader = request.getHeader(AUTHORIZATIONHEADER);
        final String jwtToken;
        final String emailOrTelefoneNumber;

        if(authHeader == null || !authHeader.startsWith("Bearer "))
        {
            filterChain.doFilter(request,response);
            return;
        }

        if(_tokenblackListRepository.existsByToken(authHeader))
        {
            filterChain.doFilter(request,response);
            return;
        }

        jwtToken = authHeader.substring(7);
        emailOrTelefoneNumber = jwtService.ExtractUsername(jwtToken);

        if(emailOrTelefoneNumber != null && SecurityContextHolder.getContext().getAuthentication() == null)
        {
            UserDetails userDetails = this.userDetailsService.loadUserByUsername(emailOrTelefoneNumber);
            if(!userDetails.isEnabled())
            {
                filterChain.doFilter(request,response);
                return;
            }

            if(jwtService.IsTokenValid(jwtToken, userDetails))
            {
                UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(
                        userDetails,null,userDetails.getAuthorities()
                );

                authenticationToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

                SecurityContextHolder.getContext().setAuthentication(authenticationToken);
            }
        }
        var a = SecurityContextHolder.getContext().getAuthentication().getAuthorities();
        filterChain.doFilter(request, response);
    }
}



























