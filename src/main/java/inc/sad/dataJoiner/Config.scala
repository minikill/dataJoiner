package inc.sad.dataJoiner

/**
 * A few case classes for pureConfig describing
 */

case class Config(applicationConfig: ApplicationConfig, kafkaConfig: KafkaConfig)

case class ApplicationConfig(consumerPoolSize: Int)

case class KafkaConfig(weatherTopic: String,
                       hotelsTopic: String,
                       outputTopic: String,
                       applicationId: String,
                       commitInterval: String,
                       autoOffsetReset: String,
                       maxPoolRecords: String,
                       bootstrapServers: List[String])

