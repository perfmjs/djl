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
package com.amazon.ai;

import com.amazon.ai.metric.Metrics;
import com.amazon.ai.ndarray.NDFactory;

public interface TranslatorContext extends AutoCloseable {

    Model getModel();

    Context getContext();

    NDFactory getNDFactory();

    Metrics getMetrics();

    @Override
    void close();
}