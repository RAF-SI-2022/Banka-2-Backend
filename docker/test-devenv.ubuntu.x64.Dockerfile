# Base image x64
FROM ubuntu:22.04 as stage0
RUN su -
RUN apt -y update
RUN apt install -y sudo
RUN apt install -y make
RUN apt install -y curl
RUN exit

# Install docker:
# https://docs.docker.com/engine/install/ubuntu/
FROM stage0 as stage1
WORKDIR /lib/docker
RUN sudo apt-get -y update
RUN sudo apt-get -y install \
        ca-certificates \
        curl \
        gnupg
RUN sudo mkdir -m 0755 -p /etc/apt/keyrings
RUN curl -fsSL https://download.docker.com/linux/ubuntu/gpg | sudo gpg --dearmor -o /etc/apt/keyrings/docker.gpg
RUN echo \
  "deb [arch="$(dpkg --print-architecture)" signed-by=/etc/apt/keyrings/docker.gpg] https://download.docker.com/linux/ubuntu \
  "$(. /etc/os-release && echo "$VERSION_CODENAME")" stable" | \
  sudo tee /etc/apt/sources.list.d/docker.list > /dev/null
RUN sudo chmod a+r /etc/apt/keyrings/docker.gpg
RUN sudo apt-get -y update
RUN sudo apt-get -y install docker-ce docker-ce-cli containerd.io docker-buildx-plugin docker-compose-plugin
RUN sudo update-alternatives --set iptables /usr/sbin/iptables-legacy
RUN sudo update-alternatives --set ip6tables /usr/sbin/ip6tables-legacy

FROM stage1 as stage2
RUN su -
RUN sudo update-alternatives --set iptables /usr/sbin/iptables-legacy
RUN sudo update-alternatives --set ip6tables /usr/sbin/ip6tables-legacy

FROM stage2 as stage3
RUN rm -rf /home/project
WORKDIR /home/project
COPY . /home/project