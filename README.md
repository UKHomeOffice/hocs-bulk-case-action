# Hocs-Bulk-Case-Action

This project is intended to be used when one off bulk operations need to be
performed on live cases.

## Features
### Close Case
Currently the only service implemented which allows the application
to close a list of cases which should be given as UUIDs.

## Environment Variables

To run this project, you will need to define the following environment variables:

`workflow-address` defines the address and port for the workflow service

`x-auth-groups` defines the groups to be passed with this header, CASE_ADMIN is required to close a CASE_ADMIN

`x-auth-userId` defines the value to be passed with this header

`gap-between-updates` defines the gap between updates being applied in milliseconds

`file-path` defines the path to the file containing the UUIDs of the cases to be actioned

