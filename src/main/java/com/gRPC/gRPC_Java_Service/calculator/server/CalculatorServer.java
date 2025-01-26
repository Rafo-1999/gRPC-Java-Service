package com.gRPC.gRPC_Java_Service.calculator.server;

import io.grpc.Server;
import io.grpc.ServerBuilder;
import java.io.IOException;

public class CalculatorServer {

  public static void main(String[] args) throws IOException, InterruptedException {

    int port=50502;

    Server server = ServerBuilder
        .forPort(port)
        .addService(new CalculatorServerImpl())
        .build();

    server.start();

    System.out.println("Server started");

    Runtime.getRuntime().addShutdownHook(new Thread(()->{
        System.out.println("Received Shutdown Request");
    server.shutdown();
    System.out.println("Server shut down");
    }));

    server.awaitTermination();
  }
}
