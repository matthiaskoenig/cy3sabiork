#!/bin/bash  
# maven build script for cy3fluxviz
# rebuilds cy3sbml and registers in local maven repository. 
# The variables for the git repositories $CY3SBML and $CY3FLUXVIZ have to be set.
#
# To build without rebuilding cy3sbml use
# 	./maven_build.sh --cy3sbml no

# Use > 1 to consume two arguments per pass in the loop (e.g. each
# argument has a corresponding value to go with it).
# Use > 0 to consume one or more arguments per pass in the loop (e.g.
# some arguments don't have a corresponding value to go with it such
# as in the --default example).
# note: if this is set to > 0 the /etc/hosts part is not recognized ( may be a bug )
while [[ $# > 1 ]]
do
key="$1"

case $key in
    -c|--cy3sbml)
    BUILD_CY3SBML="$2"
    shift # past argument
    ;;
    *)
            # unknown option
    ;;
esac
shift # past argument or value
done
echo BUILD CY3SBML  = "${BUILD_CY3SBML}"

if [ "$BUILD_CY3SBML" == "" ]; then
	# Build cy3sbml latest develop from source
	: "${CY3SBML?Need to set CY3SBML}"
	CY3SBML_VERSION="0.1.7"
	cd $CY3SBML
	mvn install -DskipTests

	# copy in local maven repo
	mvn install:install-file -DgroupId=cy3sbml-temp -DartifactId=cy3sbml -Dversion=$CY3SBML_VERSION -Dfile=$CY3SBML/target/cy3sbml-$CY3SBML_VERSION.jar -Dpackaging=jar -DgeneratePom=true
fi

# lib directory
CY3FLUXVIZ=$( cd "$( dirname "${BASH_SOURCE[0]}" )" && pwd )

cd $CY3FLUXVIZ
mvn clean install 

