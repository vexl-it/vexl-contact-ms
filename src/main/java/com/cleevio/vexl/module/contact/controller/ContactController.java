package com.cleevio.vexl.module.contact.controller;

import com.cleevio.vexl.common.dto.ErrorResponse;
import com.cleevio.vexl.module.contact.dto.request.DeleteContactsRequest;
import com.cleevio.vexl.module.contact.dto.request.FacebookContactRequest;
import com.cleevio.vexl.module.contact.dto.request.ImportRequest;
import com.cleevio.vexl.module.contact.dto.response.UserContactResponse;
import com.cleevio.vexl.module.contact.dto.response.FacebookContactResponse;
import com.cleevio.vexl.module.contact.dto.response.ImportResponse;
import com.cleevio.vexl.module.contact.exception.FacebookException;
import com.cleevio.vexl.module.contact.service.FacebookService;
import com.cleevio.vexl.module.contact.service.ContactService;
import com.cleevio.vexl.module.user.entity.User;
import com.cleevio.vexl.module.contact.exception.ImportContactsException;
import com.cleevio.vexl.module.contact.service.ImportService;
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
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import java.security.NoSuchAlgorithmException;

@Tag(name = "Contact")
@Slf4j
@RestController
@RequestMapping(value = "/api/v1/contact")
@AllArgsConstructor
@PreAuthorize("hasRole('ROLE_USER')")
public class ContactController {

    private final ImportService importService;
    private final FacebookService facebookService;
    private final ContactService userContactService;

    @PostMapping("/import")
    @ApiResponses({
            @ApiResponse(responseCode = "200"),
            @ApiResponse(responseCode = "500 (101202)", description = "Cannot write file", content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "409 (101101)", description = "User already exists", content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "400 (101103)", description = "Avatar has invalid format", content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    @Operation(summary = "Import contacts")
    ImportResponse importContacts(@AuthenticationPrincipal User user,
                                  @Valid @RequestBody ImportRequest importRequest)
            throws ImportContactsException, NoSuchAlgorithmException {
        return this.importService.importContacts(user, importRequest);
    }

    @GetMapping("/facebook/contact")
    @ApiResponses({
            @ApiResponse(responseCode = "200"),
            @ApiResponse(responseCode = "500 (101202)", description = "Cannot write file", content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "409 (101101)", description = "User already exists", content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "400 (101103)", description = "Avatar has invalid format", content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    @Operation(summary = "Get Facebook friends")
    public FacebookContactResponse getFacebookContacts(@Valid @RequestBody FacebookContactRequest facebookContactRequest)
            throws FacebookException {
        return this.facebookService.retrieveContacts(facebookContactRequest);
    }

    @GetMapping
    @ApiResponses({
            @ApiResponse(responseCode = "200"),
            @ApiResponse(responseCode = "500 (101202)", description = "Cannot write file", content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "409 (101101)", description = "User already exists", content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "400 (101103)", description = "Avatar has invalid format", content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    @Operation(summary = "Get user contacts")
    public UserContactResponse getContacts(@AuthenticationPrincipal User user) {
        return this.userContactService.retrieveUserContactsByUser(user);
    }

    @DeleteMapping
    @ApiResponses({
            @ApiResponse(responseCode = "200"),
            @ApiResponse(responseCode = "500 (101202)", description = "Cannot write file", content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "409 (101101)", description = "User already exists", content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "400 (101103)", description = "Avatar has invalid format", content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    @Operation(summary = "Remove contacts")
    public void deleteContacts(@AuthenticationPrincipal User user,
                               @Valid @RequestBody DeleteContactsRequest deleteContactsRequest) {
        this.userContactService.deleteContacts(user, deleteContactsRequest);
    }
}
