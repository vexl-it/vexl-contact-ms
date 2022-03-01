package com.cleevio.vexl.module.facebook.controller;

import com.cleevio.vexl.common.dto.ErrorResponse;
import com.cleevio.vexl.module.facebook.dto.response.FacebookContactResponse;
import com.cleevio.vexl.module.contact.exception.FacebookException;
import com.cleevio.vexl.module.contact.service.ContactService;
import com.cleevio.vexl.module.facebook.service.FacebookService;
import com.cleevio.vexl.module.user.entity.User;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.security.SecurityRequirements;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "Facebook")
@Slf4j
@RestController
@RequestMapping(value = "/api/v1/facebook")
@AllArgsConstructor
@PreAuthorize("hasRole('ROLE_USER')")
public class FacebookController {

    private final FacebookService facebookService;
    private final ContactService userContactService;

    @GetMapping("/{facebookId}/token/{accessToken}")
    @SecurityRequirements({
            @SecurityRequirement(name = "public-key"),
            @SecurityRequirement(name = "phone-hash"),
            @SecurityRequirement(name = "signature"),
    })
    @ApiResponses({
            @ApiResponse(responseCode = "200"),
            @ApiResponse(responseCode = "400 (101103)", description = "Bad request to Facebook", content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    @Operation(summary = "Get Facebook contacts.")
    FacebookContactResponse getFacebookContacts(@PathVariable String facebookId,
                                                @PathVariable String accessToken)
            throws FacebookException {
        return new FacebookContactResponse(this.facebookService.retrieveContacts(facebookId, accessToken));
    }

    @GetMapping("/{facebookId}/token/{accessToken}/new/")
    @SecurityRequirements({
            @SecurityRequirement(name = "public-key"),
            @SecurityRequirement(name = "phone-hash"),
            @SecurityRequirement(name = "signature"),
    })
    @ApiResponses({
            @ApiResponse(responseCode = "200"),
            @ApiResponse(responseCode = "400 (101103)", description = "Bad request to Facebook", content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    @Operation(summary = "Get new user contacts on Facebook. Returns all friends and in the newFriends attribute returns contacts which are not imported yet.")
    FacebookContactResponse getNewFacebookContacts(@Parameter(hidden = true) @AuthenticationPrincipal User user,
                                                   @PathVariable String facebookId,
                                                   @PathVariable String accessToken)
            throws FacebookException {
        return this.userContactService.retrieveFacebookNewContacts(user, facebookId, accessToken);
    }
}
