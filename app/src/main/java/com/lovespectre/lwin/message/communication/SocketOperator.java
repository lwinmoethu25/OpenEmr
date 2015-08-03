package com.lovespectre.lwin.message.communication;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import com.lovespectre.lwin.message.interfaces.IAppManager;
import com.lovespectre.lwin.message.interfaces.ISocketOperator;
import com.lovespectre.lwin.message.services.IMService;
import com.lovespectre.lwin.openemr.MainActivity;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.HttpURLConnection;
import java.net.InetAddress;
import java.net.MalformedURLException;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.URL;
import java.util.HashMap;
import java.util.Iterator;

/**
 * Created by lwin on 7/20/15.
 */

public class SocketOperator extends Activity implements ISocketOperator {

    Context applicationContext = MainActivity.getContextOfApplication();

    final SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(applicationContext);
    final String ipAddress = prefs.getString("IP", null);
    String AUTHENTICATION_SERVER_ADDRESS = "http://" +ipAddress+ "/emr_chat/"; //TODO change to your WebAPI Address
    private static final String HTTP_REQUEST_FAILED = null;
    private int listeningPort = 0;
    private HashMap<InetAddress, Socket> sockets = new HashMap<InetAddress, Socket>();

    private ServerSocket serverSocket = null;

    private boolean listening;

    public SocketOperator(IMService imService) {
    }

    public String sendHttpRequest(String params) {
        URL url;
        String result = new String();
        try {
            url = new URL(AUTHENTICATION_SERVER_ADDRESS);
            HttpURLConnection connection;
            connection = (HttpURLConnection) url.openConnection();
            connection.setDoOutput(true);

            PrintWriter out = new PrintWriter(connection.getOutputStream());

            out.println(params);
            out.close();

            BufferedReader in = new BufferedReader(
                    new InputStreamReader(
                            connection.getInputStream()));
            String inputLine;

            while ((inputLine = in.readLine()) != null) {
                result = result.concat(inputLine);
            }
            in.close();
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        if (result.length() == 0) {
            result = HTTP_REQUEST_FAILED;
        }

        return result;


    }

    public int startListening(int portNo) {
        listening = true;

        try {
            serverSocket = new ServerSocket(portNo);
            this.listeningPort = portNo;
        } catch (IOException e) {

            //e.printStackTrace();
            this.listeningPort = 0;
            return 0;
        }

        while (listening) {
            try {
                new ReceiveConnection(serverSocket.accept()).start();

            } catch (IOException e) {
                //e.printStackTrace();
                return 2;
            }
        }

        try {
            serverSocket.close();
        } catch (IOException e) {
            Log.e("Exception server socket", "Exception when closing server socket");
            return 3;
        }


        return 1;
    }

    public void stopListening() {
        this.listening = false;
    }

    public void exit() {
        for (Iterator<Socket> iterator = sockets.values().iterator(); iterator.hasNext(); ) {
            Socket socket = (Socket) iterator.next();
            try {
                socket.shutdownInput();
                socket.shutdownOutput();
                socket.close();
            } catch (IOException e) {
            }
        }

        sockets.clear();
        this.stopListening();
    }

    public int getListeningPort() {

        return this.listeningPort;
    }

    private class ReceiveConnection extends Thread {
        Socket clientSocket = null;

        public ReceiveConnection(Socket socket) {
            this.clientSocket = socket;
            SocketOperator.this.sockets.put(socket.getInetAddress(), socket);
        }

        @Override
        public void run() {
            try {
                //			PrintWriter out = new PrintWriter(clientSocket.getOutputStream(), true);
                BufferedReader in = new BufferedReader(
                        new InputStreamReader(
                                clientSocket.getInputStream()));
                String inputLine;

                while ((inputLine = in.readLine()) != null) {
                    if (inputLine.equals("exit") == false) {
                        //appManager.messageReceived(inputLine);
                    } else {
                        clientSocket.shutdownInput();
                        clientSocket.shutdownOutput();
                        clientSocket.close();
                        SocketOperator.this.sockets.remove(clientSocket.getInetAddress());
                    }
                }

            } catch (IOException e) {
                Log.e("ReceiveConnection.run: when receiving connection ", "");
            }
        }
    }

}
