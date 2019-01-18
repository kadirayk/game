DIR="$( cd "$( dirname "${BASH_SOURCE[0]}" )" >/dev/null && pwd )"
echo $DIR
echo Switching to directory $DIR
cd $DIR
echo Final Gaming Configuration
# run final gaming configuration inside VM
java -jar grounding.jar $1 $2 $3

# build docker image for deployment
docker build -t $1 .