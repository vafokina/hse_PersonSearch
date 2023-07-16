package hse.personsearch.service.impl

import com.fasterxml.jackson.databind.ObjectMapper
import hse.personsearch.service.ImageUploadService
import java.util.Base64
import net.dongliu.requests.Requests
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service
import org.springframework.web.multipart.MultipartFile


@Service
class ImageUploadServiceImpl : ImageUploadService {

    @Autowired
    lateinit var objectMapper: ObjectMapper

    private val log = LoggerFactory.getLogger(ImageUploadService::class.java)

    private val apiKey = "9fe513d9c52ceb18c58f4d4cf8337282"
    private val url = "https://api.imgbb.com/1/upload"

    override fun upload(file: MultipartFile, fileName: String?, expiration: Int?): String {
        log.debug("Start upload image to $url")

        val params = mapOf<String, Any>(
            Pair("key", apiKey),
            Pair("image", Base64.getEncoder().encodeToString(file.bytes))
        )
        if (fileName != null) {
            params.plus(Pair("fileName", fileName))
        }
        if (expiration != null) {
            params.plus(Pair("expiration", expiration))
        }

        val r = Requests.post(url)
            .body(params)
            .headers(
                mapOf<String, Any>(Pair("Accept", "application/json"))
            ).send().toTextResponse()

        if (r.statusCode() != 200) {
            throw Exception("Failed to upload image: " + r.body())
        }
        val result = objectMapper.readTree(r.body())
        return result.get("data").get("url").asText()
    }
}