package com.cleevio.vexl.module.facebook.service;

import com.cleevio.vexl.module.facebook.dto.FacebookUser;
import com.cleevio.vexl.module.contact.exception.FacebookException;
import com.restfb.DefaultFacebookClient;
import com.restfb.FacebookClient;
import com.restfb.Parameter;
import com.restfb.Version;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class FacebookService {

    public FacebookUser retrieveContacts(String facebookId, String accessToken)
            throws FacebookException {

        log.info("Retrieving contacts for user {}",
                facebookId);

        try {
            FacebookClient client = new DefaultFacebookClient(accessToken, Version.LATEST);
            FacebookUser facebookUser = client.fetchObject(facebookId, FacebookUser.class,
                    Parameter.with("fields", "id,name,friends{name,id,friends}")
            );
            log.info("Successfully fetched {} friends.",
                    facebookUser.getFriends().size()
            );
            return facebookUser;
        } catch (Exception e) {
            log.error("Error occurred during fetching data from Facebook", e);
            throw new FacebookException();
        }
    }
}
