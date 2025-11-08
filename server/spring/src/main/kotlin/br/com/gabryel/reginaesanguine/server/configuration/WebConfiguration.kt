package br.com.gabryel.reginaesanguine.server.configuration

import br.com.gabryel.reginaesanguine.domain.parser.gameJsonParser
import org.springframework.context.annotation.Configuration
import org.springframework.http.converter.HttpMessageConverter
import org.springframework.http.converter.HttpMessageConverters
import org.springframework.http.converter.StringHttpMessageConverter
import org.springframework.http.converter.json.KotlinSerializationJsonHttpMessageConverter
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer

@Configuration
class WebConfiguration : WebMvcConfigurer {
    override fun configureMessageConverters(converters: HttpMessageConverters.ServerBuilder) {
        val json = gameJsonParser()
        converters.stringMessageConverter(StringHttpMessageConverter())
        converters.jsonMessageConverter(KotlinSerializationJsonHttpMessageConverter(json))
    }
}
