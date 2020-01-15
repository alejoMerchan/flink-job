package com.endava.flink.twitter.model

import java.util
import java.util.Date

case class TwitterEvent(author: String, date: Date, transactionTime: Long, hashTags: util.List[java.lang.String])
