package ru.alfalab.gradle

import groovy.transform.CompileStatic
import groovy.util.logging.Slf4j
import org.gradle.api.Project
import org.gradle.api.file.FileCollection

/**
 * Created by ruslanmikhalev on 24/11/16.
 */
@CompileStatic
@Slf4j
class CucumberRunnerGenerator {

    private int classNumber = 1;

    File buildDir;
    FileCollection features;
    Project project;
    List<String> glue;
    boolean useReportPortal;

    public void generate() {
        project.mkdir(buildDir);
        new File(buildDir, "GradleTestRunner.java").withWriter("utf8") { writer ->
            writer << header;
            features.files.sort( { file -> file.name } ).each { file -> writer << generateInnerRunnerClass(file) }
            writer << footer;
        }
    }

    def generateInnerRunnerClass(File file) {
        log.debug("Generate runner for file $file.name")
        if(!file.name.endsWith(".feature")) {
            log.warn("Wrong file extension. File = $file.absolutePath");
            return;
        }
        getFeatureClass(file.absolutePath);
    }

    private static String pathToJavaSource(String path) { path.replace("\\", "\\\\") }

    private String getHeader() {"""
import cucumber.api.CucumberOptions;
import cucumber.api.junit.Cucumber;
import org.junit.runner.RunWith;
public class GradleTestRunner {
"""}

    private String getFeatureClass(String featuresPath){"""
    @RunWith(Cucumber.class)
    @CucumberOptions (
            glue = {${'"' + glue.join('", "') + '"'}},
            format = {${getCucumberFormatOptions()}},
            features = {"${pathToJavaSource(featuresPath)}"}
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

    private String getFooter(){"""
}
"""}

}
