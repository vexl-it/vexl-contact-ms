package com.cleevio.vexl.module.facebook.service;

import com.cleevio.vexl.module.contact.exception.InvalidFacebookToken;
import com.cleevio.vexl.module.contact.service.ContactService;
import com.cleevio.vexl.module.facebook.dto.FacebookUser;
import com.cleevio.vexl.module.contact.exception.FacebookException;
import com.cleevio.vexl.module.facebook.dto.response.FacebookContactResponse;
import com.cleevio.vexl.module.user.entity.User;
import com.restfb.DefaultFacebookClient;
import com.restfb.FacebookClient;
import com.restfb.Parameter;
import com.restfb.Version;
import com.restfb.exception.FacebookOAuthException;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

/**
 * Service for connection to Facebook service.
 * <p>
 * We return the current user, in the friends' field we return his friends who use the application
 * and in the friends.friends field we return mutual friends who use the application.
 * WARNING - the user himself will also be in the mutual friends.
 */
@Service
@Slf4j
@AllArgsConstructor
public class FacebookService {

    private final ContactService contactService;

    public FacebookUser retrieveContacts(String facebookId, String accessToken)
            throws FacebookException, InvalidFacebookToken {

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

        } catch (FacebookOAuthException e) {
            log.error("Invalid Facebook token.", e);
            throw new InvalidFacebookToken();
        } catch (Exception e) {
            log.error("Error occurred during fetching data from Facebook", e);
            throw new FacebookException();
        }
    }

    @Transactional(readOnly = true)
    public FacebookContactResponse retrieveFacebookNotImportedConnection(User user, String facebookId, String accessToken)
            throws FacebookException, InvalidFacebookToken {
        log.info("Checking for new Facebook connections for user {}",
                user.getId());

        List<FacebookUser> newConnections = new ArrayList<>();

        FacebookUser facebookUser = retrieveContacts(facebookId, accessToken);

        facebookUser.getFriends().stream()
                .map(FacebookUser::getId)
                .toList()
                .forEach(fbId -> {
                    if (!this.contactService.existsByHashFromAndHashTo(user.getHash(), fbId)) {
                        newConnections.addAll(facebookUser.getFriends()
                                .stream()
                                .filter(fu -> fbId.equals(fu.getId()))
                                .toList());
                    }
                });

        log.info("Found {} new Facebook contacts",
                newConnections.size());

        return new FacebookContactResponse(facebookUser, newConnections);

    }
}
