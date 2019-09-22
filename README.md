# Spring Boot with PCF Metrics Demo

Application demonstrating Logging, Metrics, Tracing, Monitoring, and Alerting functionality with Spring Boot 2.0 (with Micrometer) and functionality of the "PCF Metrics" Tile in Pivotal Cloud Foundry.

Centralized logging / metrics / and logging functionality is provided out-the-box with the PCF platform with a default storage time of 2 weeks.

Third-party platform ingestion (i.e. Splunk, Datadog, Promethus) can be added as well for more advanced use-cases.

## Demo Requirements

### 1 - PCF 2.4 or higher with PCF Metrics Tile installed (available on PWS)

### 2 - CF CLI installed locally

### 3 - Slack installed, with a Incoming Webhook created (instructions provided)

## Demo Steps

### 1 - Build and Deployment

Build the application (via Gradle)

```sh
./gradlew
```

or build the application (via Maven)
```sh
./mvnw package
```

Deploy to PCF using the CLI (for gradle build)

```sh
cf push APP-NAME
```

Deploy to PCF using the CLI (for maven build)

```sh
cf push APP-NAME -p target/spring-boot-metrics-demo-1.0.0-SNAPSHOT.jar
```

> Where APP-NAME is your desired unique application name in PCF

> If you get a *The app cannot be mapped to route metrics-demo.cfapps.io because the route exists in a different space.* error.  Run the push command with --random-route.

```sh
cf push --random-route metrics-demo
```

### 2 - Setup

#### 2.1 - Metrics Setup

##### Ensure Metric Registrar CLI plugin is installed

```sh
cf plugins
```

If *metric-registrar* does not exist run the following command:

```sh
cf install-plugin -r CF-Community "metric-registrar"
```

> Remember to replace APP-NAME with your application name.

##### Register your Metrics endpoint with PCF

```sh
cf register-metrics-endpoint APP-NAME /actuator/prometheus
``` 

> Remember to replace APP-NAME with your application name.

#### 2.2 - Alerting Setup

Create a Slack Incoming WebHook URL

One can be created by logging into your Slack account at www.slack.com, browsing the *App Directory* for *Incoming WebHooks* and adding your own configuration. 

(App Directory Link is available on home page footer of Slack under Resources).

<img src="img/appdirectory.png" width="750">

<img src="img/webhook.png" width="750">

<img src="img/webhookurl.png" width="750">

You should then be able to send Slack messages to yourself by *posting* to that URL.

Example: (note INSERT_YOUR_WEB_HOOK_URL -- update this with your generated "Webhook URL")

```sh
curl -s -d "payload={\"text\":\"Test Message\"}" INSERT_YOUR_WEB_HOOK_URL_HERE
```

To demo the Application Error Level Log Alerting with deployed PCF apps, make sure your PCF app instance has the *SLACK_INCOMING_WEB_HOOK* environment variable set to your URL.

To add this value using the CLI (update INSERT_YOUR_WEB_HOOK_URL_HERE accordingly)

```sh
cf set-env APP-NAME SLACK_INCOMING_WEB_HOOK INSERT_YOUR_WEB_HOOK_URL_HERE
```

You will need to re-stage for the changes to take effect:

```sh
cf restage APP-NAME
```

For local testing you will need to set your SLACK_INCOMING_WEB_HOOK environment variable accordingly. 

```sh
export SLACK_INCOMING_WEB_HOOK=https://hooks.slack.com/services/SOME_CORRECT_VALUE

```

#### 2.3 - Logging Setup

No setup required, PCF automatically takes all STDIO / STERR output from applications.

#### 2.4 - Tracing Setup

No setup required, PCF automatically detected tracing information provided from Spring Cloud Sleuth dependency.


### 3 - Demo

PCF Metrics GUI is PCF AppsMan application view under the "View in PCF Metrics" button.

Application functionality is available from the default application path ( i.e. the application index page ).

#### 3.1 - PCF Metrics - Metrics 

Demo Application Metrics including the built-in ones listed with the /actuator/metrics endpoint. As well as the custom ones used in the MetricsController.

Demo creation of Chart in PCF Metrics using custom application data.

<img src="img/metrics.png" width="750">

#### 3.2 - PCF Metrics - Alerting (Beta)

Create a PCF Metric Alert under the Monitor Tab -- use the build in crash event.

Demo Slack PCF Metrics Alerting, by simulating a JVM crash (button).

<img src="img/alert.png" width="750">

PCF Metrics Event alerting lag may talk up to a few minutes.

#### 3.3 - PCF Metrics - Trace Explorer

Discuss the value of "Correlation IDs" , especially with regards to MicroService designs.

Invoke a traced calling -- via provided web gui. 

Locate it in the PCF Metrics Log view, and view it in the Trace Explorer.

<img src="img/viewtrace.png" width="750">

PCF Metrics Trace ingestion may take up to a few minutes.

Note that Logs will be aggregated across Applications as well (assuming shared TraceId).

<img src="img/trace.png" width="750">

#### 3.4 - PCF Metrics Logging

Demo Logging and Search in PCF Metrics.

Also demo CLI access to centralized logging.

```sh
cf logs APP_NAME
```

#### 3.5 - Application Error Level Log Alerting

Note SLACK_INCOMING_WEB_HOOK environment variable needs to be set -- either in PCF or locally.

> TIP - Add Logging Error Level Alerts to your Applications for quick notification of Application Issues and reduction in *Mean-Time-to-Repair* (MTTR).

##### Server-Side (JVM)

Demo Server-Side Alerting (JVM), by causing a Server-Side Application Error / Exception (button).

##### Client-Side (JS)

Demo Client-Side Alerting (JS), by causing a Client-Side Error / Exception (button).

Note that we are persisting the UserAgent -- important in helping isolate "page-snap" issues.

##### Application Information

Demo the /actuator/info endpoint.  Note the inclusion of Git and Build information to easily identify deployed artifact.

<img src="img/info.png" width="300">

Note that custom /info and /health information can easily be added in a variety of ways.

### 4 - Additional Demo Notes

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

#### Metrics Label / Tag Cardinality 

High cardinality label / tag values (i.e. unique guids, user data such as emails, etc) for metrics implementations are highly discouraged, and have the potential to overwhelm time-series databases such as Datadog or Promethues. 

### 5 - 3rd-Party Metrics -- Humio Integration Instructions (Optional)

As an alternate target for Metrics, you can also use alternate Metrics Platform Solutions. One such / easy to setup solution is Humio.

Setup a Free Humio Cloud account at : https://humio.com

Add the Humio Micrometer registry to your build dependencies. 

```gradle
    compile('io.micrometer:micrometer-registry-humio:latest.release')
```

In your sandbox repo settings, create and get an Ingest Token. 

Update your application properties with the Humio ingest token:

```properties
    management.metrics.export.humio.api-token=YOUR_TOKEN
```

<img src="img/humio.png" width="800">

Sample Humio Queries: 

Timechart example:

__name=purchase | timechart(function=avg(sum))__

Timechart example split by product_name (label / tag):

__name=purchase | timechart(product_name, function=avg(sum))__
