package com.github.lassulfi.grpc.greeting.client;

import com.proto.dummy.DummyServiceGrpc;
import com.proto.greet.*;
import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;

public class GreetingClient {

    public static void main(String[] args) {
        System.out.println("Hello, I'm a gRPC client!");

        ManagedChannel channel = ManagedChannelBuilder
                .forAddress("localhost", 50051)
                .usePlaintext()
                .build();

        System.out.println("Creating stub");
        // old and dummy
        // DummyServiceGrpc.DummyServiceBlockingStub syncClient = DummyServiceGrpc.newBlockingStub(channel);

        // DummyServiceGrpc.DummyServiceFutureStub asyncClient = DummyServiceGrpc.newFutureStub(channel);
        // Created a greet service client (blocking - synchronous)
        GreetServiceGrpc.GreetServiceBlockingStub greetClient = GreetServiceGrpc.newBlockingStub(channel);

        // Unary
        // created a protocol buffer message
//        Greeting greeting = Greeting.newBuilder()
//                .setFirstName("Luis Daniel")
//                .setLastName("Assulfi")
//                .build();
//
//        // do the same for the GreetRequest
//        GreetRequest greetRequest = GreetRequest.newBuilder()
//                .setGreeting(greeting)
//                .build();
//
//        // Call the RPC and get back a GreetResponse (procotol buffers)
//        GreetResponse greetResponse = greetClient.greet(greetRequest);
//        System.out.println(greetResponse.getResult());

        //Server streaming
        GreetManyTimesRequest request = GreetManyTimesRequest.newBuilder()
                .setGreeting(Greeting.newBuilder()
                        .setFirstName("Luis Daniel")
                        .setLastName("Assulfi")
                        .build())
                .build();

        // stream the responses in a blocking manner
        greetClient.greetManyTimes(request).forEachRemaining(response -> {
            System.out.println(response.getResult());
        });

        // do something
        System.out.println("Shutting down channel");
        channel.shutdown();
    }
}
