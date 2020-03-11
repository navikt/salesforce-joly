package no.nav.arbeidsgiver.models

data class SfTaskUpdatedMessage(
    val event: SfMessageEvent,
    val sobject: SfTaskUpdatedMessageSobject
)
