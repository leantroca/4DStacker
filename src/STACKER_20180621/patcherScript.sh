if [ "$#" -lt 2 ]; then 
	echo 'patcherScript [collectionFolder] [patchFolder]'
	exit
fi 

for file in "$1"*.Collection; do
	if  [ $(basename $file | awk -F'[_-]' '{print $2}') -lt 12 ]; then
		set=$(basename $file | awk -F'[_-]' '{print $1}')
		echo ""
		echo "RE-Stacking set:" $set"....."
		/bin/bash ./SuperHodgesStacker.sh ~/Documents/KevinHodges/Track/ $2 ~/Downloads/37_YEARS/NCs/ $set $set 444
		cp $2/$set/*.Collection $2
	fi
done
