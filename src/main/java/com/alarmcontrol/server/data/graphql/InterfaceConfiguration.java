package com.alarmcontrol.server.data.graphql;

import com.alarmcontrol.server.aaos.Aao;
import com.alarmcontrol.server.notifications.messaging.mail.MailContact;
import com.alarmcontrol.server.notifications.usecases.alertcreated.AlertCreatedNotificationConfig;
import com.alarmcontrol.server.rules.BetweenTimeRangeRule;
import com.coxautodev.graphql.tools.SchemaParserDictionary;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class InterfaceConfiguration {

  @Bean
  public SchemaParserDictionary getSchemaParser() {
    SchemaParserDictionary dictionary = new SchemaParserDictionary();
    dictionary.add(MailContact.class);
    dictionary.add(AlertCreatedNotificationConfig.class);
    dictionary.add(Aao.class);
    //dictionary.add(BetweenTimeRangeRule.class);
    return dictionary;
  }
}