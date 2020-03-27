package inc.sad.dataJoiner

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

