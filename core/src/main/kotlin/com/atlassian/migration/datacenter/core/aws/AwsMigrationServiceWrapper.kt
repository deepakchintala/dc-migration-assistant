package com.atlassian.migration.datacenter.core.aws

import com.atlassian.activeobjects.external.ActiveObjects
import com.atlassian.event.api.EventPublisher
import com.atlassian.migration.datacenter.analytics.events.MigrationFailedEvent
import com.atlassian.migration.datacenter.core.application.ApplicationConfiguration
import com.atlassian.migration.datacenter.dto.MigrationContext
import com.atlassian.migration.datacenter.spi.MigrationService
import com.atlassian.migration.datacenter.spi.MigrationStage
import com.atlassian.migration.datacenter.spi.exceptions.InvalidMigrationStageError
import kotlin.math.min


open class AwsMigrationServiceWrapper(ao: ActiveObjects?, applicationConfiguration: ApplicationConfiguration?, eventPublisher: EventPublisher?) : AWSMigrationService(ao, applicationConfiguration, eventPublisher), MigrationService {

    override fun stageSpecificError(errorStage: MigrationStage, message: String) {
        if (!errorStage.isErrorStage()) {
            throw InvalidMigrationStageError("Stage $errorStage is not a valid error stage")
        }

        val migration = findFirstOrCreateMigration()
        val context: MigrationContext = this.currentContext
        val now = System.currentTimeMillis() / 1000L

        val failStage = context.migration.stage


        setCurrentStage(migration, errorStage)
        // We must truncate the error message to 450 characters so that it fits in the varchar(450) column
        // We must truncate the error message to 450 characters so that it fits in the varchar(450) column
        context.setErrorMessage(message.substring(0, min(450, message.length)))
        context.endEpoch = now
        context.save()

        this.eventPublisher.publish(MigrationFailedEvent(this.applicationConfiguration.pluginVersion,
                failStage, now - context.startEpoch))
    }

    override fun error(message: String) {
        this.stageSpecificError(MigrationStage.ERROR, message);
    }

    override fun error(e: Throwable) {
        stageSpecificError(MigrationStage.ERROR, e.message!!)
        findFirstOrCreateMigration().stage.exception = e
    }


}