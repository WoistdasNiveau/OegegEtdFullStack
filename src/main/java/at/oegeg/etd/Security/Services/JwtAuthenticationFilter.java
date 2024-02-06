package at.oegeg.etd.Security.Services;

import at.oegeg.etd.Repositories.ITokenBlackListRepository;
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

import static at.oegeg.etd.Constants.Constants.AUTHORIZATIONHEADER;


@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter
{
    private final JwtService _jwtService;
    private final ITokenBlackListRepository _blacklistedTokenRepository;
    private final UserDetailsService _userDetailsService;


    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException
    {
        final String authHeader = request.getHeader(AUTHORIZATIONHEADER);
        final String jwtToken;
        final String identifier;

        if(authHeader == null || !authHeader.startsWith("Bearer "))
        {
            filterChain.doFilter(request, response);
            return;
        }

        jwtToken = authHeader.substring(7);
        if(_blacklistedTokenRepository.existsByToken(jwtToken))
        {
            filterChain.doFilter(request, response);
            return;
        }
        identifier = _jwtService.ExtractUsername(jwtToken);

        if(identifier != null && SecurityContextHolder.getContext().getAuthentication() == null)
        {
            UserDetails userDetails = this._userDetailsService.loadUserByUsername(identifier);
            //if(!userDetails.isEnabled()) // enable when isEnabled is set to true from email verification
            //{
            //    filterChain.doFilter(request, response);
            //    return;
            //}

            if(_jwtService.IsTokenValid(jwtToken, userDetails))
            {
                UsernamePasswordAuthenticationToken token = new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
                token.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                SecurityContextHolder.getContext().setAuthentication(token);
            }
        }
        var e = SecurityContextHolder.getContext().getAuthentication();
        filterChain.doFilter(request, response);
    }
}
