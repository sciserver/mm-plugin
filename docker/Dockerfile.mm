# bring in the micromamba image so we can copy files from it
FROM mambaorg/micromamba:2.3.0 AS micromamba


FROM ubuntu:24.04

USER root

# Build instructions from the micromanager repository:
# https://github.com/micro-manager/micro-manager/blob/main/doc/how-to-build.md
RUN apt-get update && \
    apt-get upgrade -y && \
    # extra items we added
    apt install -y wget unzip && \
    apt install -y wget git subversion build-essential autoconf automake libtool pkg-config autoconf-archive openjdk-8-jdk ant libboost-all-dev


# Create the install directory where we will put the compiled micromanager
# install as an imagej plugin. ImageJ needs to be here so download and install
# there
WORKDIR /ij
RUN wget -O ij.uzip https://wsr.imagej.net/distros/linux/ij154-linux64-java8.zip
RUN unzip ij.uzip -d ./tmp && rm ij.uzip && mv ./tmp/ImageJ/* .
RUN chmod --recursive 777 /ij
RUN mkdir -p /dest
RUN chmod --recursive 777 /dest

# micromamba docker example:
# https://micromamba-docker.readthedocs.io/en/latest/advanced_usage.html#adding-micromamba-to-an-existing-docker-image
ARG MAMBA_USER=mambauser
ARG MAMBA_USER_ID=57439
ARG MAMBA_USER_GID=57439
ENV MAMBA_USER=$MAMBA_USER
ENV MAMBA_ROOT_PREFIX="/opt/conda"
ENV MAMBA_EXE="/bin/micromamba"

COPY --from=micromamba "$MAMBA_EXE" "$MAMBA_EXE"
COPY --from=micromamba /usr/local/bin/_activate_current_env.sh /usr/local/bin/_activate_current_env.sh
COPY --from=micromamba /usr/local/bin/_dockerfile_shell.sh /usr/local/bin/_dockerfile_shell.sh
COPY --from=micromamba /usr/local/bin/_entrypoint.sh /usr/local/bin/_entrypoint.sh
COPY --from=micromamba /usr/local/bin/_dockerfile_initialize_user_accounts.sh /usr/local/bin/_dockerfile_initialize_user_accounts.sh
COPY --from=micromamba /usr/local/bin/_dockerfile_setup_root_prefix.sh /usr/local/bin/_dockerfile_setup_root_prefix.sh

RUN /usr/local/bin/_dockerfile_initialize_user_accounts.sh && \
    /usr/local/bin/_dockerfile_setup_root_prefix.sh

# Mamba install instructions:
# https://github.com/conda-forge/miniforge?tab=readme-ov-file#as-part-of-a-ci-pipeline
# RUN wget -O Miniforge3.sh "https://github.com/conda-forge/miniforge/releases/latest/download/Miniforge3-$(uname)-$(uname -m).sh"
# RUN bash Miniforge3.sh -b -p "${HOME}/conda" && \
#     rm Miniforge3.sh
# RUN . "${HOME}/conda/etc/profile.d/mamba.sh"
# Micromamba installation:
# https://mamba.readthedocs.io/en/latest/installation/micromamba-installation.html#linux-and-macos
# RUN sh <(curl -L micro.mamba.pm/install.sh)
# RUN mkdir -p /install

USER $MAMBA_USER
SHELL ["/usr/local/bin/_dockerfile_shell.sh"]


WORKDIR /app
RUN git clone https://github.com/micro-manager/micro-manager.git
WORKDIR /app/micro-manager
RUN git submodule update --init --recursive

RUN micromamba create -p /tmp/env
RUN micromamba install -p /tmp/env -c conda-forge swig=3 openjdk=8


RUN micromamba run -p /tmp/env ./autogen.sh
# Th following line is detailed in ./configure.ac
RUN micromamba run -p /tmp/env ./configure --enable-imagej-plugin=/ij
WORKDIR /app/3rdpartypublic
RUN svn checkout https://svn.micro-manager.org/3rdpartypublic/classext
WORKDIR /app/micro-manager
RUN micromamba run -p /tmp/env make fetchdeps
RUN micromamba run -p /tmp/env make -j4
RUN micromamba run -p /tmp/env make install

USER root

CMD ["cp", "-r", "/ij", "/dest"]