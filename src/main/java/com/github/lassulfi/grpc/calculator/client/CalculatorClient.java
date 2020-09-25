package com.github.lassulfi.grpc.calculator.client;

import com.proto.calculator.*;
import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import io.grpc.stub.StreamObserver;

import java.util.Arrays;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

public class CalculatorClient {

    public static void main(String[] args) {
        System.out.println("Starting calculator client...");

        CalculatorClient main = new CalculatorClient();
        main.run();
    }

    private void run() {
        ManagedChannel channel = ManagedChannelBuilder
                .forAddress("localhost", 50052)
                .usePlaintext()
                .build();

//        doUnaryCall(channel);
//        doServerStreamingCall(channel);
//        doClientStreamingCall(channel);
        doBiDiStreamingCall(channel);

        System.out.println("Shutting down channel...");
        channel.shutdown();
    }

    private void doUnaryCall(ManagedChannel channel) {
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
    }

    private void doServerStreamingCall(ManagedChannel channel) {
        System.out.println("Creating a stub...");

        SumServiceGrpc.SumServiceBlockingStub sumClient = SumServiceGrpc.newBlockingStub(channel);

        // Server Streaming
        System.out.println("Server streaming request...");
        PrimeRequest primeRequest = PrimeRequest.newBuilder()
                .setNumber(120)
                .build();

        sumClient.primeNumberDecomposition(primeRequest).forEachRemaining(response -> {
            System.out.println("Prime number: " + response.getResult());
        });
    }

    private void doClientStreamingCall(ManagedChannel channel) {
        SumServiceGrpc.SumServiceStub asyncClient = SumServiceGrpc.newStub(channel);

        CountDownLatch latch = new CountDownLatch(1);

        StreamObserver<AverageRequest> requestObserver = asyncClient.calculateAverage(new StreamObserver<AverageResponse>() {
            @Override
            public void onNext(AverageResponse value) {
                System.out.println("Received a response from the server");
                System.out.println("Average: " + value.getAverage());
            }

            @Override
            public void onError(Throwable t) {

            }

            @Override
            public void onCompleted() {
                System.out.println("Server has completed sending us something!");
                latch.countDown();
            }
        });

        for(int i = 1; i <= 10000; i++) {
            System.out.println("Sending message " + i);
            requestObserver.onNext(AverageRequest.newBuilder()
                    .setNumber(i)
                    .build());
        }
        requestObserver.onCompleted();

        try {
            latch.await(4L, TimeUnit.SECONDS);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    private void doBiDiStreamingCall(ManagedChannel channel) {
        SumServiceGrpc.SumServiceStub asyncClient = SumServiceGrpc.newStub(channel);

        CountDownLatch latch = new CountDownLatch(1);

        StreamObserver<FindMaximumRequest> requestObserver = asyncClient.findMaximum(new StreamObserver<FindMaximumResponse>() {
            @Override
            public void onNext(FindMaximumResponse value) {
                System.out.println("Response from server - new max value = " + value.getMaximum());
            }

            @Override
            public void onError(Throwable t) {
                latch.countDown();
            }

            @Override
            public void onCompleted() {
                System.out.println("Server is done sending data");
                latch.countDown();
            }
        });

        Arrays.asList(1, 5, 3, 6, 2, 20).forEach(number -> {
            System.out.println("Sending number: " + number);
            requestObserver.onNext(FindMaximumRequest.newBuilder()
                    .setNumber(number)
                    .build());
            try {
                Thread.sleep(150L);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        });

        requestObserver.onCompleted();


        try {
            latch.await(3L, TimeUnit.SECONDS);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
