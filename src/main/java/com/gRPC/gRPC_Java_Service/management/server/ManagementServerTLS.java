package com.gRPC.gRPC_Java_Service.management.server;

import io.grpc.Server;
import io.grpc.ServerBuilder;
import java.io.File;
import java.io.IOException;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

public class ManagementServerTLS {

  public static void main(String[] args) throws InterruptedException, IOException {
    int port= 50501;

    Server server= ServerBuilder
        .forPort(port)
        .useTransportSecurity(
            new File("ssl/server.crt"),
            new File("ssl/server.pem")
        )
        .addService(new ManagementServerImpl())
        .build();

    server.start();

    System.out.printf("Server started on port %d\n", port);

    Runtime.getRuntime().addShutdownHook(new Thread(()->{
      System.out.println("Received Shutdown Request");
      server.shutdown();
      System.out.println("Server shut down");
    }));

    server.awaitTermination();
  }

}
