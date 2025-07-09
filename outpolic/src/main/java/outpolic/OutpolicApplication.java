package outpolic;

import org.apache.catalina.connector.Connector;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.embedded.tomcat.TomcatServletWebServerFactory;
import org.springframework.context.annotation.Bean;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@SpringBootApplication
public class OutpolicApplication {

    private static final Logger logger = LoggerFactory.getLogger(OutpolicApplication.class);

    public static void main(String[] args) {
        SpringApplication.run(OutpolicApplication.class, args);
    }

    /**
     * Tomcat 커넥터를 커스터마이징하여 multipart 요청의 최대 파트 개수를 늘립니다.
     * FileCountLimitExceededException 해결을 위한 가장 확실한 방법입니다.
     */
    @Bean
    public TomcatServletWebServerFactory tomcatFactory() {
        return new TomcatServletWebServerFactory() {
            @Override
            protected void customizeConnector(Connector connector) {
                super.customizeConnector(connector);
                String maxPartsValue = "100"; // 설정하려는 값
                connector.setProperty("maxParts", maxPartsValue);
                
                logger.info("Tomcat Connector: Attempting to set maxParts to {}", maxPartsValue);

                String actualMaxParts = (String) connector.getProperty("maxParts"); 

                if (actualMaxParts != null && actualMaxParts.equals(maxPartsValue)) {
                    logger.info("Tomcat Connector: Successfully set maxParts to {}", actualMaxParts);
                } else {
                    logger.warn("Tomcat Connector: Failed to set maxParts or value is not as expected. Current: {}", actualMaxParts);
                }
            }
        };
    }
}