# Component Tests

This module adds tests that run a fully-built Federated Catalog runtime, except that actually sending out the requests
over IDS is mocked. The majority of integration tests should be done in this fashion.

To run the component tests, run the following command:
```
./gradlew :system-tests:component-tests:test -DincludeTags="ComponentTest" 
```