package com.github.lassulfi.grpc.calculator.client;

import com.proto.calculator.*;
import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;

public class CalculatorClient {

    public static void main(String[] args) {
        System.out.println("Starting calculator client...");

        ManagedChannel channel = ManagedChannelBuilder
                .forAddress("localhost", 50052)
                .usePlaintext()
                .build();

        System.out.println("Creating a stub...");

        SumServiceGrpc.SumServiceBlockingStub sumClient = SumServiceGrpc.newBlockingStub(channel);

        int firstValue = 3;
        int secondValue = 10;

        // Unary
        System.out.println("Unary Request...");
        Sum sum = Sum.newBuilder()
                .setFirstValue(firstValue)
                .setSecondValue(secondValue)
                .build();

        SumRequest sumRequest = SumRequest.newBuilder()
                .setSum(sum)
                .build();

        SumResponse sumResponse = sumClient.sum(sumRequest);
        System.out.println("The result of the sum of " + firstValue + " to "
                + secondValue + " is " + sumResponse.getResult());

        // Server Streaming
        System.out.println("Server streaming request...");
        PrimeRequest primeRequest = PrimeRequest.newBuilder()
                .setNumber(120)
                .build();

        sumClient.primeNumberDecomposition(primeRequest).forEachRemaining(response -> {
            System.out.println("Prime number: " + response.getResult());
        });

        System.out.println("Shutting down channel...");
        channel.shutdown();
    }
}
