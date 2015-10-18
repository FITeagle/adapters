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
#  ./createNewAdapter CoffeeMachine coffeemachine
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
class="${1:-}"
package="${2:-}"
rtype="${3:-}"
rspace="${4:-}"
src_class="Motor"
src_package="motor"
dir_main="src/main/java/org/fiteagle/adapters/"
dir_test="src/test/java/org/fiteagle/adapters/"

if [ "" = "${package}" ]; then
  echo "USAGE: $0 <YourClassName> <yourpackagename> <ResourceType> <resourcenamespace>"
  echo "Example: $0 CoffeeMachine coffee Coffee coffee"
  exit 1
fi

[ -d "${package}" ] && (echo "FAIL: ${package} folder already exist"; exit 2;)
[[ "${class}" =~ [^a-zA-Z] ]] && (echo "FAIL: ${class} not a valid class name in Java"; exit 3;)
[[ "${package}" =~ [^a-z] ]] && (echo "FAIL: ${package} not a valid package name in Java"; exit 4;)

cp -r "${src_package}" "${package}"
cd "${package}" || (echo "FAIL: could not change dir" ; exit 5;)

sed -i.bak -e "s/${src_class}/${class}/g" pom.xml
sed -i.bak -e "s/${src_package}/${package}/g" pom.xml

mv ${dir_main}/${src_package} ${dir_main}/${package}
mv ${dir_test}/${src_package} ${dir_test}/${package}

find src -iname "${src_class}*.java"| xargs -I {} sh -c 'mv {} $(echo {}|sed "s/'${src_class}/${class}'/g")'
mv src/main/resources/ontologies/${src_package}.ttl src/main/resources/ontologies/${package}.ttl
sed -i.bak -e "s/${src_class}/${rtype}/g" src/main/resources/ontologies/${package}.ttl
sed -i.bak -e "s/${src_package}/${rspace}/g" src/main/resources/ontologies/${package}.ttl
find src -type f| xargs -I {} sh -c 'sed -i.bak -e "s/'${src_class}/${class}'/g" {}'
find src -type f| xargs -I {} sh -c 'sed -i.bak -e "s/'${src_package}/${package}'/g" {}'
find . -iname "*.bak" -exec rm {} \;

mkdir -p ~/.fiteagle/
cat > ~/.fiteagle/${class}Garage.properties << EOL
{
  "adapterInstances": [
    {
      "componentID": "http://localhost/resource/CoffeemachineGarage-1"
    }
  ]
}
EOL


exit 0
