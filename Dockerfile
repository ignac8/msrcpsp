FROM debian:buster
RUN echo "deb http://ftp.de.debian.org/debian bullseye main" >> /etc/apt/sources.list
RUN apt-get update
RUN apt-get install -y wget
RUN apt-get install -y unzip
RUN apt-get install -y jupyter-notebook
RUN apt-get install -y openjdk-13-jdk
RUN apt-get install -y maven
WORKDIR /ijava
RUN wget https://github.com/SpencerPark/IJava/releases/download/v1.3.0/ijava-1.3.0.zip
RUN unzip ijava-1.3.0.zip
RUN python3 install.py --sys-prefix
ENV NB_USER msrcpsp
ENV NB_UID 1000
ENV HOME /home/$NB_USER
RUN adduser --disabled-password --gecos "Default user" --uid $NB_UID $NB_USER
WORKDIR $HOME
COPY . .
RUN mvn install:install-file -Dfile=tools/validator.jar -DgroupId=pl.wroc.pwr.ii.imopse -DartifactId=validator -Dversion=1.0 -Dpackaging=jar
RUN mvn install -T 1C
RUN chown -R $NB_UID $HOME
USER $NB_USER
CMD ["jupyter", "notebook", "--ip", "0.0.0.0"]