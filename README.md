# Hocs-Bulk-Case-Action

Hocs-Bulk-Case-Action is a Java application which interacts with [hocs-workflow](https://github.com/UKHomeOffice/hocs-workflow)
to perform actions on cases in HOCS (Home Office Correspondence Service) in bulk.

## Features
### Close Case
Currently the only service implemented which allows the application
to close a list of cases which should be given as UUIDs.

## Run Locally
### Running in Docker
An image containing the application can be built using the supplied Dockerfile. Upon starting
the built container the application will perform the bulk operations automatically.

###Running in an IDE
If you are using an IDE this application can be started by running the ```HocsBulkCaseActionApplication```
main class with the environmental variables specified below set in your run configuration.

## Environment Variables

To run this project, you will need to define the following environment variables:

`workflow-address` defines the address and port for the workflow service

`x-auth-groups` defines the groups to be passed with this header, CASE_ADMIN is required to close a CASE_ADMIN

`x-auth-userId` defines the value to be passed with this header

`gap-between-updates` defines the gap between updates being applied in milliseconds

`file-path` defines the path to the file containing the UUIDs of the cases to be actioned


### Authors

This project is authored by the Home Office.
