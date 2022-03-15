#!/bin/bash
while getopts t:d: flag;
do
    case "${flag}" in
        t) DATE="${OPTARG}";;
        d) DRIVER="${OPTARG}";;
        *) echo "Invalid option";;
    esac
done

echo "Testing latest OpenLiberty Docker image"

#sed -i "\#<artifactId>liberty-maven-plugin</artifactId>#a<configuration><install><runtimeUrl>https://public.dhe.ibm.com/ibmdl/export/pub/software/openliberty/runtime/nightly/$DATE/$DRIVER</runtimeUrl></install></configuration>" pom.xml
sed -i "\#<liberty.runtime.version>22.0.0.2</liberty.runtime.version>#c<install><runtimeUrl>https://public.dhe.ibm.com/ibmdl/export/pub/software/openliberty/runtime/nightly/$DATE/$DRIVER</runtimeUrl></install>" pom.xml
cat pom.xml

sed -i "s;FROM openliberty/open-liberty:full-java11-openj9-ubi;FROM openliberty/daily:latest;g" Dockerfile
cat Dockerfile

docker pull "openliberty/daily:latest"

sudo ../scripts/startMinikube.sh
sudo ../scripts/testApp.sh
sudo ../scripts/stopMinikube.sh
