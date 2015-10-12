package com.lovespectre.lwin.message.tools;


import com.lovespectre.lwin.message.types.MessageInfo;

/**
 * Created by lwin on 7/28/15.
 */
/*
 * This class can store friendInfo and check userkey and username combination 
 * according to its stored data
 */
public class MessageController {

    private static MessageInfo[] messagesInfo = null;

    public static MessageInfo checkMessage(String username) {
        MessageInfo result = null;
        if (messagesInfo != null) {
            for (int i = 0; i < messagesInfo.length; ) {

                result = messagesInfo[i];
                break;

            }
        }
        return result;
    }

    public static MessageInfo getMessageInfo(String username) {
        MessageInfo result = null;
        if (messagesInfo != null) {
            for (int i = 0; i < messagesInfo.length; ) {
                result = messagesInfo[i];
                break;

            }
        }
        return result;
    }

    public static MessageInfo[] getMessagesInfo() {
        return messagesInfo;
    }

    public static void setMessagesInfo(MessageInfo[] messageInfo) {
        MessageController.messagesInfo = messageInfo;
    }


}
