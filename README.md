# Custom third-party dependencies

## Use case and potential problems
Sometimes, third-party libraries, required to run custom extensions, are missing in Polarion platform or exist in old versions.
In this cases developer can either:
- package third-party dependencies in own extension bundle;
- provide third-party dependencies in separate bundle

First approach can have the following problem: because own extension bundle uses Require-Bundle statement to import Polarion dependencies,
it causes import all transitive dependencies of required bundles with high priority.

As a result, imported Polarion transitive dependencies will be used instead packaged into own extension bundle. This can cause versions mismatch and runtime errors.
In such case second approach is preferred.

## Solution

We have faced the such problem when tried to use Apache POI v5.2.4. This version uses Apache Commons IO v2.13.0, but Polarion has v2.11.0.
As a result it leads to ClassNotFoundExceptions in some cases when Apache POI functionality is called.
This project demonstrates how to solve this problem using the packaging of Apache POI dependency as a separate bundle.

## Build

All the extension artifacts can be produced using maven:
```bash
mvn clean package
```

## Installation to Polarion

To install the extensions to Polarion they should be copied to `<polarion_home>/polarion/extensions/ch.sbb.polarion.thirdparty.bundles.<extension_name>/eclipse/plugins`
It can be done manually or automated using maven build:
```bash
mvn clean install -P install-to-local-polarion
```
For automated installation with maven env variable `POLARION_HOME` should be defined and point to folder where Polarion is installed.

Changes only take effect after restart of Polarion.

## How to use

To use Apache POI as an extension bundle the following changes are required:
1. `pom.xml` should have a provided dependency
```xml
<dependency>
    <groupId>ch.sbb.polarion.thirdparty.bundles</groupId>
    <artifactId>org.apache.poi</artifactId>
    <version>5.2.4</version>
    <scope>provided</scope>
</dependency>
```
2. `MANIFEST.MF` should contain `Require-Bundle` entry `ch.sbb.polarion.thirdparty.bundles.org.apache.poi`

