package test.java.com.project;

import io.quarkus.test.junit.QuarkusIntegrationTest;
import com.project.GreetingResourceTest;

@QuarkusIntegrationTest
class GreetingResourceIT extends GreetingResourceTest {
    // Execute the same tests but in packaged mode.
}
