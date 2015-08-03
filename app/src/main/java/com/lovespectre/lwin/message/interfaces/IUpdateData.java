package com.lovespectre.lwin.message.interfaces;

import com.lovespectre.lwin.message.types.FriendInfo;
import com.lovespectre.lwin.message.types.MessageInfo;

/**
 * Created by lwin on 7/20/15.
 */


public interface IUpdateData {
    public void updateData(MessageInfo[] messages, FriendInfo[] friends, FriendInfo[] unApprovedFriends, String userKey);

}
