package com.gRPC.gRPC_Java_Service.management.client;

import com.proto.management.ManagementRequest;
import com.proto.management.ManagementResponse;
import com.proto.management.ManagementServiceGrpc;
import io.grpc.ChannelCredentials;
import io.grpc.Deadline;
import io.grpc.Grpc;
import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import io.grpc.Status;
import io.grpc.StatusRuntimeException;
import io.grpc.TlsChannelCredentials;
import io.grpc.stub.StreamObserver;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import org.springframework.stereotype.Component;

@Component
public class ManagementClientTls {

  public static void main(String[] args) throws IOException {

    if (args.length == 0) {
      System.out.println("Need one argument to work");
      return ;
    }

    ChannelCredentials creds= TlsChannelCredentials.newBuilder().trustManager(
        new File("ssl/ca.crt")
    ).build();
    ManagedChannel channel = Grpc.newChannelBuilderForAddress("localhost",50501,creds).build();

    switch (args[0]) {
      case "manage":
        doManage(channel);
        break;
      default:
        System.out.println("Keyword invalid " + args[0]);
    }

    System.out.println("Shutting Down For Client ...");
    channel.shutdown();
  }

  private static void doManage(ManagedChannel channel) {
    System.out.println("Enter doManage");
    ManagementServiceGrpc.ManagementServiceBlockingStub stub = ManagementServiceGrpc.newBlockingStub(
        channel);

    ManagementResponse response = stub.manage(
        ManagementRequest.newBuilder().setFirstName("Jack").build());

    System.out.println("Management response: " + response.getResult());
  }

}
