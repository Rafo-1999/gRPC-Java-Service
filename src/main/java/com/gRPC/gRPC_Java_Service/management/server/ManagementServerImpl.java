package com.gRPC.gRPC_Java_Service.management.server;

import com.proto.management.ManagementRequest;
import com.proto.management.ManagementResponse;
import com.proto.management.ManagementServiceGrpc;
import io.grpc.stub.StreamObserver;

public class ManagementServerImpl extends ManagementServiceGrpc.ManagementServiceImplBase {


  @Override
  public void manage(ManagementRequest request, StreamObserver<ManagementResponse> responseObserver) {

    responseObserver.onNext(ManagementResponse.newBuilder().setResult("Manage"+ request.getFirstName()).build());
    responseObserver.onCompleted();
  }

  @Override
  public void manageManyTimes(ManagementRequest request, StreamObserver<ManagementResponse> responseObserver) {
    ManagementResponse response=ManagementResponse.newBuilder().setResult("Manage"+ request.getFirstName()).build();

    for (int i=0;i<10;i++){
      responseObserver.onNext(response);
    }
    responseObserver.onCompleted();
  }


  @Override
  public StreamObserver<ManagementRequest> longManage(StreamObserver<ManagementResponse> responseObserver) {
    StringBuilder sb=new StringBuilder();

    return new StreamObserver<ManagementRequest>() {

      @Override
      public void onNext(ManagementRequest request) {
          sb.append("Hello ");
          sb.append(request.getFirstName());
          sb.append( " !\n");
      }

      @Override
      public void onError(Throwable throwable) {

        responseObserver.onError(throwable);
      }

      @Override
      public void onCompleted() {
        responseObserver.onNext(ManagementResponse.newBuilder().setResult(sb.toString()).build());
        responseObserver.onCompleted();
      }
    };
  }


  @Override
  public StreamObserver<ManagementRequest> manageEveryone(StreamObserver<ManagementResponse> responseObserver) {
    return new StreamObserver<>() {

      @Override
      public void onNext(ManagementRequest request) {
        responseObserver.onNext(ManagementResponse.newBuilder().setResult("Hello  " +request.getFirstName()).build());
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
}
