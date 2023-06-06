package rs.edu.raf.si.bank2.main;

import java.util.HashMap;
import java.util.Map;
import org.junit.jupiter.api.BeforeAll;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.test.context.ContextConfiguration;

@SpringBootTest
@ContextConfiguration
public class MainApplicationTests {

    /** Document to be used in these tests. */
    private static final Map<String, Object> TEST_DOC = new HashMap<>();

    /** The collection to run tests on. */
    private static final String TEST_COL = "tests";

    @Autowired
    private MongoTemplate mongo;

    @BeforeAll
    static void init() {
        TEST_DOC.put("firstName", "John");
        TEST_DOC.put("lastName", "Doe");
        TEST_DOC.put("age", 25);
        TEST_DOC.put("cool", true);
    }

    //  @Test
    //  void givenApplicationProperties_whenAppRun_thenMongoDbInsertSucceeds() {
    //    Map<String, Object> inserted = mongo.insert(TEST_DOC, TEST_COL);
    //    assertNotNull(inserted.get("_id"));
    //    TEST_DOC.forEach(
    //        (k, v) -> {
    //          assertEquals(inserted.get(k), v);
    //        });
    //  }
}
