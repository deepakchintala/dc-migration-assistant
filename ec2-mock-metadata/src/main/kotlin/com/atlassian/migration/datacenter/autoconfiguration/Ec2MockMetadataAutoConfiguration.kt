package com.atlassian.migration.datacenter.autoconfiguration

import com.atlassian.migration.datacenter.interceptors.Ec2MetadataInterceptor
import com.atlassian.migration.datacenter.properties.Ec2Properties
import org.aspectj.lang.Aspects
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean
import org.springframework.boot.context.properties.EnableConfigurationProperties
import org.springframework.context.ApplicationContext
import org.springframework.context.annotation.*

@Configuration
@EnableConfigurationProperties(Ec2Properties::class)
@PropertySource("/ec2-metadata.properties")
@EnableLoadTimeWeaving(aspectjWeaving = EnableLoadTimeWeaving.AspectJWeaving.AUTODETECT)
open class Ec2MockMetadataAutoConfiguration {


    @Bean
    @ConditionalOnMissingBean
    open fun ec2MetadataInterceptor(properties: Ec2Properties, applicationContext: ApplicationContext): Ec2MetadataInterceptor {
        val interceptor = Aspects.aspectOf(Ec2MetadataInterceptor::class.java)
        interceptor.properties = properties
        return interceptor
    }

}