package com.appmeito.systemarchitectureexploration.networking

import io.grpc.ManagedChannel
import io.grpc.ManagedChannelBuilder
import myFirstGrpc.GreeterGrpc
import myFirstGrpc.GreeterGrpc.GreeterStub
import myFirstGrpc.MyFirstGrpc
import java.util.logging.Logger


class GrpcClient(private val channel: ManagedChannel) {

    private val stub: GreeterGrpc.GreeterBlockingStub = GreeterGrpc.newBlockingStub(channel)
    private val logger: Logger = Logger.getLogger(GrpcClient::class.java.name)

    fun sayHello(name: String): String {
        val request = MyFirstGrpc.HelloRequest.newBuilder().setName(name).build()
        logger.info("Sending request: $request")
        val response = stub.sayHello(request)
        logger.info("Received response: $response")
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
