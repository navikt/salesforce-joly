package no.nav.arbeidsgiver.models

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.annotation.JsonProperty
import no.nav.arbeidsgiver.Const

@JsonIgnoreProperties(ignoreUnknown = true)
data class SfTask(
    val Id: String,
    val Subject: String,
    val Description: String,

    @JsonProperty(Const.TASK_WARNING_CUSTOM_FIELD)
    val Warnings: String?
)
