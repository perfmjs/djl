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
package ai.djl.training.listener;

import ai.djl.metric.Metric;
import ai.djl.metric.Metrics;
import ai.djl.training.Trainer;
import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * {@link TrainingListener} that outputs the training time metrics after training is done.
 *
 * <p>The training time data is placed in the file "$outputDir/training.log".
 */
public class TimeMeasureTrainingListener implements TrainingListener {

    private static final Logger logger = LoggerFactory.getLogger(TimeMeasureTrainingListener.class);

    private String outputDir;

    /**
     * Constructs a {@link TimeMeasureTrainingListener}.
     *
     * @param outputDir the directory to output the tracked timing data in
     */
    public TimeMeasureTrainingListener(String outputDir) {
        this.outputDir = outputDir;
    }

    /** {@inheritDoc} */
    @Override
    public void onEpoch(Trainer trainer) {}

    /** {@inheritDoc} */
    @Override
    public void onTrainingBatch(Trainer trainer) {}

    /** {@inheritDoc} */
    @Override
    public void onValidationBatch(Trainer trainer) {}

    /** {@inheritDoc} */
    @Override
    public void onTrainingBegin(Trainer trainer) {}

    /** {@inheritDoc} */
    @Override
    public void onTrainingEnd(Trainer trainer) {
        Metrics metrics = trainer.getMetrics();

        if (outputDir != null) {
            dumpTrainingTimeInfo(metrics, outputDir);
        }
    }

    private static void dumpTrainingTimeInfo(Metrics metrics, String logDir) {
        if (logDir == null) {
            return;
        }
        try {
            Path dir = Paths.get(logDir);
            Files.createDirectories(dir);
            Path file = dir.resolve("training.log");
            try (BufferedWriter writer =
                    Files.newBufferedWriter(
                            file, StandardOpenOption.CREATE, StandardOpenOption.APPEND)) {
                List<Metric> list = metrics.getMetric("train");
                for (Metric metric : list) {
                    writer.append(metric.toString());
                    writer.newLine();
                }
            }
        } catch (IOException e) {
            logger.error("Failed dump training log", e);
        }
    }
}
