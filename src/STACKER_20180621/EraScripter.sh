if [ "$#" -lt 5 ]; then 
	echo 'EraScripter [parameter "vo,te"] [period "20170101-20171231"] [presure leve "250,500,850"] [format "F128,F640"] [directory/script]'
	exit
fi 

mkdir -p $5
mkdir -p $5/GRIBs
mkdir -p $5/NCs
#mkdir -p $5_Downloaded/CDLs #Takes too much space
mkdir -p $5/LOGs
echo

javac -d $5 *.java
cd $5/
FOLDOWN=${5##*/}

java EraManager $1 $2 $3 $4 $FOLDOWN

rm EraManager.class PythonScript.class ScriptSize.class

#echo
#read -p "Do you want to start downloading these files now? (Y/n)" choice
choise=Y
case "$choice" in 
 	y|Y|'' ) 
		echo "All right!"
		TIMEFORMAT='%3lR'
		chmod +x $FOLDOWN.sh
		./$FOLDOWN.sh
		#echo
		rm $FOLDOWN.sh
		echo "All downloads finished! (or interrupted)"
		TIMEFORMAT=
		echo;;
	n|N ) 
		echo "No problem. The python scripts will be waiting for you!"
		echo;;

	* ) 
		echo "Invalid answer. The python scripts will be waiting for you!";;
esac

rm -rf $5/GRIBs
