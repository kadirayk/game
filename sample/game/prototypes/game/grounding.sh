DIR="$( cd "$( dirname "${BASH_SOURCE[0]}" )" >/dev/null && pwd )"
echo $DIR
echo Switching to directory $DIR
cd $DIR
echo Final Gaming Configuration
# run final gaming configuration inside VM
java -jar grounding.jar $1 $2 $3

#copying interview_state 
cp ../../../../processes/$1/interview/interview_state.json strategies/strategy1/interview_state.json

# build docker image for deployment
docker build -t $1 .