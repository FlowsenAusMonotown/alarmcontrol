package com.alarmcontrol.server.logging.logbook;

import static java.util.stream.Collectors.joining;
import static org.zalando.logbook.BodyFilters.defaultValue;
import static org.zalando.logbook.BodyFilters.truncate;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import java.util.function.Predicate;
import java.util.function.UnaryOperator;
import java.util.regex.Pattern;
import org.apache.commons.lang3.StringUtils;
import org.apiguardian.api.API;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.zalando.logbook.BodyFilter;
import org.zalando.logbook.BodyFilters;
import org.zalando.logbook.DefaultHttpLogFormatter;
import org.zalando.logbook.HttpLogFormatter;
import org.zalando.logbook.JsonHttpLogFormatter;
import org.zalando.logbook.RawHttpRequest;
import org.zalando.logbook.SplunkHttpLogFormatter;
import org.zalando.logbook.spring.LogbookProperties;

@Configuration
@EnableConfigurationProperties({LogbookCustomProperties.class})
public class LogbookConfiguration {

  private final LogbookCustomProperties customProperties;

  public LogbookConfiguration(LogbookCustomProperties customProperties) {
    this.customProperties = customProperties;
  }

  @Bean
  public Predicate<RawHttpRequest> requestCondition() {
    return (rawHttpRequest) -> {
      LogbookFilterProperties filterProperties = customProperties.getFilterProperties(rawHttpRequest);
      return Conditions.isRelevant(rawHttpRequest, filterProperties.getInclude(), filterProperties.getExclude());
    };
  }

  @Bean
  public BodyFilter bodyFilter(final LogbookProperties properties) {
    final LogbookProperties.Write write = properties.getWrite();
    final int maxBodySize = write.getMaxBodySize();

    if (maxBodySize < 0) {
      return BodyFilter.merge(defaultValue(), customBodyFilter());
    }

    BodyFilter defaultBodyFilter = BodyFilter.merge(truncate(maxBodySize), defaultValue());
    return BodyFilter.merge(defaultBodyFilter, customBodyFilter());
  }

  private BodyFilter customBodyFilter(){
    final Set<String> properties = new HashSet<>();
    properties.add("idToken");
    properties.add("refreshToken");
    properties.add("password");
    return BodyFilters.replaceJsonStringProperty(properties,"XXX");
  }

  @Bean
  @ConditionalOnProperty(
      name = {"logbook.format.style"},
      havingValue = "http"
  )
  public HttpLogFormatter httpFormatter() {
    return new ConditionalHttpLogFormatter(new DefaultHttpLogFormatter(), customProperties);
  }

/*
  @Bean
  @ConditionalOnProperty(
      name = {"logbook.format.style"},
      havingValue = "curl"
  )
  public HttpLogFormatter curlFormatter() {
    return new ConditionalHttpLogFormatter(new CurlHttpLogFormatter(), properties);
  }*/

  @Bean
  @ConditionalOnProperty(
      name = {"logbook.format.style"},
      havingValue = "splunk"
  )
  public HttpLogFormatter splunkHttpLogFormatter() {
    return new ConditionalHttpLogFormatter(new SplunkHttpLogFormatter(), customProperties);
  }

  @Bean
  @ConditionalOnProperty(
      name = {"logbook.format.style"},
      havingValue = "json"
  )
  public HttpLogFormatter jsonFormatter(final ObjectMapper mapper) {
    return new ConditionalHttpLogFormatter(new JsonHttpLogFormatter(mapper), customProperties);
  }
}
