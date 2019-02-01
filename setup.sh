echo setting up OTF-Gaming Prototype

PROSECO_DIR=$1
echo PROSECO_DIR=$PROSECO_DIR

echo building jars
mvn clean install


mkdir -p $PROSECO_DIR/domains

mv target/game-0.0.1-SNAPSHOT.jar target/deployment.jar
cp target/deployment.jar sample/game/prototypes/game/deployment.jar
cp target/strategy.jar sample/game/prototypes/game/strategies/strategy1/strategy.jar
cp target/grounding.jar sample/game/prototypes/game/grounding.jar

cp -r sample/game $PROSECO_DIR/domains