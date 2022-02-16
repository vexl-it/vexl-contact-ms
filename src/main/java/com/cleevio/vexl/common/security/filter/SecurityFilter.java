package com.cleevio.vexl.common.security.filter;

import com.cleevio.vexl.common.exception.DigitalSignatureException;
import com.cleevio.vexl.common.security.AuthenticationHolder;
import com.cleevio.vexl.common.service.SignatureService;
import com.cleevio.vexl.module.user.service.UserService;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;

public class SecurityFilter extends OncePerRequestFilter {

    private final SignatureService signatureService;
    private final UserService userService;

    public SecurityFilter(SignatureService signatureService,
                          UserService userService) {
        this.signatureService = signatureService;
        this.userService = userService;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {

        String publicKey = request.getHeader("public-key");
        String phoneHash = request.getHeader("phone-hash");
        String signature = request.getHeader("signature");

        if (signature == null || publicKey == null || phoneHash == null) {
            filterChain.doFilter(request, response);
            return;
        }

        try {
            if (signatureService.isSignatureValid(publicKey, phoneHash, signature)) {
                AuthenticationHolder authenticationHolder;

                if (userService.findByPublicKey(publicKey).isPresent()) {
                    authenticationHolder = new AuthenticationHolder(userService.findByPublicKey(publicKey),
                            List.of(new SimpleGrantedAuthority("ROLE_USER")));
                    authenticationHolder.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                } else {
                    authenticationHolder = new AuthenticationHolder(null,
                            List.of(new SimpleGrantedAuthority("ROLE_NEW_USER")));
                    authenticationHolder.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                }

                SecurityContextHolder.getContext().setAuthentication(authenticationHolder);
            } else {
                SecurityContextHolder.clearContext();
            }
        } catch (DigitalSignatureException e) {
            SecurityContextHolder.clearContext();
            //todo handleError
        }

        filterChain.doFilter(request, response);
    }
}
