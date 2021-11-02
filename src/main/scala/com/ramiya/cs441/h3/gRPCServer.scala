package com.ramiya.cs441.h3

import java.util.Base64
import com.amazonaws.services.lambda.runtime.events.{APIGatewayProxyRequestEvent, APIGatewayProxyResponseEvent}
import com.amazonaws.services.lambda.runtime.{Context, RequestHandler}

import scala.collection.JavaConverters._
import scala.concurrent.{Await, Future}
import scala.concurrent.duration._
import java.nio.charset.StandardCharsets
import h3.{TimeData, TimeResponse, logTimeFunctionGrpc}

import scala.language.postfixOps

class gRPCServer extends RequestHandler[APIGatewayProxyRequestEvent, APIGatewayProxyResponseEvent] {

  override def handleRequest(input: APIGatewayProxyRequestEvent, context: Context): APIGatewayProxyResponseEvent = {

    // Get AWS Lambda Logger
    val logger = context.getLogger
    logger.log("Request Body:\n" + input.toString)

    // Decode base-64 encoded binary data from the request body
    val message = if (input.getIsBase64Encoded) Base64.getDecoder.decode(input.getBody.getBytes) else input.getBody.getBytes
    logger.log(s"message: (${message.mkString(", ")})")

    // Construct the expression from binary data
    val dataTime = TimeData.parseFrom(message)
    val logTimeFind = new LogTimeSearch()
    val result = Await.result(logTimeFind.timeFunction(dataTime), atMost = 5 seconds)
   // val expression = TimeData.parseFrom(message)
    //val result = TimeResponse(true)
    print(s"result: ${result.result}")
    val output = Base64.getEncoder.encodeToString(result.toByteArray)

    //val out = Base64.getEncoder.encodeToString("user:pass".getBytes(StandardCharsets.UTF_8))

    logger.log(s"outaputallnew: $output" + "\n")

    // Send the response
    new APIGatewayProxyResponseEvent()
      .withStatusCode(200)
      .withHeaders(Map("Content-Type" -> "application/grpc+proto").asJava)
      .withIsBase64Encoded(true)
      .withBody(output)
  }

  private class LogTimeSearch extends logTimeFunctionGrpc.logTimeFunction{

    override def timeFunction(request: TimeData): Future[TimeResponse] = {
      val time = request.time

      print("time input" + time + "\n")

      val result1 = BinarySearch.findTime(time)
      print("result1" + result1 + "\n")
      val reply = TimeResponse(result1)
      Future.successful(reply)
    }
  }
}
