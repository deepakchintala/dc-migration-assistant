package com.atlassian.migration.datacenter.listener

import com.atlassian.event.api.EventPublisher
import com.atlassian.migration.datacenter.analytics.events.MigrationTransitionEvent
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.DisposableBean
import org.springframework.beans.factory.InitializingBean

class MigrationTransitionEventListener(private val eventPublisher: EventPublisher) : InitializingBean, DisposableBean {

    companion object {
        private val log: Logger = LoggerFactory.getLogger(MigrationTransitionEventListener::class.java)
    }

    @com.atlassian.event.api.EventListener
    fun onMigrationTransitionEvent(event: MigrationTransitionEvent){
        log.info("Received transition event : {}", event)
    }

    override fun destroy() {
        eventPublisher.unregister(this)
    }

    override fun afterPropertiesSet() {
        this.eventPublisher.register(this)
    }
}