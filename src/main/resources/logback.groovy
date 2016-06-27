import ch.qos.logback.classic.encoder.PatternLayoutEncoder
import ch.qos.logback.core.rolling.TimeBasedRollingPolicy

// always a good idea to add an on console status listener
statusListener(OnConsoleStatusListener)

def appenderList = ["ROLLING"]
def WEBAPP_DIR = "."
def consoleAppender = true;

// does hostname match pixie or orion?
if (hostname =~ /pixie|orion/) {
    WEBAPP_DIR = "/opt/myapp"
    consoleAppender = false
} else {
    appenderList.add("CONSOLE")
}

if (consoleAppender) {
    appender("CONSOLE", ConsoleAppender) {
        encoder(PatternLayoutEncoder) {
            pattern = "%d{HH:mm:ss.SSS} [%thread] %-5level %logger{36} - %msg%n"
        }
    }
}

appender("ROLLING", RollingFileAppender) {
    encoder(PatternLayoutEncoder) {
        Pattern = "%d %level %thread %mdc %logger - %m%n"
    }
    rollingPolicy(TimeBasedRollingPolicy) {
        FileNamePattern = "${WEBAPP_DIR}/log/translator-%d{yyyy-MM}.zip"
    }
}

root(INFO, appenderList)