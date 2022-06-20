package com.foodduck.foodduck.base.config

import com.amazonaws.services.s3.AmazonS3Client
import com.amazonaws.services.s3.model.CannedAccessControlList
import com.amazonaws.services.s3.model.PutObjectRequest
import com.foodduck.foodduck.base.error.CustomException
import com.foodduck.foodduck.base.error.ErrorCode
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Component
import org.springframework.web.multipart.MultipartFile
import java.io.File
import java.io.FileOutputStream
import java.util.*

@Component
class S3Uploader(
    private val amazonS3Client: AmazonS3Client,
    @Value("\${cloud.aws.s3.bucket}")
    val bucket: String
) {
    fun upload(multipartFile: MultipartFile, dirName: String): String {
        val uploadFile: File = convert(multipartFile)

        val url = upload(uploadFile, dirName)
        removeFile(uploadFile)
        return url

    }

    private fun upload(uploadFile: File, dirName: String): String {
        val fileName: String = dirName + "/" +  UUID.randomUUID()
        return putS3(uploadFile, fileName)
    }

    private fun putS3(uploadFile: File, fileName: String): String {
        print(bucket)
        amazonS3Client.putObject(bucket, fileName, uploadFile)
        return amazonS3Client.getUrl(bucket, fileName).toString()
    }

    private fun removeFile(targetFile: File) {
        if (targetFile.delete()) return
        throw CustomException(ErrorCode.FILE_NOT_FOUND)
    }

    private fun convert(file: MultipartFile): File {
        val convertFile = File(file.originalFilename)
        convertFile.createNewFile()
        val fos = FileOutputStream(convertFile)
        fos.write(file.bytes)
        return convertFile
    }
}