../gradlew clean build -x test -p ../

docker build -t federated-catalog:latest ../launchers/postgres-prod
