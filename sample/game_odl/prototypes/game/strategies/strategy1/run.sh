DIR="$( cd "$( dirname "${BASH_SOURCE[0]}" )" >/dev/null && pwd )"
echo $DIR
echo Switching to directory $DIR
cd $DIR
echo Starting execution of Strategy in strategy.jar $1 $2 $3 $4
java -jar strategy.jar $1 $2 $3 $4 192.168.56.101 1099 remote