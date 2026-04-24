import com.storyboard.graphx.Window;
import javafx.application.Application;
import javafx.stage.Stage;

public class Main extends Application {

    @Override
    public void start(Stage stage){
        new Window();

    }

    public static void main(String[] args){
        launch(args);
    }
}
