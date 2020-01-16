package com.endava.flink.twitter

trait JobException extends Exception {

  def getMsgException(): String

  def getException(): Throwable


}
