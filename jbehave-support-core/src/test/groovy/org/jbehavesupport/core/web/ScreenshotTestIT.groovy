package org.jbehavesupport.core.web

import groovy.util.logging.Slf4j
import org.jbehavesupport.core.TestConfig
import org.jbehavesupport.core.internal.web.WebScreenshotCreator
import org.openqa.selenium.WebDriver
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.support.BeanDefinitionRegistry
import org.springframework.context.ApplicationContext
import org.springframework.test.annotation.DirtiesContext
import org.springframework.test.context.ContextConfiguration
import spock.lang.Specification

@Slf4j
@ContextConfiguration(classes = TestConfig)
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_CLASS)
class ScreenshotTestIT extends Specification {

    @Autowired
    WebDriver driver

    @Autowired
    WebScreenshotCreator webScreenshotCreator

    @Autowired
    ApplicationContext applicationContext

    void takesScreenshot() {
//        def webDrivers = applicationContext.getBeansOfType(WebDriver.class)
//        driver = webScreenshotCreator.driver

        when:
        log.warn("WebDriver identityHashCode {}", System.identityHashCode(driver))
        driver.get("http://localhost:11110")
        log.warn("WebDriver: {}", driver);
        def title = driver.getTitle();
        log.warn("page title {}", title);

        webScreenshotCreator.createScreenshot(WebScreenshotType.MANUAL)

        then:
        File screenshotDirectory = new File("./target/reports")
        FilenameFilter filter = new FilenameFilter() {
            @Override
            boolean accept(File dir, String name) {
                return name.matches("MANUAL_[0-9]+\\.png")
            }
        }
        screenshotDirectory.listFiles(filter).length > 0
    }
}
