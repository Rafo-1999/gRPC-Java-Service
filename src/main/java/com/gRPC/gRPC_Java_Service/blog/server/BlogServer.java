package com.gRPC.gRPC_Java_Service.blog.server;

import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import io.grpc.Server;
import io.grpc.ServerBuilder;
import java.io.IOException;

public class BlogServer {


  public static void main(String[] args) throws InterruptedException, IOException {
    int port= 50501;

    MongoClient client = MongoClients.create("mongodb://root:root@localhost:27017/");

    Server server= ServerBuilder
        .forPort(port)
        .addService(new BlogServerImpl(client))
        .build();

    server.start();

    System.out.printf("Server started on port %d\n", port);

    Runtime.getRuntime().addShutdownHook(new Thread(()->{
      System.out.println("Received Shutdown Request");
      server.shutdown();
      client.close();
      System.out.println("Server shut down");
    }));

    server.awaitTermination();
  }

}
