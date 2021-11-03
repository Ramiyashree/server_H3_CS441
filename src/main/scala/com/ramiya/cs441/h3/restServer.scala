package com.ramiya.cs441.h3

import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyResponseEvent

import java.util
import com.amazonaws.services.lambda.runtime.{Context, RequestHandler}
import com.google.gson.Gson

import java.time.LocalTime
//import com.mayankrastogi.cs441.hw6.protobuf.calculator.Expression
//import com.mayankrastogi.cs441.hw6.service.CalculatorService
import scalapb.json4s.JsonFormat
import com.google.gson.Gson


import scala.collection.JavaConverters._
import scala.concurrent.Await
import scala.concurrent.duration._

class restServer extends RequestHandler[util.Map[String, String], APIGatewayProxyResponseEvent] {

  // Create instance of Gson for (de)serializing Java Map to JSON string

  val gSon = new Gson()

    override def handleRequest(input: util.Map[String, String], context: Context): APIGatewayProxyResponseEvent = {

    // Get AWS Lambda Logger
    val logger = context.getLogger
//    logger.log("Request Body:\n" + input.asScala)
//    logger.log("context:\n" + gSon.toJson(input))
  //  logger.log("event:\n" + gSon.toJson(input))

      logger.log("event:\n" + input)

//    val time = LocalTime.parse("17:12:22.908")
//    val dT = LocalTime.parse("00:00:01.00")

      val time = LocalTime.parse(input.get("time"))
      val dT = LocalTime.parse(input.get("delta"))

    val result = BinarySearchRest.IntervalTime(time, dT)

    val response = ResultMessage(result.toString, Map("Content-Type" -> "application/json"))
    //  val response = Response(Map("logs" -> result), Map("Content-Type" -> "application/json"))

    val jSonresponse = gSon.toJson(response.body)
    logger.log("event" + input.getClass)

     new APIGatewayProxyResponseEvent()
      .withStatusCode(response.statusCode)
      .withHeaders(response.getHeaders)
      .withBody(jSonresponse)

  }

  case class ResultMessage(body: String, headers: Map[String, String], statusCode : Int = 200) {
    def getHeaders: java.util.Map[String, String] = headers.asJava
//    def getBody: java.util.Map[String = body.asJava
  }


  }

//    // Convert Java Map to JSON string and use ScalaPB to construct the `Expression`
//    val expression = JsonFormat.fromJsonString[Expression](gson.toJson(input))
//    logger.log(s"expression: $expression")
//
//    // Evaluate the expression using gRPC service
//    val result = Await.result(CalculatorService.evaluate(expression), 5 seconds)
//    logger.log(s"Result: $result")
//
//    // Use ScalaPB to convert `Response` protobuf to JSON and then convert it to Java Map using Gson
//    val response = gson.fromJson(JsonFormat.toJsonString(result), classOf[util.Map[String, Object]])
//
//    // Add the operation key to expression since `JsonFormat.toJsonString` skips enums for some reason
//    response.get("expression").asInstanceOf[util.Map[String, Object]].put("operation", expression.operation.name)
//    // Zero values are also getting removed, so put result as 0 if the key is missing
//    response.putIfAbsent("result", 0.0.asInstanceOf[Object])
//
//    // Send the response
//    response
//  }
//}


///////////////////////////////////

//class RestHandler extends RequestHandler[util.Map[String, String], APIGatewayProxyResponseEvent]
//{
//  val gson = new Gson()
//  override def handleRequest(event: util.Map[String, String], context: Context) : APIGatewayProxyResponseEvent = {
//    val logger : LambdaLogger = context.getLogger()
//    logger.log("CONTEXT: " + gson.toJson(context))
//    logger.log("EVENT: " + gson.toJson(event))
//    //val response : String = event.get("logTime")
//    val result = LogSearch.getLogs(event)
//
//
//    val response = Response(Map("logs" -> result), Map("Content-Type" -> "application/json"))
//    response.getBody
//    val jsonResponse = gson.toJson(response.getBody)
//    logger.log("EVENT TYPE: " + event.getClass)
//    return new APIGatewayProxyResponseEvent()
//      .withStatusCode(response.statusCode)
//      .withHeaders(response.getHeaders)
//      .withBody(jsonResponse)
//
//  }
//
//  //  case class LogTime(logTime: String) {
//  //    def getTime() : String = {
//  //      return logTime
//  //    }
//  //  }
//  case class Response(body: Map[String, Object], headers: Map[String,String], statusCode: Int = 200) {
//    def getHeaders: java.util.Map[String, String] = headers.asJava
//    def getBody: java.util.Map[String, Object] = body.asJava
//  }
//}