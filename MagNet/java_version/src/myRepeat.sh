t=("f" "k" "h")
NENs=("0" "30" "60" "90" "120" "150" "180" "210" "240" "270" "300")
EUPs=("0" "180" "360" "540" "720" "900" "1080" "1260" "1440" "1620" "1800")
NGTs=("38" "77" "155" "310" "621" "1242" "2485" "4970" "9941" "19882" "39765")
CAPs={45..451..45}

for type in $t;
do
#     for op in {0..12000..1200};
    for op in $EUPs;
    do
            java -jar -Xmx10240m jars/sugarjar_simu.jar "$type" "period" "$op" &
    done
done
    for c in $CAPs;
    do
           java -jar -Xmx10240m jars/sugarjar_simu.jar "$type" "CAP" "$c" &
    done

    for op in {0..12000..1200};
    do
            java -jar -Xmx10240m jars/main.jar "$type" "period" "$op" &
    done

    for op in $NENs;
    do
            java -jar -Xmx10240m jars/main.jar "$type" "neighbor" "$op" &
    done

done

