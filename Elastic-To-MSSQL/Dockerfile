FROM python:3
WORKDIR /usr/src/app
COPY requirements.txt ./
ENV PYMSSQL_BUILD_WITH_BUNDLED_FREETDS 1
# Install apt packages
RUN apt-get update && apt-get install -y \
    freetds-bin \
    freetds-common \
    freetds-dev

RUN pip install Cython
RUN pip install ipython
RUN pip install SQLAlchemy
RUN pip install pandas
RUN pip install Alembic

RUN pip install pymssql 
RUN pip install --no-cache-dir -r requirements.txt
COPY . .
CMD [ "python", "./mainschedule.py" ]