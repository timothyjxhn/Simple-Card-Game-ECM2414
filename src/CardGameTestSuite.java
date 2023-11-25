import org.junit.runners.Suite;
import org.junit.runner.RunWith;
import java.io.File;

@RunWith(Suite.class)
@Suite.SuiteClasses({
        CardTest.class,
        PlayerTest.class,
        CardGameTest.class,
        CardDeckTest.class
})

public class CardGameTestSuite {
    public static void cleanup() {
        final File folder = new File( "." );
        final File[] files = folder.listFiles((dir, name) -> name.matches( "\\w*_output.txt" ));
        for (File file : files) {
            if (!file.delete()) {
                System.err.println( "Can't remove " + file.getAbsolutePath() );
            }
        }
    }
}
