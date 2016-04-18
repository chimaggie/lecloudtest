package com.lecloud.api.test.UserResource;

/**
 * Created by hongyuechi on 4/12/16.
 */
public class UpdateUser {
    //admin can update user information
    //admin update password
    //admin update authorities
    //cannot update login name
    //admin can un activate the user
    // admin update non duplicate vin number
    // admin update non duplicate email


    //user do not have access right to update other user info including admin's
    // user cannot update his own authorities/role
    // user can not un activate his own account

}
