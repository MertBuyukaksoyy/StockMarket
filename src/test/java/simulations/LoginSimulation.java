package simulations;

import io.gatling.javaapi.core.*;
import io.gatling.javaapi.http.*;
import org.junit.jupiter.api.Test;

import java.time.Duration;

import static io.gatling.javaapi.core.CoreDsl.*;
import static io.gatling.javaapi.http.HttpDsl.*;

public class LoginSimulation extends Simulation {

    // 1. HTTP Protokolü Tanımla
    private final HttpProtocolBuilder httpProtocol = http
            .baseUrl("http://localhost:8080") // Spring Boot uygulamanızın base URL'si
            .acceptHeader("application/json")
            .contentTypeHeader("application/x-www-form-urlencoded");

    // 2. Kullanıcı giriş isteği (POST /authenticateUser)
    private final ScenarioBuilder loginScenario = scenario("JWT Login Load Test")
            .exec(http("Authenticate User")
                    .post("/authenticateUser")
                    .formParam("username", "admin") // Test kullanıcı adı
                    .formParam("password", "123") // Test şifresi
                    .check(status().is(302)) // HTTP durum kodunun 302 olmasını kontrol et
                    .check(header("Set-Cookie").saveAs("authCookie")) // JWT Cookie'sini kaydet
            )
            .pause(1) // Bekleme süresi

            // Giriş sonrası bir isteği test etmek için (örneğin /home)
            .exec(http("Access Home Page with JWT")
                    .get("/home")
                    .header("Cookie", "${authCookie}") // Kayıtlı JWT cookie'yi gönder
                    .check(status().is(200)) // Home sayfasının 200 OK dönmesini kontrol et
            );

    // 3. Kullanıcı yükü senaryosu
    public void setup() {
        setUp(
                loginScenario.injectOpen(
                        atOnceUsers(1) // Aynı anda 10 kullanıcı girişi
                        //rampUsers(1000).during(Duration.ofSeconds(30)) // 30 saniyede 1000 kullanıcı giriş yapar
                )
        ).protocols(httpProtocol);
    }

    // 4. JUnit ile Gatling testini çalıştır
    @Test
    public void runGatlingTest() {
        setup();
    }
}
