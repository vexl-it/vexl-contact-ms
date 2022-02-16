package com.cleevio.vexl.module.contact.dto;

import com.restfb.Facebook;
import com.restfb.types.User;

import java.util.ArrayList;
import java.util.List;

import static java.util.Collections.unmodifiableList;

public class FacebookUser extends User {

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
