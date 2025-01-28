package com.gRPC.gRPC_Java_Service.calculator.server;

import com.proto.calculator.AvgRequest;
import com.proto.calculator.AvgResponse;
import com.proto.calculator.CalculatorServiceGrpc;
import com.proto.calculator.MaxRequest;
import com.proto.calculator.MaxResponse;
import com.proto.calculator.PrimeRequest;
import com.proto.calculator.PrimeResponse;
import com.proto.calculator.SqrtRequest;
import com.proto.calculator.SqrtResponse;
import com.proto.calculator.SumRequest;
import com.proto.calculator.SumResponse;
import io.grpc.Status;
import io.grpc.stub.StreamObserver;

public class CalculatorServerImpl extends CalculatorServiceGrpc.CalculatorServiceImplBase {

  @Override
  public void sum(SumRequest request, StreamObserver<SumResponse> responseObserver) {
    responseObserver.onNext(SumResponse.newBuilder().setSum(
        request.getFirsNumber()+request.getSecondNumber()
    ).build());
    responseObserver.onCompleted();
  }

  @Override
  public void prime(PrimeRequest request, StreamObserver<PrimeResponse> responseObserver) {
    int number=request.getNumber();
    int divisor=2;

    while (number>1){
      if (number%divisor==0){
        number=number/divisor;
        responseObserver.onNext(PrimeResponse.newBuilder().setPrimeFactor(divisor).build());
      }else {
        ++divisor;
      }
      responseObserver.onCompleted();
    }
  }

  @Override
  public StreamObserver<AvgRequest> avg(StreamObserver<AvgResponse> responseObserver){
    final int[] sum = {0};
    final int[] count = {0};

    return new StreamObserver<>() {

      @Override
      public void onNext(AvgRequest avgRequest) {
        sum[0] +=avgRequest.getNumber();
        ++count[0];
      }

      @Override
      public void onError(Throwable throwable) {

        responseObserver.onError(throwable);
      }

      @Override
      public void onCompleted() {
      responseObserver.onNext(AvgResponse.newBuilder().setResult(
          (double) sum[0] / count[0]
      ).build());
      responseObserver.onCompleted();
      }
    };
  }

  @Override
  public StreamObserver<MaxRequest> max(StreamObserver<MaxResponse> responseObserver){
    return new StreamObserver<>() {

      int max=Integer.MIN_VALUE;
      @Override
      public void onNext(MaxRequest maxRequest) {

        if (maxRequest.getNumber()>max){
          max=maxRequest.getNumber();
          responseObserver.onNext(MaxResponse.newBuilder().setMax(max).build());
        }
      }

      @Override
      public void onError(Throwable throwable) {
      responseObserver.onError(throwable);
      }

      @Override
      public void onCompleted() {
        responseObserver.onCompleted();
      }
    };
  }

  @Override
  public void sqrt(SqrtRequest request,StreamObserver<SqrtResponse> responseObserver){
    int number=request.getNumber();

    if (number<0){
      responseObserver.onError(Status.INVALID_ARGUMENT
          .withDescription("The number being sent cannot be negative")
          .augmentDescription("Number: "+number)
          .asRuntimeException()
      );
      return;
    }
    responseObserver.onNext(
        SqrtResponse.newBuilder().setResult(Math.sqrt(number)).build()
    );
    responseObserver.onCompleted();
  }
}
