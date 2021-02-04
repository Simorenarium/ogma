package coffee.michel.ogma.bot.config

import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.context.annotation.Configuration

@Configuration
@ConfigurationProperties(prefix = "coffee.michel.ogma.discord")
internal class DiscordConfig {

    lateinit var clientId: String
    lateinit var clientSecret: String
    lateinit var publicKey: String
    lateinit var botToken: String

}