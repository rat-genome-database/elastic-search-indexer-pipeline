#!/bin/bash

# Configuration
ELASTICSEARCH_HOST="http://adira01.rgd.mcw.edu:9200"
REPOSITORY_NAME="travis-shared"
SNAPSHOT_NAME="variant_index_cur_snapshot_$(date +'%Y-%m-%d')"  # Snapshot name for today
ALIAS_NAME="variant_index_cur"
INDEX_PATTERN="variant_index_cur"

# Generate a unique suffix for this restore, e.g., `my_index_2025-07-01`
DATE_SUFFIX=$(date +'%Y-%m-%d')
RESTORED_INDEX="${INDEX_PATTERN}_${DATE_SUFFIX}_restored"
# Define the number of shards and replicas you want for the restored index
NUM_SHARDS=5   # Adjust to your desired number of shards
NUM_REPLICAS=1  # Adjust to your desired number of replicas

# Step 1: Get the old index name (the one the alias is pointing to currently)
echo "Getting the current index that the alias ${ALIAS_NAME} is pointing to..."
OLD_INDEX=$(curl -s -X GET "${ELASTICSEARCH_HOST}/_cat/aliases/${ALIAS_NAME}?h=alias,search" | awk '{print $1}')

if [ -z "$OLD_INDEX" ]; then
  echo "Error: Alias ${ALIAS_NAME} does not exist or is not pointing to any index."
  exit 1
else
  echo "The alias ${ALIAS_NAME} is currently pointing to ${OLD_INDEX}."
fi

# Step 2: Restore the snapshot with a unique name
echo "Restoring snapshot ${SNAPSHOT_NAME} from repository ${REPOSITORY_NAME}..."

curl -X POST "${ELASTICSEARCH_HOST}/_snapshot/${REPOSITORY_NAME}/${SNAPSHOT_NAME}/_restore" -H 'Content-Type: application/json' -d"
{
  \"indices\": \"${INDEX_PATTERN}\",
  \"rename_pattern\": \"^(.*)$\",
  \"rename_replacement\": \"${RESTORED_INDEX}\",
   \"index_settings\": {
      \"number_of_shards\": ${NUM_SHARDS},
      \"number_of_replicas\": ${NUM_REPLICAS}
    },
  \"include_global_state\": false
}
"

# Step 3: Wait for the restore to complete (you can make this smarter, e.g., using curl to check recovery status)
echo "Waiting for restore to finish..."
sleep 60  # Adjust sleep time based on expected restore duration

# Step 4: Switch alias to the most recent restored index
echo "Switching alias ${ALIAS_NAME} to the restored index ${RESTORED_INDEX}..."

curl -X POST "${ELASTICSEARCH_HOST}/_aliases" -H 'Content-Type: application/json' -d"
{
  \"actions\": [
    {
      \"remove\": {
        \"index\": \"${OLD_INDEX}\",
        \"alias\": \"${ALIAS_NAME}\"
      }
    },
    {
      \"add\": {
        \"index\": \"${RESTORED_INDEX}\",
        \"alias\": \"${ALIAS_NAME}\"
      }
    }
  ]
}
"

# Step 5: Optionally, clean up the old index (the original index that the alias was pointing to)
echo "Deleting the old index ${OLD_INDEX}..."

curl -X DELETE "${ELASTICSEARCH_HOST}/${OLD_INDEX}"

echo "Restore and alias switch completed successfully! Old index ${OLD_INDEX} deleted."
