# sbt-hadoop
An [sbt](http://scala-sbt.org) plugin for publishing artifacts to the [Hadoop](https://hadoop.apache.org) Distributed File System (HDFS).

## Table of contents

  * [Requirements](#requirements)
  * [Installation](#installation)
  * [Usage](#usage)
    * [Configuring the `hadoopClasspath`](#configuring-the-hadoopclasspath)
    * [Setting the `hadoopHdfsArtifactPath`](#setting-the-hadoophdfsartifactpath)
    * [Integration with the built-in `packageBin` task](#integration-with-the-built-in-packagebin-task)
    * [Integration with sbt-assembly](#integration-with-sbt-assembly)
  * [Contributing](#contributing)
    * [Project structure](#project-structure)
    * [Running tests](#running-tests)
    * [Releasing artifacts](#releasing-artifacts)

## Requirements
- sbt (0.13.5+)
- An installation of Hadoop (>= 2.6.x) to target

## Installation
Add the following line to `project/plugins.sbt`. See the [Using plugins](http://www.scala-sbt.org/release/docs/Using-Plugins.html) section of the sbt documentation for more information.

```
addSbtPlugin("com.tapad.sbt" % "sbt-hadoop" % "0.1.1")
```

## Usage
Assuming the `HADOOP_HOME` environmental variable is set and pointing to a local installation of the Hadoop binaries, add the following configuration to your build definition:

```
hadoopClasspath := hadoopClasspathFromExecutable.value

hadoopHdfsArtifactPath := new HdfsPath("/path/to/desired/hdfs/target/artifact.jar")

enablePlugins(HadoopPlugin)
```

### Configuring the `hadoopClasspath`
In order to publish artifacts to HDFS, sbt-hadoop needs valid configuration information about your Hadoop installation.

sbt-hadoop expects the typical Hadoop configuration files (`core-site.xml` and `hdfs-site.xml`) to be present on your local file system.

These configuration files can be discovered by sbt-hadoop in one of two ways:

1. By allowing sbt-hadoop to invoke a locally available `hadoop` binary
2. By statically adding their addresses to `hadoopClasspath`

Invoking the local binary allows sbt-hadoop to retrieve enough information to set the `hadoopClasspath` automatically.

To use a local `hadoop` binary for `hadoopClasspath` inference, add the following assignment to your build definition:

```
hadoopClasspath := hadoopClasspathFromExecutable.value
```

sbt-hadoop will attempt to find a local `hadoop` binary via the `HADOOP_HOME` environmental variable.

This can be manually overridden by setting a value for the `hadoopExecutable` setting key:

```
hadoopExecutable := Some(file("/usr/bin/hadoop"))
```

If a local `hadoop` binary is not available, the `hadoopClasspath` must be set statically.

Assuming configuration files are located on your local file system at `/usr/local/hadoop-2.7.3/etc`, you can configure your build as follows:

```
hadoopClasspath := HadoopUtils.classpathFromDirectory(file("/usr/local/hadoop-2.7.3/etc"))
```

### Setting the `hadoopHdfsArtifactPath`
`hadoopHdfsArtifactPath` must be set before attempting to publish an artifact to HDFS.

It represents the target destination on HDFS where your artifact will be published.

The value of `hadoopHdfsArtifactPath` must be an instance of `org.apache.hadoop.fs.Path`.

A type alias, `HdfsPath`, is auto-imported for your convenience:

```
hadoopHdfsArtifactPath := new HdfsPath("/user/name/foo/bar/artifact-0.1.0.jar")
```

### Integration with the built-in `packageBin` task
By default, sbt-hadoop is configured to upload the resulting artifact of the `packageBin` task to HDFS.

It is still required to configure your `hadoopClasspath`, set your `hadoopHdfsArtifactPath`, and manually enable the `HadoopPlugin`.

```
hadoopClasspath := hadoopClasspathFromExecutable.value

hadoopHdfsArtifactPath := new HdfsPath(s"/tmp/${name.value}-${version.value}.jar")

enablePlugins(HadoopPlugin)
```

Once your build is properly configured, an invocation of `hadoop:publish` will build, and subsequentially publish, your binary artifact to HDFS.

For more information, refer to the [Packaging documentation](http://www.scala-sbt.org/0.13/docs/Howto-Package.html) provided in the sbt reference manual.

### Integration with sbt-assembly
To use sbt-hadoop in conjunction with sbt-assembly, add the following to your `project/plugins.sbt` and `build.sbt` files, respectively:

```
addSbtPlugin("com.eed3sign" % "sbt-assembly" % "0.14.4")

addSbtPlugin("com.tapad.sbt" % "sbt-hadoop" % "0.1.1")
```

```
hadoopLocalArtifactPath := (assemblyOutputPath in assembly).value

hadoopHdfsArtifactPath := new HdfsPath("/tmp", (assemblyJarName in assembly).value)

publish in Hadoop := (publish in Hadoop).dependsOn(assembly).value
```

Lastly, be sure to enable sbt-hadoop in your `build.sbt` file:

```
enablePlugins(HadoopPlugin)
```

sbt-assembly will be enabled automatically.

Once the build definition is configured properly, an invocation of `hadoop:publish` will build and subsequentially publish a fat jar to HDFS.

For more information, refer to the documentation provided by [sbt-assembly](https://github.com/sbt/sbt-assembly) and the scripted integration test found at [plugin/src/sbt-test/sbt-hadoop/assembly](plugin/src/sbt-test/sbt-hadoop/assembly).

## Contributing

### Project structure
- root (.)

#### root
The sbt plugin and underlying interface used to publish artifacts to HDFS.

### Running tests
The main features and functionality of `sbt-hadoop` are tested using sbt's [`scripted-plugin`](https://github.com/sbt/sbt/tree/0.13/scripted). `scripted` tests exist in the `src/sbt-test` directory of the root project.

To run these tests, issue `scripted` from an sbt session:

```
$ sbt
> scripted
```

To selectively run a single `scripted` test suite, issue `scripted <name of plugin>/<name of test project>`. e.g. `scripted sbt-hadoop/simple`.

Please note that `publishLocal` will be invoked when running `scripted`. `scripted` tests take longer to run than unit tests and will log myriad output to stdout. Also note that any output written to stderr during the execution of a `scripted` test will result in `ERROR` level log entries. These log entries will not effect the resulting status of the actual test.

### Releasing artifacts
`sbt-hadoop` uses [https://github.com/sbt/sbt-release](sbt-release). Simply invoke `release` from the root project to release all artifacts.
