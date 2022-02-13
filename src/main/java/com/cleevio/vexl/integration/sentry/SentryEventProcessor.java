package com.cleevio.vexl.integration.sentry;

import io.sentry.EventProcessor;
import io.sentry.SentryEvent;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Component;

import java.util.Optional;

//@Component
//public class SentryEventProcessor implements EventProcessor {
//
//	@Override
//	@Nullable
//	public SentryEvent process(SentryEvent event, @Nullable Object o) {
//		boolean isLogged = Optional.ofNullable(event.getOriginThrowable())
//				.filter(ex -> ex instanceof ApiException)
//				.map(ex -> ((ApiException) ex).isLogged())
//				.orElse(true);
//
//		return isLogged ? event : null;
//	}
//}

