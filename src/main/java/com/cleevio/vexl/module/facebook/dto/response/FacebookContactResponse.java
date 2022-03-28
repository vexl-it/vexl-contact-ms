package com.cleevio.vexl.module.facebook.dto.response;

import com.cleevio.vexl.module.facebook.dto.FacebookUser;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;


@Data
@AllArgsConstructor
public class FacebookContactResponse {

    private FacebookUser facebookUser;

    public FacebookContactResponse(FacebookUser facebookUser, List<FacebookUser> facebookUsers) {
        this.facebookUser = facebookUser;
        this.facebookUser.setFriends(facebookUsers);
    }
}
