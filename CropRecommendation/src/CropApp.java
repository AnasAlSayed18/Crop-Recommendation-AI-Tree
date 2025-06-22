import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.Stage;

public class CropApp extends Application {

    private TextField[] fields = new TextField[7];
    private TextArea resultArea = new TextArea();
    private CropRecommender recommender;

    @Override
    public void start(Stage stage) throws Exception {
        recommender = new CropRecommender("Crop_recommendation.csv");
        try {
            recommender.loadAndTrain();
        } catch (Exception ignored) {}

        VBox mainContainer = new VBox(20);
        mainContainer.setPadding(new Insets(20));
        mainContainer.setStyle("-fx-background-color: #f7f9fc;");

        Label title = new Label("🌾 Crop Recommendation System");
        title.setStyle("-fx-font-size: 20px; -fx-font-weight: bold;");
        title.setAlignment(Pos.CENTER);

        VBox formContainer = new VBox(10);
        formContainer.setPadding(new Insets(15));
        formContainer.setStyle("-fx-background-color: #ffffff; -fx-border-color: #cccccc; -fx-border-radius: 8px; -fx-background-radius: 8px;");

        String[] labels = {
                "Nitrogen (N)", "Phosphorus (P)", "Potassium (K)",
                "Temperature (°C)", "Humidity (%)", "pH", "Rainfall (mm)"
        };

        for (int i = 0; i < labels.length; i++) {
            HBox inputRow = new HBox(10);
            inputRow.setAlignment(Pos.CENTER_LEFT);
            Label lbl = new Label(labels[i] + ":");
            lbl.setPrefWidth(130);
            fields[i] = new TextField();
            fields[i].setPromptText("Enter " + labels[i]);
            fields[i].setPrefWidth(200);
            inputRow.getChildren().addAll(lbl, fields[i]);
            formContainer.getChildren().add(inputRow);
        }

        HBox buttonBox = new HBox(15);
        buttonBox.setAlignment(Pos.CENTER);
        Button predictBtn = new Button("🎯 Recommend Crop");
        Button visualizeBtn = new Button("📊 Show Decision Tree");
        Button statsBtn = new Button("📈 Cross-Validation");

        predictBtn.setStyle("-fx-background-color: #4CAF50; -fx-text-fill: white;");
        visualizeBtn.setStyle("-fx-background-color: #2196F3; -fx-text-fill: white;");
        statsBtn.setStyle("-fx-background-color: #9C27B0; -fx-text-fill: white;");

        buttonBox.getChildren().addAll(predictBtn, visualizeBtn, statsBtn);

        resultArea.setEditable(false);
        resultArea.setWrapText(true);
        resultArea.setPromptText("Results will be displayed here...");
        resultArea.setStyle("-fx-control-inner-background: #eef1f4;");
        resultArea.setPrefHeight(120);

        // Button actions
        predictBtn.setOnAction(e -> handlePrediction());
        visualizeBtn.setOnAction(e -> {
            try {
                recommender.visualizeTree();
            } catch (Exception ex) {
                resultArea.setText("Error visualizing tree: " + ex.getMessage());
            }
        });
        statsBtn.setOnAction(e -> {
            try {
                String report = recommender.performCrossValidationReport();
                resultArea.setText(report);
            } catch (Exception ex) {
                resultArea.setText("❌ Error: " + ex.getMessage());
            }
        });

        mainContainer.getChildren().addAll(title, formContainer, buttonBox, resultArea);

        Scene scene = new Scene(mainContainer, 500, 600);
        stage.setTitle("Crop Recommendation");
        stage.setScene(scene);
        stage.show();
    }

    private void handlePrediction() {
        try {
            double[] inputs = new double[7];
            for (int i = 0; i < 7; i++) {
                inputs[i] = Double.parseDouble(fields[i].getText());
            }
            String result = recommender.predict(inputs);
            resultArea.setText("✅ Recommended Crop: " + result);
        } catch (Exception ex) {
            resultArea.setText("❌ Error: " + ex.getMessage());
        }
    }

    public static void main(String[] args) {
        launch(args);
    }
}
