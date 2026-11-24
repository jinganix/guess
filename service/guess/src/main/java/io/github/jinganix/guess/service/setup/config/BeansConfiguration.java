/*
 * Copyright (c) 2020 jinganix@gmail.com, All Rights Reserved.
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
 *
 * https://github.com/jinganix/guess
 */

package io.github.jinganix.guess.service.setup.config;

import io.github.jinganix.guess.service.helper.uid.SnowflakeGenerator;
import io.github.jinganix.guess.service.helper.uid.UidGenerator;
import io.github.jinganix.peashooter.Tracer;
import io.github.jinganix.peashooter.trace.DefaultTracer;
import io.github.jinganix.webpb.runtime.mvc.WebpbRequestBodyAdvice;
import io.github.jinganix.webpb.runtime.reactive.WebpbClient;
import io.netty.channel.ChannelOption;
import java.time.Duration;
import java.util.concurrent.Executors;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.autoconfigure.task.TaskExecutionAutoConfiguration;
import org.springframework.boot.web.embedded.tomcat.TomcatProtocolHandlerCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.task.AsyncTaskExecutor;
import org.springframework.core.task.support.TaskExecutorAdapter;
import org.springframework.http.client.ReactorResourceFactory;
import org.springframework.http.client.reactive.ReactorClientHttpConnector;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.netty.http.client.HttpClient;

/** Configuration for beans. */
@Configuration
@RequiredArgsConstructor
public class BeansConfiguration {

  @Bean
  UidGenerator uidGenerator() {
    return new SnowflakeGenerator();
  }

  @Bean
  WebpbRequestBodyAdvice requestBodyAdvice() {
    return new WebpbRequestBodyAdvice();
  }

  @Bean
  WebClient.Builder webClientBuilder(ReactorResourceFactory resourceFactory) {
    HttpClient httpClient =
        HttpClient.create(resourceFactory.getConnectionProvider())
            .option(ChannelOption.CONNECT_TIMEOUT_MILLIS, 5000)
            .responseTimeout(Duration.ofSeconds(10));
    return WebClient.builder().clientConnector(new ReactorClientHttpConnector(httpClient));
  }

  @Bean
  WebpbClient webpbClient(WebClient.Builder webClientBuilder) {
    return new WebpbClient(webClientBuilder.build());
  }

  @Bean(TaskExecutionAutoConfiguration.APPLICATION_TASK_EXECUTOR_BEAN_NAME)
  AsyncTaskExecutor asyncTaskExecutor() {
    return new TaskExecutorAdapter(Executors.newVirtualThreadPerTaskExecutor());
  }

  @Bean
  TomcatProtocolHandlerCustomizer<?> protocolHandlerVirtualThreadExecutorCustomizer() {
    return x -> x.setExecutor(Executors.newVirtualThreadPerTaskExecutor());
  }

  @Bean
  Tracer tracer() {
    return new DefaultTracer();
  }
}
