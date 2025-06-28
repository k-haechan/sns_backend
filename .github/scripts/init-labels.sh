#!/bin/bash

# 템플릿 기반으로 복제한 후 실행
# gh CLI 사용
labels=(
  "major:#FF0000:Major version bump"
  "minor:#84b6eb:Minor version bump"
  "patch:#0075ca:Patch version bump"
)

for label in "${labels[@]}"; do
  IFS=":" read -r name color desc <<< "$label"
  gh label create "$name" --color "${color/#\#}" --description "$desc" 2>/dev/null || \
  gh label edit "$name" --color "${color/#\#}" --description "$desc"
done