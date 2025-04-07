#!/bin/bash
set -e

echo "Fixing permissions on /var/jenkins_home..."
chown -R 1000:1000 /var/jenkins_home
chmod -R 775 /var/jenkins_home

# Execute the original Jenkins entrypoint script
exec /usr/local/bin/jenkins.sh "$@"
