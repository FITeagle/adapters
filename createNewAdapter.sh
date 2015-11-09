#!/usr/bin/env bash
# Script to create new FITeagle adapters
#
# More info:
#  - http://fiteagle.org
#
# Version 1.0.0
#
# Authors:
#  - Alexander Willner <alexander.willner@tu-berlin.de>
#
# Usage example:
#  ./createNewAdapter CoffeeMachine Coffee
#

# Enable dubugging
#set -o xtrace

# Exit on error. Append ||true if you expect an error.
# set -e is safer than #!/bin/bash -e because that is neutralised if
# someone runs your script like `bash yourscript.sh`
set -o errexit
set -o nounset

# Bash will remember & return the highest exitcode in a chain of pipes.
# This way you can catch the error in case mysqldump fails in `mysqldump |gzip`
set -o pipefail

# Environment variables and their defaults
LOG_LEVEL="${LOG_LEVEL:-6}" # 7 = debug -> 0 = emergency
__dir="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
__file="${__dir}/$(basename "${BASH_SOURCE[0]}")"
adapter="${1:-}"
resource="${2:-}"
adapter_small=$(echo ${adapter}|tr '[:upper:]' '[:lower:]')
resource_small=$(echo ${resource}|tr '[:upper:]' '[:lower:]')
src_adapter="MotorGarage"
src_resource="Motor"
src_adapter_small=$(echo ${src_adapter}|tr '[:upper:]' '[:lower:]')
src_resource_small=$(echo ${src_resource}|tr '[:upper:]' '[:lower:]')
dir_main="src/main/java/org/fiteagle/adapters/"
dir_test="src/test/java/org/fiteagle/adapters/"

if [ "" = "${resource}" ]; then
  echo "USAGE: $0 <ResourceAdapter> <Resource>"
  echo "Example: $0 CoffeeMachine Coffee"
  exit 1
fi

#echo "DEBUG From: ${src_adapter} ${src_adapter_small} ${src_resource} ${src_resource_small}"
#echo "DEBUG To: ${adapter} ${adapter_small} ${resource} ${resource_small}"

#todo: [[ "${adapter}" =~ [^A-Za-z] ]] && (echo "FAIL: ${adapter} not a valid class name in Java"; exit 2;)
#todo: [[ "${resource}" =~ [^A-Za-z] ]] && (echo "FAIL: ${resource} not a valid class name in Java"; exit 3;)
[ -d "${resource_small}" ] && (echo "FAIL: ${resource_small} folder already exist"; exit 4;)

cp -r "${src_resource_small}" "${resource_small}"
cd "${resource_small}" || (echo "FAIL: could not change dir" ; exit 5;)

sed -i.bak -e "s/${src_resource}/${resource}/g" pom.xml
sed -i.bak -e "s/${src_resource_small}/${resource_small}/g" pom.xml

mv ${dir_main}/${src_resource_small} ${dir_main}/${resource_small}
mv ${dir_test}/${src_resource_small} ${dir_test}/${resource_small}

find src -iname "${src_resource}*.java"| xargs -I {} sh -c 'mv {} $(echo {}|sed "s/'${src_resource}/${resource}'/g")'
find src -iname "*${src_resource}*.ttl"| xargs -I {} sh -c 'mv {} $(echo {}|sed "s/'${src_resource}/${resource}'/g")'
mv src/main/resources/ontologies/${src_resource_small}.ttl src/main/resources/ontologies/${resource_small}.ttl
find src -type f| xargs -I {} sh -c 'sed -i.bak -e "s/'${src_adapter}/${adapter}'/g" {}'
find src -type f| xargs -I {} sh -c 'sed -i.bak -e "s/'${src_resource}/${resource}'/g" {}'
find src -type f| xargs -I {} sh -c 'sed -i.bak -e "s/'${src_adapter_small}/${adapter_small}'/g" {}'
find src -type f| xargs -I {} sh -c 'sed -i.bak -e "s/'${src_resource_small}/${resource_small}'/g" {}'

find . -iname "*.bak" -exec rm {} \;

mkdir -p ~/.fiteagle/
cat > ~/.fiteagle/${adapter}.properties << EOL
{
  "adapterInstances": [
    {
      "componentID": "http://localhost/resource/CoffeeMachine-1"
    }
  ]
}
EOL

exit 0
