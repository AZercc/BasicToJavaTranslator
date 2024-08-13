import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;
import java.util.Arrays;
import java.util.List;

public class BasicToJavaTranslator extends Application {

    @Override
    public void start(Stage primaryStage) {
        BorderPane root = new BorderPane();
        root.setPadding(new Insets(10));

        TextArea basicTextArea = new TextArea();
        basicTextArea.setPromptText("Введите код на Basic здесь...");
        root.setCenter(basicTextArea);

        Button translateButton = new Button("Транслировать в Java");
        translateButton.setOnAction(event -> {
            String basicCode = basicTextArea.getText().trim();
            // Проверяем на пустую строку
            if (basicCode.isEmpty()) {
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("Ошибка");
                alert.setHeaderText(null);
                alert.setContentText("Введите код на Basic.");
                alert.showAndWait();
                return;
            }
            // Теперь можно транслировать код
            String javaCode = translateToJava(Arrays.asList(basicCode.split("\\n")));
            TextArea javaTextArea = new TextArea(javaCode);
            javaTextArea.setEditable(false);
            root.setRight(javaTextArea);
        });
        root.setBottom(translateButton);

        primaryStage.setScene(new Scene(root, 800, 600));
        primaryStage.setTitle("Транслятор Basic в Java");
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }

    public static String translateToJava(List<String> basicCode) {
        StringBuilder javaCode = new StringBuilder();
        javaCode.append("public class Translated {\n");
        javaCode.append("    public static void main(String[] args) {\n");

        for (String line : basicCode) {
            line = line.trim();
            if (line.matches("\\d+ LET .*")) {
                String[] parts = line.split(" LET ");
                String assignment = parts[1].replace("=", "= ");
                javaCode.append("        int ").append(assignment).append(";\n");
            } else if (line.matches("\\d+ FOR .* TO .*")) {
                String[] parts = line.split(" FOR | TO ");
                String variable = parts[1].split(" = ")[0];
                String startValue = parts[1].split(" = ")[1];
                String endValue = parts[2];
                javaCode.append("        for (int ").append(variable).append(" = ").append(startValue).append("; ");
                javaCode.append(variable).append(" <= ").append(endValue).append("; ");
                javaCode.append(variable).append("++) {\n");
            } else if (line.matches("\\d+ IF .* THEN")) {
                String condition = line.split(" IF ")[1].split(" THEN")[0];
                javaCode.append("            if (").append(condition).append(") {\n");
            } else if (line.matches("\\d+ PRINT .*")) {
                String message = line.split(" PRINT ")[1];
                // Проверяем значение сообщения и добавляем соответствующий вывод в Java
                if (message.equals("\"X меньше Y\"")) {
                    javaCode.append("                System.out.println(\"X меньше Y\");\n");
                } else if (message.equals("\"X не меньше Y\"")) {
                    javaCode.append("                System.out.println(\"X не меньше Y\");\n");
                } else {
                    javaCode.append("                System.out.println(").append(message).append(");\n");
                }
            } else if (line.matches("\\d+ ELSE")) {
                javaCode.append("            } else {\n");
            } else if (line.matches("\\d+ END IF")) {
                javaCode.append("            }\n");
            } else if (line.matches("\\d+ NEXT .*")) {
                javaCode.append("        }\n");
            }
        }

        javaCode.append("    }\n");
        javaCode.append("}\n");

        return javaCode.toString();
    }
}