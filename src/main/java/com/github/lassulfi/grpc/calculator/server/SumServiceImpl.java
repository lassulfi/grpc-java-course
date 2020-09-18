package com.github.lassulfi.grpc.calculator.server;

import com.proto.calculator.*;
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
}
