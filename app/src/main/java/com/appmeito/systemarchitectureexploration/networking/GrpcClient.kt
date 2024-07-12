package com.appmeito.systemarchitectureexploration.networking

import io.grpc.ManagedChannel
import io.grpc.ManagedChannelBuilder
import myFirstGrpc.GreeterGrpc
import myFirstGrpc.MyFirstGrpc


class GrpcClient(private val channel: ManagedChannel) {

    private val stub: GreeterGrpc.GreeterBlockingStub = GreeterGrpc.newBlockingStub(channel)

    fun sayHello(name: String): String {
        val request = MyFirstGrpc.HelloRequest.newBuilder().setName(name).build()
        val response = stub.sayHello(request)
        return response.message
    }
    fun shutDown(){
        channel.shutdownNow()
    }

    companion object {
        fun create(host: String, port: Int): GrpcClient {
            val channel = ManagedChannelBuilder.forAddress(host, port)
                .usePlaintext()
                .build()
            return GrpcClient(channel)
        }

    }
}
