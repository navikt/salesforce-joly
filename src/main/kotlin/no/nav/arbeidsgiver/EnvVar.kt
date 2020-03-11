package no.nav.arbeidsgiver

data class EnvVar(
    val httpsProxy: String = System.getenv("HTTPS_PROXY")?.toString() ?: "",
    val port: Int = System.getenv("PORT")?.toInt() ?: 8087,
    val naisClusterName: String = System.getenv("NAIS_CLUSTER_NAME")?.toString() ?: Const.LOCALDEV,
    val sfClientId: String = System.getenv("SALESFORCE_CLIENT_ID")?.toString() ?: "",
    val sfClientSecret: String = System.getenv("SALESFORCE_CLIENT_SECRET")?.toString() ?: "",
    val sfPassword: String = System.getenv("SALESFORCE_PASSWORD")?.toString() ?: "",
    val sfUrl: String = System.getenv("SALESFORCE_URL")?.toString() ?: "",
    val sfUsername: String = System.getenv("SALESFORCE_USERNAME")?.toString() ?: "",
    val sfUsertoken: String = System.getenv("SALESFORCE_USERTOKEN")?.toString() ?: ""
)
