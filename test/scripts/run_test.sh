#!/bin/bash
#
#############################################################
#  Run tests
#    arg #1 -> number times to send request
#                (defaults to 1)
#    arg #2 -> size of request to send in MB
#                (defaults to 10, opts: 10, 20, 50, 100)
#    arg #3 -> URL to send to *optional*
#                (default is configured in script)
#
#  Author:  Daniel Mikusa <dmikusa@gopivotal.com>
#    Date:  2013-12-16
#############################################################
# Default URL used by script
URL=http://localhost:8080/tomcat-8-demos/blocking-io/BlockingEchoServlet
#############################################################
LOOP=$1
SIZE=$2

if [ -z "$LOOP" ]; then
    LOOP=1
fi

if [ -z "$SIZE" ]; then
    SIZE=10
fi

# Calculate our expected result
SIZE_BYTES=$(expr $SIZE \* 1024 \* 1024)

# Search for the root directory
PARENT_DIR=$(pwd)
while [ ! -e "$PARENT_DIR/pom.xml" ]; do
    PARENT_DIR=$(dirname "$PARENT_DIR") 
done

# Extract data file to temp file
TMP_DIR=$(mktemp -dt "tc-test")
TMP_FILE="$TMP_DIR/data.txt"
gunzip -c "$PARENT_DIR/test/data/${SIZE}m.txt.gz" > "$TMP_FILE"

# Send the requests and check if the resulting byte count matches
for (( i=1; i<=$LOOP; i++)); do
    CHECK_BYTES=$(curl -s -d "@$TMP_FILE" "$URL" | wc -c)
    if [ "$SIZE_BYTES" -eq "$CHECK_BYTES" ]; then
        echo "Request #$i: OK"
    else
        echo "Request #$i: FAIL"
    fi
done

# Clean up tmp directory
if [ -d "$TMP_DIR" ]; then
    rm -rf "$TMP_DIR"
fi
