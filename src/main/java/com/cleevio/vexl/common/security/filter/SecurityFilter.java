package com.cleevio.vexl.common.security.filter;

import com.cleevio.vexl.common.dto.ErrorResponse;
import com.cleevio.vexl.common.enums.RoleEnum;
import com.cleevio.vexl.common.exception.DigitalSignatureException;
import com.cleevio.vexl.common.security.AuthenticationHolder;
import com.cleevio.vexl.common.service.SignatureService;
import com.cleevio.vexl.module.user.service.UserService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Collections;
import java.util.List;

public class SecurityFilter extends OncePerRequestFilter {

    public static final String HEADER_PUBLIC_KEY = "public-key";
    public static final String HEADER_PHONE_HASH = "phone-hash";
    public static final String HEADER_SIGNATURE = "signature";

    private final SignatureService signatureService;
    private final UserService userService;

    public SecurityFilter(SignatureService signatureService,
                          UserService userService) {
        this.signatureService = signatureService;
        this.userService = userService;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {

        String publicKey = request.getHeader(HEADER_PUBLIC_KEY);
        String phoneHash = request.getHeader(HEADER_PHONE_HASH);
        String signature = request.getHeader(HEADER_SIGNATURE);

        if (signature == null || publicKey == null || phoneHash == null) {
            filterChain.doFilter(request, response);
            return;
        }

        try {
            if (signatureService.isSignatureValid(publicKey, phoneHash, signature)) {
                AuthenticationHolder authenticationHolder;

                if (userService.findByPublicKey(publicKey).isPresent()) {
                    authenticationHolder = new AuthenticationHolder(userService.findByPublicKey(publicKey).get(),
                            List.of(new SimpleGrantedAuthority(RoleEnum.ROLE_USER.name())));
                    authenticationHolder.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                } else {
                    authenticationHolder = new AuthenticationHolder(null,
                            List.of(new SimpleGrantedAuthority(RoleEnum.ROLE_NEW_USER.name())));
                    authenticationHolder.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                }

                SecurityContextHolder.getContext().setAuthentication(authenticationHolder);
            } else {
                SecurityContextHolder.clearContext();
            }
        } catch (DigitalSignatureException e) {
            SecurityContextHolder.clearContext();
            handleError(response, "Signature verification failed: " + e.getMessage(), 400);
        }

        filterChain.doFilter(request, response);
    }

    protected void handleError(ServletResponse response, String s, int code) throws IOException {
        HttpServletResponse httpResponse = (HttpServletResponse) response;
        httpResponse.setStatus(code);

        ErrorResponse error = new ErrorResponse(Collections.singleton(s), "0");
        OutputStream out = response.getOutputStream();
        ObjectMapper mapper = new ObjectMapper();
        mapper.writeValue(out, error);
        out.flush();

        throw new RuntimeException();
    }
}
