package com.cleevio.vexl.module.contact.controller;

import com.cleevio.vexl.common.dto.ErrorResponse;
import com.cleevio.vexl.module.contact.dto.request.DeleteContactsRequest;
import com.cleevio.vexl.module.contact.dto.request.ImportRequest;
import com.cleevio.vexl.module.contact.dto.request.NewContactsRequest;
import com.cleevio.vexl.module.contact.dto.response.NewContactsResponse;
import com.cleevio.vexl.module.contact.dto.response.UserContactResponse;
import com.cleevio.vexl.module.contact.dto.response.ImportResponse;
import com.cleevio.vexl.module.contact.dto.response.UserContactsResponse;
import com.cleevio.vexl.module.contact.service.ContactService;
import com.cleevio.vexl.module.user.entity.User;
import com.cleevio.vexl.module.contact.exception.ImportContactsException;
import com.cleevio.vexl.module.contact.service.ImportService;
import com.cleevio.vexl.utils.EncryptionUtils;
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
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;

@Tag(name = "Contact")
@Slf4j
@RestController
@RequestMapping(value = "/api/v1/contact")
@AllArgsConstructor
@PreAuthorize("hasRole('ROLE_USER')")
public class ContactController {

    private final ImportService importService;
    private final ContactService userContactService;

    @PostMapping("/import")
    @SecurityRequirements({
            @SecurityRequirement(name = "public-key"),
            @SecurityRequirement(name = "phone-hash"),
            @SecurityRequirement(name = "signature"),
    })
    @ApiResponses({
            @ApiResponse(responseCode = "200"),
            @ApiResponse(responseCode = "400 (101103)", description = "Issue with an import", content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    @Operation(summary = "Import contacts. Every contact will be encrypted with HMAC-SHA256.")
    ImportResponse importContacts(@Parameter(hidden = true) @AuthenticationPrincipal User user,
                                  @Valid @RequestBody ImportRequest importRequest)
            throws ImportContactsException {
        return this.importService.importContacts(user, importRequest);
    }

    @GetMapping
    @SecurityRequirements({
            @SecurityRequirement(name = "public-key"),
            @SecurityRequirement(name = "phone-hash"),
            @SecurityRequirement(name = "signature"),
    })
    @ApiResponse(responseCode = "200")
    @Operation(summary = "Get all the user's contacts' public keys.")
    UserContactsResponse getContacts(@Parameter(hidden = true) @AuthenticationPrincipal User user,
                                     @RequestParam(required = false, defaultValue = "0") int page,
                                     @RequestParam(required = false, defaultValue = "10") int limit,
                                     HttpServletRequest request) {
        return new UserContactsResponse(
                request,
                this.userContactService.retrieveUserContactsByUser(user, page, limit)
                        .map(EncryptionUtils::encodeToBase64String)
                        .map(UserContactResponse::new)
        );
    }

    @DeleteMapping
    @SecurityRequirements({
            @SecurityRequirement(name = "public-key"),
            @SecurityRequirement(name = "phone-hash"),
            @SecurityRequirement(name = "signature"),
    })
    @ApiResponse(responseCode = "204")
    @Operation(summary = "Remove chosen contacts by public key.")
    ResponseEntity<Void> deleteContacts(@Parameter(hidden = true) @AuthenticationPrincipal User user,
                                        @Valid @RequestBody DeleteContactsRequest deleteContactsRequest) {
        this.userContactService.deleteContacts(user, deleteContactsRequest);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/new/")
    @SecurityRequirements({
            @SecurityRequirement(name = "public-key"),
            @SecurityRequirement(name = "phone-hash"),
            @SecurityRequirement(name = "signature"),
    })
    @ApiResponse(responseCode = "200")
    @Operation(summary = "Retrieve new user contacts. Send all phone contacts and all contacts which are not imported will be returned.")
    NewContactsResponse getNewPhoneContacts(@Parameter(hidden = true) @AuthenticationPrincipal User user,
                                            @Valid @RequestBody NewContactsRequest contactsRequest) {
        return this.userContactService.retrieveNewContacts(user, contactsRequest);
    }
}
