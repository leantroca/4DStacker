trackDir=$1
stackDir=$2
dataDir=$3

currentSet=$4
threshKm=$5

mkdir -p $stackDir/$currentSet/data/
mkdir -p $stackDir/$currentSet/kho/
mkdir -p $stackDir/$currentSet/logs/
cp "$stackDir"*.java $stackDir/$currentSet/
cp "$stackDir"4DParser.R $stackDir/$currentSet/

cd $trackDir
for file in $dataDir*; do
	if [ $(basename $file | awk -F'[_-]' '{print $2}') = $currentSet ]; then
		filename=$(basename -- "$file")
		extension="${filename##*.}"
		filename="${filename%.*}"
		echo $filename is being TRACKED.....
		#rm -rf output/$filename/
		#./use-track.sh netcdf "$file" "$filename" 1,32,4 #&> "$stackDir"logs/"$filename"_Track.log
		cp output/$filename/ff_trs_pos $stackDir/$currentSet/kho/$filename.kho #cp output/$filename/ff_trs_neg $stackDir/$currentSet/kho/$filename.kho
	fi
done


cd $stackDir/$currentSet/
rm -f data/*.Collection *.Collection data/*.Net *.Net data/*.csv *.csv

javac *.java

for file in "$stackDir"$currentSet/kho/*.kho; do
	gph=$(basename $file | awk -F'[_.]' '{print $3}')
	java Unity $file $gph $currentSet
done
mv *.Collection data/
mv data/`ls data/ | sort | head -1` $stackDir/$currentSet/

a=1
for file in "$stackDir"$currentSet/data/*.Collection; do

	echo
	echo $file is being STACKED.....
	let "a += 1"
	base=$(ls *.Collection)
	java Main $base $file $threshKm
	mv *.csv data/
	echo "LP R in process..."
	Rscript 4DParser.R "$stackDir"$currentSet/ &> 4DParser.log
	rm 4DParser.log
	echo "Finished!"

	java Unity $base $file *.Net data/RlpSol.csv
	mv $base data/
	#java Checker *.Collection data/$base $file

	#read  -n 1 -p "Input Selection:" mainmenuinput
	rm data/$base
	rm $file
	rm *.Net
done

mv *.Collection "$currentSet"_"$a"-stack.Collection
rm *.java *.class *.R
rm -rf data/
