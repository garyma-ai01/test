package gatling

import io.gatling.core.Predef._
import io.gatling.http.Predef._

class GatlingTest extends Simulation {

  val httpProtocol = http
    .baseUrl("http://localhost:8080")
    .acceptHeader("text/html,application/xhtml+xml,application/xml;q=0.9,*/*;q=0.8")
    .doNotTrackHeader("1")
    .acceptLanguageHeader("en-US,en;q=0.5")
    .acceptEncodingHeader("gzip, deflate")
    .userAgentHeader("Mozilla/5.0 (Macintosh; Intel Mac OS X 10.8; rv:16.0) Gecko/20100101 Firefox/16.0")

  val scn = scenario("limit 100 requests")
    .exec(
      http("weather_request")
        .get("/weather")
    )

  setUp(scn.inject(atOnceUsers(200)).protocols(httpProtocol))
}