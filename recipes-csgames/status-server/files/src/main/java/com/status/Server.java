package com.status;

import java.lang.InterruptedException;
import java.io.IOException;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashMap;
import java.util.List;
import java.util.Arrays;
import java.util.ArrayList;

public class Server {
    private static final int DEFAULT_PORT = 8000;

    private static final HashMap<String, String[]> userCommands = new HashMap<>();
    private static final HashMap<String, String[]> commandArguments = new HashMap<>();

    static {
        // Define allowed commands for users
        userCommands.put("operator", new String[]{"uname", "df"});
        userCommands.put("administrator", new String[]{"id", "cat"});
        // Define allowed arguments for commands
        commandArguments.put("uname", new String[]{"-r", "-a", "-v", "-m", "-i", "-o", "-n", "-s"});
        commandArguments.put("df", new String[]{"-h", "-H", "-T"});
        commandArguments.put("id", new String[]{"-u", "-g", "-G", "-r", "-n"});
        commandArguments.put("cat", new String[]{"/etc/hostname", "/etc/passwd", "flag.txt"});
    }

    public static void main(String[] args) {
        int port = args.length > 0 ? Integer.parseInt(args[0]) : DEFAULT_PORT;

        try {
            ServerSocket serverSocket = new ServerSocket(port);
            System.out.println("Server is listening on port " + port);

            while (true) {
                Socket socket = serverSocket.accept();
                System.out.println("New client connected: " + socket.getInetAddress().getHostAddress());

                // Handle client communication in a separate thread
                new Thread(new ClientHandler(socket)).start();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static class ClientHandler implements Runnable {
        private Socket socket;

        public ClientHandler(Socket socket) {
            this.socket = socket;
        }

        @Override
        public void run() {
            try (DataInputStream in = new DataInputStream(socket.getInputStream());
                 DataOutputStream out = new DataOutputStream(socket.getOutputStream())) {

                // Handle user authentication
                List<Object> inmsg = Common.receivePacket(in);
                String username;
                if (inmsg.size() == 1 && inmsg.get(0) instanceof String) {
                    username = (String) inmsg.get(0);
                } else {
                    System.out.println("Invalid username message");
                    return;
                }
                
                if (!userCommands.containsKey(username)) {
                    // Username not found or invalid
                    System.out.println("Invalid username: " + username);
                    return;
                }

                // Send allowed commands to the user
                List<Object> outmsg = new ArrayList<Object>(Arrays.asList(userCommands.get(username)));
                Common.sendPacket(out, outmsg);

                // Continuously handle command requests
                while (true) {
                    inmsg = Common.receivePacket(in);
                    if (inmsg.size() != 2 || !(inmsg.get(0) instanceof String) || !(inmsg.get(1) instanceof List)) {
                        System.out.println("Invalid command message");
                        return;
                    }
                    for (Object element : (List<Object>)inmsg.get(1)) {
                        if (!(element instanceof String)) {
                            System.out.println("Invalid command message");
                            return;
                        }
                    }
                    String command = (String) inmsg.get(0);
                    List<String> arguments = (List<String>) inmsg.get(1);
                    String result;
                    if (Arrays.asList(userCommands.get(username)).contains(command)) {
                        if (Arrays.asList(commandArguments.get(command)).containsAll(arguments)) {
                            // Execute the command
                            result = executeCommand(command, arguments);
                        } else {
                            // Argument not allowed for the command
                            result = "Command " + command + " called with disallowed arguments: " + arguments;
                        }
                    } else {
                        // Command not allowed for the user
                        result = "User " + username + " attempted to execute disallowed command: " + command;
                    }

                    // Send command response to the user
                    outmsg = new ArrayList<Object>(Arrays.asList(result));
                    Common.sendPacket(out, outmsg);
                }
            } catch (IOException e) {
            }
        }

        private String executeCommand(String command, List<String> arguments) {
            ArrayList argv = new ArrayList(arguments);
            argv.add(0, command);
            argv.removeAll(Arrays.asList(""));
            ProcessBuilder processBuilder = new ProcessBuilder(argv);
            processBuilder.redirectErrorStream(true);

            try {
                Process process = processBuilder.start();
                InputStream inputStream = process.getInputStream();

                BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
                StringBuilder output = new StringBuilder();
                String line;
                while ((line = reader.readLine()) != null) {
                    output.append(line).append("\n");
                }

                int exitCode = process.waitFor();
                if (exitCode != 0) {
                    return "Command execution failed with exit code " + exitCode;
                }

                return output.toString();
            } catch (IOException e) {
                return "Failed to run command";
            } catch (InterruptedException e) {
                return "Process interrupted";
            }
        }
    }
}
