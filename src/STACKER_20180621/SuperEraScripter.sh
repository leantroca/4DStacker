if [ "$#" -lt 7 ]; then 
	echo 'HodgesScripter [dataFolder] || [startSet] [endSet] [overlapDays] || [parameter "vo"] [pressureLevels "850,700"] [format "F128"]'
	exit
fi 

dataDir=$1

startSet=$2
endSet=$3
overlapDays=$4

parameter=$5
pressure=$6
format=$7

javac EraManager.java ScriptSize.java PythonScript.java
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
d=${overlapDays#0}

#let "m += 0"
#currentSet=$startSet

while [ $y -lt $endYear ]; do
	while [ $m -le 12 ]; do
		if [ $m -lt 10 ]; then
			m="0"$m
		fi
		let "d += 0"
		if [ $d -lt 10 ]; then
			d="0"$d
		fi
		startPeriod="$y""$m""01"

		yy=${y#0}
		mm=${m#0}
		y=${y#0}
		m=${m#0}
		let "mm += 1"
		if [ $mm -lt 10 ]; then
			mm="0"$mm
		fi
		if [ $m -eq 12 ]; then
			let "yy += 1"
			mm="01"
		fi
		endPeriod="$yy""$mm""$d"

		/bin/bash EraScripter.sh $parameter $startPeriod"-"$endPeriod $pressure $format $dataDir

		let "m += 1"
	done
	let "y += 1"
	m=1	
done

while [ $m -le $endMonth ]; do
	if [ $m -lt 10 ]; then
		m="0"$m
	fi
	let "d += 0"
	if [ $d -lt 10 ]; then
		d="0"$d
	fi
	startPeriod="$y""$m""01"

	yy=${y#0}
	mm=${m#0}
	y=${y#0}
	m=${m#0}
	let "mm += 1"
	if [ $mm -lt 10 ]; then
		mm="0"$mm
	fi
	if [ $m -eq 12 ]; then
		let "yy += 1"
		mm="01"
	fi
	endPeriod="$yy""$mm""$d"

	/bin/bash EraScripter.sh $parameter $startPeriod"-"$endPeriod $pressure $format $dataDir

	let "m += 1"

done

rm EraManager.class ScriptSize.class PythonScript.class


