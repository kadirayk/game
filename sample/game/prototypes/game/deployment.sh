echo Deploying service $1

docker run -d -p $3:8080 -t $1 $1 $2 8080 

echo waiting 10 seconds for the service to come up
sleep 10