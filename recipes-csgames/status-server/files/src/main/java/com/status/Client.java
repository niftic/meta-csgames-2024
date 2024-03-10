package com.status;

import java.io.IOException;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.net.Socket;
import java.util.List;
import java.util.Arrays;
import java.util.ArrayList;

public class Client {

    public static void main(String[] args) {
        if (args.length < 2) {
            System.out.println("Usage: java -jar client.jar <host> <port>");
            return;
        }

        String host = args[0];
        int port = Integer.parseInt(args[1]);

        try {
            Socket socket = new Socket(host, port);
            DataOutputStream out = new DataOutputStream(socket.getOutputStream());
            DataInputStream in = new DataInputStream(socket.getInputStream());

            // Send username to server
            List<Object> outmsg = new ArrayList<Object>(Arrays.asList("operator"));
            Common.sendPacket(out, outmsg);


            // Receive and display allowed commands from server
            List<Object> inmsg = Common.receivePacket(in);
            for (Object element : inmsg) {
                if (!(element instanceof String)) {
                    System.out.println("Invalid response message");
                    return;
                }
            }
            System.out.println("Allowed commands: " + inmsg);

            // Send command request to server
            outmsg = new ArrayList<Object>(Arrays.asList("uname", Arrays.asList("-o", "-m")));
            Common.sendPacket(out, outmsg);

            // Receive and display result from server
            inmsg = Common.receivePacket(in);
            if (inmsg.size() == 1 && inmsg.get(0) instanceof String) {
                System.out.println("Server operating system: " + inmsg.get(0));
            } else {
                System.out.println("Invalid response message");
                return;
            }

            // Send command request to server
            outmsg = new ArrayList<Object>(Arrays.asList("df", Arrays.asList("-h")));
            Common.sendPacket(out, outmsg);

            // Receive and display result from server
            inmsg = Common.receivePacket(in);
            if (inmsg.size() == 1 && inmsg.get(0) instanceof String) {
                System.out.println("Mounted filesystems:\n" + inmsg.get(0));
            } else {
                System.out.println("Invalid response message");
                return;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
