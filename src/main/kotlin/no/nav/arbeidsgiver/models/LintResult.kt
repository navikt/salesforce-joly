package no.nav.arbeidsgiver.models

data class LintResult(
    val success: Boolean,
    val message: String?
)
