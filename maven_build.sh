#!/bin/bash  
################################################################################
# maven build script
#
# Rebuilds cy3sbml and registers in local maven repository. 
#
# To install the dependencies without rebuilding cy3sbml use
# 	./maven_build.sh --cy3sbml no
################################################################################
DIR=$( cd "$( dirname "${BASH_SOURCE[0]}" )" && pwd )
CY3SBML_VERSION="0.2.2"

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
	
	cd $CY3SBML
	mvn install -DskipTests

	# install in local maven repo
	mvn install:install-file -DgroupId=cy3sbml-dep -DartifactId=cy3sbml -Dversion=$CY3SBML_VERSION -Dfile=$CY3SBML/target/cy3sbml-$CY3SBML_VERSION.jar -Dpackaging=jar -DgeneratePom=true -DlocalRepositoryPath=$DIR/lib -DcreateChecksum=true
fi

cd $DIR
mvn clean install 

