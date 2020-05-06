### Test helpers for Jira plugin

To use:

From the project root:

    docker-compose -f jira-e2e-tests/helpers/jira-psql-compose.yml up

Then visit `http://localhost:2990/jira/` and perform setup. (Todo: Automate setup.)

The latest version of the plugin can be built and installed using:

    mvn package -DskipTests && ./jira-e2e-tests/helpers/upload-plugin
     
This builds, uploads, and installs the plugin, and restarts the Jira container.

Optionally; `cd` to `jira-e2e-tests` and run `yarn cypress` to run automated
UI tests. See README for more details.
