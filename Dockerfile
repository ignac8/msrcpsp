FROM buildpack-deps:bullseye
ARG PYTHON_VERSION=3.10.3
ARG JUPYTER_NOTEBOOK_VERSION=6.4.10
ARG JAVA_VERSION=17.0.2
ARG JAVA_DOWNLOAD_CODE=dfd4a8d0985749f896bed50d7138ee7f
ARG MAVEN_VERSION=3.8.5
ARG IJAVA_VERSION=1.3.0

#dependencies
RUN apt-get update && \
apt-get install -y --no-install-recommends \
wget \
binutils \
fontconfig \
libfreetype6 \
ca-certificates \
p11-kit \
libbluetooth-dev \
tk-dev \
uuid-dev && \
rm -rf /var/lib/apt/lists/*

#python
WORKDIR /python
RUN wget https://www.python.org/ftp/python/${PYTHON_VERSION}/Python-${PYTHON_VERSION}.tar.xz && \
tar xaf Python-${PYTHON_VERSION}.tar.xz && \
cd Python-${PYTHON_VERSION} && \
./configure --enable-optimizations && \
make -j$(nproc) && \
make install && \
rm -rf /python

#jupyter notebook
RUN python3 -m pip install notebook==${JUPYTER_NOTEBOOK_VERSION}

#java
WORKDIR /java
RUN wget https://download.java.net/java/GA/jdk${JAVA_VERSION}/${JAVA_DOWNLOAD_CODE}/8/GPL/openjdk-${JAVA_VERSION}_linux-x64_bin.tar.gz && \
tar xaf openjdk-${JAVA_VERSION}_linux-x64_bin.tar.gz && \
rm -f openjdk-${JAVA_VERSION}_linux-x64_bin.tar.gz
ENV PATH="/java/jdk-${JAVA_VERSION}/bin:${PATH}"

#maven
WORKDIR /maven
RUN wget https://apache.osuosl.org/maven/maven-3/${MAVEN_VERSION}/binaries/apache-maven-${MAVEN_VERSION}-bin.tar.gz && \
tar xaf apache-maven-${MAVEN_VERSION}-bin.tar.gz && \
rm -f apache-maven-${MAVEN_VERSION}-bin.tar.gz
ENV PATH="/maven/apache-maven-${MAVEN_VERSION}/bin:${PATH}"

#ijava
WORKDIR /ijava
RUN wget https://github.com/SpencerPark/IJava/releases/download/v${IJAVA_VERSION}/ijava-${IJAVA_VERSION}.zip && \
unzip ijava-${IJAVA_VERSION}.zip && \
python3 install.py --sys-prefix && \
rm -rf /ijava

#compile code
WORKDIR /home/msrcpsp
COPY . .
RUN mvn install:install-file -Dfile=tools/validator.jar -DgroupId=pl.wroc.pwr.ii.imopse -DartifactId=validator -Dversion=1.0 -Dpackaging=jar && \
mvn install -T 1C

#finalize
RUN adduser --disabled-password --gecos "Default user" --uid 1000 msrcpsp && \
chown -R 1000 /home/msrcpsp
USER msrcpsp
CMD ["jupyter", "notebook", "--ip", "0.0.0.0", "--no-browser"]
