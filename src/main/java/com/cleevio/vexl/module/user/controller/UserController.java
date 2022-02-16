package com.cleevio.vexl.module.user.controller;

import com.cleevio.vexl.common.dto.ErrorResponse;
import com.cleevio.vexl.module.user.dto.request.CreateUserRequest;
import com.cleevio.vexl.module.user.dto.response.UserResponse;
import com.cleevio.vexl.module.user.entity.User;
import com.cleevio.vexl.module.user.exception.UserAlreadyExistsException;
import com.cleevio.vexl.module.user.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;

@Tag(name = "User")
@Slf4j
@RestController
@RequestMapping(value = "/api/v1/user")
@AllArgsConstructor
public class UserController {

    private final UserService userService;

    @PostMapping
    @ApiResponses({
            @ApiResponse(responseCode = "200"),
            @ApiResponse(responseCode = "500 (101202)", description = "Cannot write file", content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "409 (101101)", description = "User already exists", content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "400 (101103)", description = "Avatar has invalid format", content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    @Operation(summary = "Create a new user")
    @PreAuthorize("hasRole('ROLE_NEW_USER')")
    public UserResponse createUser(@Valid @RequestBody CreateUserRequest request)
            throws UserAlreadyExistsException {
        return new UserResponse(userService.createUser(request));
    }

    @DeleteMapping("/me")
    @ApiResponses({
            @ApiResponse(responseCode = "200"),
            @ApiResponse(responseCode = "500 (101202)", description = "Cannot write file", content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "409 (101101)", description = "User already exists", content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "400 (101103)", description = "Avatar has invalid format", content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    @Operation(summary = "Delete a user")
    @PreAuthorize("hasRole('ROLE_USER')")
    public void deleteMe(@AuthenticationPrincipal User user) {
        this.userService.removeUserAndContacts(user);
    }
}
