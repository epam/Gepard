Gepard
===========
Gepard is a JUnit based test automation framework for Test Automation Engineers and for Software Engineers in Test, those can focus on writing the test cases instead of focusing on how to do data driven things, how to deal with parallel test executions, how to deal with end-user readable test reports etc. 
 
What Gepard stands for:

- **Core module** of a Java/JUnit based test automation framework ( **gepard-core** ). Provides common approach for test automation engineers to use same framework from low level (class level) unit tests through service level tests up to and including UI/Functional tests.
It provides common and reliable multi-threaded, multi-dimensional data driven test execution and reporting mechanism to build automated tests on it. Test Automation Experts can focus on test implementation, meanwhile all other work (data driven execution, proper scheduling and reporting) is done by this core module of the framework. 
Easily expandable (like for Selenium, Android native application test).

* Further modules:
    * **gepard-gherkin-jbehave**: Example extension for using JBehave
    * **gepard-gherkin-cucumber**: Example extension for using Cucumber
    * **gepard-selenium**: Example extension for using Selenium
    * **gepard-rest**: Example extension for using Gepard to test Rest services, and provides connection to Jira
    * **gepard-examples**: Lot's of examples
    * **gepard-project-template**: start your test project here

# Quick intro for end users
#### Requirements
* Java JDK 8.
* Get _gepard-project-template_ zip from Github release page, and read [Gepard Users' Guide](https://github.com/epam/Gepard/wiki/Gepard-Users'-Guide) wiki page as starting point.
* To share your experienca and ask questions, pls contact to the mainatiners, or submit an issue.
* Get _gepard-examples_ module from Git, and see example tests and tips there.

#### Running
`gradlew run`
This will execute lots of example tests from gepard-examples module.

# Quick intro for developers/contributors

#### Requirements
* Java JDK 8.

#### Advised working environment
* Eclipse / IntelliJ
* Gradle, Checkstyle, Git Integration for the IDE

#### Building with Gradle
The project can be built by executing the following command from project root folder:

`gradlew clean build`

Actual build status: [![Build Status](https://travis-ci.org/epam/Gepard.svg?branch=master)](https://travis-ci.org/epam/Gepard)

## Detailed information
* Check the Wiki and Issues on GitHub
* Check further documentation at http://epam.github.io/Gepard/

# License - GPL-v3.0
Copyright 2013-2015 EPAM Systems

Gepard is free software: you can redistribute it and/or modify
it under the terms of the GNU General Public License as published by
the Free Software Foundation, either version 3 of the License, or
(at your option) any later version.

Gepard is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
GNU General Public License for more details.

You should have received a copy of the GNU General Public License
along with Gepard. If not, see <http://www.gnu.org/licenses/>.

## Contribution

There are three ways you can help us:

* **Raise an [issue](https://github.com/epam/Gepard/issues).** You found something that does not work as expected? Let us know about it.
* **Suggest a [feature](https://groups.google.com/forum/#!forum/gepard-users).** It's even better if you come up with a new feature and write us about it.
* **Write some code.** We would love to see pull requests to this tool, just make sure you have the latest sources.
