package com.github.lassulfi.grpc.blog.server;

import io.grpc.Server;
import io.grpc.ServerBuilder;
import io.grpc.protobuf.services.ProtoReflectionService;

import java.io.IOException;

public class BlogServer {

    public static void main(String[] args) throws IOException, InterruptedException {
        System.out.println("Starting blog server");

        Server server = ServerBuilder.forPort(50051)
                .addService(new BlogServiceImpl())
                .addService(ProtoReflectionService.newInstance()) // reflection
                .build();
        server.start();

        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            System.out.println("Received a shutdown request...");
            server.shutdown();
            System.out.println("Successfully shut down the server.");
        }));

        server.awaitTermination();
    }
}
