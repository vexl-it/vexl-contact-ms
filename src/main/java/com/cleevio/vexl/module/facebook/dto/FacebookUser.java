package com.cleevio.vexl.module.facebook.dto;

import com.restfb.Facebook;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

import static java.util.Collections.unmodifiableList;

@Data
public class FacebookUser {

    @Facebook
    private String id;

    @Facebook
    private String name;

    @Facebook("friends")
    private final List<FacebookUser> friends = new ArrayList<>();

    private final List<FacebookUser> newFriends = new ArrayList<>();

    public List<FacebookUser> getFriends() {
        return unmodifiableList(friends);
    }

    public void addNewFriends(FacebookUser facebookUser) {
        newFriends.add(facebookUser);
    }

    public List<FacebookUser> getNewFriends() {
        return newFriends;
    }
}
