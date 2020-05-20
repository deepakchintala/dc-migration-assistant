package com.atlassian.migration.datacenter.core.application

import com.atlassian.migration.datacenter.spi.exceptions.UnsupportedPasswordEncodingException
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty
import java.util.*

internal class DbConfigXmlElement {
    val url: String? = null

    @JacksonXmlProperty(localName = "username")
    val userName: String? = null

    @JacksonXmlProperty(localName = "atlassian-password-cipher-provider")
    private val cipher: String? = null

    @get:Throws(UnsupportedPasswordEncodingException::class)
    val password: String? = null
        get() {
            if (cipher != null) {
                if (cipher != BASE64_CLASS) {
                    throw UnsupportedPasswordEncodingException("Unsupported database password encryption in dbconfig.xml; see documentation for detail: $cipher")
                }
                return String(Base64.getDecoder().decode(field))
            }
            return field
        }

    companion object {
        private const val BASE64_CLASS = "com.atlassian.db.config.password.ciphers.base64.Base64Cipher"
    }
}