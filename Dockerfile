FROM buildpack-deps:buster

#dependencies
RUN apt-get update
RUN apt-get install -y --no-install-recommends \
wget \
binutils \
fontconfig \
libfreetype6 \
ca-certificates \
p11-kit \
libbluetooth-dev \
tk-dev \
uuid-dev
RUN rm -rf /var/lib/apt/lists/*

#python
WORKDIR /python
RUN wget https://www.python.org/ftp/python/3.9.5/Python-3.9.5.tar.xz
RUN tar xaf Python-3.9.5.tar.xz
WORKDIR /python/Python-3.9.5
RUN ./configure --enable-optimizations
RUN make -j$(nproc)
RUN make install
RUN rm -rf /python

#jupyter notebook
RUN python3 -m pip install notebook

#java
WORKDIR /java
RUN wget https://download.java.net/java/GA/jdk16.0.1/7147401fd7354114ac51ef3e1328291f/9/GPL/openjdk-16.0.1_linux-x64_bin.tar.gz
RUN tar xaf openjdk-16.0.1_linux-x64_bin.tar.gz
ENV PATH="/java/jdk-16.0.1/bin:${PATH}"
RUN rm -f openjdk-16.0.1_linux-x64_bin.tar.gz

#maven
WORKDIR /maven
RUN wget https://ftp.man.poznan.pl/apache/maven/maven-3/3.8.1/binaries/apache-maven-3.8.1-bin.tar.gz
RUN tar xaf apache-maven-3.8.1-bin.tar.gz
ENV PATH="/maven/apache-maven-3.8.1/bin:${PATH}"
RUN rm -f apache-maven-3.8.1-bin.tar.gz

#ijava
WORKDIR /ijava
RUN wget https://github.com/SpencerPark/IJava/releases/download/v1.3.0/ijava-1.3.0.zip
RUN unzip ijava-1.3.0.zip
RUN python3 install.py --sys-prefix
RUN rm -rf /ijava

#compile code
WORKDIR /home/msrcpsp
COPY . .
RUN mvn install:install-file -Dfile=tools/validator.jar -DgroupId=pl.wroc.pwr.ii.imopse -DartifactId=validator -Dversion=1.0 -Dpackaging=jar
RUN mvn install -T 1C

#finalize
RUN adduser --disabled-password --gecos "Default user" --uid 1000 msrcpsp
RUN chown -R 1000 /home/msrcpsp
USER msrcpsp
CMD ["jupyter", "notebook", "--ip", "0.0.0.0", "--no-browser"]
