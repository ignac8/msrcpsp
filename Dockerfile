FROM debian:buster
RUN echo "deb http://ftp.de.debian.org/debian bullseye main" >> /etc/apt/sources.list
RUN apt-get update
RUN apt-get install -y wget
RUN apt-get install -y unzip
RUN apt-get install -y python3
RUN apt-get install -y jupyter-notebook
RUN apt-get install -y openjdk-13-jdk
RUN apt-get install -y maven
WORKDIR /ijava
RUN wget https://github.com/SpencerPark/IJava/releases/download/v1.3.0/ijava-1.3.0.zip
RUN unzip ijava-1.3.0.zip
RUN python3 install.py --sys-prefix
WORKDIR /msrcpsp
COPY . .
RUN mvn install -T 1C
ENTRYPOINT jupyter notebook docs/ds.ipynb --ip 0.0.0.0 --no-browser --allow-root