#!/bin/bash
set -euo pipefail

export KUBE_NAMESPACE=${ENVIRONMENT}
export KUBE_SERVER=${KUBE_SERVER}
export KUBE_TOKEN=${KUBE_TOKEN}
export VERSION=${VERSION}

echo "Environment:  ${KUBE_NAMESPACE}"
echo "Version:      ${VERSION}"

export KUBE_CERTIFICATE_AUTHORITY="https://raw.githubusercontent.com/UKHomeOffice/acp-ca/master/${CLUSTER_NAME}.crt"

cd kd

kd --timeout 60m -f hocs-bulk-case-action.yaml
kd run wait --for=condition=complete "job/hocs-bulk-case-action"
kd --delete -f hocs-bulk-case-action.yaml
