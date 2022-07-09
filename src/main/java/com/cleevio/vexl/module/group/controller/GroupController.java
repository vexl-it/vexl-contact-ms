package com.cleevio.vexl.module.group.controller;

import com.cleevio.vexl.common.security.filter.SecurityFilter;
import com.cleevio.vexl.module.group.dto.request.CreateGroupRequest;
import com.cleevio.vexl.module.group.dto.request.JoinGroupRequest;
import com.cleevio.vexl.module.group.dto.response.GroupCreatedResponse;
import com.cleevio.vexl.module.group.service.GroupService;
import com.cleevio.vexl.module.user.entity.User;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.security.SecurityRequirements;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;

@Tag(name = "Group")
@RestController
@RequestMapping(value = "/api/v1/group")
@RequiredArgsConstructor
@PreAuthorize("hasRole('ROLE_USER')")
public class GroupController {

    private final GroupService groupService;

    @PostMapping
    @SecurityRequirements({
            @SecurityRequirement(name = SecurityFilter.HEADER_PUBLIC_KEY),
            @SecurityRequirement(name = SecurityFilter.HEADER_HASH),
            @SecurityRequirement(name = SecurityFilter.HEADER_SIGNATURE),
    })
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(
            summary = "Create a new group",
            description = "Each user can create a new group."
    )
    GroupCreatedResponse createGroup(@Parameter(hidden = true) @AuthenticationPrincipal User user,
                                     @Valid @RequestBody CreateGroupRequest request) {
        return new GroupCreatedResponse(this.groupService.createGroup(user, request));
    }

    @PostMapping("/join")
    @SecurityRequirements({
            @SecurityRequirement(name = SecurityFilter.HEADER_PUBLIC_KEY),
            @SecurityRequirement(name = SecurityFilter.HEADER_HASH),
            @SecurityRequirement(name = SecurityFilter.HEADER_SIGNATURE),
    })
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Operation(
            summary = "Create a new group",
            description = "Each user can create a new group."
    )
    void joinGroup(@Parameter(hidden = true) @AuthenticationPrincipal User user,
                   @Valid @RequestBody JoinGroupRequest request) {
        this.groupService.joinGroup(user, request);
    }

}