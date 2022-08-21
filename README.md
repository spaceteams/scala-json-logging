# Simple JSON logging for Scala

An [SLF4J](https://www.slf4j.org/index.html) backend that provides opinionated JSON structured logging for cloud-native environments.

It takes an SLF4J logging event and produces a `JSON` structured log line including context information to the process `STDOUT`.

It requires at least SLF4J version 2.0.0.

It supports Scala 2.13+ and Scala 3.1+

## Motivation

[SLF4J](https://www.slf4j.org/index.html) is the de-facto standard for implementing backend agnostic logging within the JVM ecosystem. With [log4j](https://logging.apache.org/log4j/2.x/manual/architecture.html) and [logback](https://logback.qos.ch/manual/architecture.html#LoggerContext), featureful backends exist that can fulfill the logging needs for most applications.

In cloud native environments, most of these features are often neither deeded nor desired. On the flip side, the complexity of these backends also come with the risks, legacy- and complexity tradeoffs that come with a large set of features. 

The motivation behind this backend is to provide an implementation that provides the bare minimum functionality required.

## Usage

SLF4J version 2 relies on the ServiceLoader mechanism to find its logging backend. Including the backend as a dependency or putting it into the classpath is sufficient for it to work. 

Be aware that this backend does not provide any JSON serialization on its own. Instead, the application is supposed to ship its own dependencies and pick the flavor of backend that matches those dependencies.

For [circe json](https://circe.github.io/circe/), pick

```scala
ThisBuild / libraryDependencies += "de.spaceteams" %% "json-logging-circe" % <version>
```

For [spray json](https://github.com/spray/spray-json), pick

```scala
ThisBuild / libraryDependencies += "de.spaceteams" %% "json-logging-spray" % <version>
```

## Configuration

Loggers and levels can be configured programatically.

```scala
val logger = LoggerFactory.getLogger("logger-name")
logger.asInstanceOf[JsonLogger].setLevel(Some(Level.INFO))
```

The backend does not come with any support for configuration files or similar mechanisms built-in.
If desired, a configuration system can be plugged in by providing a [`Service Provider`](https://docs.oracle.com/en/java/javase/11/docs/api/java.base/java/util/ServiceLoader.html) that implements the [`de.spaceteams.jsonlogging.ConfigurationService`](./common/src/main/scala/de/spaceteams/jsonlogging/ConfigurationService.scala) interface.

The `typesafe-config` sub-module is included as an example of how such a service provider might look like.

### Typesafe Config

The `typesafe-config` service provider can be used to apply simple configurations via the well-known [Typesafe Config](https://github.com/lightbend/config) library that comes included in many scala frameworks.

To make use of it, include it as a dependency in your `build.sbt`:
```scala
ThisBuild / libraryDependencies += "de.spaceteams" %% "json-logging-typesafe-config" % <version>
```

Logger configuration can then be defined in a projects `application.conf`
```hocon
logging {
    levels {
        "de.spaceteams" = "TRACE"
        "de.spaceteams.logging" = "DEBUG"
    }
}
```

### Hirarchy
The backend supports the concept of a named hirarchy.

A logger is said to be an ancestor of another logger if its name followed by a dot is a prefix of the descendant logger name. A logger is said to be a parent of a child logger if there are no ancestors between itself and the descendant logger. 

For example, `java` is a parent of `java.util` and an ancestor of `java.util.Vector`. This naming scheme should be familiar to developers familiar with backends like [log4j](https://logging.apache.org/log4j/2.x/manual/architecture.html) or [logback](https://logback.qos.ch/manual/architecture.html#LoggerContext)

Likewise, logger levels propagate from parents to children if these do not have a level assigned themselves. This to should be familiar form how well known backends operate.

| Logger name | Assigned level | Effective level |
| ----------- | -------------- | --------------- |
| root        | DEBUG          | DEBUG           |
| X           | none           | DEBUG           |
| X.Y         | INFO           | INFO            |
| X.Y.Z       | none           | INFO            |
| X.Y.Z.A     | ERROR          | ERROR           |

### Contextual information

This backend has rudamentary support for adding context information via the [MDC](https://www.slf4j.org/manual.html#mdc). Key / values pairs that are present in the MDC will be added as such to the log event context and appear in the resulting JSON line.

Global contextual information can be added to the logging context via environment variables.
Environment variables prefixed with `LOGGING_CONTEXT_` will be added to the context map for every event, eg.
```bash
  export LOGGING_CONTEXT_hostname="$(hostname)"
```
will add the `hostname` key with the machines current host name to all log events, eg.
```scala
  logger.info("test message")
```
produces a log line similar to
```json
{ "hostname": "spacemachine", "message": "test message", "level": "INFO", ... }
```

## Caveats

The current version was not optimized for performance.