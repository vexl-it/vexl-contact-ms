package com.cleevio.vexl.module.contact.dto.response;

import com.cleevio.vexl.common.dto.PaginatedResponse;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import org.springframework.data.domain.Page;

import javax.servlet.http.HttpServletRequest;

@Getter
@EqualsAndHashCode(callSuper = true)
public class UserContactsResponse extends PaginatedResponse<UserContactResponse> {

    public UserContactsResponse(HttpServletRequest request, Page<UserContactResponse> page) {
        super(request, page);
    }
}
