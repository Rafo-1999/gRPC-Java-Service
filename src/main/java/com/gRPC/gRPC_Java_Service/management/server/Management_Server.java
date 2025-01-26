package com.gRPC.gRPC_Java_Service.management.server;

import io.grpc.Server;
import io.grpc.ServerBuilder;
import java.io.IOException;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

@Component
public class Management_Server {

  @Bean
  private String runGrpcServer() throws IOException, InterruptedException {

    int port= 50501;

    Server server= ServerBuilder
        .forPort(port)
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
    return server.getPort()+"";
  }

}
