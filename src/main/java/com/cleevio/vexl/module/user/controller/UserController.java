package com.cleevio.vexl.module.user.controller;

import com.cleevio.vexl.common.dto.ErrorResponse;
import com.cleevio.vexl.common.security.filter.SecurityFilter;
import com.cleevio.vexl.module.user.entity.User;
import com.cleevio.vexl.module.user.exception.HashAlreadyUsedException;
import com.cleevio.vexl.module.user.service.UserService;
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
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


@Tag(name = "User")
@Slf4j
@RestController
@RequestMapping(value = "/api/v1/users")
@AllArgsConstructor
public class UserController {

    private final UserService userService;

    @PostMapping
    @SecurityRequirements({
            @SecurityRequirement(name = SecurityFilter.HEADER_PUBLIC_KEY),
            @SecurityRequirement(name = SecurityFilter.HEADER_HASH),
            @SecurityRequirement(name = SecurityFilter.HEADER_SIGNATURE),
    })
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "User has been created"),
            @ApiResponse(responseCode = "409 (100102)", description = "FacebookId or phone number is already in use by another user.", content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
    })
    @Operation(
            summary = "Create a new user",
            description = "This endpoint must be called first. If you call other endpoints without a user created, it will return Unauthorized."
    )
    @PreAuthorize("hasRole('ROLE_NEW_USER')")
    ResponseEntity<Void> createUser(@RequestHeader(name = SecurityFilter.HEADER_PUBLIC_KEY) String publicKey,
                                    @RequestHeader(name = SecurityFilter.HEADER_HASH) String hash)
            throws HashAlreadyUsedException {
        this.userService.createUser(publicKey, hash);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/me")
    @SecurityRequirements({
            @SecurityRequirement(name = SecurityFilter.HEADER_PUBLIC_KEY),
            @SecurityRequirement(name = SecurityFilter.HEADER_HASH),
            @SecurityRequirement(name = SecurityFilter.HEADER_SIGNATURE),
    })
    @ApiResponse(responseCode = "200")
    @Operation(summary = "Delete a user and his contacts.")
    @PreAuthorize("hasRole('ROLE_USER')")
    void deleteMe(@Parameter(hidden = true) @AuthenticationPrincipal User user) {
        this.userService.removeUserAndContacts(user);
    }
}
