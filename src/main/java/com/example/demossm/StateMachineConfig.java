/*
 * Copyright 2017-2018 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.example.demossm;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;
import org.springframework.statemachine.config.EnableStateMachineFactory;
import org.springframework.statemachine.config.StateMachineConfigurerAdapter;
import org.springframework.statemachine.config.StateMachineFactory;
import org.springframework.statemachine.config.builders.StateMachineConfigurationConfigurer;
import org.springframework.statemachine.config.builders.StateMachineModelConfigurer;
import org.springframework.statemachine.config.model.StateMachineModelFactory;
import org.springframework.statemachine.data.jpa.JpaPersistingStateMachineInterceptor;
import org.springframework.statemachine.data.jpa.JpaStateMachineRepository;
import org.springframework.statemachine.persist.StateMachineRuntimePersister;
import org.springframework.statemachine.service.DefaultStateMachineService;
import org.springframework.statemachine.service.StateMachineService;

@Configuration
public class StateMachineConfig {

    @Bean
    public StateMachineRuntimePersister<String, String, String> stateMachineRuntimePersister(
            JpaStateMachineRepository jpaStateMachineRepository) {
        return new JpaPersistingStateMachineInterceptor<>(jpaStateMachineRepository);
    }

    @Configuration
    @EnableStateMachineFactory
    public static class MachineConfig extends StateMachineConfigurerAdapter<String, String> {

        @Autowired
        private StateMachineRuntimePersister<String, String, String> stateMachineRuntimePersister;

        @Override
        public void configure(StateMachineConfigurationConfigurer<String, String> config)
                throws Exception {
            config
                    .withPersistence()
                    .runtimePersister(stateMachineRuntimePersister);
        }

        @Override
        public void configure(StateMachineModelConfigurer<String, String> stateMachineModel) throws Exception {
            stateMachineModel
                    .withModel().factory(modelFactory());
        }

        @Bean
        public StateMachineModelFactory<String, String> modelFactory() {
            return new UnverifiedUmlStateMachineModelFactory(new ClassPathResource("doc/onflow-uml/onflow.uml"));
        }

        @Bean
        public StateMachineService<String, String> stateMachineService(
                StateMachineFactory<String, String> stateMachineFactory,
                StateMachineRuntimePersister<String, String, String> stateMachineRuntimePersister) {
            return new DefaultStateMachineService<>(stateMachineFactory, stateMachineRuntimePersister);
        }

    }
}
