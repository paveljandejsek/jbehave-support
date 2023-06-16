package org.jbehavesupport.core.rest

import org.jbehavesupport.test.support.TestSupport
import org.springframework.test.annotation.DirtiesContext
import spock.lang.Specification

@DirtiesContext
class NegativeRestScenariosIT extends Specification implements TestSupport {

    def "Should validate REST response via success handlers"() {
        when:
        def result = run(runWith("rest/NegativeRestHandling.story"))

        then:
        true
        result.getTotalFailureCount() == 3
        result.getFailures().get(0).exception.getMessage().contains("Expected response code is CREATED but was BAD_REQUEST")
        result.getFailures().get(1).exception.getMessage().contains("value 'sad' is not equal to 'happy'")
        result.getFailures().get(2).exception.getMessage().contains("value 'astronaut' is not equal to 'hippopotamus'")
    }
}
