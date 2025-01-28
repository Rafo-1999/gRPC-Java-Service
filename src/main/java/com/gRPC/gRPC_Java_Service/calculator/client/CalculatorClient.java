package com.gRPC.gRPC_Java_Service.calculator.client;

import com.proto.calculator.AvgRequest;
import com.proto.calculator.AvgResponse;
import com.proto.calculator.CalculatorServiceGrpc;
import com.proto.calculator.MaxRequest;
import com.proto.calculator.MaxResponse;
import com.proto.calculator.PrimeRequest;
import com.proto.calculator.SumRequest;
import com.proto.calculator.SumResponse;
import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import io.grpc.stub.StreamObserver;
import java.util.Arrays;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

public class CalculatorClient {

  public static void main(String[] args) throws InterruptedException {
    if (args.length == 0) {
      System.out.println("Need one argument to work with calculator");
      return;
    }

    ManagedChannel managedChannel = ManagedChannelBuilder.forAddress("localhost", 50502)
        .usePlaintext()
        .build();

    switch (args[0]) {
      case "sum":
        doSum(managedChannel);
        break;
      case "primes":
        doPrimes(managedChannel);
        break;
      case "avg":
        doAvg(managedChannel);
        break;
        case "max":
        doMax(managedChannel);
        break;
      default:
        System.out.println("Keyword Invalid " + args[0]);
    }
  }

  private static void doSum(ManagedChannel managedChannel) {
    System.out.println("Enter numbers for sum ");
    CalculatorServiceGrpc.CalculatorServiceBlockingStub stub = CalculatorServiceGrpc.newBlockingStub(
        managedChannel);
    SumResponse response = stub.sum(
        SumRequest.newBuilder().setFirsNumber(10).setSecondNumber(3).build());

    System.out.println("Sum 10+3 = " + response.getSum());
  }


  private static void doPrimes(ManagedChannel managedChannel) {
    System.out.println("Enter numbers for primes ");

    CalculatorServiceGrpc.CalculatorServiceBlockingStub stub = CalculatorServiceGrpc.newBlockingStub(
        managedChannel);

    stub.prime(PrimeRequest.newBuilder().setNumber(567890).build()).forEachRemaining(
        primeResponse -> System.out.println("Prime " + primeResponse)
    );
  }


  private static void doAvg(ManagedChannel managedChannel) throws InterruptedException {
    System.out.println("Enter numbers for avg ");

    CalculatorServiceGrpc.CalculatorServiceStub stub = CalculatorServiceGrpc.newStub(
        managedChannel);
    CountDownLatch latch = new CountDownLatch(1);

    StreamObserver<AvgRequest> stream = stub.avg(new StreamObserver<>() {

      @Override
      public void onNext(AvgResponse avgResponse) {
        System.out.println("Avg= " + avgResponse.getResult());
      }

      @Override
      public void onError(Throwable throwable) {

      }

      @Override
      public void onCompleted() {
        latch.countDown();
      }
    });

    Arrays.asList(1,2,3,4,5,6,7,8,9,10).forEach(numbr->
        stream.onNext(AvgRequest.newBuilder().setNumber(numbr).build()));

    stream.onCompleted();
    latch.await(3, TimeUnit.SECONDS);
  }


  private static void doMax(ManagedChannel managedChannel) throws InterruptedException {
    System.out.println("Enter numbers for max ");

    CalculatorServiceGrpc.CalculatorServiceStub stub=CalculatorServiceGrpc.newStub(managedChannel);
    CountDownLatch latch = new CountDownLatch(1);

    StreamObserver<MaxRequest> stream=stub.max(new StreamObserver<>() {

      @Override
      public void onNext(MaxResponse maxResponse) {
        System.out.println("Max= " +maxResponse.getMax());
      }

      @Override
      public void onError(Throwable throwable) {

      }

      @Override
      public void onCompleted() {
        latch.countDown();
      }
    });

    Arrays.asList(1,5,3,6,2,20).forEach(numbr->
        stream.onNext(MaxRequest.newBuilder().setNumber(numbr).build()));

    stream.onCompleted();
    latch.await(5,TimeUnit.SECONDS);
  }

}
