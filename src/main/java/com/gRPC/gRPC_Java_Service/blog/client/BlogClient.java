package com.gRPC.gRPC_Java_Service.blog.client;

import com.google.protobuf.Empty;
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


    run(channel);

    System.out.println("Shutting Down For Client ...");
    channel.shutdown();
  }


  private static void run(ManagedChannel channel){
    BlogServiceGrpc.BlogServiceBlockingStub stub = BlogServiceGrpc.newBlockingStub(channel);
    BlogId blogId=createBlog(stub);

    if (blogId==null)
      return;

    readBlog(stub,blogId);
    updateBlog(stub,blogId);
    listBlogs(stub);
    deleteBlog(stub,blogId);
  }

  private static void readBlog(BlogServiceGrpc.BlogServiceBlockingStub stub,BlogId blogId){
    try {
      Blog readResponse=stub.readBlog(blogId);
      System.out.println("Blog read : "+readResponse);
    }catch (StatusRuntimeException e){
      System.out.println("Couldn't read blog");
      e.printStackTrace();
    }
  }

  private static void updateBlog(BlogServiceGrpc.BlogServiceBlockingStub stub,BlogId blogId){
    try {
      Blog newBlog=Blog.newBuilder()
          .setId(blogId.getId())
          .setAuthor("Jack")
          .setTitle("New Blog (Changed)")
          .setContent("Hello world this is my first blog! I've added some content")
          .build();

      stub.updateBlog(newBlog);
      System.out.println("Blog updated : "+newBlog);
    }catch (StatusRuntimeException e){
      System.out.println("Couldn't update blog");
      e.printStackTrace();
    }
  }

  private static void listBlogs(BlogServiceGrpc.BlogServiceBlockingStub stub){
    stub.listBlogs(Empty.getDefaultInstance()).forEachRemaining(System.out::println);
  }


  private static void deleteBlog(BlogServiceGrpc.BlogServiceBlockingStub stub,BlogId blogId){
    try {
      stub.deleteBlog(blogId);
      System.out.println("Blog deleted : "+blogId);
    }catch (StatusRuntimeException e){
      System.out.println("Couldn't delete blog");
      e.printStackTrace();
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
