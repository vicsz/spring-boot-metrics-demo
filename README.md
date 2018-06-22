# Spring Boot with PCF Metrics Demo

Application demonstrating Logging, Metrics, Tracing, and Alerting functionality with Spring Boot 2.0 (with Micrometer) and functionality of the "PCF Metrics" Tile in Pivotal Cloud Foundry.

Centralized logging / metrics / and logging functionality is provided out-the-box with the PCF platform with a default storage time of 2 weeks.

Third-party platform ingestion (i.e. Splunk, Datadog, Promethus) can be added as well for more advanced use-cases.

## Demo Steps

### 1. Deploy to PCF

Build the application:

```sh
./gradlew build
```

Deploy to PCF using the CLI:

Note: using random route in-case of pre-existing route.

```sh
cf push metrics-demo --random-route -p build/libs/spring-boot-metrics-demo-0.0.1-SNAPSHOT.jar
```

### 2. Create and Bind the Forwarder Service

This is required for Custom Application Metrics.

#### Ensure *Metric Forwarder* service is available in the CF MarketPlace

```sh
cf marketplace
```

Contact your PCF Cloud Ops team if it is not.

#### Create the Service

You can use a *plan* and *name* of your choice.

```sh
cf create-service metrics-forwarder unlimited myforwarder
```

#### Bind the Service to your Application

```sh
cf bind-service metrics-demo myforwarder
```

#### Restage your Application

```sh
cf restage metrics-demo
```

### 3. Create a Slack Incoming WebHook URL

Required for Alerting Functionality.

One can be created by logging into your Slack account at www.slack.com, browsing the *App Directory* for *Incoming WebHooks* and adding your own configuration. (App Directory Link is available on home page footer of Slack under Resources).

You should then be able to send Slack messages to yourself by *posting* to that URL.

Example: (note INSERT_YOUR_WEB_HOOK_URL -- update this with your URL)

```sh
curl -s -d "payload={\"text\":\"Test Message\"}" INSERT_YOUR_WEB_HOOK_URL_HERE
```

To demo the Application Error Level Log Alerting with deployed PCF apps, make sure your PCF app instance has the *SLACK_INCOMING_WEB_HOOK* environment variable set to your URL.

To add this value using the CLI (update INSERT_YOUR_WEB_HOOK_URL_HERE accordingly)

```sh
cf set-env metrics-demo SLACK_INCOMING_WEB_HOOK INSERT_YOUR_WEB_HOOK_URL_HERE
```

You will need to re-stage after this .. 

### 4. Demo the functionality

Functionality is available from the default application path / route.

#### Metrics

Demo Application Metrics including the built-in ones listed with the /actuator/metrics endpoint. As well as the custom ones used in the MetricsController.

Demo creation of Chart in PCF Metrics using custom application data.

<img src="img/metrics.png" width="750">

#### PCF Metrics - Alerting

Create a PCF Metric Alert under the Monitor Tab -- use the build in crash event.

Demo Slack PCF Metrics Alerting, by simulating a JVM crash (button).

<img src="img/alert.png" width="750">

PCF Metrics Event alerting lag may talk up to a few minutes.

#### Trace

Invoke a traced calling.

Locate it in the PCF Metrics Log view, and view it in the Trace Explorer.

PCF Metrics Trace ingestion may talk up to a few minutes.

Note that Logs will be aggregated across Applications as well (assuming shared TraceId).

<img src="img/trace.png" width="750">

#### Logging

Demo Logging in PCF Metrics.

Also demo CLI access to centralized logging.

```sh
cf logs APP_NAME
```

#### Logging - Alert

Demo Slack Logging Alerting, by causing an Application Error / Exception (button).

Note SLACK_INCOMING_WEB_HOOK environment variable needs to be set -- either in PCF or locally.

### Demo Notes

#### Exposed Actuator Endpoints

Note that most actuator endpoints are restricted / locked down by default , hence the change in the *application.properties* file to expose all of them (not recommended in production)

```properties
management.endpoints.web.exposure.include=*
```
#### Logfile Actuator Endpoint Requirements

For the logfile endpoint to be enabled, to application needs to be configured to save logs to a local flat file (not a default).

#### VCAP environment variable usage in logback.xml

Note the VCAP environment variable usage in logback.xml to include PCF information in Slack Alerts.

#### Custom CONSOLE_LOG_PATTERN in logback.xml

For Log messages to correlate to Trace / Span Id's,  these 2 pieces of information need to be included in log entries.

#### Route usage (in TraceController) via VCAP variables

For demo purposes, the Demo is calling itself (via app route) , use of localhost:8080 will prevent Tracing Information from showing up as it's captured at the route request level.

#### WebClient Bean Annotation

WebClient needs to be annotated with Bean annotation for Spring Cloud Sleuth to correctly "hook in" and inject Trace information.

#### Tracing Spring Cloud Sleuth Requirements

The org.springframework.cloud:spring-cloud-starter-sleuth dependency is required for injection of required Tracing information.


