package com.appmeito.systemarchitectureexploration.networking

import android.content.ContentValues.TAG
import android.os.Environment
import android.util.Log
import io.grpc.CallOptions
import io.grpc.Channel
import io.grpc.ClientCall
import io.grpc.ClientInterceptor
import io.grpc.ForwardingClientCall
import io.grpc.ForwardingClientCallListener
import io.grpc.ManagedChannel
import io.grpc.ManagedChannelBuilder
import io.grpc.Metadata
import io.grpc.MethodDescriptor
import io.grpc.Status
import io.grpc.stub.StreamObserver
import myfirstgrpc.GreeterGrpc
import myfirstgrpc.MyFirstGrpc
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.io.OutputStream


class GrpcClient(private val channel: ManagedChannel) {

    private val stub: GreeterGrpc.GreeterBlockingStub = GreeterGrpc.newBlockingStub(channel)
    private val asyncStub: GreeterGrpc.GreeterStub = GreeterGrpc.newStub(channel)

    fun sayHello(name: String): String {
        val request = MyFirstGrpc.HelloRequest.newBuilder().setName(name).build()
        val response = stub.sayHello(request)
        return response.message
    }
    fun shutDown(){
        channel.shutdownNow()
    }

    fun streamGreetings(name: String) {
        val request = MyFirstGrpc.HelloRequest.newBuilder().setName(name).build()
        val responseObserver = object : StreamObserver<MyFirstGrpc.HelloReply> {
            override fun onNext(value: MyFirstGrpc.HelloReply) {
                println("Received Stream: ${value.message}")
            }

            override fun onError(t: Throwable) {
                t.printStackTrace()
            }

            override fun onCompleted() {
                println("Stream completed")
            }
        }
        asyncStub.streamGreetings(request, responseObserver)
    }

    fun sendGreetings(names: List<String>) {
        val responseObserver = object : StreamObserver<MyFirstGrpc.HelloReply> {
            override fun onNext(value: MyFirstGrpc.HelloReply) {
                println("Received: ${value.message}")
            }

            override fun onError(t: Throwable) {
                t.printStackTrace()
            }

            override fun onCompleted() {
                println("Send Stream completed")
            }
        }
        val requestObserver = asyncStub.sendGreetings(responseObserver)
        for (name in names) {
            val request = MyFirstGrpc.HelloRequest.newBuilder().setName(name).build()
            requestObserver.onNext(request)
        }
        requestObserver.onCompleted()
    }

    fun chat() {
        val responseObserver = object : StreamObserver<MyFirstGrpc.HelloReply> {
            override fun onNext(value: MyFirstGrpc.HelloReply) {
                println("Received Chat: ${value.message}")
            }

            override fun onError(t: Throwable) {
                t.printStackTrace()
            }

            override fun onCompleted() {
                println("Chat Stream completed")
            }
        }
        val requestObserver = asyncStub.chat(responseObserver)
        val names = listOf("Alice", "Bob", "Charlie")
        for (name in names) {
            val request = MyFirstGrpc.HelloRequest.newBuilder().setName(name).build()
            requestObserver.onNext(request)
        }
        requestObserver.onCompleted()
    }

    fun downloadFile(filename: String,onDownloadComplete: () -> Unit) {
        val request = MyFirstGrpc.FileRequest.newBuilder()
            .setFilename(filename)
            .build()

        asyncStub.downloadFile(request, object : StreamObserver<MyFirstGrpc.FileChunk> {
            private var outputStream: FileOutputStream? = null

            override fun onNext(fileChunk: MyFirstGrpc.FileChunk) {
                try {
                    if (outputStream == null) {
                        val downloadDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)
                        val outputFile = File(downloadDir, filename)
                        outputStream = FileOutputStream(outputFile)
                    }
                    outputStream?.write(fileChunk.content.toByteArray())
                } catch (e: IOException) {
                    Log.e(TAG, "Error writing file chunk", e)
                }
            }

            override fun onError(t: Throwable) {
                Log.e(TAG, "File download failed", t)
                try {
                    outputStream?.close()
                } catch (e: IOException) {
                    Log.e(TAG, "Error closing file output stream", e)
                }
            }

            override fun onCompleted() {
                try {
                    outputStream?.close()
                    Log.i(TAG, "File download completed")
                } catch (e: IOException) {
                    Log.e(TAG, "Error closing file output stream", e)
                }
                onDownloadComplete()
            }
        })
    }

    companion object {
        fun create(host: String, port: Int): GrpcClient {
            val channel = ManagedChannelBuilder.forAddress(host, port)
                .usePlaintext()
                .intercept(LoggingInterceptor())
                .build()
            return GrpcClient(channel)
        }

    }
}

class LoggingInterceptor : ClientInterceptor {
    override fun <ReqT, RespT> interceptCall(
        method: MethodDescriptor<ReqT, RespT>,
        callOptions: CallOptions,
        next: Channel
    ): ClientCall<ReqT, RespT> {
        return object : ForwardingClientCall.SimpleForwardingClientCall<ReqT, RespT>(next.newCall(method, callOptions)) {
            override fun start(responseListener: Listener<RespT>?, headers: io.grpc.Metadata?) {
                println("Request headers: $headers")
                super.start(object : ForwardingClientCallListener.SimpleForwardingClientCallListener<RespT>(responseListener) {
                    override fun onMessage(message: RespT) {
                        println("Response  message: $message")
                        super.onMessage(message)
                    }

                    override fun onClose(status: Status?, trailers: Metadata?) {
                        println("Response trailers: $trailers")
                        super.onClose(status, trailers)
                    }
                }, headers)
            }

            override fun sendMessage(message: ReqT) {
                println("Request message: $message")
                super.sendMessage(message)
            }
        }
    }
}
