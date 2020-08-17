call mvn install:install-file -Dfile=tools/validator.jar -DgroupId=pl.wroc.pwr.ii.imopse -DartifactId=validator -Dversion=1.0 -Dpackaging=jar && ^
mvn clean install -T 1C && ^
jupyter notebook docs/ds.ipynb
pause