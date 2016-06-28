/**
 * Copyright 2016 National Cyber Security Centre, Netherlands Forensic Institute
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package io.parsingdata.metal.util.serialize;

import static io.parsingdata.metal.Util.checkNotNull;

import java.util.ArrayList;
import java.util.List;

import io.parsingdata.metal.data.Environment;
import io.parsingdata.metal.data.ParseGraph;
import io.parsingdata.metal.data.ParseItem;
import io.parsingdata.metal.data.ParseResult;
import io.parsingdata.metal.data.ParseValue;
import io.parsingdata.metal.util.GraphUtil;
import io.parsingdata.metal.util.ValueUpdater;
import io.parsingdata.metal.util.serialize.constraint.TransformConstraint;
import io.parsingdata.metal.util.serialize.process.ParseValueProcessor;
import io.parsingdata.metal.util.serialize.transform.ConditionalTransformer;
import io.parsingdata.metal.util.serialize.transform.ParseValueTransformer;

/**
 * Main serializer, serializes a Metal parse result to an output token serializer.
 *
 * Can apply various {@link ParseValueTransformer}s sequentially.
 *
 * @author Netherlands Forensic Institute.
 */
public final class Serializer {

    private final List<ConditionalTransformer> _transformers;

    public Serializer() {
        _transformers = new ArrayList<>();
    }

    /**
     * Adds a new transformer to this serializer.
     *
     * The constraint determines when a value should be transformed.
     * The fieldName determines what value should be transformed.
     * The transformer determines how the value should be transformed.
     *
     * @param valueName the name of the value to transform
     * @param transformer the transformer to transfrom the value with
     * @return this
     */
    public Serializer transform(final TransformConstraint constraint, final String fieldName, final ParseValueTransformer transformer) {
        final ConditionalTransformer transformModule = new ConditionalTransformer(
               checkNotNull(constraint, "constraint"),
               checkNotNull(fieldName, "fieldName"),
               checkNotNull(transformer, "transformer"));
        _transformers.add(transformModule);
        return this;
    }

    /**
     * Same as {@link #transform(TransformConstraint, String, ParseValueTransformer)}, with
     * {@link TransformConstraint#TRUE} as the constraint.
     *
     * @param valueName the name of the value to transform
     * @param transformer the transformer to transfrom the value with
     * @return this
     */
    public Serializer transform(final String valueName, final ParseValueTransformer transformer) {
        return transform(TransformConstraint.TRUE, valueName, transformer);
    }

    /**
     * Transform all parsed values in ParseResult and serialize them using a TokenSerializer.
     *
     * @param result the result to serialize
     * @param parseValueProcessor the serializer to use
     */
    public void serialize(final ParseResult result, final ParseValueProcessor parseValueProcessor) {
        serialize(result.getEnvironment(), parseValueProcessor);
    }

    /**
     * Transform all parsed values in ParseResult and serialize them using a TokenSerializer.
     *
     * @param environment the result to serialize
     * @param parseValueProcessor the serializer to use
     */
    public void serialize(final Environment environment, final ParseValueProcessor parseValueProcessor) {
        Environment env = environment;
        for (final ConditionalTransformer transformer : _transformers) {
            env = updateEnv(env, transformer);
        }
        serialize(parseValueProcessor, env.order);
    }

    private void serialize(final ParseValueProcessor parseValueProcessor, final ParseGraph graph) {
        final ParseItem head = graph.head;
        if (head == null) {
            return;
        }
        else if (head.isValue()) {
            parseValueProcessor.process(head.asValue());
        }
        else if (head.isGraph()) {
            serialize(parseValueProcessor, head.asGraph());
        }
        serialize(parseValueProcessor, graph.tail);
    }

    private Environment updateEnv(final Environment environment, final ConditionalTransformer transformer) {
        return updateEnv(environment.order, environment, transformer);
    }

    private Environment updateEnv(final ParseGraph graph, final Environment environment, final ConditionalTransformer transformer) {
        Environment newEnvironment = environment;
        final ParseItem head = graph.head;
        if (head == null) {
            return newEnvironment;
        }
        else if (head.isValue()) {
            newEnvironment = transform(head.asValue(), newEnvironment, transformer);
        }
        else if (head.isGraph()) {
            newEnvironment = updateEnv(head.asGraph(), newEnvironment, transformer);
        }
        return updateEnv(graph.tail, newEnvironment, transformer);
    }

    private Environment transform(final ParseValue value, final Environment environment, final ConditionalTransformer transformer) {
        Environment currentEnv = environment;
        ParseValue newValue = value;
        if (transformer.matchesFieldName(value.getName())) {
            final ParseGraph subGraph = GraphUtil.findSubGraph(environment, value, transformer.getTransformerContext());
            final Environment transformerEnvironment = new Environment(subGraph, environment.input, environment.offset);

            if (transformer.isSatisfiedBy(transformerEnvironment)) {
                newValue = transformer.transform(newValue, transformerEnvironment);
                currentEnv = ValueUpdater.updateEnv(currentEnv, newValue);
            }
        }
        return currentEnv;
    }
}