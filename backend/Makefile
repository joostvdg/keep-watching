
build-all:
	 mvn flyway:migrate generate-resources generate-sources package -e \
      -Ddb.url="jdbc:postgresql://${DB_IP}/kw" \
      -Dspring.datasource.url=jdbc:postgresql://${DB_IP}/kw \
      -Dspring.datasource.username=docker \
      -Dspring.datasource.password=docker
#	  -Drun.jvmArguments="-Dencrypt.keyStore.secret=${JKS_SECRET} \
#      -Dencrypt.keyStore.password=${JKS_PASS}
