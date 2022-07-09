package com.cleevio.vexl.module.group.exception;

import com.cleevio.vexl.common.exception.ErrorType;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
enum GroupErrorType implements ErrorType {

    GROUP_NOT_FOUND("102", "Group not found."),
    ;

	private final String code;
	private final String message;
}
