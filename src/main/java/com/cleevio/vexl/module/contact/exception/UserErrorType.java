package com.cleevio.vexl.module.contact.exception;

import com.cleevio.vexl.common.exception.ErrorType;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum UserErrorType  implements ErrorType {

    FACEBOOK("102", "Issue on Facebook side"),
    INVALID_TOKEN("103", "Expired Facebook token"),
    MISSING_CONTACTS("104", "Import list is empty. Nothing to import.");

	/**
	 * Error custom code
	 */
	private final String code;

	/**
	 * Error custom message
	 */
	private final String message;
}
