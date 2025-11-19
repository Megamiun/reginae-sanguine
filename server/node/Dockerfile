FROM eclipse-temurin:21-jdk AS build

WORKDIR /app

COPY . .

RUN chmod +x gradlew
RUN --mount=type=cache,target=/root/.gradle \
    ./gradlew :server:node:jsProductionExecutableCompileSync :server:node:jsPublicPackageJson --no-daemon

FROM node:20-alpine

WORKDIR /app

COPY --from=build /app/build/js/packages/reginae-sanguine-server-node .

RUN npm install --omit=dev

EXPOSE 3000

CMD ["node", "kotlin/reginae-sanguine-server-node.js"]
