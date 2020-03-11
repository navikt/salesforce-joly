package no.nav.arbeidsgiver

import no.nav.arbeidsgiver.linter.LinterClient
import no.nav.arbeidsgiver.sf.SalesforceBayeux
import no.nav.arbeidsgiver.sf.SalesforceQueries
import org.slf4j.LoggerFactory

object Bootstrap {

    private val log = LoggerFactory.getLogger(javaClass)

    fun start(ev: EnvVar = EnvVarFactory.envVar) {
        try {
            Server.create(ev)
            work()
        } catch (t: Throwable) {
            log.error(t.cause.toString(), t)
        }
    }

    /**
     * https://developer.salesforce.com/docs/atlas.en-us.platform_events.meta/platform_events/platform_events_subscribe_cometd.htm
     */
    private fun work() {
        log.info("Starting the work...")
        SalesforceBayeux.getClient().getChannel(Const.TASK_UPDATES_TOPIC).subscribe { channel, message ->
            val sobject = message.dataAsMap["sobject"] as Map<*, *>
            val taskId = sobject["Id"].toString()
            log.info("State for $taskId changed, message '$message'")
            val sfTask = SalesforceQueries.getTask(taskId)
            val lintResult = LinterClient.lintText(sfTask.Description)
            if (lintResult.message != sfTask.Warnings) {
                SalesforceQueries.patchTask(taskId, mapOf(Const.TASK_WARNING_CUSTOM_FIELD to lintResult.message))
            }
        }
        // val taskPatch = SalesforceQueries.patchTask("00T1j000006tYTzEAM", mapOf("Subject" to "New Subject"));
    }
}
