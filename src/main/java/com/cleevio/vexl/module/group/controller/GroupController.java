package com.cleevio.vexl.module.group.controller;

import com.cleevio.vexl.common.security.filter.SecurityFilter;
import com.cleevio.vexl.module.group.dto.mapper.GroupMapper;
import com.cleevio.vexl.module.group.dto.request.CreateGroupRequest;
import com.cleevio.vexl.module.group.dto.request.JoinGroupRequest;
import com.cleevio.vexl.module.group.dto.request.LeaveGroupRequest;
import com.cleevio.vexl.module.group.dto.request.NewMemberRequest;
import com.cleevio.vexl.module.group.dto.response.GroupCreatedResponse;
import com.cleevio.vexl.module.group.dto.response.GroupsResponse;
import com.cleevio.vexl.module.group.dto.response.NewMembersResponse;
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
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import java.util.List;

@Tag(name = "Group")
@RestController
@RequestMapping(value = "/api/v1/groups")
@RequiredArgsConstructor
@PreAuthorize("hasRole('ROLE_USER')")
public class GroupController {

    private final GroupService groupService;
    private final GroupMapper groupMapper;

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
            summary = "Join to a group",
            description = "For joining to a group, you need QR code of a group."
    )
    void joinGroup(@Parameter(hidden = true) @AuthenticationPrincipal User user,
                   @Valid @RequestBody JoinGroupRequest request) {
        this.groupService.joinGroup(user, request);
    }

    @PostMapping("/members/new")
    @SecurityRequirements({
            @SecurityRequirement(name = SecurityFilter.HEADER_PUBLIC_KEY),
            @SecurityRequirement(name = SecurityFilter.HEADER_HASH),
            @SecurityRequirement(name = SecurityFilter.HEADER_SIGNATURE),
    })
    @ResponseStatus(HttpStatus.OK)
    @Operation(
            summary = "Get new members.",
            description = """
                    EP returns new members. It is a POST because of needed payload.
                    You should have public keys of users you already know. Send them within this request.
                    BE will return diff - that means BE will return public keys you do not have.
                    """
    )
    NewMembersResponse retrieveNewMembers(@Parameter(hidden = true) @AuthenticationPrincipal User user,
                                          @Valid @RequestBody NewMemberRequest request) {
        return new NewMembersResponse(this.groupService.retrieveNewMembers(request.groups(), user));
    }

    @GetMapping("/me")
    @SecurityRequirements({
            @SecurityRequirement(name = SecurityFilter.HEADER_PUBLIC_KEY),
            @SecurityRequirement(name = SecurityFilter.HEADER_HASH),
            @SecurityRequirement(name = SecurityFilter.HEADER_SIGNATURE),
    })
    @ResponseStatus(HttpStatus.OK)
    @Operation(
            summary = "Find my groups.",
            description = "EP returns the groups the user is in."
    )
    GroupsResponse retrieveMyGroups(@Parameter(hidden = true) @AuthenticationPrincipal User user) {
        return new GroupsResponse(
                groupMapper.mapListToGroupResponse(
                        this.groupService.retrieveMyGroups(user)
                )
        );
    }

    @GetMapping
    @SecurityRequirements({
            @SecurityRequirement(name = SecurityFilter.HEADER_PUBLIC_KEY),
            @SecurityRequirement(name = SecurityFilter.HEADER_HASH),
            @SecurityRequirement(name = SecurityFilter.HEADER_SIGNATURE),
    })
    @ResponseStatus(HttpStatus.OK)
    @Operation(
            summary = "Get group by UUID.",
            description = "Put group UUIDs you're interested in into request params."
    )
    GroupsResponse retrieveGroupsByUuid(@RequestParam List<String> groupUuids) {
        return new GroupsResponse(
                groupMapper.mapListToGroupResponse(
                        this.groupService.retrieveGroupsByUuid(groupUuids)
                )
        );
    }

    @GetMapping("/expired")
    @SecurityRequirements({
            @SecurityRequirement(name = SecurityFilter.HEADER_PUBLIC_KEY),
            @SecurityRequirement(name = SecurityFilter.HEADER_HASH),
            @SecurityRequirement(name = SecurityFilter.HEADER_SIGNATURE),
    })
    @ResponseStatus(HttpStatus.OK)
    @Operation(
            summary = "Get expired groups.",
            description = "Put group UUIDs you know about into request params and EP will return which of them are expired."
    )
    GroupsResponse retrieveExpiredGroups(@RequestParam List<String> groupUuids) {
        return new GroupsResponse(
                groupMapper.mapListToGroupResponse(
                        this.groupService.retrieveExpiredGroups(groupUuids)
                )
        );
    }

    @PutMapping("/leave")
    @SecurityRequirements({
            @SecurityRequirement(name = SecurityFilter.HEADER_PUBLIC_KEY),
            @SecurityRequirement(name = SecurityFilter.HEADER_HASH),
            @SecurityRequirement(name = SecurityFilter.HEADER_SIGNATURE),
    })
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Operation(
            summary = "Leave group.",
            description = "If user want to leave group, send hash 256 of group uuid in payload."
    )
    void leaveGroup(@Parameter(hidden = true) @AuthenticationPrincipal User user,
                    @RequestBody LeaveGroupRequest request) {
        this.groupService.leaveGroup(user, request);
    }

}
