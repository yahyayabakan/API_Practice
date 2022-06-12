import io.restassured.path.json.JsonPath;
import io.restassured.response.Response;
import org.testng.annotations.Test;
import static io.restassured.RestAssured.given;
import static io.restassured.RestAssured.when;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.testng.AssertJUnit.assertEquals;

public class ApiTesting {
    @Test
    public void findFilmWithATitle() {
        //1. Find film with a title ”A New Hope”
        Response response =
                given()
                        .baseUri("https://swapi.dev/api/")
                        .queryParam("search", "A New Hope").
                        when()
                        .get("films/");
        assertEquals(response.statusCode(), 200);
        assertEquals(response.path("results.title[0]"), "A New Hope");
    }

    @Test
    public void findCharecterFromMovie(){
        //2. Using previous response (1) find person with name “Biggs Darklighter”
        // among the characters that were part of that film.

        // First we have to find his url
        Response response =
                given()
                        .baseUri("https://swapi.dev/api/")
                        .queryParam("search", "Biggs Darklighter").
                when()
                        .get("people/");
        assertEquals(response.statusCode(),200);
        assertEquals(response.path("results.name[0]"), "Biggs Darklighter");
        String characterUrl = response.path("results.url[0]");

        //second lets check out is he in the characters list of the movie or not
        response =
                given()
                       .baseUri("https://swapi.dev/api/")
                       .queryParam("search", "A New Hope").
                when()
                       .get("films/");
        JsonPath jpath = response.jsonPath();
        assertThat(jpath.getList("results.characters[0]"), hasItem(characterUrl));
        }

    @Test
    public void starshipName() {
        //3. Using previous response (2) find which starship he/she was flying on

        // first lets get his/her starship
        Response response =
                given()
                        .baseUri("https://swapi.dev/api/")
                        .queryParam("search", "Biggs Darklighter").
                        when()
                        .get("people/");
        String starshipUrl = response.path("results.starships[0][0]");

        // second lets find the name of his/her starship
        response =
                when()
                        .get(starshipUrl);
        String starshipName = response.path("name");
        System.out.println("Name of the starship is: " + starshipName);
        assertEquals("X-wing", starshipName);
    }

    @Test
    public void lukeSkywalkedIsPilotOfStarfighter(){

        // First lets find Luke Skywalker' s uri for checking whether it is in the pilots attribute of the starship
        Response response =
                given()
                        .baseUri("https://swapi.dev/api/")
                        .queryParam("search", "Luke Skywalker").
                        when()
                        .get("people/");
        String lukeSkyWalkerUri = response.path("results.url[0]");

        // Second lets get Biggs Darklighter' starship
        response =
                given()
                        .baseUri("https://swapi.dev/api/")
                        .queryParam("search", "Biggs Darklighter").
                        when()
                        .get("people/");
        String starshipUrl = response.path("results.starships[0][0]");

        // Third lets check out whether Luke Skywalker uri is in the pilots list of starship
        response =
                given()
               .when()
                      .get(starshipUrl);
        JsonPath jpath = response.jsonPath();
        System.out.println(jpath.getList("pilots"));
        assertThat(jpath.getList("pilots"), hasItem(lukeSkyWalkerUri));
    }
}
