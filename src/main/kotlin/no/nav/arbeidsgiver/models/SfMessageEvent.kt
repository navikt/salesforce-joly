package no.nav.arbeidsgiver.models

data class SfMessageEvent(
    val createdDate: String,
    val replayId: Int,
    val type: String
)
