package no.nav.arbeidsgiver

import io.kotlintest.shouldBe
import io.kotlintest.specs.StringSpec
import no.nav.arbeidsgiver.sf.SalesforceClient

class SalesforceClientTest : StringSpec({
    "Token should be parseable" {
        val testToken = """
        {
            "access_token" : "xxx",
            "instance_url" : "https://xxx--preprod.my.salesforce.com",
            "id" : "https://test.salesforce.com/id/xxx/xx",
            "token_type" : "Bearer",
            "issued_at" : "1579532482874",
            "signature" : "xxx="
        }
        """
        val token = SalesforceClient.mapToken(testToken)
        token.accessToken shouldBe "xxx"
        token.instanceUrl.toString() shouldBe "https://xxx--preprod.my.salesforce.com"
        token.issuedAt shouldBe 1579532482874
    }
})
