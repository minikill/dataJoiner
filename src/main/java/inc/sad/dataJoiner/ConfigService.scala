package inc.sad.dataJoiner

import java.io.File
import java.nio.file.Path

import javax.naming.ConfigurationException
import pureconfig.ConfigSource

object ConfigService {

  def loadConfiguration(configFilePath: String): DataJoinerConfig = {

    import pureconfig.generic.auto._

    val configFile = new File(configFilePath)

    if (!configFile.exists()) {
      throw new ConfigurationException(s"Config file doesn't exists `${configFile.getCanonicalPath}`")
    }
    ConfigSource.file(configFile).loadOrThrow[DataJoinerConfig]
  }
}
