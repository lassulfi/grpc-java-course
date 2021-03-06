package com.github.lassulfi.grpc.calculator.server;

import com.proto.calculator.*;
import io.grpc.Status;
import io.grpc.stub.StreamObserver;

public class SumServiceImpl extends SumServiceGrpc.SumServiceImplBase {

    @Override
    public void sum(SumRequest request, StreamObserver<SumResponse> responseObserver) {
        Sum sum = request.getSum();
        int firstValue = sum.getFirstValue();
        int secondValue = sum.getSecondValue();

        int result = firstValue + secondValue;

        SumResponse response = SumResponse.newBuilder()
                .setResult(result)
                .build();

        responseObserver.onNext(response);

        responseObserver.onCompleted();
    }

    @Override
    public void primeNumberDecomposition(PrimeRequest request, StreamObserver<PrimeResponse> responseObserver) {
        int number = request.getNumber();
        int k = 2;

        try {
            while (number > 1) {
                if (number % k == 0) {
                    int result = k;
                    PrimeResponse response = PrimeResponse.newBuilder()
                            .setResult(result)
                            .build();
                    responseObserver.onNext(response);
                    Thread.sleep(1000L);
                    number = number / k;
                } else {
                    k = k + 1;
                }
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        } finally {
            responseObserver.onCompleted();
        }
    }

    @Override
    public StreamObserver<AverageRequest> calculateAverage(StreamObserver<AverageResponse> responseObserver) {
        StreamObserver<AverageRequest> requestObserver = new StreamObserver<AverageRequest>() {
            int count = 0;
            double sum = 0;

            @Override
            public void onNext(AverageRequest request) {
                int number = request.getNumber();
                sum += number;
                count++;
            }

            @Override
            public void onError(Throwable t) {

            }

            @Override
            public void onCompleted() {
                double average = sum / count;
                responseObserver.onNext(AverageResponse.newBuilder()
                        .setAverage(average)
                        .build());
                responseObserver.onCompleted();
            }
        };

        return requestObserver;
    }

    @Override
    public StreamObserver<FindMaximumRequest> findMaximum(StreamObserver<FindMaximumResponse> responseObserver) {
        StreamObserver<FindMaximumRequest> requestObserver = new StreamObserver<FindMaximumRequest>() {
            int max = Integer.MIN_VALUE;
            @Override
            public void onNext(FindMaximumRequest value) {
                int number = value.getNumber();
                if (number > max) {
                    max = number;
                    FindMaximumResponse findMaximumResponse = FindMaximumResponse.newBuilder()
                            .setMaximum(max)
                            .build();
                    responseObserver.onNext(findMaximumResponse);
                }
            }

            @Override
            public void onError(Throwable t) {
                responseObserver.onCompleted();
            }

            @Override
            public void onCompleted() {
                FindMaximumResponse findMaximumResponse = FindMaximumResponse.newBuilder()
                        .setMaximum(max)
                        .build();
                responseObserver.onNext(findMaximumResponse);
                responseObserver.onCompleted();
            }
        };

        return requestObserver;
    }

    @Override
    public void squareRoot(SquareRootRequest request, StreamObserver<SquareRootResponse> responseObserver) {
        Integer number = request.getNumber();
        if (number >= 0) {
            double result = Math.sqrt(number);
            responseObserver.onNext(SquareRootResponse.newBuilder()
                    .setResult(result)
                    .build());
            responseObserver.onCompleted();
        } else {
            //we contruct the error message
            responseObserver.onError(
                    Status.INVALID_ARGUMENT
                            .withDescription("The request number value must be positive")
                            .augmentDescription("Request number: " + number)
                            .asRuntimeException()
            );
        }
    }
}
