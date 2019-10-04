/*
 * Copyright 2019 Amazon.com, Inc. or its affiliates. All Rights Reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"). You may not use this file except in compliance
 * with the License. A copy of the License is located at
 *
 * http://aws.amazon.com/apache2.0/
 *
 * or in the "license" file accompanying this file. This file is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES
 * OR CONDITIONS OF ANY KIND, either express or implied. See the License for the specific language governing permissions
 * and limitations under the License.
 */
package software.amazon.ai.examples.inference;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import javax.imageio.ImageIO;
import org.apache.mxnet.zoo.ModelZoo;
import software.amazon.ai.Device;
import software.amazon.ai.examples.inference.util.AbstractExample;
import software.amazon.ai.examples.inference.util.Arguments;
import software.amazon.ai.inference.Predictor;
import software.amazon.ai.metric.Metrics;
import software.amazon.ai.modality.cv.DetectedObjects;
import software.amazon.ai.modality.cv.ImageVisualization;
import software.amazon.ai.modality.cv.util.BufferedImageUtils;
import software.amazon.ai.translate.TranslateException;
import software.amazon.ai.zoo.ModelNotFoundException;
import software.amazon.ai.zoo.ZooModel;

public final class SsdExample extends AbstractExample {

    public static void main(String[] args) {
        new SsdExample().runExample(args);
    }

    @Override
    public DetectedObjects predict(Arguments arguments, Metrics metrics, int iteration)
            throws IOException, ModelNotFoundException, TranslateException {
        DetectedObjects predictResult = null;
        Path imageFile = arguments.getImageFile();
        BufferedImage img = BufferedImageUtils.fromFile(imageFile);

        // Device is not not required, default device will be used by Model if not provided.
        // Change to a specific device if needed.
        Device device = Device.defaultDevice();

        Map<String, String> criteria = new ConcurrentHashMap<>();
        criteria.put("size", "512");
        criteria.put("backbone", "resnet50_v1");
        criteria.put("dataset", "voc");
        ZooModel<BufferedImage, DetectedObjects> model = ModelZoo.SSD.loadModel(criteria, device);

        try (Predictor<BufferedImage, DetectedObjects> predictor = model.newPredictor()) {
            predictor.setMetrics(metrics); // Let predictor collect metrics

            for (int i = 0; i < iteration; ++i) {
                predictResult = predictor.predict(img);
                printProgress(iteration, i);
                collectMemoryInfo(metrics);
            }
        }
        drawBoundingBox(img, predictResult, arguments.getLogDir());

        model.close();
        return predictResult;
    }

    private void drawBoundingBox(BufferedImage img, DetectedObjects predictResult, String logDir)
            throws IOException {
        if (logDir == null) {
            return;
        }

        Path dir = Paths.get(logDir);
        Files.createDirectories(dir);

        ImageVisualization.drawBoundingBoxes(img, predictResult);

        Path out = Paths.get(logDir, "ssd.jpg");
        ImageIO.write(img, "jpg", out.toFile());
    }
}