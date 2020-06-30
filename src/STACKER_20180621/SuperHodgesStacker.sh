if [ "$#" -lt 6 ]; then 
	echo 'HodgesScripter [trackFolder] [stackFolder] [dataFolder] || [startSet] [endSet] [threshKm]'
	exit
fi 

trackDir=$1
stackDir=$2
dataDir=$3

startSet=$4
endSet=$5
threshKm=$6

mkdir -p $stackDir
cp *.java $stackDir
cp 4DParser.R $stackDir

startYear=${startSet:0:4}
startYear=${startYear#0}
startMonth=${startSet:4:2}
startMonth=${startMonth#0}
endYear=${endSet:0:4}
endYear=${endYear#0}
endMonth=${endSet:4:2}
endMonth=${endMonth#0}

y=${startYear#0}
m=${startMonth#0}
let "m += 0"
currentSet=$startSet

while [ $y -lt $endYear ]; do
	while [ $m -le 12 ]; do
		if [ $m -lt 10 ]; then
			currentSet="$y"0"$m"01
		else
			currentSet="$y""$m"01
		fi

		/bin/bash HodgesStacker.sh $trackDir $stackDir $dataDir $currentSet $threshKm

		y=${y#0}
		m=${m#0}
		let "m += 1"
	done
	let "y += 1"
	m=1	
done

while [ $m -le $endMonth ]; do
	if [ $m -lt 10 ]; then
		currentSet="$y"0"$m"01
	else
		currentSet="$y""$m"01
	fi

	/bin/bash HodgesStacker.sh $trackDir $stackDir $dataDir $currentSet $threshKm

	let "m += 1"

done


