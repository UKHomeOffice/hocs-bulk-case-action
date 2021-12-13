# Hocs-Bulk-Case-Action

Hocs-Bulk-Case-Action is a Java application which interacts with [hocs-workflow](https://github.com/UKHomeOffice/hocs-workflow)
to perform actions on cases in HOCS (Home Office Correspondence Service) in bulk.

## Features
### Close Case
This is currently the only service implemented which allows the application
to close a list of cases which should be given as UUIDs in an input file. This input 
file should be placed within the [update files](/files) folder and should be a CSV without headers.

## Run Locally
### Running in Docker
An image containing the application can be built using the supplied Dockerfile. Upon starting
the built container the application will perform the bulk operations automatically.

### Running in an IDE
If you are using an IDE this application can be started by running the ```HocsBulkCaseActionApplication```
main class with the environmental variables specified below set in your run configuration.

## Environment Variables

To run this project, you will need to define the following environment variables:

| Property | Description | Default | Mandatory |
| -------- | -------- |-------- |-------- |
| HOCS_WORKFLOW_SERVICE | The web URL for the workflow service | http://localhost:8091 | Yes |
| GAP_BETWEEN_UPDATES | The time to wait between requests (ms) | 5000 | No |
| FILE_PATH | The full path to the file to use as the update | | Yes
| X_AUTH_GROUPS | The groups to use on the action request | | Yes
| X_AUTH_USERID | The user account UUID to use on the action request | | Yes

### Authors

This project is authored by the Home Office.
