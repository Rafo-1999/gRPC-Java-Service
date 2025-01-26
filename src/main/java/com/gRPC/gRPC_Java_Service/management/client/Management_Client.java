package com.gRPC.gRPC_Java_Service.management.client;

import com.proto.management.ManagementRequest;
import com.proto.management.ManagementResponse;
import com.proto.management.ManagementServiceGrpc;
import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import io.grpc.stub.StreamObserver;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import org.springframework.stereotype.Component;

@Component
public class Management_Client {

public static void main(String[] args) throws InterruptedException {

    getChannel(new String[]{ "longManage"});
}


private static ManagedChannel getChannel(String[] args) throws InterruptedException {
    if (args.length == 0) {
      System.out.println("Need one argument to work");
      return null;
    }

    ManagedChannel channel = ManagedChannelBuilder.forAddress("localhost", 50501).usePlaintext()
        .build();

   switch (args[0]){
     case "manage":doManage(channel);break;
     case  "manageManyTimes": doManageManyTimes(channel);break;
     case "longManage": doLongManage(channel); break;
     default:
       System.out.println("Keyword invalid "+args[0]);
   }

    System.out.println("Shutting Down For Client ...");
    channel.shutdown();

    return channel;
  }

  private static void doManage(ManagedChannel channel) {
    System.out.println("Enter doManage");
    ManagementServiceGrpc.ManagementServiceBlockingStub stub = ManagementServiceGrpc.newBlockingStub(channel);

    ManagementResponse response=stub.manage(ManagementRequest.newBuilder().setFirstName("Jack").build());

    System.out.println("Management response: "+response.getResult());
  }



  private static void doManageManyTimes(ManagedChannel channel) {

    System.out.println("Enter doManageManyTimes");

    ManagementServiceGrpc.ManagementServiceBlockingStub stub = ManagementServiceGrpc.newBlockingStub(channel);
    stub.manageManyTimes(ManagementRequest.newBuilder().setFirstName("Jack").build()).forEachRemaining(
        managementResponse-> System.out.println(managementResponse.getResult())
    );

  }

  private static void doLongManage(ManagedChannel channel) throws InterruptedException {
    System.out.println("Enter doLongManage");

    ManagementServiceGrpc.ManagementServiceStub stub=ManagementServiceGrpc.newStub(channel);
    List<String> names=new ArrayList<>();
    CountDownLatch countDownLatch=new CountDownLatch(1);

    Collections.addAll(names,"Jack","Marie","Daniel");

    StreamObserver<ManagementRequest> streamObserver=stub.longManage(new StreamObserver<>() {

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

    for (String name:names){
      streamObserver.onNext(ManagementRequest.newBuilder().setFirstName(name).build());
    }

    streamObserver.onCompleted();
    countDownLatch.await(3, TimeUnit.SECONDS);
  }


}
