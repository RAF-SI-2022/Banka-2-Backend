package rs.raf.demo.filters;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import rs.raf.demo.services.UserDetailService;
import rs.raf.demo.utils.JwtUtil;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Collection;

@Component
public class JwtFilter extends OncePerRequestFilter {

    private final UserDetailService userService;
    private final JwtUtil jwtUtil;

    public JwtFilter(UserDetailService userService, JwtUtil jwtUtil){
        this.userService = userService;
        this.jwtUtil = jwtUtil;
    }
    @Override
    protected void doFilterInternal(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, FilterChain filterChain) throws ServletException, IOException {
        String authHeader = httpServletRequest.getHeader("Authorization");
        String jwt = null;
        String username = null;


        if(authHeader != null && authHeader.startsWith("Bearer ")){
            jwt = authHeader.substring("Bearer ".length());
            username = jwtUtil.extractUsername(jwt);
        }

        //pronadjemo usera, nasetujemo mu authorities, i proverimo koje ima a koji mu trebaju u SpringSecurityConfig
        if(username != null && SecurityContextHolder.getContext().getAuthentication() == null){
//            System.out.println();
//            UserDetails userDetails = this.userService.loadUserByUsername(username);
//
//            if(jwtUtil.validateToken(jwt, userDetails)){
//                UsernamePasswordAuthenticationToken usernamePasswordAuthenticationToken =
//                        new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
//
//                usernamePasswordAuthenticationToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(httpServletRequest));
//                SecurityContextHolder.getContext().setAuthentication(usernamePasswordAuthenticationToken);
//            }
        }
        filterChain.doFilter(httpServletRequest, httpServletResponse);
    }
}
