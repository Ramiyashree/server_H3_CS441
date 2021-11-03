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
      logger.log("response length" + result.toString.split(",").length)

    val jSonresponse = gSon.toJson(response.body)


      new APIGatewayProxyResponseEvent()
      .withStatusCode(response.statusCode)
      .withHeaders(response.getHeaders)
      .withBody(jSonresponse)

  }

  case class ResultMessage(body : String, headers: Map[String, String]) {
    def getHeaders: java.util.Map[String, String] = headers.asJava
    val statusCode : Int = if(body.split(",").length == 1) 400 else 200
  }

  }
