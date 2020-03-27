package inc.sad.dataJoiner

import java.util.Properties

import org.apache.log4j.Logger
import inc.sad.dataJoiner.Config
import org.apache.kafka.clients.consumer.ConsumerConfig
import org.apache.kafka.streams.kstream.{JoinWindows, KStream, StreamJoined, ValueJoiner}
import org.apache.kafka.streams.{KafkaStreams, KeyValue, StreamsBuilder, StreamsConfig}
import pureconfig.ConfigSource
import java.time.Duration
import pureconfig.generic.auto._

object ApplicationStart extends App {

  val LOG = Logger.getLogger(this.getClass.getName)

  val conf = ConfigSource.default.loadOrThrow[Config]

  val props = new Properties
  props.setProperty(StreamsConfig.APPLICATION_ID_CONFIG, conf.kafkaConfig.applicationId)
  props.setProperty(StreamsConfig.BOOTSTRAP_SERVERS_CONFIG, conf.kafkaConfig.bootstrapServers.mkString(","))
  props.setProperty(StreamsConfig.COMMIT_INTERVAL_MS_CONFIG, conf.kafkaConfig.commitInterval)
  props.setProperty(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, conf.kafkaConfig.autoOffsetReset)
  props.setProperty(ConsumerConfig.MAX_POLL_RECORDS_CONFIG, conf.kafkaConfig.maxPoolRecords)

  val builder = new StreamsBuilder

  val weatherStream: KStream[String, String] = builder.stream(conf.kafkaConfig.weatherTopic)
  val hotelsStream: KStream[String, String] = builder.stream(conf.kafkaConfig.hotelsTopic)

  val weatheredHotelsStream = weatherStream.join(
    hotelsStream,
    new ValueJoiner[String, String, String] {
      override def apply(left: String, right: String): String = {left + "," + right}
    },
    JoinWindows.of(Duration.ofSeconds(5))
  )

  val streams = new KafkaStreams(builder.build, props)
  streams.start()

  Runtime.getRuntime.addShutdownHook(new Thread(() => {
    try {
      streams.close
      LOG.info("String closed")
    } catch {
      case e: Exception =>
        LOG.error(e)
    }
  }))

}