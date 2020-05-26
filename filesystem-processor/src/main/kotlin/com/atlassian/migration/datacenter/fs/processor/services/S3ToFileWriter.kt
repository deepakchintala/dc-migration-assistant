package com.atlassian.migration.datacenter.fs.processor.services

import com.amazonaws.services.s3.AmazonS3
import com.amazonaws.services.s3.event.S3EventNotification
import lombok.SneakyThrows
import org.apache.commons.io.IOUtils
import org.slf4j.LoggerFactory
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.net.URLDecoder
import java.nio.charset.Charset
import java.nio.file.Paths

class S3ToFileWriter(private val s3Client: AmazonS3?, private val entity: S3EventNotification.S3Entity?, private val jiraHome: String?) : Runnable {

    private val log = LoggerFactory.getLogger(S3ToFileWriter::class.java)

    @SneakyThrows
    override fun run() {
        val key = URLDecoder.decode(entity!!.getObject().key, Charset.defaultCharset().toString())
        try {
            s3Client!!.getObject(entity.bucket.name, key).use { s3object ->
                s3object.objectContent.use { inputStream ->
                    val localPath = File("$jiraHome/$key")
                    val keyFile: String = Paths.get(localPath.path).fileName.toString()
                    log.info("Got request to write file: $keyFile")
                    // FIXME: Is this determining if the object is a file by checking if it has a file extension?
                    if (keyFile.contains(".")) {
                        if (!localPath.parentFile.exists()) {
                            if (localPath.parentFile.mkdirs()) {
                                log.info("Made the missing parent directory {}", localPath.path)
                            }
                        }
                        try {
                            IOUtils.copy(inputStream, FileOutputStream(localPath))
                        } catch (e: IOException) {
                            log.error("Failed to write file $keyFile", e)
                        }
                    } else {
                        localPath.mkdirs()
                    }
                }
            }
        } catch (ex: Exception) {
            log.error("Failed to process " + ex.localizedMessage)
            log.error(ex.cause!!.localizedMessage)
        }
    }

}