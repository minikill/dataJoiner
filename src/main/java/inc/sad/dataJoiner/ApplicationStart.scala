package inc.sad.dataJoiner

import java.util.Properties

import org.apache.log4j.Logger
import org.apache.kafka.clients.consumer.ConsumerConfig
import org.apache.kafka.streams.kstream.{JoinWindows, KStream, Produced, ValueJoiner}
import org.apache.kafka.streams.{KafkaStreams, StreamsBuilder, StreamsConfig}
import pureconfig.ConfigSource
import java.time.Duration
import org.apache.kafka.common.serialization.Serdes
import pureconfig.generic.auto._

/**
 * The object for joining hotels and weather data into target topic
 * Join by key: geohash + dateTime
 */

object ApplicationStart extends App {

  val LOG = Logger.getLogger(this.getClass.getName)

  val conf = ConfigSource.default.loadOrThrow[Config]

  // Properties for kafka
  val props = new Properties
  props.setProperty(StreamsConfig.APPLICATION_ID_CONFIG, conf.kafkaConfig.applicationId)
  props.setProperty(StreamsConfig.BOOTSTRAP_SERVERS_CONFIG, conf.kafkaConfig.bootstrapServers.mkString(","))
  props.setProperty(StreamsConfig.COMMIT_INTERVAL_MS_CONFIG, conf.kafkaConfig.commitInterval)
  props.setProperty(StreamsConfig.DEFAULT_KEY_SERDE_CLASS_CONFIG, Serdes.String().getClass().getName())
  props.setProperty(StreamsConfig.DEFAULT_VALUE_SERDE_CLASS_CONFIG, Serdes.String().getClass().getName())
  props.setProperty(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, conf.kafkaConfig.autoOffsetReset)
  props.setProperty(ConsumerConfig.MAX_POLL_RECORDS_CONFIG, conf.kafkaConfig.maxPoolRecords)

  val builder = new StreamsBuilder

  // Introducing source streams
  val weatherStream: KStream[String, String] = builder.stream(conf.kafkaConfig.weatherTopic)
  val hotelsStream: KStream[String, String] = builder.stream(conf.kafkaConfig.hotelsTopic)

  // Join processing
  val weatheredHotelsStream = weatherStream.join(
    hotelsStream,
    new ValueJoiner[String, String, String] {
      override def apply(weather: String, hotel: String): String = {
        hotel.stripLineEnd + "," + weather
      }
    },
    JoinWindows.of(Duration.ofDays(1))
  )
  .to(conf.kafkaConfig.outputTopic,
    Produced.`with`(Serdes.String(), Serdes.String())
  )

  // Streaming start
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