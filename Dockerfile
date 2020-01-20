FROM debian:buster-slim
RUN mkdir -p /usr/share/man/man1
RUN echo "deb http://ftp.de.debian.org/debian bullseye main" >> /etc/apt/sources.list
RUN apt-get update
RUN apt-get install -y openjdk-13-jre-headless
RUN apt-get install -y maven
WORKDIR /msrcpsp
COPY . .
RUN mvn install -T 1C

FROM debian:buster-slim
RUN mkdir -p /usr/share/man/man1
RUN echo "deb http://ftp.de.debian.org/debian bullseye main" >> /etc/apt/sources.list
RUN apt-get update
RUN apt-get install -y wget
RUN apt-get install -y unzip
WORKDIR /ijava
RUN wget https://github.com/SpencerPark/IJava/releases/download/v1.3.0/ijava-1.3.0.zip
RUN unzip ijava-1.3.0.zip

FROM debian:buster-slim
RUN mkdir -p /usr/share/man/man1
RUN echo "deb http://ftp.de.debian.org/debian bullseye main" >> /etc/apt/sources.list
RUN apt-get update
RUN apt-get install -y openjdk-13-jre-headless
RUN apt-get install -y jupyter-notebook
WORKDIR /ijava
COPY --from=1 /ijava /ijava
RUN python3 install.py --sys-prefix
RUN rm -rf /ijava
WORKDIR /msrcpsp
COPY --from=0 /msrcpsp/target/msrcpsp-0.2-SNAPSHOT.jar /msrcpsp/target/msrcpsp-0.2-SNAPSHOT.jar
COPY docs/ds.ipynb /msrcpsp/docs/ds.ipynb
ENTRYPOINT jupyter notebook --ip 0.0.0.0 --no-browser --allow-root docs/ds.ipynb