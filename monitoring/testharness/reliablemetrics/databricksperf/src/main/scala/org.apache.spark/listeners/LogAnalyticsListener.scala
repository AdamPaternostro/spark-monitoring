package org.apache.spark.listeners

import org.apache.spark.internal.Logging
import org.apache.spark.scheduler._
import org.apache.spark.util.Utils
import org.apache.spark.{SparkConf,LogAnalytics,LogAnalyticsPerf2}
//import org.apache.spark.LogAnalyticsListenerConfiguration
import org.apache.spark.LogAnalyticsListenerConfiguration2
/**
  * A SparkListener that logs events to a Log Analytics workspace.
  *
  * Event logging is specified by the following configurable parameters:
  *   spark.logAnalytics.workspaceId - Log Analytics Workspace ID
  *   spark.logAnalytics.workspaceKey" - Key for the Log Analytics Workspace ID
  *   spark.logAnalytics.logType" - Optional Log Type name for Log Analytics
  *   spark.logAnalytics.timestampFieldName" - Optional field name for the event timestamp
  *   spark.logAnalytics.logBlockUpdates" - Optional setting specifying whether or not to log block updates
  */
class LogAnalyticsListener(sparkConf: SparkConf)
  extends SparkListener with Logging /*with LogAnalytics*/ {
// @transient lazy val logger = new LogAnalyticsPerf()

  val config = new LogAnalyticsListenerConfiguration2(sparkConf)
  val logger = new LogAnalyticsPerf2(config)

 // val config = new LogAnalyticsListenerConfiguration(sparkConf)
//  val logger = new LogAnalyticsPerf()


  override def onStageSubmitted(event: SparkListenerStageSubmitted): Unit ={

    logger.logEvent(event)
  }

  override def onTaskStart(event: SparkListenerTaskStart): Unit = {
    logger.logEvent(event)
  }


  //override def onTaskGettingResult(event: SparkListenerTaskGettingResult): Unit = logEvent(event)

  override def onTaskEnd(event: SparkListenerTaskEnd): Unit = {
    logger.logEvent(event)
  }

 /* override def onEnvironmentUpdate(event: SparkListenerEnvironmentUpdate): Unit = {
    logEvent(redactEvent(event))
  }*/

  override def onStageCompleted(event: SparkListenerStageCompleted): Unit = {
    logger.logEvent(event)
  }

 override def onJobStart(event: SparkListenerJobStart): Unit = {
   logger.logEvent(event)

 }
  override def onJobEnd(event: SparkListenerJobEnd): Unit ={

    logger.logEvent(event)

  }

 /* override def onBlockManagerAdded(event: SparkListenerBlockManagerAdded): Unit = {
    logEvent(event)
  }*/

  /*override def onBlockManagerRemoved(event: SparkListenerBlockManagerRemoved): Unit = {
    logEvent(event)
  }*/

 /* override def onUnpersistRDD(event: SparkListenerUnpersistRDD): Unit = {
    logEvent(event)
  }
*/
  override def onApplicationStart(event: SparkListenerApplicationStart): Unit = {
    logger.logEvent(event)
  }

  override def onApplicationEnd(event: SparkListenerApplicationEnd): Unit = {
    logger.logEvent(event)
  }
  override def onExecutorAdded(event: SparkListenerExecutorAdded): Unit = {
    logger.logEvent(event)
  }

  override def onExecutorRemoved(event: SparkListenerExecutorRemoved): Unit = {
    logger.logEvent(event)
  }

  override def onExecutorBlacklisted(event: SparkListenerExecutorBlacklisted): Unit = {
    logger.logEvent(event)
  }

  override def onExecutorUnblacklisted(event: SparkListenerExecutorUnblacklisted): Unit = {
    logger.logEvent(event)
  }

  override def onNodeBlacklisted(event: SparkListenerNodeBlacklisted): Unit = {
    logger.logEvent(event)
  }

  override def onNodeUnblacklisted(event: SparkListenerNodeUnblacklisted): Unit = {
    logger.logEvent(event)
  }

 /* override def onBlockUpdated(event: SparkListenerBlockUpdated): Unit = {
    if (config.logBlockUpdates) {
      logEvent(event)
    }
  }*/

  // No-op because logging every update would be overkill
 // override def onExecutorMetricsUpdate(event: SparkListenerExecutorMetricsUpdate): Unit = { }

  override def onOtherEvent(event: SparkListenerEvent): Unit = {
    if (event.logEvent) {
      logger.logEvent(event)
    }
  }

  private def redactEvent(event: SparkListenerEnvironmentUpdate): SparkListenerEnvironmentUpdate = {
    // environmentDetails maps a string descriptor to a set of properties
    // Similar to:
    // "JVM Information" -> jvmInformation,
    // "Spark Properties" -> sparkProperties,
    // ...
    // where jvmInformation, sparkProperties, etc. are sequence of tuples.
    // We go through the various  of properties and redact sensitive information from them.
    val redactedProps = event.environmentDetails.map{ case (name, props) =>
      name -> Utils.redact(sparkConf, props)
    }
    SparkListenerEnvironmentUpdate(redactedProps)
  }
}

