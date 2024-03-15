# Metal

A Java library for parsing binary data formats, using declarative descriptions.

![GH Actions Metal build](https://github.com/parsingdata/metal/actions/workflows/build.yml/badge.svg)
[![Build status](https://ci.appveyor.com/api/projects/status/69hk2llxjjyatuyq/branch/master?svg=true)](https://ci.appveyor.com/project/parsingdata/metal/branch/master)
[![codecov.io](https://codecov.io/github/parsingdata/metal/coverage.svg?branch=master)](https://codecov.io/github/parsingdata/metal?branch=master)
[![CodeFactor](https://www.codefactor.io/repository/github/parsingdata/metal/badge)](https://www.codefactor.io/repository/github/parsingdata/metal)
[![Codacy Badge](https://app.codacy.com/project/badge/Grade/58fd44c214a4425f967e27214bb3a924)](https://www.codacy.com/gh/parsingdata/metal/dashboard?utm_source=github.com&amp;utm_medium=referral&amp;utm_content=parsingdata/metal&amp;utm_campaign=Badge_Grade)
[![SonarCloud](https://sonarcloud.io/api/project_badges/measure?project=parsingdata_metal&metric=alert_status)](https://sonarcloud.io/dashboard?id=parsingdata_metal)

## Using Metal

Metal releases are available in the central Maven repository. To use the latest (10.0.0) release of Metal, include the following section in the pom.xml under dependencies:

```xml
<dependency>
  <groupId>io.parsingdata</groupId>
  <artifactId>metal-core</artifactId>
  <version>10.0.0</version>
</dependency>
```

In addition, snapshots are published to GitHub Packages. In order to use those, add the following section in the pom.xml under repositories:

```xml
<repository>
  <id>github-metal-snapshots</id>
  <url>https://maven.pkg.github.com/parsingdata/metal</url>
  <snapshots>
    <enabled>true</enabled>
  </snapshots>
</repository>
```

Please read the [Authenticating to GitHub Packages](https://docs.github.com/en/packages/working-with-a-github-packages-registry/working-with-the-apache-maven-registry#authenticating-to-github-packages) documentation to learn how give Maven access to the repository.

## License

Copyright 2013-2024 Netherlands Forensic Institute
Copyright 2021-2024 Infix Technologies B.V.

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

[http://www.apache.org/licenses/LICENSE-2.0](http://www.apache.org/licenses/LICENSE-2.0)

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
