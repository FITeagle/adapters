[![Build Status](https://travis-ci.org/FITeagle/adapters.svg)](https://travis-ci.org/FITeagle/adapters)
[![Coverage Status](https://coveralls.io/repos/FITeagle/adapters/badge.svg)](https://coveralls.io/r/FITeagle/adapters)

FITeagle Resource Adapters
===========================

To run the tests and build the documentation:

    mvn clean install test site site:stage
    open target/staging/adapters/index.html

To deploy all adapters:

    mvn clean package wildfly:deploy

