#!/bin/bash

echo "Creating Cleanup Job"
# // daniel everything ok?
curl -X POST "https://localhost:8080/jobs/workflow-execution-cleanup"