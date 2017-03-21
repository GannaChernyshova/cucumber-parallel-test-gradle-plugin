package ru.alfalab.gradle

import groovy.transform.CompileStatic
import groovy.util.logging.Slf4j

@CompileStatic
@Slf4j
class CucumberRunnerClassGenerator {

    private int classNumber = 1;
    List<String> glue;
    boolean useReportPortal;
    boolean monochrome;
    boolean strict;

    def generateInnerRunnerClass(File file) {
        log.debug("Generate runner for file $file.name")
        if(!file.name.endsWith(".feature")) {
            log.warn("Wrong file extension. File = $file.absolutePath");
            return;
        }
        generateInnerRunnerClass(file.name, file.absolutePath);
    }

    def generateInnerRunnerClass(String fileName, String absolutePath) {
        log.debug("Generate runner for file $fileName")
        getFeatureClass(absolutePath);
    }

    String pathToJavaSource(String path) { path.replace("\\", "\\\\") }

    String getHeader() {"""
import cucumber.api.CucumberOptions;
import cucumber.api.junit.Cucumber;
import org.junit.runner.RunWith;
public class GradleTestRunner {
"""}

    String getFeatureClass(String featuresPath){"""
    @RunWith(Cucumber.class)
    @CucumberOptions (
            glue = {${'"' + glue.join('", "') + '"'}},
            format = {${getCucumberFormatOptions()}},
            features = {"${pathToJavaSource(featuresPath)}"},
            strict = ${strict},
            monochrome = ${monochrome}
    )
    public static class GradleTestRunner${classNumber++} { }
"""}

    def getCucumberFormatOptions() {
        def options = [ "pretty", "json:build/cucumber/cucumber${classNumber}.json" ]
        if(useReportPortal) {
            options << "com.epam.reportportal.cucumber.ScenarioReporter"
        }
        return '"' + options.join('", "') + '"'
    }

    String getFooter(){"""
}
"""}
}
