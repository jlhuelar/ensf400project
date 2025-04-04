#!/bin/bash

# Start ZAP in daemon mode
/zap/zap.sh -daemon \
  -port 8080 \
  -host 0.0.0.0 \
  -config api.addrs.addr.name=.* \
  -config api.addrs.addr.regex=true \
  -config api.disablekey=true &

# Wait for ZAP to be ready
timeout 300 bash -c 'while [[ "$(curl -s -o /dev/null -w "%{http_code}" localhost:8080)" != "200" ]]; do sleep 5; done'