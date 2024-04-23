#!/bin/bash
while getopts t:d:b:u: flag;
do
    case "${flag}" in
        t) DATE="${OPTARG}";;
        d) DRIVER="${OPTARG}";;
        *) echo "Invalid option";;
    esac
done

echo "Testing daily build image"

sed -i "\#</copyDependencies>#a<install><runtimeUrl>https://public.dhe.ibm.com/ibmdl/export/pub/software/openliberty/runtime/nightly/$DATE/$DRIVER</runtimeUrl></install>" pom.xml
cat pom.xml

sudo -u runner ../scripts/testApp.sh
