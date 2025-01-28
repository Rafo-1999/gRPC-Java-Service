package com.gRPC.gRPC_Java_Service.management.client;

import com.proto.management.ManagementRequest;
import com.proto.management.ManagementResponse;
import com.proto.management.ManagementServiceGrpc;
import io.grpc.Deadline;
import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import io.grpc.Status;
import io.grpc.StatusRuntimeException;
import io.grpc.stub.StreamObserver;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import org.springframework.stereotype.Component;

@Component
public class ManagementClient {

  public static void main(String[] args) throws InterruptedException {

    getChannel(new String[]{"manageWithDeadline"});
  }


  private static ManagedChannel getChannel(String[] args) throws InterruptedException {
    if (args.length == 0) {
      System.out.println("Need one argument to work");
      return null;
    }

    ManagedChannel channel = ManagedChannelBuilder.forAddress("localhost", 50501).usePlaintext()
        .build();

    switch (args[0]) {
      case "manage":
        doManage(channel);
        break;
      case "manageManyTimes":
        doManageManyTimes(channel);
        break;
      case "longManage":
        doLongManage(channel);
        break;
      case "manageEveryone":
        doManageEveryone(channel);
        break;
      case "manageWithDeadline":
        doManageWithDeadline(channel);
        break;
      default:
        System.out.println("Keyword invalid " + args[0]);
    }

    System.out.println("Shutting Down For Client ...");
    channel.shutdown();

    return channel;
  }

  private static void doManage(ManagedChannel channel) {
    System.out.println("Enter doManage");
    ManagementServiceGrpc.ManagementServiceBlockingStub stub = ManagementServiceGrpc.newBlockingStub(
        channel);

    ManagementResponse response = stub.manage(
        ManagementRequest.newBuilder().setFirstName("Jack").build());

    System.out.println("Management response: " + response.getResult());
  }


  private static void doManageManyTimes(ManagedChannel channel) {

    System.out.println("Enter doManageManyTimes");

    ManagementServiceGrpc.ManagementServiceBlockingStub stub = ManagementServiceGrpc.newBlockingStub(
        channel);
    stub.manageManyTimes(ManagementRequest.newBuilder().setFirstName("Jack").build())
        .forEachRemaining(
            managementResponse -> System.out.println(managementResponse.getResult())
        );

  }

  private static void doLongManage(ManagedChannel channel) throws InterruptedException {
    System.out.println("Enter doLongManage");

    ManagementServiceGrpc.ManagementServiceStub stub = ManagementServiceGrpc.newStub(channel);
    List<String> names = new ArrayList<>();
    CountDownLatch countDownLatch = new CountDownLatch(1);

    Collections.addAll(names, "Jack", "Marie", "Daniel");

    StreamObserver<ManagementRequest> streamObserver = stub.longManage(new StreamObserver<>() {

      @Override
      public void onNext(ManagementResponse managementResponse) {
        System.out.println(managementResponse.getResult());
      }

      @Override
      public void onError(Throwable throwable) {

      }

      @Override
      public void onCompleted() {
        countDownLatch.countDown();
      }
    });

    for (String name : names) {
      streamObserver.onNext(ManagementRequest.newBuilder().setFirstName(name).build());
    }

    streamObserver.onCompleted();
    countDownLatch.await(3, TimeUnit.SECONDS);
  }


  private static void doManageEveryone(ManagedChannel channel) throws InterruptedException {
    System.out.println("Enter doManageEveryone");

    ManagementServiceGrpc.ManagementServiceStub stub = ManagementServiceGrpc.newStub(channel);
    CountDownLatch countDownLatch = new CountDownLatch(1);


    StreamObserver<ManagementRequest> manage=stub.manageEveryone(new StreamObserver<>() {

      @Override
      public void onNext(ManagementResponse managementResponse) {
        System.out.println(managementResponse.getResult());
      }

      @Override
      public void onError(Throwable throwable) {

      }

      @Override
      public void onCompleted() {
        countDownLatch.countDown();
      }
    });

    Arrays.asList("Jack", "Marie", "Daniel").forEach(name -> {
      manage.onNext(ManagementRequest.newBuilder().setFirstName(name).build());
    });

    manage.onCompleted();
    countDownLatch.await(3,TimeUnit.SECONDS);

  }


  private static void doManageWithDeadline(ManagedChannel channel) {
    System.out.println("Enter doManageWithDeadline");
    ManagementServiceGrpc.ManagementServiceBlockingStub stub = ManagementServiceGrpc.newBlockingStub(channel);
    ManagementResponse response = stub.withDeadline(Deadline.after(3,TimeUnit.SECONDS))
        .manageWithDeadline(ManagementRequest.newBuilder().setFirstName("Jack").build());

    System.out.println("manage with deadline response: " + response.getResult());

    try {
      response=stub.withDeadline(Deadline.after(100,TimeUnit.MILLISECONDS))
          .manageWithDeadline(ManagementRequest.newBuilder().setFirstName("Jack").build());

      System.out.println("manage with deadline response: " + response.getResult());
    }catch (StatusRuntimeException e){
      if (e.getStatus().getCode() == Status.Code.DEADLINE_EXCEEDED) {
        System.out.println("Deadline exceeded");
      }else {
        System.out.println("Got an exception in manageWithDeadline");
        e.printStackTrace();
      }
    }
  }


}
