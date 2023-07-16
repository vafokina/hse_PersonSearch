package hse.personsearch.config

import io.swagger.annotations.ApiModelProperty
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.data.domain.Pageable
import springfox.documentation.builders.PathSelectors
import springfox.documentation.builders.RequestHandlerSelectors
import springfox.documentation.spi.DocumentationType
import springfox.documentation.spring.web.plugins.Docket
import springfox.documentation.swagger2.annotations.EnableSwagger2

@Configuration
@EnableSwagger2
open class SwaggerConfiguration {

    @Bean
    open fun swaggerSpringfoxApiDocket(): Docket {
        return Docket(DocumentationType.SWAGGER_2)
            .select()
            .apis( RequestHandlerSelectors.basePackage( "hse.personsearch" ) )
            .paths(PathSelectors.any())
            .build()
            .directModelSubstitute(Pageable::class.java, SwaggerPageable::class.java)
    }

    data class SwaggerPageable(
        @ApiModelProperty("number of records per page", example = "20")
        val size: Int?,

        @ApiModelProperty("the page to retrieve (0..N)", example = "0")
        val page: Int?
    )
}