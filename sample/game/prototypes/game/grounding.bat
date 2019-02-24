echo Final Gaming Configuration
java -jar grounding.jar %1 %2 %3

copy ..\..\..\..\processes\%1\interview\interview_state.json strategies\strategy1\interview_state.json

docker build -t %1 .