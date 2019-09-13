package com.vbarjovanu.workouttimer.business.models.userprofiles;

import org.junit.Assert;
import org.junit.Test;

import static org.junit.Assert.*;

public class UserProfileTest {
    @Test
    public void testUserProfile() {
        String id="123";
        String name="profile1";
        UserProfile userProfile = new UserProfile(id);
        userProfile.setName(name);
        Assert.assertEquals(id, userProfile.getId());
        Assert.assertEquals(name, userProfile.getName());
    }

    @Test
    public void testUpdate() {
        String id="123";
        String name="profile1";
        UserProfile userProfile = new UserProfile(id);
        userProfile.setName(name);

        String id2="456";
        String name2="profile2";
        UserProfile userProfile2 = new UserProfile(id2);
        userProfile2.setName(name2);

        userProfile.update(userProfile2);
        Assert.assertEquals(id2, userProfile.getId());
        Assert.assertEquals(name2, userProfile.getName());
    }
}