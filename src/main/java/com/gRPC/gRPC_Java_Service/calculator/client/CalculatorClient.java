package com.gRPC.gRPC_Java_Service.calculator.client;

import com.proto.calculator.CalculatorServiceGrpc;
import com.proto.calculator.PrimeRequest;
import com.proto.calculator.SumRequest;
import com.proto.calculator.SumResponse;
import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;

public class CalculatorClient {

  public static void main(String[] args) {
    if (args.length == 0) {
      System.out.println("Need one argument to work with calculator");
      return;
    }

    ManagedChannel managedChannel = ManagedChannelBuilder.forAddress("localhost",50502)
        .usePlaintext()
        .build();

    switch (args[0]){
      case "sum" : doSum(managedChannel); break;
      case "primes" :doPrimes(managedChannel);break;
      default:  System.out.println("Keyword Invalid " +args[0]);
    }
  }


  private static void doSum(ManagedChannel managedChannel) {
    System.out.println("Enter numbers for sum ");
    CalculatorServiceGrpc.CalculatorServiceBlockingStub stub = CalculatorServiceGrpc.newBlockingStub(managedChannel);
    SumResponse response = stub.sum(SumRequest.newBuilder().setFirsNumber(10).setSecondNumber(3).build());

    System.out.println("Sum 10+3 = " +response.getSum());
  }


  private static void doPrimes(ManagedChannel managedChannel) {
    System.out.println("Enter numbers for primes ");

    CalculatorServiceGrpc.CalculatorServiceBlockingStub stub = CalculatorServiceGrpc.newBlockingStub(managedChannel);

    stub.prime(PrimeRequest.newBuilder().setNumber(567890).build()).forEachRemaining(
        primeResponse -> System.out.println("Prime " + primeResponse)
    );
  }

}
