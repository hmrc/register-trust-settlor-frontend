
# Register Trust Settlor Frontend

This service is responsible for collecting details about the settlor of the trust when registering a trust.

## Running the micro-service
To run locally using the micro-service provided by the service manager:

```
sm2 --start TRUSTS_ALL
```

Or

```
sm2 --start REGISTER_TRUST_ALL
```


If you want to run your local copy, then stop the frontend ran by the service manager and run your local code by using the following (port number is 8843 but is defaulted to that in build.sbt):

```sbt run```

## Testing the service

This service uses [sbt-scoverage](https://github.com/scoverage/sbt-scoverage) to
provide test coverage reports.

Use the following commands to run the tests with coverage and generate a report.

Run this script before raising a PR to ensure your code changes pass the Jenkins pipeline. This runs all the unit tests with scalastyle and checks for dependency updates:
```
./run_all_tests.sh
```


## Making content changes

### Messages in the past tense

When adding content in the past tense for the check answer pages, include "PastTense" in the message key. This is so the content can be displayed in the correct tense on the registration draft page ([trusts-frontend](https://github.com/hmrc/trusts-frontend)). You will need to add the new message keys on [trusts-frontend](https://github.com/hmrc/trusts-frontend) as well. 

Example:

For the key```settlorIndividualName.checkYourAnswersLabel``` the past tense would be ```settlorIndividualNamePastTense.checkYourAnswersLabel``` 

### License

This code is open source software licensed under the [Apache 2.0 License]("http://www.apache.org/licenses/LICENSE-2.0.html").
