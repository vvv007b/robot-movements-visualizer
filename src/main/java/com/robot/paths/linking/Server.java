package com.robot.paths.linking;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;

public class Server {
    private final int port;
    private ServerSocket serverSocket;
    private Socket client;
    private BufferedReader inBuffer;

    public Server() {
        port = 4422;
    }

    public boolean start() {
        try {
            serverSocket = new ServerSocket(port);
            System.out.println("Waiting for a client on port " + port);
            client = serverSocket.accept();
            System.out.println("Client connected");
            inBuffer = new BufferedReader(new InputStreamReader(client.getInputStream()));
            return true;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return false;
    }

    public int getPort() {
        return port;
    }

    public static ArrayList<double[]> getAllCoordinates() {
        return new ArrayList<>();
    }

    public static ArrayList<Double> getSpeeds() {
        return new ArrayList<>();
    }

    public static ArrayList<Long> getUpdateTimes() {
        return new ArrayList<>();
    }
}
