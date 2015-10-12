package com.lovespectre.lwin.message.interfaces;

/**
 * Created by lwin on 7/20/15.
 */

public interface ISocketOperator {

    public String sendHttpRequest(String params);

    public int startListening(int port);

    public void stopListening();

    public void exit();

    public int getListeningPort();

}
