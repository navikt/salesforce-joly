package no.nav.arbeidsgiver.models

data class SfdxListOrgResult(
    val accessToken: String,
    val alias: String,
    val clientId: String,
    val createdBy: String,
    val createdDate: String,
    val devHubId: String,
    val edition: String,
    val expirationDate: String,
    val id: String,
    val instanceUrl: String,
    val orgName: String,
    val status: String,
    val username: String
)
