NENs=("0" "30" "60" "90" "120" "150" "180" "210" "240" "270" "300")
EUPs=("0" "180" "360" "540" "720" "900" "1080" "1260" "1440" "1620" "1800")
NGTs=("38" "77" "155" "310" "621" "1242" "2485" "4970" "9941" "19882" "39765")
CAPs=("1" "2" "3" "4" "5" "6")

for c in $CAPs;
do
       java -jar -Xmx10240m jars/main.jar "c" "classic_CAP" "$c" &
done

for op in $NENs;
do
        java -jar -Xmx10240m jars/main.jar "c" "classic_NEN" "$op" &
done

for op in {0..10800..1080};
do
        java -jar -Xmx10240m jars/main.jar "c" "classic_EUP" "$op" &
done
