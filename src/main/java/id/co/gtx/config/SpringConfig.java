package id.co.gtx.config;

import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.zkoss.spring.config.ZkScopesConfigurer;

@Configuration
@ComponentScan(basePackages = {"id.co.gtx"})
@Import(ZkScopesConfigurer.class)
public class SpringConfig {
}
