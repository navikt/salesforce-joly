package no.nav.arbeidsgiver.linter

import no.nav.arbeidsgiver.models.LintResult

object LinterClient {
    fun lintText(text: String): LintResult {
        return if (text.contains("PII")) {
            LintResult(
                false,
                "Denne teksten inneholder noe PII, ta en ekstra titt (testmelding ignorers i prod)"
            )
        } else {
            LintResult(true, null)
        }
    }
}
