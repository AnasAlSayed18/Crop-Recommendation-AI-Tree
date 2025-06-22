import weka.classifiers.Evaluation;
import weka.classifiers.trees.J48;
import weka.core.*;
import weka.gui.treevisualizer.PlaceNode2;
import weka.gui.treevisualizer.TreeVisualizer;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.*;

public class CropRecommender {
    private final String path;   // path to the CSV file
    private Instances data;         // full dataset loaded into Weka
    private J48 dTree;

    public CropRecommender(String path) {
        this.path = path;
    }

    // Loads the dataset, builds feature and label attributes, and trains the final dTree
    public void loadAndTrain() throws Exception {
        BufferedReader br = new BufferedReader(new FileReader(path));
        ArrayList<Attribute> attributes = new ArrayList<>();

        attributes.add(new Attribute("N"));
        attributes.add(new Attribute("P"));
        attributes.add(new Attribute("K"));
        attributes.add(new Attribute("temperature"));
        attributes.add(new Attribute("humidity"));
        attributes.add(new Attribute("ph"));
        attributes.add(new Attribute("rainfall"));

        ArrayList<String> classLabels = new ArrayList<>();
        ArrayList<double[]> instanceValues = new ArrayList<>();
        ArrayList<String> rawLabels = new ArrayList<>();

        String line = br.readLine();
        while ((line = br.readLine()) != null) {
            String[] tokens = line.split(",");

            double[] values = new double[7];
            for (int i = 0; i < 7; i++) values[i] = Double.parseDouble(tokens[i]);
            instanceValues.add(values);

            String label = tokens[7].trim();
            if (!classLabels.contains(label)) classLabels.add(label); // add only unique labels
            rawLabels.add(label);
        }

        attributes.add(new Attribute("label", classLabels));
        data = new Instances("CropData", attributes, instanceValues.size());
        data.setClassIndex(data.numAttributes() - 1); // last column is the class (label)

        for (int i = 0; i < instanceValues.size(); i++) {
            DenseInstance inst = new DenseInstance(data.numAttributes());
            inst.setDataset(data);
            for (int j = 0; j < 7; j++) inst.setValue(j, instanceValues.get(i)[j]);
            inst.setValue(7, rawLabels.get(i)); // set label
            data.add(inst);
        }

        // Build the final dTree on the full dataset
        dTree = new J48();
        dTree.buildClassifier(data);
    }

    // Predicts the crop using the final trained dTree (`dTree`) for a new set of inputs
    public String predict(double[] inputs) throws Exception {
        Instance newInst = new DenseInstance(data.numAttributes());
        newInst.setDataset(data);
        for (int i = 0; i < 7; i++) newInst.setValue(i, inputs[i]);

        double labelIndex = dTree.classifyInstance(newInst);
        return data.classAttribute().value((int) labelIndex);
    }

    // Displays a graphical decision tree using Weka's built-in viewer


    public void visualizeTree() throws Exception {
        // Set modern look and feel
        try {
            UIManager.setLookAndFeel("javax.swing.plaf.nimbus.NimbusLookAndFeel");
        } catch (Exception ignored) {}

        // Create frame
        JFrame frame = new JFrame("🌿 Crop Decision Tree Visualizer");
        frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        frame.setSize(1000, 700);
        frame.setLocationRelativeTo(null);
        frame.setLayout(new BorderLayout(10, 10));

        // Create the tree visualizer
        TreeVisualizer treeVis = new TreeVisualizer(null, dTree.graph(), new PlaceNode2());
        treeVis.setPreferredSize(new Dimension(900, 600));
        JScrollPane scrollPane = new JScrollPane(treeVis);
        scrollPane.setBorder(BorderFactory.createTitledBorder("Decision Tree"));

        // Buttons with soft material colors
        JButton btnFit = new JButton("🔄 Fit to Screen");
        JButton btnSave = new JButton("💾 Save as JPG");
        JButton btnClose = new JButton("❌ Close");

        btnFit.setBackground(new Color(76, 175, 80));     // #4CAF50
        btnFit.setForeground(Color.WHITE);

        btnSave.setBackground(new Color(33, 150, 243));   // #2196F3
        btnSave.setForeground(Color.WHITE);

        btnClose.setBackground(new Color(156, 39, 176));  // #9C27B0
        btnClose.setForeground(Color.WHITE);

        // Action: Auto scale
        btnFit.addActionListener(e -> treeVis.fitToScreen());

        // Action: Save tree as PNG
        btnSave.addActionListener(e -> {
            try {
                // Get the size of the full tree
                Dimension size = treeVis.getPreferredSize();

                // Create RGB image with white background
                BufferedImage image = new BufferedImage(size.width, size.height, BufferedImage.TYPE_INT_RGB);
                Graphics2D g2 = image.createGraphics();

                // Fill with white background first
                g2.setColor(Color.WHITE);
                g2.fillRect(0, 0, size.width, size.height);

                // Then draw the tree on top
                treeVis.paint(g2);
                g2.dispose();

                // Save to JPG
                File outputFile = new File("decision_tree.jpg");
                ImageIO.write(image, "jpg", outputFile);

                JOptionPane.showMessageDialog(frame, "✅ Tree saved as decision_tree.jpg", "Saved", JOptionPane.INFORMATION_MESSAGE);
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(frame, "❌ Failed to save image:\n" + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        });


        // Action: Close window
        btnClose.addActionListener(e -> frame.dispose());

        // Controls panel
        JPanel controls = new JPanel();
        controls.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        controls.setLayout(new FlowLayout(FlowLayout.CENTER, 20, 10));
        controls.add(btnFit);
        controls.add(btnSave);
        controls.add(btnClose);

        // Add components to frame
        frame.add(scrollPane, BorderLayout.CENTER);
        frame.add(controls, BorderLayout.SOUTH);

        // Show window
        frame.setVisible(true);
        treeVis.fitToScreen(); // auto-fit initially
    }



    // Performs 5-fold cross-validation and prints Accuracy, Precision, and Recall per fold
    public String performCrossValidationReport() throws Exception {
        int folds = 5;

        Random rand = new Random(1);
        data.randomize(rand);

        if (data.classAttribute().isNominal()) {
            data.stratify(folds);
        }

        StringBuilder result = new StringBuilder(" 5-Fold Cross-Validation Results:\n--------------------------------------\n");

        for (int i = 0; i < folds; i++) {
            // Create training and test sets for this fold
            Instances train = data.trainCV(folds, i, rand);
            Instances test = data.testCV(folds, i);

            // Train a NEW temporary tree on this fold's training set
            J48 tempDTree = new J48();
            tempDTree.buildClassifier(train);

            // Evaluate on the fold's test set
            Evaluation foldEval = new Evaluation(train);
            foldEval.evaluateModel(tempDTree, test);

            double acc = (1 - foldEval.errorRate()) * 100;
            double prec = foldEval.weightedPrecision(); // average over all crops
            double rec = foldEval.weightedRecall();

            result.append(String.format(" Fold %d:\n", i + 1));
            result.append(String.format(" Accuracy: %.2f%%\n", acc));
            result.append(String.format(" Precision: %.4f\n", prec));
            result.append(String.format(" Recall: %.4f\n\n", rec));
        }

        return result.toString();
    }
}
