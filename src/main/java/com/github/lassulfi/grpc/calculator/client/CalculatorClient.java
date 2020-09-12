package com.github.lassulfi.grpc.calculator.client;

import com.proto.calculator.Sum;
import com.proto.calculator.SumRequest;
import com.proto.calculator.SumResponse;
import com.proto.calculator.SumServiceGrpc;
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

        System.out.println("Shutting down channel...");
        channel.shutdown();
    }
}
