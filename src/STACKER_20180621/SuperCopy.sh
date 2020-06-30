if [ "$#" -lt 3 ]; then 
	echo 'HodgesScripter [stackFolder] [startSet] [endSet] [concatFolder]'
	exit
fi 

stackDir=$1

startSet=$2
endSet=$3

concatDir=$4

#concatDir=$(dirname "${outDir}")
mkdir -p $concatDir
cp *.java $concatDir
cd $concatDir
javac *.java

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

#find $startSet/ -maxdepth 1 -type f | xargs cp -t .

while [ $y -lt $endYear ]; do
	while [ $m -le 12 ]; do
		if [ $m -lt 10 ]; then
			currentSet="$y"0"$m"01
		else
			currentSet="$y""$m"01
		fi

		cp $stackDir/$currentSet/*.Collection $concatDir

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

	cp $stackDir/$currentSet/*.Collection $concatDir

	let "m += 1"

done

java Concat *.Collection

