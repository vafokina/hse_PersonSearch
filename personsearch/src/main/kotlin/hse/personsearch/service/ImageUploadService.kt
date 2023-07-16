package hse.personsearch.service

import org.springframework.web.multipart.MultipartFile

interface ImageUploadService {

    fun upload(file: MultipartFile, fileName: String?, expiration: Int?) : String
}