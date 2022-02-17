package com.cleevio.vexl.module.contact.service;

import com.cleevio.vexl.module.contact.dto.FacebookUser;
import com.cleevio.vexl.module.contact.dto.request.FacebookContactRequest;
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

    public FacebookUser retrieveContacts(FacebookContactRequest facebookContactRequest)
            throws FacebookException {

        log.info("Retrieving contacts for user {}",
                facebookContactRequest.getFacebookId());

        try {
            FacebookClient client = new DefaultFacebookClient(facebookContactRequest.getAccessToken(), Version.LATEST);
            FacebookUser facebookFriends = client.fetchObject(facebookContactRequest.getFacebookId(), FacebookUser.class,
                    Parameter.with("fields", "id,name,friends{name,id,friends}")
            );
            log.info("Successfully fetched {} friends.",
                    facebookFriends.getFriends().size()
            );
            return facebookFriends;
        } catch (Exception e) {
            log.error("Error occurred during fetching data from Facebook", e);
            throw new FacebookException(e);
        }
    }
}
