FROM python:3.9

# add chrome repository
RUN wget -q -O - https://dl-ssl.google.com/linux/linux_signing_key.pub | apt-key add -
RUN sh -c 'echo "deb [arch=amd64] http://dl.google.com/linux/chrome/deb/ stable main" >> /etc/apt/sources.list.d/google-chrome.list'

RUN apt -yqq update && apt -yqq upgrade && apt-get -yqq --no-install-recommends install \
      apt-transport-https \
      curl \
      gnupg \
      lsb-release \
      software-properties-common \
      git \
      default-jdk \
      bash-completion \
      build-essential \
      cmake \
      libcurl3-openssl-dev  \
      libssl-dev \
      libxml2 \
      libxml2-dev  \
      pkg-config \
      ca-certificates \
      xclip \
      google-chrome-stable \
      unzip

RUN curl -fsSL https://download.docker.com/linux/debian/gpg | gpg --dearmor -o /usr/share/keyrings/docker-archive-keyring.gpg
RUN echo "deb [arch=amd64 signed-by=/usr/share/keyrings/docker-archive-keyring.gpg] https://download.docker.com/linux/debian $(lsb_release -cs) stable" | tee /etc/apt/sources.list.d/docker.list > /dev/null
RUN apt -yqq update && apt -yqq install docker-ce docker-ce-cli containerd.io

# Build and install the lastpass cli
RUN git clone https://github.com/lastpass/lastpass-cli.git && cd lastpass-cli && make && cp build/lpass /usr/local/bin

# set display port to avoid crash
ENV DISPLAY=:99

# upgrade pip
RUN pip install --upgrade pip

# install selenium
RUN pip install selenium requests urllib3 garminconnect pymysql

# install chromedriver
RUN wget -O /tmp/chromedriver.zip http://chromedriver.storage.googleapis.com/`curl -sS chromedriver.storage.googleapis.com/LATEST_RELEASE`/chromedriver_linux64.zip
RUN unzip /tmp/chromedriver.zip chromedriver -d /usr/local/bin/
