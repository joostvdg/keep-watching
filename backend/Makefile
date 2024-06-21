
build-all:
	 mvn flyway:migrate generate-resources generate-sources package -e \
      -Ddb.url="jdbc:postgresql://${DB_IP}/kw" \
      -Dspring.datasource.url=jdbc:postgresql://${DB_IP}/kw \
      -Dspring.datasource.username=docker \
      -Dspring.datasource.password=docker
#	  -Drun.jvmArguments="-Dencrypt.keyStore.secret=${JKS_SECRET} \
#      -Dencrypt.keyStore.password=${JKS_PASS}

verifyd:
	mvnd clean verify \
 		-Dparallel=all \
 		-DperCoreThreadCount=false \
 		-DthreadCount=16 -T 1C -e \
	 	-Dspring.profiles.active=local \
	 	-Ddb.url=jdbc:postgresql://localhost:15432/kw \
	 	-Dspring.datasource.url=jdbc:postgresql://localhost:15432/kw \
	 	-Dspring.datasource.username=docker \
	 	-Dspring.datasource.password=docker

build:
	mvn -e clean package \
	 	-Dspring.profiles.active=local \
	 	-Ddb.url=jdbc:postgresql://localhost:15432/kw \
	 	-Dspring.datasource.url=jdbc:postgresql://localhost:15432/kw \
	 	-Dspring.datasource.username=docker \
	 	-Dspring.datasource.password=docker \
		-DskipTests

run:
	mvn -e spring-boot:run \
	 	-Dspring.profiles.active=local \
	 	-Ddb.url=jdbc:postgresql://${DB_IP}/kw \
	 	-Dspring.datasource.url=jdbc:postgresql://${DB_IP}/kw \
	 	-Dspring.datasource.username=docker \
	 	-Dspring.datasource.password=docker


run2:
	mvn -e spring-boot:run \
	 	-Dspring.profiles.active=local \
	 	-Ddb.url=jdbc:postgresql://localhost:15432/kw \
	 	-Dspring.datasource.url=jdbc:postgresql://localhost:15432/kw \
	 	-Dspring.datasource.username=docker \
	 	-Dspring.datasource.password=docker \
		-DskipTests

dpush:
	docker buildx build . \
		-f docker/boot1/Dockerfile \
		--platform linux/amd64,linux/arm64 \
		--tag ghcr.io/joostvdg/keep-watching:$(VERSION) \
		--build-arg BUILDKIT_INLINE_BUILDINFO_ATTRS=1 \
		--provenance=false \
		--sbom=false \
		--push

dpush-hub:
	docker buildx build . \
		-f docker/boot1/Dockerfile \
		--platform linux/amd64,linux/arm64 \
		--tag joostvdgtanzu/keep-watching:$(VERSION) \
		--build-arg BUILDKIT_INLINE_BUILDINFO_ATTRS=1 \
		--provenance=false \
		--sbom=false \
		--push

dpush-harbor:
	docker buildx build . \
		-f docker/boot1/Dockerfile \
		--platform linux/amd64,linux/arm64 \
		--tag harbor.tap1.h2o-2-24224.h2o.vmware.com/tap-apps/keep-watching:$(VERSION) \
		--build-arg BUILDKIT_INLINE_BUILDINFO_ATTRS=1 \
		--provenance=false \
		--sbom=false \
		--push
