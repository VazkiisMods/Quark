#!/usr/bin/env bash
set -euo pipefail

# If you cd'd into this directory
if [ -f zeta_progress.sh ]
then
  cd ..
fi

netmf=$(find src/ -name '*.java' -exec grep --max-count=1 --files-with-matches net.minecraftforge {} \+ || true)
arl=$(find src/ -name '*.java' -exec grep --max-count=1 --files-with-matches vazkii.arl {} \+ || true)
netmf_ct=$(find src/ -name '*.java' -exec grep --max-count=1 --files-with-matches net.minecraftforge {} \+ | wc -l)
arl_ct=$(find src/ -name '*.java' -exec grep --max-count=1 --files-with-matches vazkii.arl {} \+ | wc -l)

echo "$netmf"
echo
echo "$arl"
echo
echo "$netmf_ct classes touch net.minecraftforge, and $arl_ct classes touch vazkii.arl"