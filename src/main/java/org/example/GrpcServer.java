package org.example;

import io.grpc.Server;
import io.grpc.ServerBuilder;
import io.grpc.stub.StreamObserver;
import org.example.grpc.*;

import java.io.IOException;

public class GrpcServer {

    private static final int PORT = 50051;

    public static void main(String[] args) throws IOException, InterruptedException {
        Server server = ServerBuilder
                .forPort(PORT)
                .addService(new GreeterImpl())
                .build()
                .start();

        System.out.println("gRPC Server started on port " + PORT);
        System.out.println("Methods:");
        System.out.println("  SayHello (HelloRequest) -> HelloReply");
        System.out.println("  GetUsers (EmptyRequest) -> UserListReply");

        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            System.out.println("Shutting down gRPC server...");
            server.shutdown();
        }));

        server.awaitTermination();
    }

    // ── Service implementation ──────────────────────────────────────
    static class GreeterImpl extends GreeterGrpc.GreeterImplBase {

        @Override
        public void sayHello(HelloRequest request, StreamObserver<HelloReply> responseObserver) {
            System.out.println("[SayHello] name=" + request.getName());
            HelloReply reply = HelloReply.newBuilder()
                    .setMessage("Hello, " + request.getName() + "! (from gRPC server)")
                    .build();
            responseObserver.onNext(reply);
            responseObserver.onCompleted();
        }

        @Override
        public void getUsers(EmptyRequest request, StreamObserver<UserListReply> responseObserver) {
            System.out.println("[GetUsers] request received");
            UserListReply reply = UserListReply.newBuilder()
                    .addUsers(UserInfo.newBuilder().setId(1).setUserName("Alice").setIsActive(true).addRoles("Admin").build())
                    .addUsers(UserInfo.newBuilder().setId(2).setUserName("Bob").setIsActive(true).addRoles("User").build())
                    .addUsers(UserInfo.newBuilder().setId(3).setUserName("Carol").setIsActive(false).addRoles("User").addRoles("Moderator").build())
                    .build();
            responseObserver.onNext(reply);
            responseObserver.onCompleted();
        }
    }
}
