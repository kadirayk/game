echo "Copying docker folder to $3"
cp -r dockerfolder/. "$3"
cd "$3"

echo "Copying icpl.properties from $2 to $3"
cp "$2"/icpl.properties "$3"
echo "Copying icpl from $2 to $3"
cp "$2"/icpl "$3"
echo "Copying service instances from $2 to $3"
cp -r "$2"/instances "$3"

docker build -t $1 "$3"