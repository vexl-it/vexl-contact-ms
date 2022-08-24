package com.cleevio.vexl.common.integration.firebase.service;

import com.cleevio.vexl.common.exception.InvalidResponseFromIntegrationException;
import com.cleevio.vexl.common.integration.firebase.config.FirebaseProperties;
import com.cleevio.vexl.common.integration.firebase.dto.request.LinkRequest;
import com.cleevio.vexl.common.integration.firebase.dto.response.LinkResponse;
import com.cleevio.vexl.common.integration.firebase.exception.FirebaseException;
import com.cleevio.vexl.common.util.ErrorHandlerUtil;
import com.cleevio.vexl.module.push.dto.PushNotification;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.messaging.Message;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

@Slf4j
@Service
@RequiredArgsConstructor
public class FirebaseService implements NotificationService, DeeplinkService {

    private final FirebaseProperties properties;
    private final WebClient webClient;
    private static final String GROUP_UUID = "group_uuid";
    private static final String PUBLIC_KEY = "public_key";
    private static final String TYPE = "type";
    private static final String API_URL = "https://firebasedynamiclinks.googleapis.com/v1/shortLinks?key=";
    private static final String CODE = "?code=";

    @Override
    public void sendPushNotification(final PushNotification push) {
        push.membersFirebaseTokens().forEach(m -> {
            processNotification(m, push);
        });
    }

    @Override
    public String createDynamicLink(final String code) {
        final String url = API_URL + properties.key();
        final String link = properties.uri();
        final String params = CODE + code;

        final LinkRequest linkRequest = new LinkRequest(properties.domainUriPrefix(), link, properties.iosBundle(),
                properties.iosStore(), properties.androidPackage());

        final LinkResponse linkResponse = webClient.post()
                .uri(url)
                .bodyValue(linkRequest)
                .retrieve()
                .onStatus(HttpStatus::isError, clientResponse -> ErrorHandlerUtil.defaultHandler(clientResponse, url))
                .bodyToMono(LinkResponse.class)
                .blockOptional()
                .orElseThrow(() -> new InvalidResponseFromIntegrationException("Received empty body from Firebase for creating dynamic link EP: " + url));

        if (linkResponse != null) {
            return linkResponse.link() + params;
        }

        throw new FirebaseException();
    }

    private void processNotification(String firebaseToken, PushNotification push) {
        try {
            var messageBuilder = Message.builder();

            messageBuilder.setToken(firebaseToken);
            if (push.groupUuid() != null) {
                messageBuilder.putData(GROUP_UUID, push.groupUuid());
            }
            if (push.newUserPublicKey() != null) {
                messageBuilder.putData(PUBLIC_KEY, push.newUserPublicKey());
            }
            messageBuilder.putData(TYPE, push.type().name());

            final String response = FirebaseMessaging.getInstance().sendAsync(messageBuilder.build()).get();
            log.info("Sent message: " + response);

        } catch (Exception e) {
            log.error("Error sending notification", e);
        }
    }
}
