# GitHub Team Dashboard

Whether it is in your laptop or in a big screen, _GitHub Team Dashboard_ will allow you to comfortably Visualize
useful information related to your GitHub team.

## Starting the application

Before starting your application you need to set the following environment variables:

    GITHUB_ORGANIZATION=<your github organization>
    GITHUB_TEAM=<your github team within that organization>
    GITHUB_TOKEN<your personal access token>

You can generate a GitHub personal access token in GitHub's settings menu, under _Developer settings_. The token
requires these permissions:

* All permissions under _repo_ including itself.
* _read:org_

Make sure you store this token somewhere, as you can only see it once.

Once you've set all the environment variables above you can start the application as follows:

    ./gradlew run

Thanks to incremental annotation processing, startup time will improve significantlythe next time you run the command
above.

Once the application is started you'll find it under [localhost:8080](http://localhost:8080).

## Running tests

The following command runs all checks:

    ./gradlew check