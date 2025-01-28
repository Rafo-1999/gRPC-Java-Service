package com.gRPC.gRPC_Java_Service.blog.client;

import com.proto.blog.Blog;
import com.proto.blog.BlogId;
import com.proto.blog.BlogServiceGrpc;
import com.proto.blog.BlogServiceGrpc.BlogServiceBlockingStub;
import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import io.grpc.StatusRuntimeException;

public class BlogClient {

  public static void main(String[] args) {

    ManagedChannel channel = ManagedChannelBuilder.forAddress("localhost", 50501).usePlaintext()
        .build();



    System.out.println("Shutting Down For Client ...");
    channel.shutdown();
  }


  private static void run(ManagedChannel channel){
    BlogServiceGrpc.BlogServiceBlockingStub stub = BlogServiceGrpc.newBlockingStub(channel);
    BlogId blogId=createBlog(stub);

    if (blogId==null){
      return;
    }
  }

  private static BlogId createBlog(BlogServiceBlockingStub stub) {
    try {
      BlogId createResponse=stub.createBlog(
          Blog.newBuilder()
              .setAuthor("Jack")
              .setTitle("New Blod!")
              .setContent("Hello world this is a new blog!")
              .build()
      );
      System.out.println("Blog created: "+createResponse.getId());
      return createResponse;
    }catch (StatusRuntimeException e){
      System.out.println("Blog created failed: "+e.getMessage());
      e.printStackTrace();
      return null;
    }
  }


}
