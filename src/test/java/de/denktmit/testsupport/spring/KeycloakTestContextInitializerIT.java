package de.denktmit.testsupport.spring;

import io.restassured.RestAssured;
import io.restassured.filter.cookie.CookieFilter;
import io.restassured.path.xml.XmlPath;
import io.restassured.response.Response;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.support.GenericApplicationContext;

import static io.restassured.http.Method.GET;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.startsWith;

public class KeycloakTestContextInitializerIT {

    private static final String REDIRECT_URI = "http://localhost/login/oauth2/code/keycloak";
    private KeycloakTestContextInitializer initializer;
    private ConfigurableApplicationContext ctx;


    @BeforeEach
    void setUp() {
        initializer = new KeycloakTestContextInitializer();
        ctx = new GenericApplicationContext();
    }

    @Test
    void testInitialize() {
        initializer.initialize(ctx);
        KeycloakTestContextInitializer.Config ic = initializer.getConfig();
        RestAssured.baseURI = ic.getKeycloakAddress();
        CookieFilter cookieFilter = new CookieFilter();
        String authenticationUri = visitAndVerifyLoginPage(ic, cookieFilter);
        login(ic, cookieFilter, authenticationUri);
    }

    private static String visitAndVerifyLoginPage(KeycloakTestContextInitializer.Config ic, CookieFilter cookieFilter) {
        Response response = RestAssured
                .given()
                .filter(cookieFilter)
                .queryParam("client_id", ic.getTestClientId())
                .queryParam("redirect_uri", REDIRECT_URI)
                .queryParam("response_type", "code")
                .queryParam("scope", "openid")

                .when()
                .request(GET, "/realms/{realmName}/protocol/openid-connect/auth", ic.getTestRealmName())

                .then()
                .statusCode(200)
                .body(containsString("Username or email"))
                .extract().response();

        XmlPath htmlPath = new XmlPath(XmlPath.CompatibilityMode.HTML, response.body().asString());
        return htmlPath.getString("html.body.**.findAll { it.name() == 'form' }.@action");
    }

    private static void login(KeycloakTestContextInitializer.Config ic, CookieFilter cookieFilter, String authenticationUri) {
        String redirectUri = ic.getKeycloakAddress() + "/realms/" + ic.getTestRealmName() + "/login-actions/authenticate";
        RestAssured
                .given()
                .filter(cookieFilter)
                .redirects().follow(false)
                .formParam("username", ic.getTestAdminUsername())
                .formParam("password", ic.getTestAdminPassword())
                .formParam("credentialId", "")
                .queryParam("client_id", ic.getTestClientId())
                .queryParam("redirect_uri", redirectUri)
                .queryParam("response_type", "code")
                .queryParam("scope", "openid")

                .when()
                .post(authenticationUri)

                .then()
                .statusCode(302)
                .header("Location", startsWith(REDIRECT_URI))
                .extract().response();
    }

}
