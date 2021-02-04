package coffee.michel.ogma.bot.context

import coffee.michel.ogma.bot.config.DiscordConfig
import mu.KotlinLogging
import net.dv8tion.jda.api.JDA
import net.dv8tion.jda.api.JDABuilder
import net.dv8tion.jda.api.requests.GatewayIntent
import net.dv8tion.jda.api.utils.Compression
import org.springframework.beans.factory.config.ConfigurableBeanFactory
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Scope
import org.springframework.stereotype.Component

private val log = KotlinLogging.logger {}

@Component
internal class JDAProvider(
    private val config: DiscordConfig
) {

    @Bean
    @Scope(value = ConfigurableBeanFactory.SCOPE_SINGLETON)
    fun getJDA(): JDA {
        log.info { "Starting initialisation of JDA with token ${config.botToken}" }
        val jda = JDABuilder.create(config.botToken, GatewayIntent.values().asList())
            .setBulkDeleteSplittingEnabled(true)
            .setCompression(Compression.ZLIB)
            .build()

        log.info { "Awaiting JDA to be Ready." }
        jda.awaitReady()
        log.info { "Initialisation is done." }
        return jda
    }

}